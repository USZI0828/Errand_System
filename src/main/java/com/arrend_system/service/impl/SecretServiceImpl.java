package com.arrend_system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.arrend_system.pojo.entity.Secret;
import com.arrend_system.service.SecretService;
import com.arrend_system.mapper.SecretMapper;
import org.springframework.stereotype.Service;

/**
* @author 张明阳
* @description 针对表【secret】的数据库操作Service实现
* @createDate 2025-04-08 08:59:32
*/
@Service
public class SecretServiceImpl extends ServiceImpl<SecretMapper, Secret>
    implements SecretService{

}




