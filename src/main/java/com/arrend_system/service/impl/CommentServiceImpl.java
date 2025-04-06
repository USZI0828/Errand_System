package com.arrend_system.service.impl;

import com.arrend_system.pojo.entity.Comment;
import com.arrend_system.mapper.CommentMapper;
import com.arrend_system.service.CommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
* @author 张明阳
* @description 针对表【comment】的数据库操作Service实现
* @createDate 2025-04-06 12:14:15
*/
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>
    implements CommentService {

}




