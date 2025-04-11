package com.arrend_system.service;

import com.arrend_system.pojo.entity.Goods;
import com.arrend_system.pojo.entity.Orders;
import com.arrend_system.pojo.entity.Shop;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 张明阳
* @description 针对表【shop】的数据库操作Service
* @createDate 2025-04-06 12:14:15
*/
public interface ShopService extends IService<Shop> {
    String addGoods(Goods good, String imagePath);

    String updateShop(Shop shop);

    List<Orders> getAllOrders(Integer shopId);

    List<Orders> getWaitingOrders(Integer shopId);

    List<Orders> getFinishedOrders(Integer shopId);

    List<Orders> getGoingOrders(Integer shopId);

    String deleteGood(Integer itemId);
}
