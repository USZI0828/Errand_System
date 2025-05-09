package com.arrend_system.mapper;

import com.arrend_system.pojo.entity.Orders;
import com.arrend_system.pojo.query.OrdersQuery;
import com.arrend_system.pojo.vo.GoodsVo;
import com.arrend_system.pojo.vo.OrdersVo;
import com.arrend_system.pojo.vo.ShopInfoVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
* @author 张明阳
* @description 针对表【orders】的数据库操作Mapper
* @createDate 2025-04-06 12:14:15
* @Entity genertor.entity.Orders
*/
public interface OrdersMapper extends BaseMapper<Orders> {

    Page<OrdersVo> queryOrderList(Page<OrdersVo> page, @Param("query") OrdersQuery query);

    List<ShopInfoVo> getShop();

    List<GoodsVo> getGoodsList(@Param("shopId") Integer shopId);
}




