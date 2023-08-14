package com.mingzhi.service.impl;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOptionsBuilders;
import co.elastic.clients.elasticsearch._types.SortOrder;
import com.mingzhi.es.pojo.Items;
import com.mingzhi.service.ItemsESService;
import com.mingzhi.utils.PagedGridResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightParameters;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ItemsESServiceImpl implements ItemsESService {
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public PagedGridResult searchItems(String keywords, String sort, Integer page, Integer pageSize) {

        Pageable pageable = PageRequest.of(page, pageSize);

        // 排序
        List<SortOptions> sortOptionsList = new ArrayList<>();
        SortOptions sortOptions = null;

        if (sort.equals("c")) {
            sortOptions = SortOptionsBuilders.field(f -> f.field("sellCounts").order(SortOrder.Desc));

        } else if (sort.equals("p")) {
            sortOptions = SortOptionsBuilders.field(f -> f.field("price").order(SortOrder.Asc));
        } else {
            sortOptions = SortOptionsBuilders.field(f -> f.field("itemName.keyword").order(SortOrder.Asc));
        }
        sortOptionsList.add(sortOptions);
        // 高亮样式
        String preTag = "<font color ='red'>";
        String postTag = "</font>";
        List<HighlightField> highlightFieldList = new ArrayList<>();
        HighlightParameters.HighlightParametersBuilder highlightParametersBuilder = new HighlightParameters.HighlightParametersBuilder();
        highlightParametersBuilder.withPreTags(preTag);
        highlightParametersBuilder.withPostTags(postTag);
        HighlightParameters highlightParameters = highlightParametersBuilder.build();

        // 高亮字段
        String highlightFiled = "itemName";
        highlightFieldList.add(new HighlightField(highlightFiled));

        Highlight highlight = new Highlight(highlightParameters, highlightFieldList);

        Query query = NativeQuery.builder()
                .withQuery(q -> q.match(m -> m.field(highlightFiled).query(keywords)))
                .withHighlightQuery(new HighlightQuery(highlight, Items.class))
                .withPageable(pageable)
                .withSort(sortOptionsList)
                .build();
        SearchHits<Items> searchHits = elasticsearchTemplate.search(query, Items.class, elasticsearchTemplate.getIndexCoordinatesFor(Items.class));
        SearchPage<Items> pageList = SearchHitSupport.searchPageFor(searchHits, query.getPageable());
        List<Items> itemsList = new ArrayList<>();
        for (SearchHit<Items> hits : pageList) {
            Map<String, List<String>> highLightFields = hits.getHighlightFields();
            // 将高亮的内容填充到content中
            hits.getContent().setItemName(highLightFields.get(highlightFiled) == null ? hits.getContent().getItemName() : highLightFields.get(highlightFiled).get(0));
            // 放到实体类中
            System.out.println(hits.getContent());
            itemsList.add(hits.getContent());
        }

        // 转换PageImpl来填充参数
        PageImpl<Items> pageInfo = new PageImpl<>(itemsList, query.getPageable(), searchHits.getTotalHits());
        PagedGridResult pagedGridResult = new PagedGridResult();
        pagedGridResult.setRows(pageInfo.getContent());
        pagedGridResult.setPage(page + 1);
        pagedGridResult.setTotal(pageInfo.getTotalPages());
        pagedGridResult.setRecords(pageInfo.getTotalElements());
        return pagedGridResult;
    }
}
