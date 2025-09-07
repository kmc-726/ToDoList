import commentAPI from "./axiosInstance";

// 댓글 목록 조회
export const fetchComments = (boardId, page = 0, size = 10) => {
    return commentAPI.get(`/comments/post/${boardId}?page=${page}&size=${size}`);
};

// 댓글 작성
export const createComment = (data) => {
    return commentAPI.post('/comments', data);
};

// 댓글 수정
export const updateComment = (commentId, data) => {
    return commentAPI.put(`/comments/${commentId}`, data);
};

// 댓글 삭제
export const deleteComment = (commentId) => {
    return commentAPI.delete(`/comments/${commentId}`);
};

// 댓글 좋아요 처리
export const likeComment = (commentId, type) => {
    return commentAPI.post(`/likes/comment/${commentId}/${type}`);
};