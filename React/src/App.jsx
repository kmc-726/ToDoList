// App.jsx
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import SignupPage from "./pages/SignupPage";
import DeviceRegisterPage from "./pages/DeviceRegisterPage";
import BoardListPage from "./pages/board/BoardListPage";
import BoardDetailPage from "./pages/board/BoardDetailPage";
import BoardCreatePage from "./pages/board/BoardCreatePage";
import BoardEditPage from "./pages/board/BoardEditPage";
import TodoPage from "./pages/TodosPage.jsx";
import MainLayout from "./pages/MainLayout.jsx"
import "./App.css";

function App() {
    return (
        <Router>
            <Routes>
                {/* 로그인, 회원가입은 레이아웃 없이 */}
                <Route path="/" element={<Navigate to="/login" />} />
                <Route path="/login" element={<LoginPage />} />
                <Route path="/signup" element={<SignupPage />} />

                {/* 공통 레이아웃을 사용하는 라우트들 */}
                <Route element={<MainLayout />}>
                    <Route path="/todos" element={<TodoPage />} />
                    <Route path="/devices" element={<DeviceRegisterPage />} />
                    <Route path="/boards" element={<BoardListPage />} />
                    <Route path="/boards/create" element={<BoardCreatePage />} />
                    <Route path="/boards/:boardId" element={<BoardDetailPage />} />
                    <Route path="/boards/:boardId/edit" element={<BoardEditPage />} />
                </Route>

                <Route path="*" element={<h2>404 - 페이지를 찾을 수 없습니다</h2>} />
            </Routes>
        </Router>
    );
}

export default App;
