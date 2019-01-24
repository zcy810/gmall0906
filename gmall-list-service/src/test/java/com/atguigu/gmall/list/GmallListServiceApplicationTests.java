package com.atguigu.gmall.list;


import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.bean.SkuLsInfo;
import com.atguigu.gmall.list.testbean.Movie;
import com.atguigu.service.SkuService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.swing.text.Highlighter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallListServiceApplicationTests {


	@Autowired
	JestClient jestClient;


	@Reference
	SkuService skuService;


	@Test
	public void dslTest() throws IOException {
		String dsl = getMyDsl();
		System.out.println(dsl);
		Search build = new Search.Builder(dsl).addIndex("gmall0906").addType("SkuLsInfo").build();
		SearchResult execute = jestClient.execute(build);
		List<SearchResult.Hit<SkuLsInfo, Void>> hits = execute.getHits(SkuLsInfo.class);
		List<SkuLsInfo> skuLsInfos = new ArrayList<>();
		for (SearchResult.Hit<SkuLsInfo, Void> hit : hits) {
			SkuLsInfo source = hit.source;
			skuLsInfos.add(source);
		}
		System.out.println(skuLsInfos.size());


	}

	private String getMyDsl() {

		//查询语句
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		//联合查询
		BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
		MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName","我自己");
		boolQueryBuilder.must(matchQueryBuilder);
		searchSourceBuilder.query(boolQueryBuilder);
		//高亮
		HighlightBuilder highlightBuilder = new HighlightBuilder();
		highlightBuilder.field("skuName");
		searchSourceBuilder.highlight(highlightBuilder);
		return searchSourceBuilder.toString();


	}

	@Test
	public void test0() throws IOException {
	List<SkuInfo> skuInfos = skuService.getSkuListByCatalog3Id("61");
		for (SkuInfo skuInfo : skuInfos) {
			SkuLsInfo skuLsInfo = new SkuLsInfo();
			BeanUtils.copyProperties(skuInfo,skuLsInfo);
			Index build = new Index.Builder(skuLsInfo).index("gmall0906").type("SkuLsInfo").id(skuLsInfo.getId()).build();
			jestClient.execute(build);

		}
	}

	@Test
	public void contextLoads() throws IOException {
		Search search = new Search.Builder("{\n" +
				"  \"query\": {\n" +
				"    \"bool\": {\n" +
				"     \"filter\": [\n" +
				"       {\n" +
				"         \"terms\": {\n" +
				"           \"actorList.id\": [\"3\",\"1\",\"2\",\"4\"]\n" +
				"         }\n" +
				"       },\n" +
				"       {\n" +
				"         \"term\":{\n" +
				"           \"actorList.id\":\"4\"\n" +
				"         }\n" +
				"       }\n" +
				"     ] \n" +
				"    }\n" +
				"  }\n" +
				"}").addIndex("movie_chn").addType("movie").build();

		SearchResult execute = jestClient.execute(search);

		List<SearchResult.Hit<Movie, Void>> hits = execute.getHits(Movie.class);


		for (SearchResult.Hit<Movie, Void> hit : hits) {
			Movie source = hit.source;
			System.err.println(source.getId()+source.getName()+source.getDoubanScore());

		}
	}


	}



