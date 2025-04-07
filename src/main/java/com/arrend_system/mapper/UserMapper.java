package com.arrend_system.mapper;

import com.arrend_system.pojo.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.data.repository.query.Param;

/**
* @author 张明阳
* @description 针对表【user】的数据库操作Mapper
* @createDate 2025-04-06 12:14:15
* @Entity genertor.entity.User
*/
public interface UserMapper extends BaseMapper<User> {
    User findUserByUsername(@Param("username") String username);

}




