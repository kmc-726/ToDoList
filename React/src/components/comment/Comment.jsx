import React, {useEffect} from "react";
import './Comment.css'
import LikeButton from "../LikeButton.jsx";

const Comment = ({ comment, likeCount, onDelete, currentUser, refreshData, commentId }) => {
    const isAuthor = currentUser?.loginId === comment?.user?.loginId;

    return (
        <div className={"comment"}>
            <div>
                <p>{comment.user.userName}</p>
                <p>{comment.content}</p>
                <p>{new Date(comment.createdAt).toLocaleString()}</p>
            </div>
            {/*<p>좋아요: {likeCount} 싫어요: {dislikeCount}</p>*/}
            <div style={{display:"flex", justifyContent:"space-between", flexDirection:"column-reverse", alignItems:"flex-end"}}>
                <div style={{display:"flex", gap : "20px", justifyContent :"center", alignItems:"center"}}>
                    <LikeButton color={"red"} entityId={comment.id} likedByMe={comment.likedByMe} entityType="comment" type="LIKE" onLikeChange={refreshData} /> {likeCount}
                    {/*<LikeButton color={"blue"} entityId={boardId} entityType="board" type="DISLIKE" onLikeChange={refreshData} /> {dislikeCount}*/}
                </div>
                {isAuthor && (
                <button className={"comment-delete-btn"} onClick={onDelete}>X</button>
                    )}
            </div>
        </div>
    )
}

export default Comment;
