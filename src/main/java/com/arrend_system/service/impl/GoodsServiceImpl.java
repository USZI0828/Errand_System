package com.arrend_system.service.impl;

import com.arrend_system.common.Result;
import com.arrend_system.exception.ShopException.ShopException;
import com.arrend_system.exception.ShopException.ShopNoGoodException;
import com.arrend_system.exception.ShopException.ShopNoOrderException;
import com.arrend_system.pojo.entity.Goods;
import com.arrend_system.mapper.GoodsMapper;
import com.arrend_system.service.GoodsService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
        Page<Goods> page = new Page<>(1, 10);

        // 构建查询条件，筛选出指定商店 ID 的商品
        LambdaQueryWrapper<Goods> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Goods::getShopId, shopId);

        // 执行分页查询
        IPage<Goods> goodsPage = goodsMapper.selectPage(page, queryWrapper);

        return Result.success(goodsPage);
    }

    @Override
    public Result<?> getSaleGoods(Integer shopId) {
        Page<Goods> page = new Page<>(1, 10);

        // 构建查询条件，筛选出指定商店 ID 的商品
        LambdaQueryWrapper<Goods> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Goods::getShopId, shopId).gt(Goods::getStock, 0);

        // 执行分页查询
        IPage<Goods> goodsPage = goodsMapper.selectPage(page, queryWrapper);

        return Result.success(goodsPage);
    }

    @Override
    public Result<?> getSoldOutGoods(Integer shopId) {
        Page<Goods> page = new Page<>(1, 10);

        // 构建查询条件，筛选出指定商店 ID 且库存为 0 的商品
        LambdaQueryWrapper<Goods> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Goods::getShopId, shopId).eq(Goods::getStock, 0);

        // 执行分页查询
        IPage<Goods> goodsPage = goodsMapper.selectPage(page, queryWrapper);

        return Result.success(goodsPage);
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




