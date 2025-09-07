import React, {useEffect, useState} from "react";
import { likeBoard } from "../api/board"; // 댓글도 비슷하게 구현
import { likeComment } from "../api/comment"
import { FaThumbsUp, FaRegThumbsUp, FaThumbsDown, FaRegThumbsDown } from "react-icons/fa";


const LikeButton = ({ entityId, color, entityType, type, onLikeChange, likedByMe }) => {
    const [liked, setLiked] = useState(false);

    useEffect(() => {
        setLiked(likedByMe || false);
    }, [likedByMe]);

    const handleLike = async () => {
        try {
            if (entityType === "board") {
                await likeBoard(entityId, type);
            } else {
                await likeComment(entityId, type);
            }
            setLiked(!liked);
            if (onLikeChange) onLikeChange(); // 좋아요 상태 변경 콜백
        } catch (err) {
            console.error("좋아요 처리 실패", err);
        }
    };

    // 채워진 아이콘 vs 비어있는 아이콘 선택
    const renderIcon = () => {
        if (type === "LIKE") {
            return liked ? <FaThumbsUp color="#e53935" /> : <FaRegThumbsUp />;
        } else {
            return liked ? <FaThumbsDown color="#1877f2" /> : <FaRegThumbsDown />;
        }
    };

    return (
        <button
            onClick={handleLike}
            style={{
                color : color,
                background: "none",
                border: "none",
                cursor: "pointer",
                fontSize: "1.5rem",
                padding: "0.3rem"
            }}
        >
            {renderIcon()}
        </button>
    );
};
//
//     return (
//         <button onClick={handleLike}>
//             {liked ? (type === "LIKE" ? "좋아요 취소" : "싫어요 취소") : (type === "LIKE" ? "좋아요" : "싫어요")}
//         </button>
//     );
// };

export default LikeButton;