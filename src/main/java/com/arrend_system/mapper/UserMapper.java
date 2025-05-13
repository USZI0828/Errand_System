package com.arrend_system.mapper;

import com.arrend_system.pojo.entity.User;
import com.arrend_system.pojo.form.update.UpdateUserForm;
import com.arrend_system.pojo.vo.UserInfoVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

/**
* @author 张明阳
* @description 针对表【user】的数据库操作Mapper
* @createDate 2025-04-06 12:14:15
* @Entity genertor.entity.User
*/
public interface UserMapper extends BaseMapper<User> {
    User findUserByUsername(@Param("username") String username);

    UserInfoVo findUserByEmail(@Param("email") String email);

    UserInfoVo findUserByName(@Param("userName")String userName);

    BigDecimal findCountById(@Param("userId") Integer publisher);

    void updateUserInfoById(UpdateUserForm updateUserForm);
}




