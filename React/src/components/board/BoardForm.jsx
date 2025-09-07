import React, { useState, useEffect } from "react";
import './BoardForm.css'

const BoardForm = ({ initialData = { title: "", description: "", isPublic: true }, onSubmit, loading }) => {
    const [formData, setFormData] = useState(initialData);

    useEffect(() => {
        if (
            formData.title !== initialData.title ||
            formData.description !== initialData.description ||
            formData.isPublic !== initialData.isPublic
        ){setFormData(initialData)}
    }, [initialData]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({
            ...prev,
            [name]: value,
        }));
    };

    const handleCheckboxChange = (e) => {
        const { name, checked } = e.target;
        setFormData((prev) => ({
            ...prev,
            [name]: checked,
        }))
    }

    const handleSubmit = (e) => {
        e.preventDefault();
        onSubmit(formData);
    };

    return (
        <form onSubmit={handleSubmit} className="board-form">
            <div className="form-group">
                <label htmlFor="title">제목</label>
                <input id="title" type="text" name="title" value={formData.title} onChange={handleChange} required />
            </div>
            <div className="form-group">
                <label htmlFor="description">내용</label>
                <textarea id="description" name="description" value={formData.description} onChange={handleChange} required />
            </div>
            <div className="form-group">
                <label>
                    공개 여부
                    <input type="checkbox" name="isPublic" checked={formData.isPublic} onChange={handleCheckboxChange} />
                </label>
            </div>
            <div style={{display:"flex", justifyContent: "center"}}>
                <button type="submit" disabled={loading}>{loading ? "저장 중..." : "저장"}</button>
            </div>
        </form>
    );
};

export default BoardForm;
