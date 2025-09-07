import boardAPI from "./axiosInstance";

// 게시글 목록 조회
export const fetchBoards = (page = 0, size = 10) => {
    return boardAPI.get(`/boards?page=${page}&size=${size}`);
};

// 게시글 상세 조회
export const fetchBoardDetail = (boardId) => {
    return boardAPI.get(`/boards/${boardId}`);
};

// 게시글 작성
export const createBoard = (data) => {
    return boardAPI.post('/boards', data);
};

// 게시글 수정
export const updateBoard = (boardId, data) => {
    return boardAPI.put(`/boards/${boardId}`, data);
};

// 게시글 삭제
export const deleteBoard = (boardId) => {
    return boardAPI.delete(`/boards/${boardId}`);
};

// 게시글 좋아요 처리
export const likeBoard = (boardId, type) => {
    return boardAPI.post(`/likes/board/${boardId}/${type}`);
};
