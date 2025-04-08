package com.arrend_system.service.impl;

import com.arrend_system.common.Result;
import com.arrend_system.pojo.entity.Goods;
import com.arrend_system.pojo.entity.Orders;
import com.arrend_system.mapper.OrdersMapper;
import com.arrend_system.pojo.form.add.AddOrderForm;
import com.arrend_system.pojo.query.OrdersQuery;
import com.arrend_system.pojo.vo.OrdersVo;
import com.arrend_system.service.OrdersService;
import com.arrend_system.utils.JsonUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author 张明阳
* @description 针对表【orders】的数据库操作Service实现
* @createDate 2025-04-06 12:14:15
*/
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders>
    implements OrdersService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Override
    public Result<?> publish(AddOrderForm addOrderForm) {
        Orders orders = new Orders(
                null,
                addOrderForm.getTitle(),
                addOrderForm.getOrderType(),
                addOrderForm.getAddressId(),
                addOrderForm.getPosition(),
                addOrderForm.getCost(),
                addOrderForm.getPublisher(),
                null,
                0,
                LocalDateTime.now(),
                null,
                addOrderForm.getItemId(),
                addOrderForm.getDescription()
        );
        ordersMapper.insert(orders);
        return Result.success("任务发布成功，等待管理员审核");
    }

    @Override
    public Result<?> getList(OrdersQuery ordersQuery) {
        Page<OrdersVo> page = new Page<>(ordersQuery.getCurrentPage(),ordersQuery.getPageSize());
        ordersMapper.queryOrderList(page,ordersQuery);
        Map<String, Object> data = new HashMap<>();
        data.put("total",page.getTotal());
        data.put("currentPage",page.getCurrent());
        data.put("pageNumber",page.getPages());
        data.put("records",page.getRecords());
        return Result.success(data);
    }

    @Override
    public Result<?> cancelArrend(Integer ordersId) {
        Orders orders = ordersMapper.selectById(ordersId);
        orders.setStatus(-1);
        orders.setFinishTime(LocalDateTime.now());
        ordersMapper.updateById(orders);
        return Result.success("取消订单成功");
    }
}




