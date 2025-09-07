import React from "react";
import { Link } from "react-router-dom";
import './BoardList.css'

const BoardList = ({ boards }) => {
    return (
        <div className="board-list">
            <div className={"board-write"}>
                <button>글 쓰기</button>
            </div>
            <div className="board-list1">
                <ul>
                    {boards.map((board) => (
                        <li key={board.id}>
                            <Link to={`/boards/${board.id}`}>{board.title}</Link>
                            <div>
                                {new Date(board.createdAt).toLocaleString()}
                            </div>
                        </li>
                    ))}
                </ul>
            </div>
        </div>
    );
};

export default BoardList;