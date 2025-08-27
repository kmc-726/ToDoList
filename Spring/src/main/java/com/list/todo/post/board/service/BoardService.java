package com.list.todo.post.board.service;

import com.list.todo.post.board.dto.BoardRequest;
import com.list.todo.post.board.dto.BoardResponse;

import java.util.List;

public interface BoardService {
    BoardResponse createBoard(BoardRequest request, String loginId);
    List<BoardResponse> getBoards(int page, int size);
    BoardResponse getBoard(Long id);
    BoardResponse updateBoard(Long id, BoardRequest request, String loginId);
    void deleteBoard(Long id, String loginId);
}
