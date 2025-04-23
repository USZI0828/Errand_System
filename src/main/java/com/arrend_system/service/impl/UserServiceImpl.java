package com.arrend_system.service.impl;

import com.arrend_system.common.Result;
import com.arrend_system.exception.UserException.CodeExpiredException;
import com.arrend_system.exception.UserException.EmailUsedException;
import com.arrend_system.exception.UserException.UserDeletedException;
import com.arrend_system.exception.UserException.UserExitedException;
import com.arrend_system.mapper.UserPermMapper;
import com.arrend_system.pojo.entity.User;
import com.arrend_system.mapper.UserMapper;
import com.arrend_system.pojo.entity.UserPerm;
import com.arrend_system.pojo.form.LoginForm;
import com.arrend_system.pojo.form.RegisterForm;
import com.arrend_system.pojo.vo.UserInfoVo;
import com.arrend_system.service.SecretService;
import com.arrend_system.service.UserService;
import com.arrend_system.utils.EmailCodeUtil;
import com.arrend_system.utils.JsonUtil;
import com.arrend_system.utils.JwtUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.val;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
* @author 张明阳
* @description 针对表【user】的数据库操作Service实现
* @createDate 2025-04-06 12:14:15
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate redisTemplate;

    @Autowired
    private SecretService service;


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserPermMapper upMapper;

    @Override
    public Result<?> login(LoginForm loginForm) throws JsonProcessingException {
        //将用户名和密码存入authenticationToekn中，调用authenticate方法验证
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(loginForm.getUsername(),loginForm.getPassword());
        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        User user = (User)authentication.getPrincipal();
        if (user.getDeleteFlag() == 1) {
            //用户已经被删除
            throw new UserDeletedException();
        }
        String userInfo_ky = "user_" + user.getUsername();
        String userInfo = JsonUtil.serialize(user);
        redisTemplate.opsForValue().set(userInfo_ky,userInfo,30L,TimeUnit.DAYS);
        return Result.success(JwtUtils.createJwt(userInfo_ky,redisTemplate,service));
    }

    @Override
    public Result<?> getEmailCode(String email) {
        //查询邮箱是否已经被使用
        UserInfoVo user = userMapper.findUserByEmail(email);
        if (user != null) {
            //邮箱已经被使用
            throw new EmailUsedException();
        }
        //生成四位验证码
        String code = EmailCodeUtil.generateCode();
        //发送邮箱验证码
        try {
            // 创建邮件对象
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            // 开启支持 multipart
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
            // 设置邮件基本信息
            messageHelper.setFrom("3277512331@qq.com");
            messageHelper.setTo(email);
            messageHelper.setSubject("跑腿平台邮箱验证");
            // 设置邮件正文（纯文本）
            String content = "您好，您的验证码为：" + code + "，请在1分钟内完成验证。";
            messageHelper.setText(content, false);
            // 发送邮件
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        //将验证码存入redis中，有效期为一分钟
        String code_key = "email_" + email;
        redisTemplate.opsForValue().set(code_key,code,1L,TimeUnit.MINUTES);
        return Result.success("验证码发送成功");
    }

    @Override
    public Result<?> register(RegisterForm registerForm) {
        //检查用户名是否重复
        User user = userMapper.findUserByUsername(registerForm.getUsername());
        if (user != null) {
            //用户已存在
            throw new UserExitedException();
        }
        String code = redisTemplate.opsForValue().get("email_" + registerForm.getEmail());
        if (code == null) {
            //验证码过期
            throw new CodeExpiredException();
        }
        //检查验证码是否正确
        if (code.equals(registerForm.getCode())) {
            //将用户信息存入数据库
            User newUser = new User(registerForm.getUsername(),
                    passwordEncoder.encode(registerForm.getPassword()),
                    registerForm.getEmail());
            userMapper.insert(newUser);
            UserPerm up = new UserPerm(null, newUser.getUserId(), registerForm.getPermId());
            upMapper.insert(up);
            return Result.success("注册成功");
        }
        return Result.fail(300,"验证码错误",null);
    }

    @Override
    public Result<?> getUserInfo(String userName) {
        UserInfoVo userInfo = userMapper.findUserByName(userName);
        return Result.success(userInfo);
    }
}




