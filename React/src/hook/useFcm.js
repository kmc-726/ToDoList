import {useEffect} from "react";
import {onMessageListener, requestForToken} from "../api/firebase.js";
import axios from "axios";

const useFcm = () => {
    useEffect(() => {
        requestForToken().then((token) => {
            if (token) {
                // TODO: 서버에 토큰 저장 API 호출
                console.log("서버에 토큰 저장:", token);

                axios.post("/api/save-fcm-token", {token})
                    .then(() => console.log("토큰저장완료"))
                    .catch((err) => console.log(err));
            }
        });

        onMessageListener()
            .then((payload) => {
                alert("푸시 알림 받음: " + payload.notification.title);
            })
            .catch((err) => console.log("알림 수신 에러:", err));
    }, []);
}

export default useFcm;