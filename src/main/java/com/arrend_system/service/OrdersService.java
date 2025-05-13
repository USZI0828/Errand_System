package com.arrend_system.service;

import com.arrend_system.common.Result;
import com.arrend_system.pojo.entity.Orders;
import com.arrend_system.pojo.form.add.AddOrderForm;
import com.arrend_system.pojo.query.OrdersQuery;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 张明阳
* @description 针对表【orders】的数据库操作Service
* @createDate 2025-04-06 12:14:15
*/
public interface OrdersService extends IService<Orders> {

    Result<?> publish(AddOrderForm addOrderForm);

    Result<?> getList(OrdersQuery ordersQuery);

    Result<?> cancelArrend(Integer ordersId);

    Result<?> getShopList();

    Result<?> getGoodsByShop(Integer shopId);

    Result<?> getArrendType();

}
