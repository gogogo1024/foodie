package com.mingzhi.controller.center;

import com.mingzhi.controller.BaseController;
import com.mingzhi.pojo.vo.OrderStatusCountsVO;
import com.mingzhi.service.center.MyOrdersService;
import com.mingzhi.utils.MingzhiJSONResult;
import com.mingzhi.utils.PagedGridResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户中心-我的订单", description = "用户中心-我的订单")
@RestController()
@ResponseBody()
@RequestMapping("myorders")
public class MyOrdersController extends BaseController {
    @Autowired
    private MyOrdersService myOrdersService;

    @Autowired
    private Environment env;

    @Operation(summary = "查询个人订单列表", description = "查询个人订单列表", method = "GET")
    @GetMapping("/query")
    public MingzhiJSONResult query(
            @Parameter(name = "userId", required = true, description = "用户id")
            @RequestParam String userId,
            @Parameter(name = "orderStatus", description = "订单状态")
            @RequestParam(required = false) Integer orderStatus,
            @Parameter(name = "page", example = "1")
            @RequestParam Integer page,
            @Parameter(name = "pageSize", example = "10")
            @RequestParam Integer pageSize,
            HttpServletRequest request,
            HttpServletResponse response) {
        if (StringUtils.isBlank(userId)) {
            return MingzhiJSONResult.errorMap(null);

        }
        PagedGridResult pagedGridResult = myOrdersService.queryMyOrders(userId, orderStatus, page, pageSize);

        return MingzhiJSONResult.ok(pagedGridResult);

    }

    @Operation(summary = "商家发货", description = "商家发货", method = "GET")
    @GetMapping("/deliver")
    public MingzhiJSONResult deliver(
            @Parameter(name = "orderId", required = true, description = "订单ID")
            @RequestParam String orderId
    ) {
        if (StringUtils.isBlank(orderId)) {
            return MingzhiJSONResult.errorMsg("订单id不能为空");

        }
        myOrdersService.updateDeliverOrderStatus(orderId);

        return MingzhiJSONResult.ok();

    }

    @Operation(summary = "用户确认收货", description = "用户确认收货", method = "GET")
    @GetMapping("/confirmReceive")
    public MingzhiJSONResult confirmReceive(
            @Parameter(name = "orderId", required = true, description = "订单id")
            @RequestParam String orderId,
            @Parameter(name = "userId", required = true, description = "用户id")
            @RequestParam String userId
    ) {
        if (StringUtils.isBlank(orderId) || StringUtils.isBlank(userId)) {
            return MingzhiJSONResult.errorMsg("订单id或者用户id不能为空");

        }
        MingzhiJSONResult result = checkUserOrder(userId, orderId);
        if (result.getStatus() != HttpStatus.OK.value()) {
            return result;
        }
        boolean flag = myOrdersService.updateReceiveOrderStatus(orderId);
        if (!flag) {
            return MingzhiJSONResult.errorMsg("订单确认收货失败!");
        }
        return MingzhiJSONResult.ok();

    }

    @Operation(summary = "用户删除订单", description = "用户删除订单", method = "DELETE")
    @DeleteMapping("/delete")
    public MingzhiJSONResult deleteOrder(
            @Parameter(name = "orderId", required = true, description = "订单id")
            @RequestParam String orderId,
            @Parameter(name = "userId", required = true, description = "用户id")
            @RequestParam String userId
    ) {
        if (StringUtils.isBlank(orderId) || StringUtils.isBlank(userId)) {
            return MingzhiJSONResult.errorMsg("订单id或者用户id不能为空");

        }
        MingzhiJSONResult result = checkUserOrder(userId, orderId);
        if (result.getStatus() != HttpStatus.OK.value()) {
            return result;
        }
        boolean flag = myOrdersService.deleteOrder(userId, orderId);
        if (!flag) {
            return MingzhiJSONResult.errorMsg("订单删除失败!");
        }
        return MingzhiJSONResult.ok();

    }

    @Operation(summary = "获取用户不同状态订单数", description = "获取用户不同状态订单数", method = "POST")
    @PostMapping("/statusCounts")
    public MingzhiJSONResult getOrderStatusCounts(
            @Parameter(name = "userId", required = true, description = "用户id")
            @RequestParam(required = true) String userId
    ) {
        if (StringUtils.isBlank(userId)) {
            return MingzhiJSONResult.errorMsg("用户id不能为空");

        }
        OrderStatusCountsVO orderStatusCountsVO = myOrdersService.getMyOrderStatusCounts(userId);
        return MingzhiJSONResult.ok(orderStatusCountsVO);

    }

    @Operation(summary = "查询订单动向", description = "查询订单动向", method = "POST")
    @PostMapping("/trend")
    public MingzhiJSONResult trend(
            @Parameter(name = "userId", description = "用户id", required = true)
            @RequestParam String userId,
            @Parameter(name = "page", example = "1")
            @RequestParam Integer page,
            @Parameter(name = "pageSize", example = "10")
            @RequestParam Integer pageSize) {

        if (StringUtils.isBlank(userId)) {
            return MingzhiJSONResult.errorMsg(null);
        }

        PagedGridResult grid = myOrdersService.getOrdersTrend(userId,
                page,
                pageSize);

        return MingzhiJSONResult.ok(grid);
    }


}
