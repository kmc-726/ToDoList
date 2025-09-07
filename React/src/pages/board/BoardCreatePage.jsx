import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { createBoard } from "../../api/board";
import BoardForm from "../../components/board/BoardForm.jsx";

const BoardCreatePage = () => {
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    const handleSubmit = async (data) => {
        setLoading(true);
        try {
            await createBoard(data);
            navigate("/boards"); // 게시글 목록 페이지로 이동
        } catch (err) {
            setError("게시글 생성에 실패했습니다.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={{display:"flex", flexDirection:"column", alignItems:"center"}}>
            <h1 style={{margin : "0px 0px 35px"}}>새 게시글 작성</h1>
            {error && <p>{error}</p>}
            <BoardForm
                initialData={{ title: "", description: "", isPublic: true }}
                onSubmit={handleSubmit} loading={loading} />
        </div>
    );
};

export default BoardCreatePage;
