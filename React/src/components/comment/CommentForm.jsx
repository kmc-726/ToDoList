import React, { useState } from "react";
import { createComment } from "../../api/comment";
import './Comment.css'

const CommentForm = ({ boardId, refreshData }) => {
    const [comment, setComment] = useState("");
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        try {
            await createComment({ content: comment, boardId });
            setComment(""); // 제출 후 폼 초기화
            refreshData({ boardId });
        } catch (err) {
            console.error("댓글 작성 실패", err);
        } finally {
            setLoading(false);
        }
    };

    return (
        <form onSubmit={handleSubmit} className="comment-form">
            <textarea
                value={comment}
                onChange={(e) => setComment(e.target.value)}
                placeholder="댓글을 입력하세요"
                required
            />
            <button type="submit" disabled={loading}>
                {loading ? "댓글 작성 중..." : "댓글 작성"}
            </button>
        </form>
    );
};

export default CommentForm;
