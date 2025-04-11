package com.arrend_system.service.impl;

import com.arrend_system.mapper.OrdersMapper;
import com.arrend_system.mapper.TakerMapper;
import com.arrend_system.pojo.entity.Orders;
import com.arrend_system.pojo.entity.User;
import com.arrend_system.service.TakerService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TakerServiceImpl extends ServiceImpl<TakerMapper, User> implements TakerService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Override
    public List<Orders> getUnChooseOrders() {
        // status为1表示待接单
        Integer status = 1;
        LambdaQueryWrapper<Orders> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Orders::getStatus,status);
        return ordersMapper.selectList(lqw);
    }

    @Override
    public String chooseOrders(Integer order_id, Integer order_taker) {
        Orders orders = ordersMapper.selectById(order_id);
        orders.setOrderTaker(order_taker);
        orders.setStatus(2);
        ordersMapper.updateById(orders);
        return "接取订单成功";
    }

    @Override
    public String getChoseOrders(Integer orderTaker) {
        Orders orders = ordersMapper.selectById(orderTaker);
        return "查询已接订单";
    }

    @Override
    public BigDecimal countMoney(Integer orderTaker) {
        BigDecimal money = new BigDecimal(0);
        LambdaQueryWrapper<Orders> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Orders::getOrderTaker,orderTaker);
        for (Orders orders : ordersMapper.selectList(lqw)) {
            // 计算该跑腿员已完成订单的金额
            if (orders.getStatus().equals(3)) {
                money = money.add(orders.getCost());
            }
        }
        return money;
    }


}
