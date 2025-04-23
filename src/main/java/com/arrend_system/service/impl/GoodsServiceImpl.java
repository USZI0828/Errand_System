package com.arrend_system.service.impl;

import com.arrend_system.common.Result;
import com.arrend_system.exception.ShopException.ShopException;
import com.arrend_system.exception.ShopException.ShopNoGoodException;
import com.arrend_system.exception.ShopException.ShopNoOrderException;
import com.arrend_system.pojo.entity.Goods;
import com.arrend_system.mapper.GoodsMapper;
import com.arrend_system.service.GoodsService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
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
    public Result<?> getAllGoods(Integer shopId) {
        LambdaQueryWrapper<Goods> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Goods::getShopId, shopId);
        List<Goods> goodsList = goodsMapper.selectList(queryWrapper);
        return Result.success(goodsList);
    }

    @Override
    public Result<?> getSaleGoods(Integer shopId) {
        LambdaQueryWrapper<Goods> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Goods::getShopId, shopId);
        List<Goods> goods = goodsMapper.selectList(queryWrapper);
        // 筛选库存不为0的商品
        List<Goods> goodsList = goods.stream()
                .filter(order -> order.getStock() != 0)
                .collect(Collectors.toList());
        return Result.success(goodsList);
    }

    @Override
    public Result<?> getSoldOutGoods(Integer shopId) {
        LambdaQueryWrapper<Goods> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Goods::getShopId, shopId);
        List<Goods> goods = goodsMapper.selectList(queryWrapper);
        List<Goods> goodsList = goods.stream()
                .filter(order -> order.getStock() == 0)
                .collect(Collectors.toList());
        return Result.success(goodsList);
    }

    @Override
    public Result<?> addGoods(Goods good, String imagePath) {
        try {
            good.setImg(imagePath);
            goodsMapper.insert(good);
            return Result.success("添加商品成功！");
        } catch (Exception e) {
            throw new ShopException("添加商品失败！");
        }
    }

    @Override
    public Result<?> deleteGood(Integer itemId) {
        // 没有对应的商品
        if (goodsMapper.selectById(itemId) == null) {
            throw new ShopNoGoodException("该商品不存在！");
        }
        goodsMapper.deleteById(itemId);
        return Result.success("商品删除成功");
    }

    @Override
    public Result<?> updateGood(Integer itemId, Goods good, String imgPath) {
        // 没有对应的商品
        if (goodsMapper.selectById(itemId) == null) {
            throw new ShopNoGoodException("该商品不存在！");
        }
        good.setImg(imgPath);
        goodsMapper.updateById(good);
        return Result.success("商品更新成功！");
    }




}




