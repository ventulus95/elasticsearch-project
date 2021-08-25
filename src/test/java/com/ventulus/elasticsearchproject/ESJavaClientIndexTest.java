package com.ventulus.elasticsearchproject;

import com.ventulus.elasticsearchproject.config.ElasticRestClientConfig;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {ElasticRestClientConfig.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ESJavaClientIndexTest {

    @Autowired
    private RestHighLevelClient client;

    @AfterAll//생성된 테스트 인덱스 삭제
    public void deleteIndex() throws IOException {
        client.indices().delete(new DeleteIndexRequest("test"), RequestOptions.DEFAULT);
    }

    @Test
    @DisplayName("index 넣는 기능 추가")
    public void esIndexInsert() throws IOException {
        CreateIndexResponse response = client.indices().create(new CreateIndexRequest("test"), RequestOptions.DEFAULT);
        assertTrue(response.isAcknowledged());
    }

    @Test
    @Disabled
    @DisplayName("index mapping 기능")
    public void esIndexInsertMapping() throws IOException {
        Settings setting = Settings.builder().put("index.analysis.tokenizer.nori_user_dict.type", "nori_tokenizer")
                .put("index.analysis.tokenizer.nori_user_dict.decompound_mode", "mixed")
                .put("index.analysis.tokenizer.nori_user_dict.user_dictionary", "userdict.txt")
                .put("index.analysis.analyzer.my_analyzer.type", "custom")
                .put("index.analysis.analyzer.my_analyzer.tokenizer", "nori_user_dict")
                .build();
        CreateIndexResponse response = client.indices().create(new CreateIndexRequest("test").settings(setting)
                .mapping("{\n" +
                "  \"properties\": {\n" +
                "    \"message\": {\n" +
                "      \"type\": \"text\",\n" +
                "       \"analyzer\": \"my_analyzer\"\n"+
                "    }\n" +
                "  }\n" +
                "}", XContentType.JSON), RequestOptions.DEFAULT);
        assertTrue(response.isAcknowledged());
    }

    @Test
    @DisplayName("document 추가 기능")
    public void esDocumentInsert() throws IOException {
        IndexResponse response = client.index(new IndexRequest("test").id("1").source("message", "흑흑 오늘의 밥은 맛있었다.", "title", "1234"), RequestOptions.DEFAULT);
        assertEquals(response.getIndex(), "test");
        assertEquals(response.getResult(), DocWriteResponse.Result.CREATED);
    }

    @Test
    @DisplayName("document Bulk 추가 기능")
    public void esDocumentBulkInsert() throws IOException {
        //TODO 이거 BulkRequest 변수 지정해놓고, for문 돌면서 넣어주는게 더 편해보임 메서드 채이닝식으로 구현하려면 넘 빡셈
        String[] arr = new String[]{"message", "흑흑 오늘의 밥은 맛있었다.", "title", "1234"};
        BulkResponse response = client.bulk(new BulkRequest("test").add(new IndexRequest()
                .id("2").source("message", "흑흑 오늘의 밥은 맛있었다.", "title", "1234"))
                .add(new IndexRequest().id("1111").source(arr))
                .add(new IndexRequest().id("5").source("message", "흑흑 오늘의 밥은 맛있었다.", "title", "1234"))
                .add(new IndexRequest().id("7").source("message", "흑흑 오늘의 밥은 맛있었다.", "title", "1234"))
                .add(new IndexRequest().id("9").source("message", "흑흑 오늘의 밥은 맛있었다.", "title", "1234")
        ), RequestOptions.DEFAULT);
        assertFalse(response.hasFailures());

    }


}
