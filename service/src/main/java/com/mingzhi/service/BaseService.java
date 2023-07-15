package com.mingzhi.service;

import com.github.pagehelper.PageInfo;
import com.mingzhi.utils.PagedGridResult;

import java.util.List;

public class BaseService {
    public PagedGridResult setterPagedGrid(List<?> list, Integer page) {
        PageInfo<?> pageList = new PageInfo<>(list);
        PagedGridResult pagedGridResult = new PagedGridResult();
        pagedGridResult.setPage(page);
        pagedGridResult.setRows(list);
        pagedGridResult.setTotal(pageList.getPages());
        pagedGridResult.setRecords(pageList.getTotal());
        return pagedGridResult;

    }
}
