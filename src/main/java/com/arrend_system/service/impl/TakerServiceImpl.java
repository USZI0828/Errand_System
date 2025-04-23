package com.arrend_system.service.impl;

import com.arrend_system.common.Result;
import com.arrend_system.exception.TakerException.TakeOrderException;
import com.arrend_system.mapper.OrdersMapper;
import com.arrend_system.mapper.TakerMapper;
import com.arrend_system.pojo.entity.Orders;
import com.arrend_system.pojo.entity.User;
import com.arrend_system.service.TakerService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TakerServiceImpl extends ServiceImpl<TakerMapper, User> implements TakerService {

    @Autowired
    private OrdersMapper ordersMapper;

    private static final Object lock = new Object();

    @Override
    public Result<?> getUnChooseOrders() {
        // status为1表示待接单
        Integer status = 1;
        LambdaQueryWrapper<Orders> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Orders::getStatus,status);
        List<Orders> list = ordersMapper.selectList(lqw);
        return Result.success(list);
    }

    public Result<?> chooseOrders(Integer order_id, Integer order_taker) {
        synchronized (lock) {
            Orders orders = ordersMapper.selectById(order_id);
            if (orders == null) {
                return Result.fail(400,"订单不存在！","");
            }
            if (orders.getStatus() != 1) {
                throw new TakeOrderException("该订单无法接取！");
            }
            orders.setOrderTaker(order_taker);
            orders.setStatus(2);
            ordersMapper.updateById(orders);
            return Result.success("接取订单成功");
        }
    }

    @Override
    public Result<?> getChoseOrders(Integer orderTaker) {
        LambdaQueryWrapper<Orders> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Orders::getOrderTaker,orderTaker);
        List<Orders> orders = ordersMapper.selectList(lqw);
        return Result.success(orders);
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


}
