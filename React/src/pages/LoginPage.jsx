import React, {useEffect, useState} from "react";
import { login } from "../api/auth";
import { useNavigate } from "react-router-dom";
import Login from "../components/Login.jsx";

function LoginPage() {
    const [loginId, setLoginId] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const navigate = useNavigate();

    useEffect(() => {
        // 컴포넌트 마운트 시 한 번만 권한 요청
        Notification.requestPermission().then(permission => {
            if(permission !== "granted") {
                console.log("알림 권한 거부됨");
            }
        });
    }, []);

    const onSubmit = async (e) => {
        e.preventDefault();
        try {
            await login({ loginId, password }); // 쿠키에 자동 저장
            setError("");

            if (Notification.permission === "granted") {
                new Notification("로그인 성공! 환영합니다.");
            }

            navigate("/todos"); // 로그인 성공 시 투두 페이지로 이동
        } catch (err) {
            setError("❌ 로그인 실패: " + (err.response?.data || err.message));
        }
    };

    return (
        <Login
            onSubmit={onSubmit}
            setLoginId={setLoginId}
            setPassword={setPassword}
            loginId={loginId}
            password={password}
            error={error}
        />
    );
}

export default LoginPage;
