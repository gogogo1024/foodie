package com.mingzhi.mapper;

import com.mingzhi.my.mapper.MyMapper;
import com.mingzhi.pojo.OrderItems;
import tk.mybatis.mapper.additional.insert.InsertListMapper;

public interface OrderItemsMapper extends MyMapper<OrderItems>, InsertListMapper<OrderItems> {
}
