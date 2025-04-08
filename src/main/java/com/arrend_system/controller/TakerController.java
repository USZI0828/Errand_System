package com.arrend_system.controller;

import com.arrend_system.pojo.entity.Orders;
import com.arrend_system.service.TakerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/{order_taker}")
public class TakerController {

    @Autowired
    TakerService takerService;

    // 获取没有被接单的订单
    @GetMapping("/getUnChooseOrders")
    public List<Orders> getUnChooseOrders() {
        return takerService.getUnChooseOrders();
    }

    // 接取订单
    @PutMapping("/chooseOrders/{order_id}")
    public String chooseOrders(@PathVariable Integer order_id,
                               @PathVariable Integer order_taker,
                               @RequestParam("status") Integer status) {
        return takerService.chooseOrders(order_id, order_taker, status);
    }

    // 查询自己接取的订单
    @GetMapping("/getChoseOrders")
    public String getChoseOrders(@PathVariable Integer order_taker) {
        return takerService.getChoseOrders(order_taker);
    }

    // 计算收入明细
    @GetMapping("/countMoney")
    public BigDecimal countMoney(@PathVariable Integer order_taker) {
        return takerService.countMoney(order_taker);
    }

}
