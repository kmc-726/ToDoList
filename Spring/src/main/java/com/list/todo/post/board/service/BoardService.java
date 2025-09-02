package com.list.todo.post.board.service;

import com.list.todo.auth.entity.UserEntity;
import com.list.todo.auth.repository.UserRepository;
import com.list.todo.post.board.dto.BoardRequest;
import com.list.todo.post.board.dto.BoardResponse;
import com.list.todo.post.board.entity.BoardEntity;
import com.list.todo.post.board.repository.BoardRepository;
import com.list.todo.post.shared.entity.LikeEntity;
import com.list.todo.post.shared.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final LikeService likeService;

    public BoardResponse createBoard(BoardRequest request, UserEntity user) {

        BoardEntity board = new BoardEntity();
        board.setTitle(request.getTitle());
        board.setDescription(request.getDescription());
        board.setUser(user);

        boardRepository.save(board);
        return toResponse(board);
    }

    public BoardResponse updateBoard(Long boardId, BoardRequest request, String loginId) {
        BoardEntity board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board not found"));
        board.setTitle(request.getTitle());
        board.setDescription(request.getDescription());
        boardRepository.save(board);
        return toResponse(board);
    }

    public void deleteBoard(Long boardId, String loginId) {
        BoardEntity board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board not found"));
        board.setDeleted(true);
        boardRepository.save(board);
    }

    public BoardResponse getBoard(Long boardId) {
        BoardEntity board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board not found"));
        return toResponse(board);
    }

    public Page<BoardResponse> getBoardList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<BoardEntity> boards = boardRepository.findAll(pageable);
        return boards.map(this::toResponse);
    }

    private BoardResponse toResponse(BoardEntity board) {
        BoardResponse res = new BoardResponse();
        res.setId(board.getId());
        res.setUserName(board.getUser().getUserName());
        res.setTitle(board.getTitle());
        res.setDescription(board.getDescription());
        res.setCreatedAt(board.getCreatedAt());
        res.setUpdatedAt(board.getUpdatedAt());
        Map<String, Integer> likeCounts = likeService.getLikeCount(board.getId(), "BOARD");
        res.setLikes(likeCounts.getOrDefault("like", 0));
        res.setDisLikes(likeCounts.getOrDefault("dislike", 0));
        // 추가적인 속성
        return res;
    }

    // 좋아요/싫어요 처리
    public String handleLike(Long boardId, String loginId, LikeEntity.LikeType type) {
        return likeService.handleLike(boardId, loginId, type, "BOARD");
    }
}