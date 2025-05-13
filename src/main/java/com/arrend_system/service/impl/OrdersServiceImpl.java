package com.arrend_system.service.impl;

import com.arrend_system.common.Result;
import com.arrend_system.exception.OrderException.CompletedOrderException;
import com.arrend_system.exception.OrderException.NoEnoughCountException;
import com.arrend_system.mapper.ArrendTypeMapper;
import com.arrend_system.mapper.GoodsMapper;
import com.arrend_system.mapper.UserMapper;
import com.arrend_system.pojo.delayed.AutoOrder;
import com.arrend_system.pojo.entity.ArrendType;
import com.arrend_system.pojo.entity.Goods;
import com.arrend_system.pojo.entity.Orders;
import com.arrend_system.mapper.OrdersMapper;
import com.arrend_system.pojo.entity.User;
import com.arrend_system.pojo.form.add.AddOrderForm;
import com.arrend_system.pojo.query.OrdersQuery;
import com.arrend_system.pojo.vo.GoodsVo;
import com.arrend_system.pojo.vo.OrdersVo;
import com.arrend_system.pojo.vo.ShopInfoVo;
import com.arrend_system.service.OrdersService;
import com.arrend_system.utils.JsonUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.DelayQueue;
import java.util.stream.Collectors;

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

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private ArrendTypeMapper arrendTypeMapper;

    @Override
    public Result<?> publish(AddOrderForm addOrderForm) {
        //发布之前检查用户余额是否充足
        User user = userMapper.selectById(addOrderForm.getPublisher());
        if (user.getCount().compareTo(addOrderForm.getCost()) == -1) {
            //余额不足，无法发布任务
            throw new NoEnoughCountException();
        }
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
                addOrderForm.getItemList(),
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
        //封装商品列表
        for (OrdersVo record : page.getRecords()) {
            Orders orders = ordersMapper.selectById(record.getOrderId());
            String items = orders.getItems();
            List<Integer> itemIdList = Arrays.stream(items.split(","))
                    .map(Integer::valueOf)     // 字符串转 Integer
                    .collect(Collectors.toList());
            List<Goods> goods = goodsMapper.selectBatchIds(itemIdList);
            record.setItemList(goods);
        }
        data.put("total",page.getTotal());
        data.put("currentPage",page.getCurrent());
        data.put("pageNumber",page.getPages());
        data.put("records",page.getRecords());
        return Result.success(data);
    }

    //用户在跑腿员接到跑腿任务后取消支付20%的费用
    @Override
    public Result<?> cancelArrend(Integer ordersId) {
        Orders orders = ordersMapper.selectById(ordersId);
        if (orders.getStatus() == 2) {
            // 查询发布者余额
            BigDecimal publisherCount = userMapper.findCountById(orders.getPublisher());
            // 提成金额 = 订单金额 * 0.2
            BigDecimal commission = orders.getCost().multiply(new BigDecimal("0.2"));
            // 发布者扣除提成
            BigDecimal newPublisherCount = publisherCount.subtract(commission);
            // 更新发布者余额
            UpdateWrapper<User> publisherWrapper = new UpdateWrapper<>();
            publisherWrapper.eq("user_id", orders.getPublisher())
                    .set("count", newPublisherCount);
            userMapper.update(publisherWrapper);
            // 更新接单者余额（提成金额）
            UpdateWrapper<User> takerWrapper = new UpdateWrapper<>();
            takerWrapper.eq("user_id", orders.getOrderTaker())
                    .set("count", commission);
            userMapper.update(takerWrapper);
        }

        if(orders.getStatus() == 3) {
            //订单已完成，无法取消，请联系管理员
            throw new CompletedOrderException();
        }
        orders.setStatus(-1);
        orders.setFinishTime(LocalDateTime.now());
        ordersMapper.updateById(orders);
        return Result.success("取消订单成功");
    }

    @Override
    public Result<?> getShopList() {
        List<ShopInfoVo> shopInfoVoList = ordersMapper.getShop();
        return Result.success(shopInfoVoList);
    }

    @Override
    public Result<?> getGoodsByShop(Integer shopId) {
        List<GoodsVo> goodsVoList = ordersMapper.getGoodsList(shopId);
        return Result.success(goodsVoList);
    }

    @Override
    public Result<?> getArrendType() {
       List<ArrendType> list =  arrendTypeMapper.getList();
       return Result.success(list);
    }
}




