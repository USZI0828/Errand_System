package com.arrend_system.service.impl;

import com.arrend_system.pojo.entity.Goods;
import com.arrend_system.mapper.GoodsMapper;
import com.arrend_system.service.GoodsService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
* @author 张明阳
* @description 针对表【goods】的数据库操作Service实现
* @createDate 2025-04-06 12:14:15
*/
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods>
    implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Override
    public List<Goods> getAllGoods(Integer shopId) {
        LambdaQueryWrapper<Goods> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Goods::getShopId, shopId);
        return goodsMapper.selectList(queryWrapper);
    }

    @Override
    public List<Goods> getSaleGoods(Integer shopId) {
        LambdaQueryWrapper<Goods> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Goods::getShopId, shopId);
        List<Goods> goods = goodsMapper.selectList(queryWrapper);
        return goods.stream()
                .filter(order -> order.getStock() != 0)
                .collect(Collectors.toList());
    }

    @Override
    public List<Goods> getSoldOutGoods(Integer shopId) {
        LambdaQueryWrapper<Goods> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Goods::getShopId, shopId);
        List<Goods> goods = goodsMapper.selectList(queryWrapper);
        return goods.stream()
                .filter(order -> order.getStock() == 0)
                .collect(Collectors.toList());
    }

    @Override
    public String updateGood(Integer itemId, Goods good, String imgPath) {
        good.setImg(imgPath);
        goodsMapper.updateById(good);
        return "编辑成功";
    }


}




