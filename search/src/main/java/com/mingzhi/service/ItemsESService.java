package com.mingzhi.service;

import com.mingzhi.utils.PagedGridResult;

public interface ItemsESService {
    public PagedGridResult searchItems(String keywords,
                                       String sort,
                                       Integer page,
                                       Integer pageSize);
}
