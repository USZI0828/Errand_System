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
        // 先查询该商店的商品
        LambdaQueryWrapper<Goods> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Goods::getShopId, shopId);
        List<Goods> goods = goodsMapper.selectList(lqw);

        // 获取所有商品的 itemName
        List<String> itemNames = goods.stream()
                .map(Goods::getItemName)
                .collect(Collectors.toList());

        Page<Orders> page = new Page<>(1, 10);

        // 批量查询订单并分页
        IPage<Orders> allOrdersPage = selectOrdersByItemIdsWithPage(page, itemNames, ordersMapper);

        // 筛选出状态不为 0 的订单
        List<Orders> orders = allOrdersPage.getRecords().stream()
                .filter(order -> order.getStatus() != 0)
                .collect(Collectors.toList());

        // 将筛选后的订单重新封装到分页对象中
        IPage<Orders> filteredOrdersPage = new Page<>(allOrdersPage.getCurrent(), allOrdersPage.getSize(), allOrdersPage.getTotal());
        filteredOrdersPage.setRecords(orders);

        return Result.success(filteredOrdersPage);
    }

    @Override
    public Result<?> getWaitingOrders(Integer shopId) {
        // 先查询该商店的商品
        LambdaQueryWrapper<Goods> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Goods::getShopId, shopId);
        List<Goods> goods = goodsMapper.selectList(lqw);

        // 获取所有商品的 itemName
        List<String> itemNames = goods.stream()
                .map(Goods::getItemName)
                .collect(Collectors.toList());

        Page<Orders> page = new Page<>(1, 10);

        // 批量查询订单并分页
        IPage<Orders> allOrdersPage = selectOrdersByItemIdsWithPage(page, itemNames, ordersMapper);

        // 筛选出待接单的订单
        List<Orders> orders = allOrdersPage.getRecords().stream()
                .filter(order -> order.getStatus() == 1)
                .collect(Collectors.toList());

        // 将筛选后的订单重新封装到分页对象中
        IPage<Orders> filteredOrdersPage = new Page<>(allOrdersPage.getCurrent(), allOrdersPage.getSize(), allOrdersPage.getTotal());
        filteredOrdersPage.setRecords(orders);

        return Result.success(filteredOrdersPage);
    }

    @Override
    public Result<?> getGoingOrders(Integer shopId) {
        // 先查询该商店的商品
        LambdaQueryWrapper<Goods> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Goods::getShopId, shopId);
        List<Goods> goods = goodsMapper.selectList(lqw);

        // 获取所有商品的 itemName
        List<String> itemNames = goods.stream()
                .map(Goods::getItemName)
                .collect(Collectors.toList());

        Page<Orders> page = new Page<>(1, 10);

        // 批量查询订单并分页
        IPage<Orders> allOrdersPage = selectOrdersByItemIdsWithPage(page, itemNames, ordersMapper);

        // 筛选出进行中的订单
        List<Orders> orders = allOrdersPage.getRecords().stream()
                .filter(order -> order.getStatus() == 2)
                .collect(Collectors.toList());

        // 将筛选后的订单重新封装到分页对象中
        IPage<Orders> filteredOrdersPage = new Page<>(allOrdersPage.getCurrent(), allOrdersPage.getSize(), allOrdersPage.getTotal());
        filteredOrdersPage.setRecords(orders);

        return Result.success(filteredOrdersPage);
    }



    @Override
    public Result<?> getFinishedOrders(Integer shopId) {
        // 先查询该商店的商品
        LambdaQueryWrapper<Goods> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Goods::getShopId, shopId);
        List<Goods> goods = goodsMapper.selectList(lqw);

        // 获取所有商品的 itemName
        List<String> itemNames = goods.stream()
                .map(Goods::getItemName)
                .collect(Collectors.toList());

        Page<Orders> page = new Page<>(1, 10);

        // 批量查询订单并分页
        IPage<Orders> allOrdersPage = selectOrdersByItemIdsWithPage(page, itemNames, ordersMapper);

        // 筛选出已完成的订单
        List<Orders> orders = allOrdersPage.getRecords().stream()
                .filter(order -> order.getStatus() == 3)
                .collect(Collectors.toList());

        // 将筛选后的订单重新封装到分页对象中
        IPage<Orders> filteredOrdersPage = new Page<>(allOrdersPage.getCurrent(), allOrdersPage.getSize(), allOrdersPage.getTotal());
        filteredOrdersPage.setRecords(orders);

        return Result.success(filteredOrdersPage);
    }

    // 分页查询订单的方法
    private IPage<Orders> selectOrdersByItemIdsWithPage(Page<Orders> page, List<String> itemNames, OrdersMapper ordersMapper) {
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Orders::getItems, itemNames);
        return ordersMapper.selectPage(page, queryWrapper);
    }


}




