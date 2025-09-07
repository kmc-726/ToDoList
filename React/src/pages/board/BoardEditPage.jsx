import React, { useState, useEffect } from "react";
import {useParams, useNavigate} from "react-router-dom";
import { fetchBoardDetail, updateBoard } from "../../api/board.js";
import BoardForm from "../../components/board/BoardForm.jsx";

const BoardEditPage = () => {
    const { boardId } = useParams();
    const navigate = useNavigate();
    const [board, setBoard] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchBoard = async () => {
            try {
                const response = await fetchBoardDetail(boardId);
                setBoard(response.data);
            } catch (err) {
                setError("게시글을 불러오는 데 실패했습니다.");
            } finally {
                setLoading(false);
            }
        };
        fetchBoard();
    }, [boardId]);

    const handleSubmit = async (data) => {
        setLoading(true);
        try {
            await updateBoard(boardId, data);
            navigate(`/boards/${boardId}`); // 수정 후 상세 페이지로 이동
        } catch (err) {
            setError("게시글 수정에 실패했습니다.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div>
            <h1>게시글 수정</h1>
            {loading ? <p>Loading...</p> : <BoardForm initialData={board} onSubmit={handleSubmit} loading={loading} />}
            {error && <p>{error}</p>}
        </div>
    );
};

export default BoardEditPage;
