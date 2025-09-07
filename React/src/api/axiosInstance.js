import axios from "axios";
import Cookies from "js-cookie";

const api = axios.create({
    baseURL: "http://localhost:8080",
    withCredentials: true,
    headers: {
        "Content-Type": "application/json",
    },
});

// AccessToken 자동으로 헤더에 넣기
api.interceptors.request.use(
    (config) => {
        const accessToken = Cookies.get("accessToken");
        if (accessToken) {
            config.headers.Authorization = `Bearer ${accessToken}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

// AccessToken 만료 시 RefreshToken으로 재발급 시도
api.interceptors.response.use(
    (response) => response,
    async (error) => {
        const originalRequest = error.config;

        console.warn("⚠️ 응답 에러 발생:", error.response?.status);
        if (!originalRequest) {
            console.error("❌ error.config 없음 (요청 재시도 불가능)");
            return Promise.reject(error);
        }


        if (error.response?.status === 401 && !originalRequest._retry) {
            originalRequest._retry = true;

            try {
                // refresh 요청
                await axios.post("/auth/refresh", {}, {
                    withCredentials: true,
                });

                // 새로운 accessToken 쿠키로 재설정됨
                return api(originalRequest); // 원래 요청 재시도
            } catch (refreshError) {
                console.error("Refresh 실패", refreshError);
                window.location.href = "/login"; // or logout 처리
                return Promise.reject(refreshError);
            }
        }

        return Promise.reject(error);
    }
);

export default api;