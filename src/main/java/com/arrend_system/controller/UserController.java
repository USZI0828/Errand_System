package com.arrend_system.controller;

import com.arrend_system.common.Result;
import com.arrend_system.pojo.form.LoginForm;
import com.arrend_system.pojo.form.RegisterForm;
import com.arrend_system.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Tag(name = "用户控制器")
public class UserController {
    @Autowired
    private UserService userService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<?> login(@RequestBody LoginForm loginForm) throws JsonProcessingException {
        return userService.login(loginForm);
    }

    @Operation(summary = "发送验证码")
    @GetMapping("/getEmail_code")
    public Result<?> getEmailCode(@RequestParam("email") String email){
        return userService.getEmailCode(email);
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<?> register(@RequestBody RegisterForm registerForm) {
        return userService.register(registerForm);
    }

    @Operation(summary = "查看个人信息")
    @GetMapping("/getUserInfo")
    public Result<?> getUserInfo(@RequestParam("userId")Integer userId) {
        return userService.getUserInfo(userId);
    }


}
