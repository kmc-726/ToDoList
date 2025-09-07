import React, { useState } from "react";
import './Signpu.css'

function Login({ onSubmit, loginId, setLoginId, setPassword, password, error}) {

    return (
        <div>
            <h2>로그인</h2>
            <form className={"auth-form"} onSubmit={onSubmit}>
                <input
                    type="text"
                    placeholder="아이디"
                    value={loginId}
                    onChange={(e) => setLoginId(e.target.value)}
                    required
                />
                <input
                    type="password"
                    placeholder="비밀번호"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                />
                <br />
                <button type="submit">로그인</button>
            </form>
            {error && <p>{error}</p>}
        </div>
    );
}

export default Login;
