package com.mingzhi.mapper;

import com.mingzhi.pojo.OrderStatus;
import com.mingzhi.pojo.vo.MyOrdersVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface OrdersMapperCustom {
    public List<MyOrdersVO> queryMyOrders(@Param("paramsMap") Map<String, Object> map);

    public List<OrderStatus> getMyOrderTrend(@Param("paramsMap") Map<String, Object> map);


}