import React from "react";
import './BoardDetail.css'
import LikeButton from "../LikeButton.jsx";
import {FaThumbsDown, FaThumbsUp} from "react-icons/fa";

const BoardDetail = ({ board, likeCount, dislikeCount, refreshData, boardId, onEdit, onDelete, currentUser }) => {
    const isAuthor = currentUser?.loginId === board?.user?.loginId;

    return (
        <div className={"board-detail"}>
            <h2>{board.title}</h2>
            <div>
                <div>작성자: {board.user.userName}</div>
                <div>작성일: {new Date(board.createdAt).toLocaleString()}</div>
            </div>
            <p>{board.description}</p>
            <div style={{display:"flex", gap : "20px", justifyContent :"center", alignItems:"center"}}>
                <LikeButton color={"red"} entityId={boardId} likedByMe={board.likedByMe} entityType="board" type="LIKE" onLikeChange={refreshData} /> {likeCount}
                <LikeButton color={"blue"} entityId={boardId} likedByMe={board.dislikedByMe} entityType="board" type="DISLIKE" onLikeChange={refreshData} /> {dislikeCount}
            </div>
            {isAuthor && (
                <div style={{ marginTop: "20px", display: "flex", gap: "10px", justifyContent: "center" }}>
                    <button onClick={onEdit}>✏️ 수정</button>
                    <button onClick={onDelete}>🗑️ 삭제</button>
                </div>
            )}
        </div>
    );
};

export default BoardDetail;
