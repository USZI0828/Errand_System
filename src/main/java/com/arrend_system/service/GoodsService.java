package com.arrend_system.service;

import com.arrend_system.pojo.entity.Goods;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 张明阳
* @description 针对表【goods】的数据库操作Service
* @createDate 2025-04-06 12:14:15
*/
public interface GoodsService extends IService<Goods> {

    List<Goods> getAllGoods(Integer shopId);

    List<Goods> getSaleGoods(Integer shopId);

    List<Goods> getSoldOutGoods(Integer shopId);

    String updateGood(Integer itemId, Goods good, String imgPath);
}
