package com.list.todo.post.board.service;

import com.list.todo.auth.entity.UserEntity;
import com.list.todo.auth.repository.UserRepository;
import com.list.todo.post.board.dto.BoardRequest;
import com.list.todo.post.board.dto.BoardResponse;
import com.list.todo.post.board.entity.BoardEntity;
import com.list.todo.post.board.entity.BoardLikeEntity;
import com.list.todo.post.board.repository.BoardLikeRepository;
import com.list.todo.post.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService{

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final BoardLikeRepository boardLikeRepository;

    @Override
    public BoardResponse createBoard(BoardRequest request, String loginId) {
        UserEntity user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        BoardEntity board = new BoardEntity();
        board.setTitle(request.getTitle());
        board.setDescription(request.getDescription());
        board.setUser(user);
        board.setPublic(Boolean.TRUE.equals(request.getIsPublic()));

        BoardEntity saved = boardRepository.save(board);
        return toResponse(saved);
    }

    @Override
    public List<BoardResponse> getBoards(int page, int size) {
        Pageable pageable = (Pageable) PageRequest.of(page, size);
        return boardRepository.findByDeletedFalseOrderByCreatedAtDesc(pageable)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BoardResponse getBoard(Long id) {
        BoardEntity board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Board not found"));

        if (board.isDeleted()) throw new RuntimeException("This board is deleted");
        return toResponse(board);
    }

    @Override
    public BoardResponse updateBoard(Long id, BoardRequest request, String loginId) {
        BoardEntity board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Board not found"));

        if (!board.getUser().getLoginId().equals(loginId)) {
            throw new RuntimeException("You can only edit your own posts");
        }

        board.setTitle(request.getTitle());
        board.setDescription(request.getDescription());
        board.setPublic(Boolean.TRUE.equals(request.getIsPublic()));
        return toResponse(boardRepository.save(board));
    }

    @Override
    public void deleteBoard(Long id, String loginId) {
        BoardEntity board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Board not found"));

        if (!board.getUser().getLoginId().equals(loginId)) {
            throw new RuntimeException("You can only delete your own posts");
        }

        board.setDeleted(true);
        boardRepository.save(board);
    }

    private BoardResponse toResponse(BoardEntity board) {
        BoardResponse res = new BoardResponse();
        res.setId(board.getId());
        res.setTitle(board.getTitle());
        res.setDescription(board.getDescription());
        res.setCreatedAt(board.getCreatedAt());
        res.setUpdatedAt(board.getUpdatedAt());
        res.setUserName(board.getUser().getUserName());
        res.setIsPublic(board.isPublic());
        res.setLikes(boardLikeRepository.countByBoardAndType(board, BoardLikeEntity.LikeType.LIKE).intValue());
        res.setDisLikes(boardLikeRepository.countByBoardAndType(board, BoardLikeEntity.LikeType.DISLIKE).intValue());

        res.setIsPublic(board.isPublic());
        return res;
    }
}
