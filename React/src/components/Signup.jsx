import React from "react";
import './Signpu.css'

function Signup({onSubmit, form, result, onChange}){
    return(
        <div>
            <h2>회원가입</h2>
            <form className={"auth-form"} onSubmit={onSubmit}>
                {Object.keys(form).filter(k => k !== "fcmToken").map((key) => (
                    <input
                        key={key}
                        name={key}
                        placeholder={key}
                        value={form[key]}
                        onChange={onChange}
                        type={key.toLowerCase().includes("password") ? "password" : "text"}
                        required
                    />
                ))}
                <br />
                <button type="submit">회원가입</button>
            </form>
            <p>{result}</p>
        </div>
    )
}

export default Signup;