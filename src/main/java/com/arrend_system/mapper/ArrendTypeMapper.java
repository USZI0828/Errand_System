package com.arrend_system.mapper;

import com.arrend_system.pojo.entity.ArrendType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author 张明阳
* @description 针对表【arrend_type】的数据库操作Mapper
* @createDate 2025-04-06 12:14:15
* @Entity genertor.entity.ArrendType
*/
public interface ArrendTypeMapper extends BaseMapper<ArrendType> {

    @Select("select * from arrend_type")
    List<ArrendType> getList();

}




