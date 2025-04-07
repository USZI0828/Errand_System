package com.arrend_system.service.impl;

import com.arrend_system.pojo.entity.Orders;
import com.arrend_system.mapper.OrdersMapper;
import com.arrend_system.service.OrdersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
* @author 张明阳
* @description 针对表【orders】的数据库操作Service实现
* @createDate 2025-04-06 12:14:15
*/
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders>
    implements OrdersService {

}




