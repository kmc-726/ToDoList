import { initializeApp } from "firebase/app";
import { getMessaging, getToken, onMessage } from "firebase/messaging";

const firebaseConfig = {
    // 2단계에서 복사한 설정값 붙여넣기
    apiKey: "AIzaSyAnb-Y23o0HWNlaInCAhzKMGcOp8_5--lI",
    authDomain: "todolist-5c904.firebaseapp.com",
    projectId: "todolist-5c904",
    storageBucket: "todolist-5c904.firebasestorage.app",
    messagingSenderId: "938492716103",
    appId: "1:938492716103:web:d7adacf72a91f8383e3d62",
    measurementId: "G-Q859044HP6"
};

const app = initializeApp(firebaseConfig);
const messaging = getMessaging(app);

export { messaging };

export const requestForToken = async () => {
    try {
        const permission = await Notification.requestPermission();
        if (permission !== "granted") {
            console.warn("🔕 알림 권한이 거부되었습니다.");
            return null;
        }

        const currentToken = await getToken(messaging, { vapidKey: "BNeTb0meW8qRZKEGCzch87p1A3DtaerlZ1iHQPhHy3XajaiJfaqnYqI_fDdVr-1nTnx-ap2kkaUx9HhbYB_f204" });
        if (currentToken) {
            console.log("FCM 토큰:", currentToken);
            return currentToken;
        } else {
            console.log("알림 권한이 없거나 토큰 발급 실패");
            return null;
        }
    } catch (error) {
        console.error("토큰 요청 중 에러:", error);
        return null;
    }
};

export const onMessageListener = () =>
    new Promise((resolve) => {
        onMessage(messaging, (payload) => {
            resolve(payload);
        });
    });
