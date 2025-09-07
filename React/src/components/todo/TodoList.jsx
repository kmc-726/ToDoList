import React from "react";
import TodoItem from "./TodoItem";

function TodoList({
                      todos,
                      isEditMode,
                      editedTodos,
                      onEditChange,
                      onToggleEditMode,
                      onUpdate,
                      onDelete,
                      changeClear
                  }) {
    return (
        <ul>
            {todos.map((todo) => (
                <TodoItem
                    key={todo.listId}
                    todo={todo}
                    isEditMode={isEditMode[todo.listId]}
                    editedTodo={editedTodos[todo.listId]}
                    onEditChange={onEditChange}
                    onToggleEditMode={onToggleEditMode}
                    onUpdate={onUpdate}
                    onDelete={onDelete}
                    changeClear={changeClear}
                />
            ))}
        </ul>
    );
}

export default TodoList;
