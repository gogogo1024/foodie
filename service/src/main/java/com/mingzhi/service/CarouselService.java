package com.mingzhi.service;

import com.mingzhi.pojo.Carousel;

import java.util.List;

public interface CarouselService {
    /**
     * 查询轮播图列表
     *
     * @param isShow 是否展示
     * @return 轮播图列表
     */
    public List<Carousel> queryAll(int isShow);

}
