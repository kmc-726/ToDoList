import React, { useEffect } from "react";
import { requestForToken, onMessageListener } from "../hook/useFcm.js";

function Firebase() {
    useEffect(() => {
        requestForToken().then((token) => {
            if (token) {
                // TODO: 서버에 토큰 저장 API 호출
                console.log("서버에 토큰 저장:", token);
            }
        });

        onMessageListener()
            .then((payload) => {
                alert("푸시 알림 받음: " + payload.notification.title);
            })
            .catch((err) => console.log("알림 수신 에러:", err));
    }, []);

    return <div>FCM 테스트</div>;
}

export default Firebase;
