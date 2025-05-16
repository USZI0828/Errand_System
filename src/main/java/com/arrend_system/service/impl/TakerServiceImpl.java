package com.arrend_system.service.impl;

import com.arrend_system.common.Result;
import com.arrend_system.exception.TakerException.TakeOrderException;
import com.arrend_system.mapper.AddressMapper;
import com.arrend_system.mapper.GoodsMapper;
import com.arrend_system.mapper.OrdersMapper;
import com.arrend_system.mapper.TakerMapper;
import com.arrend_system.pojo.entity.Address;
import com.arrend_system.pojo.entity.Goods;
import com.arrend_system.pojo.entity.Orders;
import com.arrend_system.pojo.entity.User;
import com.arrend_system.pojo.vo.OrdersVo;
import com.arrend_system.service.TakerService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TakerServiceImpl extends ServiceImpl<TakerMapper, User> implements TakerService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    private static final Object lock = new Object();

    @Override
    public Result<?> getUnChooseOrders() {
        Page<Orders> page = new Page<>(1, 10);
        Integer status = 1;

        // 查询订单
        LambdaQueryWrapper<Orders> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Orders::getStatus, status);
        IPage<Orders> ordersPage = ordersMapper.selectPage(page, lqw);

        // 提取所有addressId（去重）
        List<Integer> addressIds = ordersPage.getRecords().stream()
                .map(Orders::getAddressId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        // 批量查询地址信息
        Map<Integer, Address> addressMap = addressIds.isEmpty()
                ? Collections.emptyMap()
                : addressMapper.selectBatchIds(addressIds).stream()
                .collect(Collectors.toMap(Address::getAddressId, addr -> addr));

        // 提取所有商品ID（从item字段解析）
        Set<Integer> goodsIds = new HashSet<>();
        for (Orders order : ordersPage.getRecords()) {
            String item = order.getItems();
            if (item != null && !item.isEmpty()) {
                String[] ids = item.split(",");
                for (String id : ids) {
                    try {
                        goodsIds.add(Integer.valueOf(id.trim()));
                    } catch (NumberFormatException e) {
                        log.warn("Invalid goods ID format: " + id);
                    }
                }
            }
        }

        // 批量查询商品信息
        Map<Integer, Goods> goodsMap = goodsIds.isEmpty()
                ? Collections.emptyMap()
                : goodsMapper.selectBatchIds(goodsIds).stream()
                .collect(Collectors.toMap(Goods::getItemId, goods -> goods));

        // 将Orders转换为OrdersVO并关联地址和商品信息
        List<OrdersVo> ordersVOList = ordersPage.getRecords().stream()
                .map(order -> {
                    OrdersVo vo = new OrdersVo();
                    BeanUtils.copyProperties(order, vo);

                    // 设置地址
                    if (order.getAddressId() != null) {
                        vo.setAddress(addressMap.get(order.getAddressId()));
                    }

                    // 设置商品列表
                    if (order.getItems() != null && !order.getItems().isEmpty()) {
                        String[] ids = order.getItems().split(",");
                        List<Goods> goodsList = Arrays.stream(ids)
                                .map(id -> {
                                    try {
                                        return goodsMap.get(Integer.valueOf(id.trim()));
                                    } catch (NumberFormatException e) {
                                        return null;
                                    }
                                })
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList());
                        vo.setItemList(goodsList);
                    }

                    return vo;
                })
                .collect(Collectors.toList());

        // 封装VO分页结果
        Page<OrdersVo> voPage = new Page<>(ordersPage.getCurrent(), ordersPage.getSize(), ordersPage.getTotal());
        voPage.setRecords(ordersVOList);

        return Result.success(voPage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> chooseOrders(Integer order_id, Integer order_taker) {
        // 创建要更新的订单对象
        Orders order = new Orders();
        order.setOrderId(order_id);
        order.setOrderTaker(order_taker);
        order.setStatus(2);

        // 设置乐观锁条件：订单状态必须为1（可接取）
        UpdateWrapper<Orders> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("status", 1);

        // 执行更新并检查影响行数
        int rows = ordersMapper.update(order, updateWrapper);

        // 根据更新结果返回相应信息
        if (rows > 0) {
            return Result.success("接取订单成功！");
        } else {
            // 查询订单以获取更详细的失败原因
            Orders existingOrder = ordersMapper.selectById(order_id);
            if (existingOrder == null) {
                return Result.fail(400, "订单不存在！", "");
            } else if (existingOrder.getStatus() != 1) {
                return Result.fail(400, "订单状态已变更，无法接取！", existingOrder.getStatus());
            } else {
                return Result.fail(500, "系统繁忙，请稍后再试！", "");
            }
        }
    }

    @Override
    public Result<?> getChoseOrders(Integer orderTaker) {
        Page<Orders> page = new Page<>(1, 10);
        // 创建查询条件
        LambdaQueryWrapper<Orders> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Orders::getOrderTaker, orderTaker);
        IPage<Orders> ordersPage = ordersMapper.selectPage(page, lqw);

        // 提取所有addressId（去重）
        List<Integer> addressIds = ordersPage.getRecords().stream()
                .map(Orders::getAddressId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        // 批量查询地址信息
        Map<Integer, Address> addressMap = addressIds.isEmpty()
                ? Collections.emptyMap()
                : addressMapper.selectBatchIds(addressIds).stream()
                .collect(Collectors.toMap(Address::getAddressId, addr -> addr));

        // 提取所有商品ID（从item字段解析）
        Set<Integer> goodsIds = new HashSet<>();
        for (Orders order : ordersPage.getRecords()) {
            String item = order.getItems();
            if (item != null && !item.isEmpty()) {
                String[] ids = item.split(",");
                for (String id : ids) {
                    try {
                        goodsIds.add(Integer.valueOf(id.trim()));
                    } catch (NumberFormatException e) {
                        log.warn("Invalid goods ID format: " + id);
                    }
                }
            }
        }

        // 批量查询商品信息
        Map<Integer, Goods> goodsMap = goodsIds.isEmpty()
                ? Collections.emptyMap()
                : goodsMapper.selectBatchIds(goodsIds).stream()
                .collect(Collectors.toMap(Goods::getItemId, goods -> goods));

        // 将Orders转换为OrdersVO并关联地址和商品信息
        List<OrdersVo> ordersVOList = ordersPage.getRecords().stream()
                .map(order -> {
                    OrdersVo vo = new OrdersVo();
                    BeanUtils.copyProperties(order, vo);

                    // 设置地址
                    if (order.getAddressId() != null) {
                        vo.setAddress(addressMap.get(order.getAddressId()));
                    }

                    // 设置商品列表
                    if (order.getItems() != null && !order.getItems().isEmpty()) {
                        String[] ids = order.getItems().split(",");
                        List<Goods> goodsList = Arrays.stream(ids)
                                .map(id -> {
                                    try {
                                        return goodsMap.get(Integer.valueOf(id.trim()));
                                    } catch (NumberFormatException e) {
                                        return null;
                                    }
                                })
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList());
                        vo.setItemList(goodsList);
                    }

                    return vo;
                })
                .collect(Collectors.toList());

        // 封装VO分页结果
        Page<OrdersVo> voPage = new Page<>(ordersPage.getCurrent(), ordersPage.getSize(), ordersPage.getTotal());
        voPage.setRecords(ordersVOList);

        return Result.success(voPage);
    }

    @Override
    public Result<?> countMoney(Integer orderTaker) {
        BigDecimal money = new BigDecimal(0);
        LambdaQueryWrapper<Orders> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Orders::getOrderTaker,orderTaker);
        List<Orders> orders = ordersMapper.selectList(lqw);
        for (Orders order : orders) {
            // 计算该跑腿员已完成订单的金额
            if (order.getStatus().equals(3)) {
                money = money.add(order.getCost());
            }
        }
        return Result.success(money);
    }

    @Override
    public Result<?> updateOrder(Integer orderId) {
        Orders order = ordersMapper.selectById(orderId);
        order.setStatus(3);
        ordersMapper.updateById(order);
        return Result.success("订单已送达！");
    }

    @Override
    public Result<?> getOngoingOrders(Integer orderTaker) {
        Page<Orders> page = new Page<>(1, 10);
        // 创建查询条件
        LambdaQueryWrapper<Orders> lqw = new LambdaQueryWrapper<>();
        // 查询对应跑腿员id和状态码为2的订单
        lqw.eq(Orders::getOrderTaker, orderTaker).eq(Orders::getStatus, 2);
        IPage<Orders> ordersPage = ordersMapper.selectPage(page, lqw);

        // 提取所有addressId（去重）
        List<Integer> addressIds = ordersPage.getRecords().stream()
                .map(Orders::getAddressId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        // 批量查询地址信息
        Map<Integer, Address> addressMap = addressIds.isEmpty()
                ? Collections.emptyMap()
                : addressMapper.selectBatchIds(addressIds).stream()
                .collect(Collectors.toMap(Address::getAddressId, addr -> addr));

        // 提取所有商品ID（从item字段解析）
        Set<Integer> goodsIds = new HashSet<>();
        for (Orders order : ordersPage.getRecords()) {
            String item = order.getItems();
            if (item != null && !item.isEmpty()) {
                String[] ids = item.split(",");
                for (String id : ids) {
                    try {
                        goodsIds.add(Integer.valueOf(id.trim()));
                    } catch (NumberFormatException e) {
                        log.warn("Invalid goods ID format: " + id);
                    }
                }
            }
        }

        // 批量查询商品信息
        Map<Integer, Goods> goodsMap = goodsIds.isEmpty()
                ? Collections.emptyMap()
                : goodsMapper.selectBatchIds(goodsIds).stream()
                .collect(Collectors.toMap(Goods::getItemId, goods -> goods));

        // 将Orders转换为OrdersVO并关联地址和商品信息
        List<OrdersVo> ordersVOList = ordersPage.getRecords().stream()
                .map(order -> {
                    OrdersVo vo = new OrdersVo();
                    BeanUtils.copyProperties(order, vo);

                    // 设置地址
                    if (order.getAddressId() != null) {
                        vo.setAddress(addressMap.get(order.getAddressId()));
                    }

                    // 设置商品列表
                    if (order.getItems() != null && !order.getItems().isEmpty()) {
                        String[] ids = order.getItems().split(",");
                        List<Goods> goodsList = Arrays.stream(ids)
                                .map(id -> {
                                    try {
                                        return goodsMap.get(Integer.valueOf(id.trim()));
                                    } catch (NumberFormatException e) {
                                        return null;
                                    }
                                })
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList());
                        vo.setItemList(goodsList);
                    }

                    return vo;
                })
                .collect(Collectors.toList());

        // 封装VO分页结果
        Page<OrdersVo> voPage = new Page<>(ordersPage.getCurrent(), ordersPage.getSize(), ordersPage.getTotal());
        voPage.setRecords(ordersVOList);

        return Result.success(voPage);
    }

    @Override
    public Result<?> getHistoryOrders(Integer orderTaker) {
        Page<Orders> page = new Page<>(1, 10);
        // 创建查询条件
        LambdaQueryWrapper<Orders> lqw = new LambdaQueryWrapper<>();
        // 查询对应跑腿员id和状态码为3的订单
        lqw.eq(Orders::getOrderTaker, orderTaker).eq(Orders::getStatus, 3);
        IPage<Orders> ordersPage = ordersMapper.selectPage(page, lqw);

        // 提取所有addressId（去重）
        List<Integer> addressIds = ordersPage.getRecords().stream()
                .map(Orders::getAddressId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        // 批量查询地址信息
        Map<Integer, Address> addressMap = addressIds.isEmpty()
                ? Collections.emptyMap()
                : addressMapper.selectBatchIds(addressIds).stream()
                .collect(Collectors.toMap(Address::getAddressId, addr -> addr));

        // 提取所有商品ID（从item字段解析）
        Set<Integer> goodsIds = new HashSet<>();
        for (Orders order : ordersPage.getRecords()) {
            String item = order.getItems();
            if (item != null && !item.isEmpty()) {
                String[] ids = item.split(",");
                for (String id : ids) {
                    try {
                        goodsIds.add(Integer.valueOf(id.trim()));
                    } catch (NumberFormatException e) {
                        log.warn("Invalid goods ID format: " + id);
                    }
                }
            }
        }

        // 批量查询商品信息
        Map<Integer, Goods> goodsMap = goodsIds.isEmpty()
                ? Collections.emptyMap()
                : goodsMapper.selectBatchIds(goodsIds).stream()
                .collect(Collectors.toMap(Goods::getItemId, goods -> goods));

        // 将Orders转换为OrdersVo并关联地址和商品信息
        List<OrdersVo> ordersVOList = ordersPage.getRecords().stream()
                .map(order -> {
                    OrdersVo vo = new OrdersVo();
                    BeanUtils.copyProperties(order, vo);

                    // 设置地址
                    if (order.getAddressId() != null) {
                        vo.setAddress(addressMap.get(order.getAddressId()));
                    }

                    // 设置商品列表
                    if (order.getItems() != null && !order.getItems().isEmpty()) {
                        String[] ids = order.getItems().split(",");
                        List<Goods> goodsList = Arrays.stream(ids)
                                .map(id -> {
                                    try {
                                        return goodsMap.get(Integer.valueOf(id.trim()));
                                    } catch (NumberFormatException e) {
                                        return null;
                                    }
                                })
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList());
                        vo.setItemList(goodsList);
                    }

                    return vo;
                })
                .collect(Collectors.toList());

        // 封装VO分页结果
        Page<OrdersVo> voPage = new Page<>(ordersPage.getCurrent(), ordersPage.getSize(), ordersPage.getTotal());
        voPage.setRecords(ordersVOList);

        return Result.success(voPage);
    }
}
