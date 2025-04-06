package com.arrend_system.service.impl;

import com.arrend_system.pojo.entity.User;
import com.arrend_system.mapper.UserMapper;
import com.arrend_system.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
* @author 张明阳
* @description 针对表【user】的数据库操作Service实现
* @createDate 2025-04-06 12:14:15
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

}




