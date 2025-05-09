package com.arrend_system.service;

import com.arrend_system.common.Result;
import com.arrend_system.pojo.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

public interface TakerService extends IService<User> {


    Result<?> getUnChooseOrders();

    Result<?> chooseOrders(Integer order_id, Integer order_taker);

    Result<?> getChoseOrders(Integer orderTaker);

    Result<?> countMoney(Integer orderTaker);

    Result<?> updateOrder(Integer orderId);

    Result<?> getOngoingOrders(Integer orderTaker);

    Result<?> getHistoryOrders(Integer orderTaker);
}
