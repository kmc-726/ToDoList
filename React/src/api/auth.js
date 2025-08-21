import axios from "axios";

const API = axios.create({
    baseURL: "http://localhost:8080/auth",
    headers: {
        "Content-Type": "application/json"
    },
    withCredentials: true
});

export const signup = (signupData) => API.post("/signup", signupData);
export const login = (loginData) => API.post("/login", loginData);
