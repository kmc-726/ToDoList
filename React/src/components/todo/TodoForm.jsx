import React from "react";

function TodoForm({ newTodo, setNewTodo, onCreate }) {
    return (
        <form className="todo-form" onSubmit={onCreate}>
        <input
                type="text"
                placeholder="제목"
                value={newTodo.title}
                onChange={(e) => setNewTodo({ ...newTodo, title: e.target.value })}
                required
            />
            <input
                type="text"
                placeholder="내용"
                value={newTodo.description}
                onChange={(e) => setNewTodo({ ...newTodo, description: e.target.value })}
                required
            />
            <input
                type="datetime-local"
                value={newTodo.dueDate}
                onChange={(e) => setNewTodo({ ...newTodo, dueDate: e.target.value })}
            />
            <input
                type="number"
                min="1"
                max="5"
                value={newTodo.priority}
                onChange={(e) => setNewTodo({ ...newTodo, priority: e.target.value })}
            />
            <button type="submit">추가</button>
        </form>
    );
}

export default TodoForm;
