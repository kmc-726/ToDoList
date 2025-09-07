import React, { useState, useEffect } from "react";
import { fetchBoards } from "../../api/board";
import BoardList from "../../components/board/BoardList.jsx";

const BoardListPage = () => {
    const [boards, setBoards] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const response = await fetchBoards();
                setBoards(response.data.content);
            } catch (err) {
                console.error("게시글 목록을 불러오는 데 실패했습니다.", err);
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, []);

    return (
        <div style={{display:"flex", flexDirection:"column", alignItems:"center"}}>
            <h1 style={{margin : "0px 0px 35px"}}>게시글 목록</h1>
            {loading ? <p>Loading...</p> : <BoardList boards={boards} />}
        </div>
    );
};

export default BoardListPage;