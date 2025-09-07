import React, {useEffect, useState} from "react";
import { signup } from "../api/auth";
import {messaging, requestForToken} from "../api/firebase.js";
import Signup from "../components/Signup.jsx";

function SignupPage() {
    const [form, setForm] = useState({
        loginId: "",
        userName: "",
        nickName: "",
        email: "",
        phoneNumber: "",
        password: "",
        confirmPassword: "",
        fcmToken: ""
    });

    const [result, setResult] = useState("");

    useEffect(() => {
        const fetchToken = async () => {
            const token = await requestForToken();
            if (token) {
                setForm((prev) => ({ ...prev, fcmToken: token }));
            }
        };

        fetchToken();
    }, []);

    const onChange = (e) => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    const onSubmit = async (e) => {
        e.preventDefault();
        try {
            await signup(form);
            setResult("✅ 회원가입 성공");
        } catch (error) {
            setResult("❌ 실패: " + error.response?.data || error.message);
        }
    };

    useEffect(() => {
        console.log("📦 폼 상태:", form);
    }, [form]);

    return (
        <Signup form={form} onSubmit={onSubmit} onChange={onChange} result={result}/>
    );
}

export default SignupPage;
