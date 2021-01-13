package com.bp.esapi01;

import com.alibaba.fastjson.JSON;
import com.bp.esapi01.entity.User;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.common.TimeUtil;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class EsApi01ApplicationTests {
    @Autowired
    @Qualifier(value = "restHighLevelClient")
    private RestHighLevelClient client;

    @Test
    void contextLoads() {
    }

    /**
     * 创建索引
     */
    @Test
    void testCreateIndex() throws IOException {
        //1.创建索引请求
        CreateIndexRequest request = new CreateIndexRequest("wyy_index");
        //2.客户端执行请求并得到响应
        CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
        System.out.println(response);
    }

    /**
     * 获取索引
     */
    @Test
    void testIndexExist() throws IOException {
        GetIndexRequest request = new GetIndexRequest("wyy_index");
        boolean b = client.indices().exists(request,RequestOptions.DEFAULT);
        System.out.println(b);
    }

    /**
     * 删除索引
     */
    @Test
    void testDeleteIndex() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest("wyy_index");
        AcknowledgedResponse acknowledgedResponse = client.indices().delete(request,RequestOptions.DEFAULT);
        System.out.println(acknowledgedResponse);
    }

    /**
     * 添加文档
     */
    @Test
    void testAddDocument() throws IOException {
        User user = new User("张三", 23);
        IndexRequest request = new IndexRequest("wyy_index")
                .id("1")
                .timeout(TimeValue.timeValueSeconds(1))
                .timeout("1s").source(JSON.toJSONString(user), XContentType.JSON);
        //客户端执行添加请求
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        System.out.println(response.toString());
        System.out.println(response.status());
    }
    /**
     * 判断是否存在
     */
    @Test
    void testExist() throws IOException {
        GetRequest request = new GetRequest("wyy_index", "1");
        request.storedFields("_none_");
        boolean b = client.exists(request, RequestOptions.DEFAULT);
        System.out.println(b);
    }
    /**
     * 获取文档信息
     */
    @Test
    void testGetDocument() throws IOException {
        GetRequest request = new GetRequest("wyy_index", "1");
        GetResponse getResponse = client.get(request, RequestOptions.DEFAULT);
        System.out.println(getResponse.getSourceAsString());
        System.out.println(getResponse);
    }
    /**
     * 更新文档
     */
    @Test
    void testUpdateDocument() throws IOException {
        UpdateRequest updateRequest = new UpdateRequest("wyy_index","1").timeout("1s");
        User user = new User();
        user.setAge(24);
        updateRequest.doc(JSON.toJSONString(user), XContentType.JSON);
        UpdateResponse update = client.update(updateRequest, RequestOptions.DEFAULT);
        System.out.println(update.status());
    }
    /**
     *删除文档
     */
    @Test
    void testDeleteDocument() throws IOException {
        DeleteRequest request = new DeleteRequest("wyy_index","1");
        DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
        System.out.println(response.status());
    }

    /**
     * 批量新增文档
     */
    @Test
    void testBulkAdd() throws IOException {
        BulkRequest bulkRequest = new BulkRequest().timeout("10s");
        ArrayList<User> userList = new ArrayList<>();
        userList.add(new User("张三",23));
        userList.add(new User("李四",24));
        userList.add(new User("王五",25));
        userList.add(new User("赵六",26));
        for (int i = 0; i < userList.size(); i++) {
            bulkRequest.add(
                    new IndexRequest("wyy_index")
                            .id(""+(i+1))
                            .source(JSON.toJSONString(userList.get(i)),XContentType.JSON)
            );
        }
        BulkResponse bulk = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(bulk.hasFailures());
    }

    /**
     * 查询
     */
    @Test
    void testSearch() throws IOException {
        SearchRequest searchRequest = new SearchRequest("wyy_index");
        //构建搜索条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("name", "张三");
        sourceBuilder.query(matchQueryBuilder);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println(JSON.toJSONString(searchResponse.getHits()));
        System.out.println("==================");
        for (SearchHit documentFields : searchResponse.getHits()){
            System.out.println(documentFields.getSourceAsMap());
        }
    }
}
