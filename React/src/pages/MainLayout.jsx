// src/layout/MainLayout.jsx
import { Outlet, Link } from "react-router-dom";
import "./MainLayout.css"; // 스타일은 따로 작성

const MainLayout = () => {
    return (
        <div className="layout">
            <nav className="sidebar">
                <h2>목록</h2>
                <ul>
                    <li><Link to="/todos">Todos</Link></li>
                    <li><Link to="/boards">Boards</Link></li>
                    <li><Link to="/devices">Devices</Link></li>
                </ul>
            </nav>
            <main className="content">
                <Outlet />
            </main>
        </div>
    );
};

export default MainLayout;
