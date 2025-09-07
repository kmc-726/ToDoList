package com.list.todo.post.board.service;

import com.list.todo.auth.dto.UserDto;
import com.list.todo.auth.entity.UserEntity;
import com.list.todo.global.exception.BoardException;
import com.list.todo.auth.repository.UserRepository;
import com.list.todo.global.exception.UnauthorizedException;
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
import org.springframework.stereotype.Service;

import java.util.Map;

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

    public BoardResponse updateBoard(Long boardId, BoardRequest request, Long userId) {
        BoardEntity board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardException("게시글을 찾을 수 없습니다."));
        boolean isAuthor = board.getUser().getId().equals(userId);

        if (!isAuthor) {
            throw new UnauthorizedException("작성자만 수정할 수 있습니다.");
        }
        board.setTitle(request.getTitle());
        board.setDescription(request.getDescription());
        boardRepository.save(board);
        return toResponse(board);
    }

    public void deleteBoard(Long boardId, Long userId, boolean isAdmin) {
        BoardEntity board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardException("게시글을 찾을 수 없습니다."));

        // 작성자 본인 또는 관리자 권한 확인
        boolean isAuthor = board.getUser().getId().equals(userId);

        if (!isAuthor && !isAdmin) {
            throw new UnauthorizedException("작성자 또는 관리자만 삭제할 수 있습니다.");
        }

        board.setDeleted(true); // soft delete
        boardRepository.save(board);
    }

    public BoardResponse getBoard(Long boardId, String loginId) {
        BoardEntity board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardException("게시글을 찾을 수 없습니다."));
        return toResponse(board, loginId);
    }

    public Page<BoardResponse> getBoardList(int page, int size, String loginId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<BoardEntity> boards = boardRepository.findByDeletedFalseOrderByCreatedAtDesc(pageable);
        return boards.map(board -> toResponse(board, loginId));
    }

    private BoardResponse toResponse(BoardEntity board) {
        BoardResponse res = new BoardResponse();
        res.setId(board.getId());

        UserEntity userEntity = board.getUser();
        UserDto userDto = new UserDto();
        userDto.setUserName(userEntity.getUserName());
        userDto.setLoginId(userEntity.getLoginId());
        // 필요한 UserDto 필드들 채우기

        res.setUser(userDto);

        res.setTitle(board.getTitle());
        res.setDescription(board.getDescription());
        res.setCreatedAt(board.getCreatedAt());
        res.setUpdatedAt(board.getUpdatedAt());

        Map<String, Integer> likeCounts = likeService.getLikeCount(board.getId(), "BOARD");
        res.setLikes(likeCounts.getOrDefault("like", 0));
        res.setDisLikes(likeCounts.getOrDefault("dislike", 0));

        return res;
    }

    private BoardResponse toResponse(BoardEntity board, String loginId) {
        BoardResponse res = new BoardResponse();
        res.setId(board.getId());

        UserEntity userEntity = board.getUser();
        UserDto userDto = new UserDto();
        userDto.setUserName(userEntity.getUserName());
        userDto.setLoginId(userEntity.getLoginId());
        // 필요한 UserDto 필드들 채우기

        res.setUser(userDto);

        res.setTitle(board.getTitle());
        res.setDescription(board.getDescription());
        res.setCreatedAt(board.getCreatedAt());
        res.setUpdatedAt(board.getUpdatedAt());

        Map<String, Integer> likeCounts = likeService.getLikeCount(board.getId(), "BOARD");
        res.setLikes(likeCounts.getOrDefault("like", 0));
        res.setDisLikes(likeCounts.getOrDefault("dislike", 0));

        if (loginId != null) {
            boolean liked = likeService.isLikedByUser(board.getId(), loginId, "BOARD", LikeEntity.LikeType.LIKE);
            boolean disliked = likeService.isLikedByUser(board.getId(), loginId, "BOARD", LikeEntity.LikeType.DISLIKE);
            res.setLikedByMe(liked);
            res.setDislikedByMe(disliked);
        } else {
            res.setLikedByMe(false);
            res.setDislikedByMe(false);
        }

        return res;
    }

    // 좋아요/싫어요 처리
//    public String handleLike(Long boardId, String loginId, LikeEntity.LikeType type) {
//        return likeService.handleLike(boardId, loginId, type, "BOARD");
//    }
}