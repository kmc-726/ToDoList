package com.list.todo.post.board.service;

import com.list.todo.auth.repository.UserRepository;
import com.list.todo.post.board.entity.BoardLikeEntity;
import com.list.todo.post.board.repository.BoardLikeRepository;
import com.list.todo.post.board.repository.BoardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardLikeService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final BoardLikeRepository boardLikeRepository;

    @Transactional
    public String likeBoard(Long boardId, String loginId) {
        return handleLike(boardId, loginId, BoardLikeEntity.LikeType.LIKE);
    }

    @Transactional
    public String dislikeBoard(Long boardId, String loginId) {
        return handleLike(boardId, loginId, BoardLikeEntity.LikeType.DISLIKE);
    }

    private String handleLike(Long boardId, String loginId, BoardLikeEntity.LikeType type) {
        var user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        var board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board not found"));

        var existing = boardLikeRepository.findByUserAndBoard(user, board);

        if (existing.isPresent()) {
            BoardLikeEntity like = existing.get();

            // 이미 같은 타입이면 -> 취소
            if (like.getType() == type) {
                boardLikeRepository.delete(like);
                return type.name().toLowerCase() + " cancelled";
            } else {
                // 타입 변경 (like <-> dislike)
                like.setType(type);
                return "changed to " + type.name().toLowerCase();
            }
        }

        // 처음 누르는 경우
        BoardLikeEntity newLike = new BoardLikeEntity();
        newLike.setUser(user);
        newLike.setBoard(board);
        newLike.setType(type);
        boardLikeRepository.save(newLike);

        return type.name().toLowerCase() + " added";
    }
}