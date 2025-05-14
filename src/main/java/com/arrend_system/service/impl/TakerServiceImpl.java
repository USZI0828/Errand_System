package com.arrend_system.service.impl;

import com.arrend_system.common.Result;
import com.arrend_system.exception.TakerException.TakeOrderException;
import com.arrend_system.mapper.AddressMapper;
import com.arrend_system.mapper.OrdersMapper;
import com.arrend_system.mapper.TakerMapper;
import com.arrend_system.pojo.entity.Address;
import com.arrend_system.pojo.entity.Orders;
import com.arrend_system.pojo.entity.User;
import com.arrend_system.service.TakerService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TakerServiceImpl extends ServiceImpl<TakerMapper, User> implements TakerService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private AddressMapper addressMapper;

    private static final Object lock = new Object();

    @Override
    public Result<?> getUnChooseOrders() {
        Page<Orders> page = new Page<>(1, 10);
        Integer status = 1;

        // 查询订单
        LambdaQueryWrapper<Orders> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Orders::getStatus, status);
        IPage<Orders> ordersPage = ordersMapper.selectPage(page, lqw);

        // 批量查询地址并关联（避免 N+1 查询问题）
        List<Integer> addressIds = ordersPage.getRecords().stream()
                .map(Orders::getAddressId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (!addressIds.isEmpty()) {
            // 批量查询地址
            Map<Integer, Address> addressMap = addressMapper.selectBatchIds(addressIds).stream()
                    .collect(Collectors.toMap(Address::getAddressId, addr -> addr));

            // 关联地址到订单
            for (Orders order : ordersPage.getRecords()) {
                if (order.getAddressId() != null && addressMap.containsKey(order.getAddressId())) {
                    order.setAddress(addressMap.get(order.getAddressId()));
                }
            }
        }

        return Result.success(ordersPage);
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
        // 执行分页查询
        IPage<Orders> ordersPage = ordersMapper.selectPage(page, lqw);

        // 批量查询地址并关联（避免 N+1 查询问题）
        List<Integer> addressIds = ordersPage.getRecords().stream()
                .map(Orders::getAddressId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (!addressIds.isEmpty()) {
            // 批量查询地址
            Map<Integer, Address> addressMap = addressMapper.selectBatchIds(addressIds).stream()
                    .collect(Collectors.toMap(Address::getAddressId, addr -> addr));

            // 关联地址到订单
            for (Orders order : ordersPage.getRecords()) {
                if (order.getAddressId() != null && addressMap.containsKey(order.getAddressId())) {
                    order.setAddress(addressMap.get(order.getAddressId()));
                }
            }
        }

        return Result.success(ordersPage);
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
        // 执行分页查询
        IPage<Orders> ordersPage = ordersMapper.selectPage(page, lqw);

        // 批量查询地址并关联（避免 N+1 查询问题）
        List<Integer> addressIds = ordersPage.getRecords().stream()
                .map(Orders::getAddressId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (!addressIds.isEmpty()) {
            // 批量查询地址
            Map<Integer, Address> addressMap = addressMapper.selectBatchIds(addressIds).stream()
                    .collect(Collectors.toMap(Address::getAddressId, addr -> addr));

            // 关联地址到订单
            for (Orders order : ordersPage.getRecords()) {
                if (order.getAddressId() != null && addressMap.containsKey(order.getAddressId())) {
                    order.setAddress(addressMap.get(order.getAddressId()));
                }
            }
        }

        return Result.success(ordersPage);
    }

    @Override
    public Result<?> getHistoryOrders(Integer orderTaker) {
        Page<Orders> page = new Page<>(1, 10);
        // 创建查询条件
        LambdaQueryWrapper<Orders> lqw = new LambdaQueryWrapper<>();
        // 查询对应跑腿员id和状态码为3的订单
        lqw.eq(Orders::getOrderTaker, orderTaker).eq(Orders::getStatus, 3);
        // 执行分页查询
        IPage<Orders> ordersPage = ordersMapper.selectPage(page, lqw);

        // 批量查询地址并关联（避免 N+1 查询问题）
        List<Integer> addressIds = ordersPage.getRecords().stream()
                .map(Orders::getAddressId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (!addressIds.isEmpty()) {
            // 批量查询地址
            Map<Integer, Address> addressMap = addressMapper.selectBatchIds(addressIds).stream()
                    .collect(Collectors.toMap(Address::getAddressId, addr -> addr));

            // 关联地址到订单
            for (Orders order : ordersPage.getRecords()) {
                if (order.getAddressId() != null && addressMap.containsKey(order.getAddressId())) {
                    order.setAddress(addressMap.get(order.getAddressId()));
                }
            }
        }

        return Result.success(ordersPage);
    }


}
