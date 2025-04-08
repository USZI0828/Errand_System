package com.arrend_system.service;

import com.arrend_system.pojo.entity.Orders;
import com.arrend_system.pojo.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.List;

public interface TakerService extends IService<User> {
    List<Orders> getUnChooseOrders();


    String chooseOrders(Integer order_id, Integer order_taker, Integer status);

    String getChoseOrders(Integer orderTaker);

    BigDecimal countMoney(Integer orderTaker);
}
