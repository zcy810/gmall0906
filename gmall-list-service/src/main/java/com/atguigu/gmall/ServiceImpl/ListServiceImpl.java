package com.atguigu.gmall.ServiceImpl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.SkuLsInfo;
import com.atguigu.gmall.bean.SkuLsParam;
import com.atguigu.service.ListService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ListServiceImpl implements ListService {
    @Autowired
    JestClient jestClient;

    @Override
    public List<SkuLsInfo> list(SkuLsParam skuLsParam) {
        List<SkuLsInfo> skuLsInfos = new ArrayList<>();
        // 查询
        String dsl = getMyDsl(skuLsParam);
        System.out.println(dsl);


        Search build = new Search.Builder(dsl).addIndex("gmall0906").addType("SkuLsInfo").build();
        SearchResult execute = null;
        try {
            execute = jestClient.execute(build);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Long total = execute.getTotal();

        if(total>0){
            List<SearchResult.Hit<SkuLsInfo, Void>> hits = execute.getHits(SkuLsInfo.class);

            for (SearchResult.Hit<SkuLsInfo, Void> hit : hits) {
                SkuLsInfo source = hit.source;
                Map<String, List<String>> highlight = hit.highlight;
                if(highlight != null) {
                    List<String> skuName = highlight.get("skuName");

                    String s = skuName.get(0);
                    source.setSkuName(s);
                }
                    skuLsInfos.add(source);
            }
        }


        return skuLsInfos;
    }



    public String getMyDsl(SkuLsParam skuLsParam) {

        // 查询语句封装
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();


        // 联合查询
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        // 三级分类id
        String catalog3Id = skuLsParam.getCatalog3Id();
        if(StringUtils.isNotBlank(catalog3Id)){
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("catalog3Id", catalog3Id);
            boolQueryBuilder.filter(termQueryBuilder);
        }

        // 分类属性值
        String[] valueId = skuLsParam.getValueId();
        if(valueId!=null&&valueId.length>0){
            for (String id : valueId) {
                TermQueryBuilder termQueryBuilder = new TermQueryBuilder("skuAttrValueList.valueId", id);
                boolQueryBuilder.filter(termQueryBuilder);
            }
        }


        // 关键字
        String keyword = skuLsParam.getKeyword();
        if(StringUtils.isNotBlank(keyword)){
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName", keyword);
            boolQueryBuilder.must(matchQueryBuilder);
        }


        searchSourceBuilder.query(boolQueryBuilder);

        //高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("skuName");
        searchSourceBuilder.highlight(highlightBuilder);


        // 查询数量
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(100);

        return searchSourceBuilder.toString();
    }
}
