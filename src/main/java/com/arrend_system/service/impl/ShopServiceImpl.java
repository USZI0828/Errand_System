package com.arrend_system.service.impl;

import com.arrend_system.common.Result;
import com.arrend_system.exception.ShopException.ShopException;
import com.arrend_system.exception.ShopException.ShopNoOrderException;
import com.arrend_system.mapper.AddressMapper;
import com.arrend_system.mapper.GoodsMapper;
import com.arrend_system.mapper.OrdersMapper;
import com.arrend_system.pojo.entity.Address;
import com.arrend_system.pojo.entity.Goods;
import com.arrend_system.pojo.entity.Orders;
import com.arrend_system.pojo.entity.Shop;
import com.arrend_system.mapper.ShopMapper;
import com.arrend_system.pojo.vo.OrdersVo;
import com.arrend_system.service.ShopService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
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

    @Autowired
    private AddressMapper addressMapper;



    @Override
    public Result<?> getShopInfo(Integer userId) {
        LambdaQueryWrapper<Shop> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Shop::getUserId, userId);
        Shop shop = shopMapper.selectOne(lqw);
        return Result.success(shop);
    }


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

            // 查询该商店的商品ID列表
            LambdaQueryWrapper<Goods> lqw = new LambdaQueryWrapper<>();
            lqw.select(Goods::getItemId).eq(Goods::getShopId, shopId);
            List<Integer> itemIds = goodsMapper.selectObjs(lqw)
                    .stream()
                    .map(obj -> (Integer) obj)
                    .collect(Collectors.toList());

            // 处理商品列表为空的情况
            if (itemIds.isEmpty()) {
                return Result.success(new Page<>());
            }

            // 生成正则表达式条件
            String regex = String.join("|", itemIds.stream()
                    .map(String::valueOf)
                    .collect(Collectors.toList()));

            // 构建查询条件（移除status限制）
            QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
            queryWrapper.apply("items REGEXP {0}", "(^|,)(" + regex + ")(,|$)");

            // 执行分页查询
            Page<Orders> page = new Page<>(1, 10);
            IPage<Orders> ordersPage = ordersMapper.selectPage(page, queryWrapper);

            // 封装结果
            List<OrdersVo> orderVoList = convertToOrderVo(ordersPage.getRecords());
            Page<OrdersVo> voPage = new Page<>(ordersPage.getCurrent(), ordersPage.getSize(), ordersPage.getTotal());
            voPage.setRecords(orderVoList);

            return Result.success(voPage);

        } catch (Exception e) {
            log.error("获取订单列表失败", e);
            return Result.fail(500, "系统内部错误，请稍后重试", e.getMessage());
        }
    }


    @Override
    public Result<?> getUndoOrders(Integer shopId) {
        try {
            // 参数校验
            if (shopId == null) {
                return Result.fail(400, "店铺ID不能为空", "");
            }

            // 查询该商店的商品ID列表
            LambdaQueryWrapper<Goods> lqw = new LambdaQueryWrapper<>();
            lqw.select(Goods::getItemId).eq(Goods::getShopId, shopId);
            List<Integer> itemIds = goodsMapper.selectObjs(lqw)
                    .stream()
                    .map(obj -> (Integer) obj)
                    .collect(Collectors.toList());

            // 处理商品列表为空的情况
            if (itemIds.isEmpty()) {
                return Result.success(new Page<>());
            }

            // 生成正则表达式条件
            String regex = String.join("|", itemIds.stream()
                    .map(String::valueOf)
                    .collect(Collectors.toList()));

            // 构建查询条件
            QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
            queryWrapper.apply("items REGEXP {0}", "(^|,)(" + regex + ")(,|$)")
                    .eq("status", -1);

            // 执行分页查询
            Page<Orders> page = new Page<>(1, 10);
            IPage<Orders> ordersPage = ordersMapper.selectPage(page, queryWrapper);

            // 封装结果
            List<OrdersVo> orderVoList = convertToOrderVo(ordersPage.getRecords());
            Page<OrdersVo> voPage = new Page<>(ordersPage.getCurrent(), ordersPage.getSize(), ordersPage.getTotal());
            voPage.setRecords(orderVoList);

            return Result.success(voPage);

        } catch (Exception e) {
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

            // 查询该商店的商品ID列表
            LambdaQueryWrapper<Goods> lqw = new LambdaQueryWrapper<>();
            lqw.select(Goods::getItemId).eq(Goods::getShopId, shopId);
            List<Integer> itemIds = goodsMapper.selectObjs(lqw)
                    .stream()
                    .map(obj -> (Integer) obj)
                    .collect(Collectors.toList());

            // 处理商品列表为空的情况
            if (itemIds.isEmpty()) {
                return Result.success(new Page<>());
            }

            // 生成正则表达式条件
            String regex = String.join("|", itemIds.stream()
                    .map(String::valueOf)
                    .collect(Collectors.toList()));

            // 构建查询条件
            QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
            queryWrapper.apply("items REGEXP {0}", "(^|,)(" + regex + ")(,|$)")
                    .eq("status", 1);

            // 执行分页查询
            Page<Orders> page = new Page<>(1, 10);
            IPage<Orders> ordersPage = ordersMapper.selectPage(page, queryWrapper);

            // 封装结果
            List<OrdersVo> orderVoList = convertToOrderVo(ordersPage.getRecords());
            Page<OrdersVo> voPage = new Page<>(ordersPage.getCurrent(), ordersPage.getSize(), ordersPage.getTotal());
            voPage.setRecords(orderVoList);

            return Result.success(voPage);

        } catch (Exception e) {
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

            // 查询该商店的商品ID列表
            LambdaQueryWrapper<Goods> lqw = new LambdaQueryWrapper<>();
            lqw.select(Goods::getItemId).eq(Goods::getShopId, shopId);
            List<Integer> itemIds = goodsMapper.selectObjs(lqw)
                    .stream()
                    .map(obj -> (Integer) obj)
                    .collect(Collectors.toList());

            // 处理商品列表为空的情况
            if (itemIds.isEmpty()) {
                return Result.success(new Page<>());
            }

            // 生成正则表达式条件
            String regex = String.join("|", itemIds.stream()
                    .map(String::valueOf)
                    .collect(Collectors.toList()));

            // 构建查询条件
            QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
            queryWrapper.apply("items REGEXP {0}", "(^|,)(" + regex + ")(,|$)")
                    .eq("status", 2);

            // 执行分页查询
            Page<Orders> page = new Page<>(1, 10);
            IPage<Orders> ordersPage = ordersMapper.selectPage(page, queryWrapper);

            // 封装结果
            List<OrdersVo> orderVoList = convertToOrderVo(ordersPage.getRecords());
            Page<OrdersVo> voPage = new Page<>(ordersPage.getCurrent(), ordersPage.getSize(), ordersPage.getTotal());
            voPage.setRecords(orderVoList);

            return Result.success(voPage);

        } catch (Exception e) {
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

            // 查询该商店的商品ID列表
            LambdaQueryWrapper<Goods> lqw = new LambdaQueryWrapper<>();
            lqw.select(Goods::getItemId).eq(Goods::getShopId, shopId);
            List<Integer> itemIds = goodsMapper.selectObjs(lqw)
                    .stream()
                    .map(obj -> (Integer) obj)
                    .collect(Collectors.toList());

            // 处理商品列表为空的情况
            if (itemIds.isEmpty()) {
                return Result.success(new Page<>());
            }

            // 生成正则表达式条件
            String regex = String.join("|", itemIds.stream()
                    .map(String::valueOf)
                    .collect(Collectors.toList()));

            // 构建查询条件
            QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
            queryWrapper.apply("items REGEXP {0}", "(^|,)(" + regex + ")(,|$)")
                    .eq("status", 3);

            // 执行分页查询
            Page<Orders> page = new Page<>(1, 10);
            IPage<Orders> ordersPage = ordersMapper.selectPage(page, queryWrapper);

            // 封装结果
            List<OrdersVo> orderVoList = convertToOrderVo(ordersPage.getRecords());
            Page<OrdersVo> voPage = new Page<>(ordersPage.getCurrent(), ordersPage.getSize(), ordersPage.getTotal());
            voPage.setRecords(orderVoList);

            return Result.success(voPage);

        } catch (Exception e) {
            log.error("获取订单列表失败", e);
            return Result.fail(500, "系统内部错误，请稍后重试", e.getMessage());
        }
    }


    // 订单信息转换为VO对象
    private List<OrdersVo> convertToOrderVo(List<Orders> ordersList) {
        if (CollectionUtils.isEmpty(ordersList)) {
            return Collections.emptyList();
        }

        // 提取所有需要查询的addressId和itemId
        Set<Integer> addressIds = new HashSet<>();
        Set<Integer> itemIds = new HashSet<>();

        for (Orders order : ordersList) {
            addressIds.add(order.getAddressId());
            // 解析items字段获取所有商品ID
            String[] itemIdArray = order.getItems().split(",");
            for (String id : itemIdArray) {
                itemIds.add(Integer.valueOf(id));
            }
        }

        // 批量查询地址信息和商品信息（减少数据库访问）
        Map<Integer, Address> addressMap = addressMapper.selectBatchIds(addressIds).stream()
                .collect(Collectors.toMap(Address::getAddressId, addr -> addr));

        Map<Integer, Goods> goodsMap = goodsMapper.selectBatchIds(itemIds).stream()
                .collect(Collectors.toMap(Goods::getItemId, item -> item));

        // 组装VO对象
        List<OrdersVo> result = new ArrayList<>();
        for (Orders order : ordersList) {
            OrdersVo vo = new OrdersVo();
            BeanUtils.copyProperties(order, vo);

            // 设置地址信息
            vo.setAddress(addressMap.get(order.getAddressId()));

            // 设置商品信息列表
            List<Goods> orderItems = new ArrayList<>();
            String[] itemIdArray = order.getItems().split(",");
            for (String id : itemIdArray) {
                Goods good = goodsMap.get(Integer.valueOf(id));
                if (good != null) {
                    orderItems.add(good);
                }
            }
            vo.setItemList(orderItems);

            result.add(vo);
        }

        return result;
    }
}




