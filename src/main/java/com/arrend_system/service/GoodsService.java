package com.arrend_system.service;

import com.arrend_system.common.Result;
import com.arrend_system.pojo.entity.Goods;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 张明阳
* @description 针对表【goods】的数据库操作Service
* @createDate 2025-04-06 12:14:15
*/
public interface GoodsService extends IService<Goods> {

    Result<?> getAllGoods(Integer shopId);

    Result<?> getSaleGoods(Integer shopId);

    Result<?> getSoldOutGoods(Integer shopId);

    Result<?> addGoods(Goods good, String imagePath);

    Result<?> deleteGood(Integer itemId);

    Result<?> updateGood(Integer itemId, Goods good, String imgPath);
}
