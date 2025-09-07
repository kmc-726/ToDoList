import React, { useEffect, useState } from "react";
import './DeviceRegisterPage.css'
import { requestForToken } from "../api/firebase";
import {
    fetchDevices,
    deleteDevice,
    toggleDevice,
    saveFcmToken
} from "../api/devices";

function DeviceRegisterPage() {
    const [devices, setDevices] = useState([]);
    const [loading, setLoading] = useState(true);
    const [deviceInfo, setDeviceInfo] = useState("");
    const MAX_DEVICES = 2;

    const loadDevices = async () => {
        try {
            const res = await fetchDevices();
            setDevices(res.data);
        } catch (err) {
            console.error("📛 디바이스 불러오기 실패:", err);
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async (id) => {
        try {
            await deleteDevice(id);
            loadDevices();
        } catch (err) {
            console.error("📛 디바이스 삭제 실패:", err);
        }
    };

    const handleToggle = async (id) => {
        try {
            await toggleDevice(id);
            loadDevices();
        } catch (err) {
            console.error("📛 디바이스 상태 변경 실패:", err);
        }
    };

    const handleRegisterDevice = async () => {
        try {
            const token = await requestForToken();
            if (!token) {
                alert("알림 권한이 없거나 FCM 토큰 발급 실패");
                return;
            }
            if (!deviceInfo.trim()) {
                alert("디바이스 이름을 입력해주세요.");
                return;
            }
            await saveFcmToken({ token, deviceInfo });
            setDeviceInfo("");  // 등록 후 입력창 초기화
            loadDevices();
        } catch (err) {
            console.error("📛 디바이스 등록 실패:", err);
        }
    };


    useEffect(() => {
        loadDevices();
    }, []);

    return (
        <div>
            <h2>📱 내 디바이스 목록</h2>

            {loading ? (
                <p>로딩 중...</p>
            ) : (
                <>
                    <ul className={"device-list"}>
                        {devices.map((device, index) => (
                            <li key={device.id} className={"device-item"}>
                                <strong>디바이스 {index + 1}</strong> {" "}
                                {device.deviceInfo ? device.deviceInfo + " - " : ""}
                                {device.enabled ? "✅ 사용 중" : "❌ 비활성화됨"}
                                <button onClick={() => handleToggle(device.id)}>
                                    {device.enabled ? "비활성화" : "활성화"}
                                </button>
                                <button onClick={() => handleDelete(device.id)}>삭제</button>
                            </li>
                        ))}
                    </ul>

                    {devices.length < MAX_DEVICES && (
                        <>
                            <input
                                type="text"
                                placeholder="디바이스 이름 입력 (예: 내 노트북)"
                                value={deviceInfo}
                                onChange={(e) => setDeviceInfo(e.target.value)}
                            />
                            <button onClick={handleRegisterDevice} className={"device-plus"}>디바이스 등록</button>
                        </>
                    )}
                </>
            )}
        </div>
    );
}

export default DeviceRegisterPage;
