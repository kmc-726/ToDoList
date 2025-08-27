package com.list.todo.post.comment.service;

import com.list.todo.auth.entity.UserEntity;
import com.list.todo.auth.repository.UserRepository;
import com.list.todo.post.comment.entity.CommentEntity;
import com.list.todo.post.comment.entity.CommentLikeEntity;
import com.list.todo.post.comment.repository.CommentLikeRepository;
import com.list.todo.post.comment.repository.CommentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentLikeService {

    private final UserRepository userRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public String likeComment(Long commentId,String loginId){
        return handleLikeComment(commentId, loginId, CommentLikeEntity.LikeType.LIKE);
    }

    @Transactional
    public String dislikeComment(Long commentId, String loginId){
        return handleLikeComment(commentId, loginId, CommentLikeEntity.LikeType.DISLIKE);
    }

    private String handleLikeComment(Long commentId, String loginId, CommentLikeEntity.LikeType type){
        UserEntity user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        Optional<CommentLikeEntity> existing = commentLikeRepository.findByUserAndComment(user, comment);

        if (existing.isPresent()) {
            CommentLikeEntity commentLike = existing.get();
            if (commentLike.getType() == type){
                commentLikeRepository.delete(commentLike);
                return type.name().toLowerCase() + "cancelled";
            } else {
                commentLike.setType(type);
                return "changed to " + type.name().toLowerCase();
            }
        }

        CommentLikeEntity newCommentLike = new CommentLikeEntity();
        newCommentLike.setUser(user);
        newCommentLike.setComment(comment);
        newCommentLike.setType(type);
        commentLikeRepository.save(newCommentLike);

        return type.name().toLowerCase() + "added";
    }
}
