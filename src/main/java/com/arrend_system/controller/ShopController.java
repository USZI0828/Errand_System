package com.arrend_system.controller;

import com.arrend_system.pojo.entity.Goods;
import com.arrend_system.pojo.entity.Orders;
import com.arrend_system.pojo.entity.Shop;
import com.arrend_system.service.GoodsService;
import com.arrend_system.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/{shop_id}")
public class ShopController {

    @Autowired
    ShopService shopService;

    @Autowired
    GoodsService goodsService;

    // 修改商店信息
    @PutMapping("/updateShop")
    public String updateShop(@RequestBody Shop shop) {
        return shopService.updateShop(shop);
    }

    // 查询全部订单
    @GetMapping("/getAllOrders")
    public List<Orders> getAllOrders(@PathVariable Integer shop_id) {
        return shopService.getAllOrders(shop_id);
    }

    // 查询待接单订单
    @GetMapping("/getWaitingOrders")
    public List<Orders> getWaitingOrders(@PathVariable Integer shop_id) {
        return shopService.getWaitingOrders(shop_id);
    }

    // 查询进行中订单
    @GetMapping("/getGoingOrders")
    public List<Orders> getGoingOrders(@PathVariable Integer shop_id) {
        return shopService.getGoingOrders(shop_id);
    }

    // 查询已完成订单
    @GetMapping("/getFinishedOrders")
    public List<Orders> getFinishedOrders(@PathVariable Integer shop_id) {
        return shopService.getFinishedOrders(shop_id);
    }

    // 查询全部商品
    @GetMapping("/getAllGoods")
    public List<Goods> getAllGoods(@PathVariable Integer shop_id) {
        return goodsService.getAllGoods(shop_id);
    }

    // 查询库存还有的商品
    @GetMapping("/getSaleGoods")
    public List<Goods> getSaleGoods(@PathVariable Integer shop_id) {
        return goodsService.getSaleGoods(shop_id);
    }

    // 查询库存还有的商品
    @GetMapping("/getSoldOutGoods")
    public List<Goods> getSoldOutGoods(@PathVariable Integer shop_id) {
        return goodsService.getSoldOutGoods(shop_id);
    }


    // 添加商品
    @PostMapping
    public String addGoods(@RequestBody Goods good, String imagePath) {
        return shopService.addGoods(good, imagePath);
    }

    // 下架商品
    @DeleteMapping("/{item_id}")
    public String deleteGoods(@PathVariable Integer item_id) {
        return shopService.deleteGood(item_id);
    }

    // 更改商品信息
    @PutMapping("/{item_id}")
    public String updateGood(@PathVariable Integer item_id, @RequestBody Goods good, String imgPath) {
        return goodsService.updateGood(item_id, good, imgPath);
    }

}
