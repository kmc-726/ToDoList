import axios from "axios";
import Cookies from "js-cookie";
import todosAPI from "./axiosInstance";

export const fetchTodos = () => todosAPI.get("/todos");

// 새 투두 추가
export const createTodo = (todo) => todosAPI.post("/todos/psing", todo);

// 투두 수정
export const updateTodo = (listId, todo) => todosAPI.put(`/todos/updateTodo/${listId}`, todo);

// 투두 삭제
export const deleteTodo = (listId) => todosAPI.delete(`/todos/${listId}`);