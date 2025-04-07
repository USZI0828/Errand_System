package com.arrend_system.service.impl;

import com.arrend_system.mapper.GoodsMapper;
import com.arrend_system.mapper.OrdersMapper;
import com.arrend_system.pojo.entity.Goods;
import com.arrend_system.pojo.entity.Orders;
import com.arrend_system.pojo.entity.Shop;
import com.arrend_system.mapper.ShopMapper;
import com.arrend_system.service.ShopService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
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
    public String addGoods(Shop shop) {
        shopMapper.insert(shop);
        return "添加商品成功";
    }

    @Override
    public String updateGoods(Shop shop) {
        shopMapper.updateById(shop);
        return "修改商品信息成功";
    }

    @Override
    public List<Orders> getOrders(Integer shopId) {
        // 先查询该商店的商品
        LambdaQueryWrapper<Goods> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Goods::getShopId, shopId);
        List<Goods> goods = goodsMapper.selectList(lqw);

        if (CollectionUtils.isEmpty(goods)) {
            return new ArrayList<>();
        }

        // 获取所有商品的 itemId
        List<Integer> itemIds = goods.stream()
                .map(Goods::getItemId)
                .collect(Collectors.toList());

        // 使用 in 条件批量查询订单
        LambdaQueryWrapper<Orders> lqwOrders = new LambdaQueryWrapper<>();
        lqwOrders.in(Orders::getItemId, itemIds);
        List<Orders> allOrders = ordersMapper.selectList(lqwOrders);

        // 筛选出状态不为 0 的订单
        return allOrders.stream()
                .filter(order -> order.getStatus() != 0)
                .collect(Collectors.toList());
    }
}




