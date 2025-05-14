package com.arrend_system.service.impl;

import com.arrend_system.common.Result;
import com.arrend_system.exception.ShopException.ShopException;
import com.arrend_system.exception.ShopException.ShopNoOrderException;
import com.arrend_system.mapper.GoodsMapper;
import com.arrend_system.mapper.OrdersMapper;
import com.arrend_system.pojo.entity.Goods;
import com.arrend_system.pojo.entity.Orders;
import com.arrend_system.pojo.entity.Shop;
import com.arrend_system.mapper.ShopMapper;
import com.arrend_system.service.ShopService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 张明阳
 * @description 针对表【shop】的数据库操作Service实现
 * @createDate 2025-04-06 12:14:15
 */
@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop>
        implements ShopService {

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private GoodsMapper goodsMapper;



    @Override
    public Result<?> updateShop(Shop shop) {
        try {
            // 尝试更新商铺信息
            shopMapper.updateById(shop);
            return Result.success("商铺信息更新成功！");
        } catch (Exception e) {
            throw new ShopException(e.getMessage());
        }
    }

    @Override
    public Result<?> getAllOrders(Integer shopId) {
        try {
            // 参数校验
            if (shopId == null) {
                return Result.fail(400, "店铺ID不能为空", "");
            }

            // 查询该商店的商品
            LambdaQueryWrapper<Goods> lqw = new LambdaQueryWrapper<>();
            lqw.eq(Goods::getShopId, shopId);
            List<Goods> goods = goodsMapper.selectList(lqw);

            // 处理商品列表为空的情况
            if (goods == null || goods.isEmpty()) {
                return Result.success(new Page<>()); // 返回空分页
            }

            // 获取所有商品的 itemName
            List<String> itemNames = goods.stream()
                    .map(Goods::getItemName)
                    .collect(Collectors.toList());

            Page<Orders> page = new Page<>(1, 10);

            // 执行分页查询（修复方法调用）
            IPage<Orders> allOrdersPage = ordersMapper.selectPage(page, new QueryWrapper<Orders>()
                    .in("item_name", itemNames)
                    .ne("status", 0)); // 直接在SQL中过滤状态不为0的订单

            return Result.success(allOrdersPage);

        } catch (Exception e) {
            // 记录异常日志
            log.error("获取订单列表失败", e);
            return Result.fail(500, "系统内部错误，请稍后重试", e.getMessage());
        }
    }

    @Override
    public Result<?> getWaitingOrders(Integer shopId) {
        try {
            // 参数校验
            if (shopId == null) {
                return Result.fail(400, "店铺ID不能为空", "");
            }

            // 查询该商店的商品
            LambdaQueryWrapper<Goods> lqw = new LambdaQueryWrapper<>();
            lqw.eq(Goods::getShopId, shopId);
            List<Goods> goods = goodsMapper.selectList(lqw);

            // 处理商品列表为空的情况
            if (goods == null || goods.isEmpty()) {
                return Result.success(new Page<>()); // 返回空分页
            }

            // 获取所有商品的 itemName
            List<String> itemNames = goods.stream()
                    .map(Goods::getItemName)
                    .collect(Collectors.toList());

            Page<Orders> page = new Page<>(1, 10);

            // 执行分页查询（修复方法调用）
            IPage<Orders> allOrdersPage = ordersMapper.selectPage(page, new QueryWrapper<Orders>()
                    .in("item_name", itemNames)
                    .eq("status", 1)); // 只查询状态为1的订单

            return Result.success(allOrdersPage);

        } catch (Exception e) {
            // 记录异常日志
            log.error("获取订单列表失败", e);
            return Result.fail(500, "系统内部错误，请稍后重试", e.getMessage());
        }
    }

    @Override
    public Result<?> getGoingOrders(Integer shopId) {
        try {
            // 参数校验
            if (shopId == null) {
                return Result.fail(400, "店铺ID不能为空", "");
            }

            // 查询该商店的商品
            LambdaQueryWrapper<Goods> lqw = new LambdaQueryWrapper<>();
            lqw.eq(Goods::getShopId, shopId);
            List<Goods> goods = goodsMapper.selectList(lqw);

            // 处理商品列表为空的情况
            if (goods == null || goods.isEmpty()) {
                return Result.success(new Page<>()); // 返回空分页
            }

            // 获取所有商品的 itemName
            List<String> itemNames = goods.stream()
                    .map(Goods::getItemName)
                    .collect(Collectors.toList());

            Page<Orders> page = new Page<>(1, 10);

            // 执行分页查询（修复方法调用）
            IPage<Orders> allOrdersPage = ordersMapper.selectPage(page, new QueryWrapper<Orders>()
                    .in("item_name", itemNames)
                    .eq("status", 2)); // 只查询状态为2的订单

            return Result.success(allOrdersPage);

        } catch (Exception e) {
            // 记录异常日志
            log.error("获取订单列表失败", e);
            return Result.fail(500, "系统内部错误，请稍后重试", e.getMessage());
        }
    }



    @Override
    public Result<?> getFinishedOrders(Integer shopId) {
        try {
            // 参数校验
            if (shopId == null) {
                return Result.fail(400, "店铺ID不能为空", "");
            }

            // 查询该商店的商品
            LambdaQueryWrapper<Goods> lqw = new LambdaQueryWrapper<>();
            lqw.eq(Goods::getShopId, shopId);
            List<Goods> goods = goodsMapper.selectList(lqw);

            // 处理商品列表为空的情况
            if (goods == null || goods.isEmpty()) {
                return Result.success(new Page<>()); // 返回空分页
            }

            // 获取所有商品的 itemName
            List<String> itemNames = goods.stream()
                    .map(Goods::getItemName)
                    .collect(Collectors.toList());

            Page<Orders> page = new Page<>(1, 10);

            // 执行分页查询（修复方法调用）
            IPage<Orders> allOrdersPage = ordersMapper.selectPage(page, new QueryWrapper<Orders>()
                    .in("item_name", itemNames)
                    .eq("status", 3)); // 只查询状态为3的订单

            return Result.success(allOrdersPage);

        } catch (Exception e) {
            // 记录异常日志
            log.error("获取订单列表失败", e);
            return Result.fail(500, "系统内部错误，请稍后重试", e.getMessage());
        }
    }

}




