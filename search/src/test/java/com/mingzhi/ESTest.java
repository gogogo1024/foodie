package com.mingzhi;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOptionsBuilders;
import co.elastic.clients.elasticsearch._types.SortOrder;
import com.mingzhi.pojo.Stu;
import com.mingzhi.utils.JsonUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightParameters;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ESTest {
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    // 使用ElasticsearchClient也可以但是不建议
//    @Autowired
//    @Qualifier("elasticsearchClient")
//    private ElasticsearchClient client;

    @Test
    public void createIndexStu() throws IOException {
        Stu stu = new Stu();
        stu.setAge(12L);
        stu.setName("go1024");
        stu.setStuId(11L);
        stu.setMoney(99999.9999f);
        stu.setSign("a superman~");
        IndexQuery indexQuery = new IndexQueryBuilder().withObject(stu).build();
        elasticsearchTemplate.index(indexQuery, IndexCoordinates.of("stu"));

//        client.indices().create(c -> c.index("stu"));

    }

    @Test
    public void deleteIndexStu() {
        elasticsearchTemplate.indexOps(IndexCoordinates.of("stu")).delete();

    }

    @Test
    public void updateStu() throws IOException {
        Stu updatStu = new Stu();
        updatStu.setStuId(101L);
        updatStu.setAge(22L);
        updatStu.setMoney(99.9999f);
        updatStu.setSign("superman~");
        updatStu.setDescription("update stu");


//        UpdateResponse<Stu> updateResponse = client.update(u -> u
//                        .index("stu")
//                        .id("100")
//                        .doc(updateStu)
//                , Stu.class);

        String jsonString = JsonUtils.objectToJson(updatStu);
        Document document = Document.parse(jsonString);
        UpdateQuery updateQuery = UpdateQuery
                .builder(String.valueOf(updatStu.getStuId()))
                .withDocument(document)
                .build();
        UpdateResponse updateResponse = elasticsearchTemplate
                .update(updateQuery, elasticsearchTemplate.getIndexCoordinatesFor(Stu.class));

        System.out.println(updateResponse.getResult());
    }

    @Test
    public void getStuDoc() {
        Stu stu = elasticsearchTemplate.get("100",
                Stu.class,
                IndexCoordinates.of(Stu.getIndexName()));
        if (stu != null) {
            System.out.println(stu.toString());
        }
    }

    @Test
    public void deleteStuDoc() {
        elasticsearchTemplate.delete("100", Stu.class);
    }

    @Test
    public void searchStuDoc() {
        Pageable pageable = PageRequest.of(0, 10);
        Query query = NativeQuery.builder()
                .withQuery(q -> q.match(m -> m.field("sign").query("superman")))
                .withPageable(pageable)
                .build();
        SearchHits<Stu> searchHits = elasticsearchTemplate.search(query, Stu.class, elasticsearchTemplate.getIndexCoordinatesFor(Stu.class));
        List<SearchHit<Stu>> list = searchHits.getSearchHits();
        System.out.println(list.get(0).getContent());

    }

    @Test
    public void searchHighlightStuDoc() {
        // 分页
        Pageable pageable = PageRequest.of(0, 10);
        // 排序
        List<SortOptions> sortOptionsList = new ArrayList<>();
        SortOptions sortOptions1 = SortOptionsBuilders.field(f -> f.field("money").order(SortOrder.Asc));
        SortOptions sortOptions2 = SortOptionsBuilders.field(f -> f.field("age").order(SortOrder.Asc));

        sortOptionsList.add(sortOptions1);
        sortOptionsList.add(sortOptions2);
        // 高亮样式
        String preTag = "<font color ='red'>";
        String postTag = "</font>";
        List<HighlightField> highlightFieldList = new ArrayList<>();
        HighlightParameters.HighlightParametersBuilder highlightParametersBuilder = new HighlightParameters.HighlightParametersBuilder();
        highlightParametersBuilder.withPreTags(preTag);
        highlightParametersBuilder.withPostTags(postTag);
        HighlightParameters highlightParameters = highlightParametersBuilder.build();

        // 高亮字段
        highlightFieldList.add(new HighlightField("sign"));

        Highlight highlight = new Highlight(highlightParameters, highlightFieldList);

        Query query = NativeQuery.builder()
                .withQuery(q -> q.match(m -> m.field("sign").query("superman")))
                .withHighlightQuery(new HighlightQuery(highlight, Stu.class))
                .withPageable(pageable)
                .withSort(sortOptionsList)
                .build();
        SearchHits<Stu> searchHits = elasticsearchTemplate.search(query, Stu.class, elasticsearchTemplate.getIndexCoordinatesFor(Stu.class));
//        List<SearchHit<Stu>> list = searchHits.getSearchHits();
        List<Stu> stuList = new ArrayList<>();
        for (SearchHit<Stu> hits : searchHits) {
            Map<String, List<String>> highLightFields = hits.getHighlightFields();
            // 将高亮的内容填充到content中
            hits.getContent().setSign(highLightFields.get("sign") == null ? hits.getContent().getSign() : highLightFields.get("sign").get(0));
            // 放到实体类中
            System.out.println(hits.getContent().toString());
            stuList.add(hits.getContent());
        }
        System.out.println(stuList);

    }
}
