import './TodoPage.css';
import React, { useEffect, useState } from "react";
import {
    fetchTodos,
    createTodo,
    updateTodo,
    deleteTodo
} from "../api/todos";
import Cookies from "js-cookie";
import TodoForm from "../components/todo/TodoForm.jsx";
import TodoList from "../components/todo/TodoList.jsx";
import './TodoPage.css'

function TodoPage() {
    const [isEditMode, setIsEditMode] = useState({});
    const [editedTodos, setEditedTodos] = useState({});
    const [todos, setTodos] = useState([]);
    const [newTodo, setNewTodo] = useState({
        title: "",
        description: "",
        priority: 1,
        dueDate: ""
    });

    const getAccessToken = () => {
        const token = Cookies.get("accessToken");
        console.log("accessToken:", token);
        return token;
    };

    const loadTodos = async () => {
        try {
            const res = await fetchTodos();
            setTodos(res.data);

            const modes = {};
            const edits = {};
            res.data.forEach((todo) => {
                modes[todo.listId] = false;
                edits[todo.listId] = {
                    title: todo.title,
                    description: todo.description,
                    priority: todo.priority,
                    dueDate: todo.dueDate
                };
            });
            setIsEditMode(modes);
            setEditedTodos(edits);
        } catch (err) {
            console.error("불러오기 실패:", err);
        }
    };

    const changeClear = (dueDate) => {
        if (!dueDate) return false;
        return new Date(dueDate) < new Date();
    };

    const handleCreate = async (e) => {
        e.preventDefault();
        try {
            await createTodo(newTodo);
            setNewTodo({ title: "", description: "", priority: 1, dueDate: "" });
            loadTodos();
        } catch (err) {
            console.error("등록 실패:", err);
        }
    };

    const toggleEditMode = (id) => {
        setIsEditMode((prev) => ({ ...prev, [id]: !prev[id] }));
    };

    const handleEditChange = (id, field, value) => {
        setEditedTodos((prev) => ({
            ...prev,
            [id]: {
                ...prev[id],
                [field]: value
            }
        }));
    };

    const handleUpdate = async (id) => {
        try {
            await updateTodo(id, editedTodos[id]);
            toggleEditMode(id);
            loadTodos();
        } catch (err) {
            console.error("수정 실패:", err);
        }
    };

    const handleDelete = async (id) => {
        try {
            await deleteTodo(id);
            loadTodos();
        } catch (err) {
            console.error("삭제 실패:", err);
        }
    };

    useEffect(() => {
        getAccessToken();
        loadTodos();
    }, []);

    return (
        <>
            <h2>📝 나의 투두 리스트</h2>
            <TodoForm
                newTodo={newTodo}
                setNewTodo={setNewTodo}
                onCreate={handleCreate}
            />
            <TodoList
                todos={todos}
                isEditMode={isEditMode}
                editedTodos={editedTodos}
                onEditChange={handleEditChange}
                onToggleEditMode={toggleEditMode}
                onUpdate={handleUpdate}
                onDelete={handleDelete}
                changeClear={changeClear}
            />
        </>
    );
}

export default TodoPage;
