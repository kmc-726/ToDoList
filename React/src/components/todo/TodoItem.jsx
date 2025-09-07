import React from "react";

function TodoItem({
                      todo,
                      isEditMode,
                      editedTodo,
                      onEditChange,
                      onToggleEditMode,
                      onUpdate,
                      onDelete,
    changeClear
                  }) {
    const { listId, title, description, priority, dueDate } = todo;

    return (
        <li className="todo-card">
        {isEditMode ? (
                <>
                    <input
                        type="text"
                        value={editedTodo?.title || ""}
                        onChange={(e) => onEditChange(listId, "title", e.target.value)}
                    />
                    <input
                        type="text"
                        value={editedTodo?.description || ""}
                        onChange={(e) => onEditChange(listId, "description", e.target.value)}
                    />
                    <input
                        type="datetime-local"
                        value={editedTodo?.dueDate || ""}
                        onChange={(e) => onEditChange(listId, "dueDate", e.target.value)}
                    />
                    <input
                        type="number"
                        min="1"
                        max="5"
                        value={editedTodo?.priority || 1}
                        onChange={(e) => onEditChange(listId, "priority", parseInt(e.target.value))}
                    />
                    <button className="btn save" onClick={() => onUpdate(listId)}>저장</button>
                    <button className="btn cancel" onClick={() => onToggleEditMode(listId)}>취소</button>
                </>
            ) : (
                <div className={changeClear(dueDate) ? "todo-card-clear" : "todo-card-non-clear"}>
                    <strong>{title}</strong> - {description} (우선순위: {priority})
                    <p>마감일: {dueDate ? new Date(dueDate).toLocaleString() : "없음"}</p>
                    <button className="btn edit" onClick={() => onToggleEditMode(listId)}>수정</button>
                    <button className="btn delete" onClick={() => onDelete(listId)}>삭제</button>
                </div>
            )}
        </li>
    );
}

export default TodoItem;
