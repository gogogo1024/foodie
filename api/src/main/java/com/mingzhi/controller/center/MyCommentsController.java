package com.mingzhi.controller.center;

import com.mingzhi.controller.BaseController;
import com.mingzhi.enums.YesOrNo;
import com.mingzhi.pojo.ItemsComments;
import com.mingzhi.pojo.OrderItems;
import com.mingzhi.pojo.Orders;
import com.mingzhi.service.center.MyCommentsService;
import com.mingzhi.utils.MingzhiJSONResult;
import com.mingzhi.utils.PagedGridResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@Tag(name = "用户中心-我的评价", description = "用户中心-我的评价")
@RestController()
@ResponseBody()
@RequestMapping("mycomments")
public class MyCommentsController extends BaseController {
    @Autowired
    private MyCommentsService myCommentsService;

    @Operation(summary = "查询个人对商品评价", description = "查询个人对商品评价", method = "GET")
    @GetMapping("/pending")
    public MingzhiJSONResult userComment(
            @Parameter(name = "userId", required = true, description = "用户id")
            @RequestParam String userId,
            @Parameter(name = "orderId", description = "订单id")
            @RequestParam String orderId,
            HttpServletRequest request,
            HttpServletResponse response) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(orderId)) {
            return MingzhiJSONResult.errorMsg(null);

        }
        MingzhiJSONResult result = checkUserOrder(userId, orderId);
        if (result.getStatus() != HttpStatus.OK.value()) {
            return result;
        }
        Orders myComments = (Orders) result.getData();
        if (myComments != null && Objects.equals(myComments.getIsComment(), YesOrNo.YES.type)) {
            return MingzhiJSONResult.errorMsg("已评价该订单");
        }
        List<OrderItems> list = myCommentsService.queryPendingComment(orderId);
        return MingzhiJSONResult.ok(list);

    }

    @Operation(summary = "保存商品评论列表", description = "保存商品评论列表", method = "POST")
    @PostMapping("/saveList")
    public MingzhiJSONResult saveCommentList(
            @Parameter(name = "userId", required = true, description = "用户id")
            @RequestParam String userId,
            @Parameter(name = "orderId", description = "订单id")
            @RequestParam String orderId,
            @RequestBody List<ItemsComments> commentList,
            HttpServletRequest request,
            HttpServletResponse response) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(orderId)) {
            return MingzhiJSONResult.errorMsg(null);

        }
        MingzhiJSONResult result = checkUserOrder(userId, orderId);
        if (result.getStatus() != HttpStatus.OK.value()) {
            return result;
        }
        if (CollectionUtils.isEmpty(commentList)) {
            return MingzhiJSONResult.errorMsg("不能空评论");
        }

        myCommentsService.saveComment(orderId, userId, commentList);
        return MingzhiJSONResult.ok();

    }

    @Operation(summary = "查询我的评价", description = "查询我的评价", method = "POST")
    @PostMapping("/query")
    public MingzhiJSONResult query(
            @Parameter(name = "userId", required = true, description = "用户id")
            @RequestParam String userId,
            @Parameter(name = "page", example = "1")
            @RequestParam Integer page,
            @Parameter(name = "pageSize", example = "10")
            @RequestParam Integer pageSize,
            HttpServletRequest request,
            HttpServletResponse response) {
        if (StringUtils.isBlank(userId)) {
            return MingzhiJSONResult.errorMap(null);

        }
        PagedGridResult pagedGridResult = myCommentsService.queryMyComments(userId, page, pageSize);

        return MingzhiJSONResult.ok(pagedGridResult);

    }

}
