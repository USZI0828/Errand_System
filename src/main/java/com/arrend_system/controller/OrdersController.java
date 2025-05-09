package com.arrend_system.controller;

import com.arrend_system.common.Result;
import com.arrend_system.mapper.CommentMapper;
import com.arrend_system.pojo.form.add.AddOrderForm;
import com.arrend_system.pojo.query.OrdersQuery;
import com.arrend_system.service.OrdersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.simpleframework.xml.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    //用户发布跑腿任务
    @Operation(summary = "用户发布跑腿任务")
    @PostMapping("/publish")
    public Result<?>publish(@RequestBody AddOrderForm addOrderForm) {
        return ordersService.publish(addOrderForm);
    }

    //用户查看跑腿任务
    @Operation(summary = "用户查看跑腿任务")
    @PostMapping("/getList")
    public Result<?>getList(@RequestBody OrdersQuery ordersQuery) {
        return ordersService.getList(ordersQuery);
    }

    //用户取消跑腿任务
    @Operation(summary = "用户取消跑腿任务")
    @PostMapping("/cancelArrend")
    public Result<?>cancelArrend(@RequestParam("ordersId")Integer ordersId){
        return ordersService.cancelArrend(ordersId);
    }

    //用户获取商店列表
    @Operation(summary = "用户获取商店列表")
    @GetMapping("/getShopList")
    public Result<?>getShopList() {
        return ordersService.getShopList();
    }

    //用户查看商品列表
    @Operation(summary = "用户获取商品列表")
    @GetMapping("/getGoodsList")
    public Result<?>getGoodsList(@Parameter Integer shopId) {
        return ordersService.getGoodsByShop(shopId);
    }

    //获取跑腿类型
    @Operation(summary = "用户获取跑腿类型")
    @GetMapping("/getArrendType")
    public Result<?>getArrendType(){
        return ordersService.getArrendType();
    }

}
