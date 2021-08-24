package com.ventulus.elasticsearchproject;


import com.ventulus.elasticsearchproject.model.Article;
import com.ventulus.elasticsearchproject.model.ArticleRepository;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class ElasticJavaClientTest {

    private static RestHighLevelClient client;
    @Autowired
    private ArticleRepository articleRepository;

    @BeforeAll
    public static void initclient(){
        client = new RestHighLevelClient(RestClient.builder(new HttpHost("localhost", 9200, "http")));
    }

    @Test
    @DisplayName("엘라스틱에 재대로 전달되는지?")
    public void elastic_transfer(){
        for (int i = 0; i <5; i++) {
            Article article = new Article();
            article.setId(String.valueOf(i));
            article.setPrice(12134L);
            article.setContent("이럴수가! 크렇게 쉬울수가?");
            article.setTitle("엘라스틱서치가 이렇게 쉽다며?");
            articleRepository.save(article);
        }
    }

    @Test
    @DisplayName("엘라스틱 Java client로 구현")
    public void java_elasticclient() throws IOException {
        SearchRequest searchRequest = new SearchRequest("article").source(new SearchSourceBuilder().query(QueryBuilders.matchAllQuery()));
        for (SearchHit hit :client.search(searchRequest, RequestOptions.DEFAULT).getHits()){
            System.out.println(hit.getSourceAsMap().get("id"));
        }
        System.out.println();
    }

    @Test
    @DisplayName("match 기능 구현") //한개만 매칭시키려면 다음처럼 처리해도 좋다
    public void esJavaClientMatch() throws IOException {
        SearchRequest searchRequest = new SearchRequest("article").source(new SearchSourceBuilder().query(QueryBuilders.matchQuery("title", "엘라스틱")));
        SearchResponse res = client.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println("res의 경우: "+res.getHits().getTotalHits());
        for (SearchHit hit: res.getHits()){
            System.out.println(hit.getSourceAsString());
        }
    }

    @Test
    @DisplayName("하이라이팅 기능 구현")
    public void esJavaClientHighlight() throws IOException {
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        for (String value: new String[]{"title", "content"}){ //빌더타입의 경우 걍 for문 돌려서 빌더에 필드 계속 넣어주면 된다.
            highlightBuilder.field(value);
        }
        SearchRequest searchRequest = new SearchRequest("article").source(new SearchSourceBuilder()
                //multiMatching 기능을 실험해보고 싶어서 했는데, multimatching의 경우 가변인자로 field를 받게 되어있는데, 이것은 결국 배열로 넘겨줘도 처리가 가능함.
                .query(QueryBuilders.multiMatchQuery("쉽다", new String[]{"title", "content"})).highlighter(highlightBuilder));
        SearchResponse res = client.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println("res의 경우: "+res.getHits().getTotalHits());
        for (SearchHit hit: res.getHits()){
            System.out.println(hit.getHighlightFields());
        }
    }

    @Test
    @DisplayName("필터링 기능 구현") //여기서 말하는 filtering은 화면에 노출시킬수 있는 정보를 필터링하는 기능이다
    public void esJavaClientFiltering() throws IOException {
        String[] include = new String[]{"title", "content"};
        HighlightBuilder highlightBuilder = new HighlightBuilder().field("content");
        SearchRequest searchRequest = new SearchRequest("article").source(new SearchSourceBuilder()
                .query(QueryBuilders.matchQuery("title", "쉽다")).fetchSource(include, new String[]{}).highlighter(highlightBuilder));
        SearchResponse res = client.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println("res의 경우: "+res.getHits().getTotalHits());
        for (SearchHit hit: res.getHits()){
            System.out.println(hit);
        }
    }
}
