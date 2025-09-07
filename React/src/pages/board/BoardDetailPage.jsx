import React, { useState, useEffect } from "react";
import {useNavigate, useParams} from "react-router-dom";
import {deleteBoard, fetchBoardDetail} from "../../api/board";
import BoardDetail from "../../components/board/BoardDetail.jsx";
import Comment from "../../components/comment/Comment";
import CommentForm from "../../components/comment/CommentForm";
import {deleteComment, fetchComments} from "../../api/comment";
import {getUser} from "../../api/auth.js";
import comment from "../../components/comment/Comment";

const BoardDetailPage = () => {
    const { boardId } = useParams();
    const [board, setBoard] = useState(null);
    const [comments, setComments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [currentUser, setCurrentUser] = useState(null);
    const navigate = useNavigate();

    // const isAuthor = currentUser?.userId === board.userId;

    // 좋아요/싫어요 변경시 최신 데이터 다시 불러오기
    const refreshData = async () => {
        try {
            const boardResponse = await fetchBoardDetail(boardId);
            setBoard(boardResponse.data);
            console.log(boardResponse.data)
            const commentResponse = await fetchComments(boardId);
            setComments(commentResponse.data.content);
            console.log(commentResponse.data)
        } catch (err) {
            console.error("Error fetching data", err);
        }
    };

    useEffect(() => {
        const fetchUser = async () => {
            try {
                const userResponse = await getUser(); // 로그인 유저 API
                setCurrentUser(userResponse.data); // { id, userName, ... }
                console.log(userResponse.data)
            } catch (err) {
                console.error("유저 정보 불러오기 실패", err);
            }
        };

        fetchUser();
    }, []);

    useEffect(() => {
        const fetchData = async () => {
            try {
                // const userResponse = await getCurrentUser();
                // setCurrentUser(userResponse.data); // 예: { id, userName }

                await refreshData(); // 게시글과 댓글 데이터도 함께
            } catch (err) {
                console.error("유저 정보 또는 게시글 불러오기 실패", err);
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, [boardId]);

    const handleDelete = async () => {
        if (window.confirm("정말 삭제하시겠습니까?")) {
            await deleteBoard(board.id);
            alert("삭제되었습니다.");
            navigate("/boards");
        }
    };

    const handleCommentDelete = async (commentId) => {
        if (window.confirm("정말 삭제하시겠습니까?")) {
            try {
                await deleteComment(commentId);
                alert("삭제되었습니다.");
                await refreshData(); // 댓글 목록 갱신
            } catch (err) {
                console.error("댓글 삭제 실패", err);
                alert("댓글 삭제에 실패했습니다.");
            }
        }
    };

    return (
        <div>
            {loading ? (
                <p>Loading...</p>
            ) : (
                <>
                    <BoardDetail board={board}
                                 likeCount={board.likes || 0}
                                 dislikeCount={board.disLikes || 0}
                                 boardId={boardId}
                                 refreshData={refreshData}
                                 // isAuthor={isAuthor}
                                 currentUser={currentUser} // 현재 로그인한 사용자
                                 onEdit={() => navigate(`/boards/${board.id}/edit`)}
                                 onDelete={handleDelete}
                    />

                    {/* 게시글 좋아요/싫어요 버튼 */}
                    {/*<LikeButton entityId={boardId} entityType="board" type="LIKE" onLikeChange={refreshData} />*/}
                    {/*<LikeButton entityId={boardId} entityType="board" type="DISLIKE" onLikeChange={refreshData} />*/}

                    <CommentForm boardId={boardId} refreshData={refreshData} />

                    <div>
                        {comments.map((comment) => (
                            <div key={comment.id}>
                                <Comment comment={comment}
                                         likeCount={comment.likes || 0}
                                         dislikeCount={comment.dislikes || 0}
                                         boardId={boardId}
                                         refreshData={refreshData}
                                         currentUser={currentUser} // 현재 로그인한 사용자
                                         onDelete={() => handleCommentDelete(comment.id)}
                                />
                                {/* 댓글 좋아요/싫어요 버튼 */}
                                {/*<LikeButton entityId={comment.id} entityType="comment" type="LIKE" onLikeChange={refreshData} />*/}
                                {/*<LikeButton entityId={comment.id} entityType="comment" type="DISLIKE" onLikeChange={refreshData} />*/}
                            </div>
                        ))}
                    </div>
                </>
            )}
        </div>
    );
};

export default BoardDetailPage;
