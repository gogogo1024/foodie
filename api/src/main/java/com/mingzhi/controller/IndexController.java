package com.mingzhi.controller;

import com.mingzhi.enums.YesOrNo;
import com.mingzhi.pojo.Carousel;
import com.mingzhi.pojo.Category;
import com.mingzhi.pojo.vo.CategoryVO;
import com.mingzhi.pojo.vo.NewItemsVO;
import com.mingzhi.service.CarouselService;
import com.mingzhi.service.CategoryService;
import com.mingzhi.utils.MingzhiJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Api(value = "首页", tags = {"首页展示的相关接口"})
@RestController()
@RequestMapping("index")
public class IndexController {
    final static Logger logger = LoggerFactory.getLogger(IndexController.class);
    @Autowired
    private CarouselService carouselService;
    @Autowired
    private CategoryService categoryService;

    @ApiOperation(value = "获取首页轮播图列表", notes = "获取首页轮播图列表", httpMethod = "GET")
    @GetMapping("/carousel")
    public MingzhiJSONResult carousel() {
        List<Carousel> list = carouselService.queryAll(YesOrNo.Yes.type);
        return MingzhiJSONResult.ok(list);
    }

    @ApiOperation(value = "获取商品分类", notes = "获取商品分类列表", httpMethod = "GET")
    @GetMapping("/category")
    public MingzhiJSONResult category(@RequestParam Integer type) {
        List<Category> list = categoryService.queryLevelCategory(1);
        return MingzhiJSONResult.ok(list);
    }

    @ApiOperation(value = "获取商品分类下的子分类", notes = "获取商品分类下的子分类", httpMethod = "GET")
    @GetMapping("/subCategory/{categoryId}")
    public MingzhiJSONResult subCategory(
            @ApiParam(name = "categoryId", value = "分类id", required = true)
            @PathVariable Integer categoryId) {
        if (categoryId == null) {
            return MingzhiJSONResult.errorMsg("分类不存在");

        }
        List<CategoryVO> list = categoryService.getSubCategoryList(categoryId);
        return MingzhiJSONResult.ok(list);
    }

    @ApiOperation(value = "获取对应分类下的最新n条商品信息", notes = "获取对应分类下的最新n条商品信息", httpMethod = "GET")
    @GetMapping("/moreItems/{categoryId}")
    public MingzhiJSONResult getMoreItemsList(
            @ApiParam(name = "categoryId", value = "分类id", required = true)
            @PathVariable Integer categoryId) {
        if (categoryId == null) {
            return MingzhiJSONResult.errorMsg("分类不存在");

        }
        // 默认6个
        List<NewItemsVO> list = categoryService.getMoreItemsList(categoryId, 6);
        return MingzhiJSONResult.ok(list);
    }

    @GetMapping("setSession")
    public Object setSession(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute("userInfo", "user");
        session.setMaxInactiveInterval(3600);
        session.getAttribute("userInfo");
//        session.removeAttribute("userInfo");
        return "ok";
    }
}
