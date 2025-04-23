package com.arrend_system.service;

import com.arrend_system.common.Result;
import com.arrend_system.pojo.entity.User;
import com.arrend_system.pojo.form.LoginForm;
import com.arrend_system.pojo.form.RegisterForm;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
* @author 张明阳
* @description 针对表【user】的数据库操作Service
* @createDate 2025-04-06 12:14:15
*/
public interface UserService extends IService<User> {

    Result<?> login(LoginForm loginForm) throws JsonProcessingException;

    Result<?> getEmailCode(String email);

    Result<?> register(RegisterForm registerForm);

    Result<?> getUserInfo(String userName);
}
