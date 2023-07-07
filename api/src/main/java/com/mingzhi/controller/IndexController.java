package com.mingzhi.controller;

import com.mingzhi.enums.YesOrNo;
import com.mingzhi.pojo.Carousel;
import com.mingzhi.pojo.Category;
import com.mingzhi.pojo.vo.CategoryVO;
import com.mingzhi.pojo.vo.NewItemsVO;
import com.mingzhi.service.CarouselService;
import com.mingzhi.service.CategoryService;
import com.mingzhi.utils.MingzhiJSONResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "首页", description = "首页展示的相关接口")
@RestController()
@ResponseBody()
@RequestMapping("index")
public class IndexController {
    final static Logger logger = LoggerFactory.getLogger(IndexController.class);
    @Autowired
    private CarouselService carouselService;
    @Autowired
    private CategoryService categoryService;

    @Operation(summary = "获取首页轮播图列表", description = "获取首页轮播图列表", method = "GET")
    @GetMapping("/carousel")
    public MingzhiJSONResult carousel() {
        List<Carousel> list = carouselService.queryAll(YesOrNo.Yes.type);
        return MingzhiJSONResult.ok(list);
    }

    @Operation(summary = "获取商品分类", description = "获取商品分类列表", method = "GET")
    @GetMapping("/category")
    public MingzhiJSONResult category(@RequestParam Integer type) {
        List<Category> list = categoryService.queryLevelCategory(1);
        return MingzhiJSONResult.ok(list);
    }

    @Operation(summary = "获取商品分类下的子分类", description = "获取商品分类下的子分类", method = "GET")
    @GetMapping("/subCategory/{categoryId}")
    public MingzhiJSONResult subCategory(
            @Parameter(name = "categoryId", required = true)
            @PathVariable Integer categoryId) {
        if (categoryId == null) {
            return MingzhiJSONResult.errorMsg("分类不存在");

        }
        List<CategoryVO> list = categoryService.getSubCategoryList(categoryId);
        return MingzhiJSONResult.ok(list);
    }

    @Operation(summary = "获取对应分类下的最新n条商品信息", description = "获取对应分类下的最新n条商品信息", method = "GET")
    @GetMapping("/moreItems/{categoryId}")
    public MingzhiJSONResult getMoreItemsList(
            @Parameter(name = "categoryId", required = true)
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
