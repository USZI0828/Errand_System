package com.arrend_system.controller;

import com.arrend_system.common.Result;
import com.arrend_system.mapper.CommentMapper;
import com.arrend_system.pojo.entity.Comment;
import com.arrend_system.pojo.form.add.AddCommentForm;
import com.arrend_system.pojo.form.update.UpdateCommentForm;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentMapper commentMapper;

    //用户评价跑腿任务
    @Operation(summary = "用户评价跑腿任务")
    @PostMapping("/addComment")
    public Result<?> addComment(@RequestBody AddCommentForm addCommentForm) {
        Comment comment = new Comment(
                null,
                addCommentForm.getOrderId(),
                addCommentForm.getDescription(),
                addCommentForm.getUserId(),
                null,
                LocalDateTime.now()
        );
        commentMapper.insert(comment);
        return Result.success("评论成功");
    }

    //用户修改跑腿任务评论
    @Operation(summary = "用户修改评论")
    @PostMapping("/updateComment")
    public Result<?>updateComment(@RequestBody UpdateCommentForm updateCommentForm) {
        UpdateWrapper<Comment> wrapper = new UpdateWrapper<>();
        wrapper.eq("comment_id",updateCommentForm.getCommentId());
        wrapper.set("desciption",updateCommentForm.getDescription());
        commentMapper.update(wrapper);
        return Result.success("修改评论成功");
    }
}
