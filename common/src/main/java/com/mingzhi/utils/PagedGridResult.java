package com.mingzhi.utils;

import com.github.pagehelper.PageInfo;

import java.util.List;


/**
 * 用来返回分页Grid的数据格式
 */
public class PagedGridResult {

    private int page;            // 当前页数
    private int total;            // 总页数
    private long records;        // 总记录数
    private List<?> rows;        // 每行显示的内容

    public static PagedGridResult setterPagedGrid(List<?> list, Integer page) {
        PageInfo<?> pageList = new PageInfo<>(list);
        PagedGridResult pagedGridResult = new PagedGridResult();
        pagedGridResult.setPage(page);
        pagedGridResult.setRows(list);
        pagedGridResult.setTotal(pageList.getPages());
        pagedGridResult.setRecords(pageList.getTotal());
        return pagedGridResult;

    }

    // TODO 考虑封装elastic search 中的PageImpl
    public static PagedGridResult setterESPagedGrid(List<?> list, Integer page) {
        PagedGridResult pagedGridResult = new PagedGridResult();
        pagedGridResult.setPage(page);
        pagedGridResult.setRows(list);
        pagedGridResult.setTotal(pagedGridResult.total);
        pagedGridResult.setRecords(pagedGridResult.records);
        return pagedGridResult;

    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public long getRecords() {
        return records;
    }

    public void setRecords(long records) {
        this.records = records;
    }

    public List<?> getRows() {
        return rows;
    }

    public void setRows(List<?> rows) {
        this.rows = rows;
    }
}
