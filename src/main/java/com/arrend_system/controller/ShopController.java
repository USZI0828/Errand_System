package com.arrend_system.controller;

import com.arrend_system.pojo.entity.Orders;
import com.arrend_system.pojo.entity.Shop;
import com.arrend_system.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/{shop_id}")
public class ShopController {

    @Autowired
    ShopService shopService;

    // 添加商品
    @PostMapping
    public String addGoods(@RequestBody Shop shop) {
        return shopService.addGoods(shop);
    }

    // 修改商品
    @PutMapping
    public String updateGoods(@RequestBody Shop shop) {
        return shopService.updateGoods(shop);
    }

    // 查看用户提交的订单
    @GetMapping
    public List<Orders> getOrders(@PathVariable Integer shop_id) {
        return shopService.getOrders(shop_id);
    }
}
