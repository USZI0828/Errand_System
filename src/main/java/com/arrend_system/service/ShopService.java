package com.arrend_system.service;

import com.arrend_system.common.Result;
import com.arrend_system.pojo.entity.Goods;
import com.arrend_system.pojo.entity.Shop;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 张明阳
* @description 针对表【shop】的数据库操作Service
* @createDate 2025-04-06 12:14:15
*/
public interface ShopService extends IService<Shop> {

    Result<?> updateShop(Shop shop);

    Result<?> getAllOrders(Integer shopId);

    Result<?> getWaitingOrders(Integer shopId);

    Result<?> getFinishedOrders(Integer shopId);

    Result<?> getGoingOrders(Integer shopId);

    Result<?> getShopInfo(Integer userId);
}
