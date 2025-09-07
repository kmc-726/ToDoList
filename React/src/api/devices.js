import deviceAPI from "./axiosInstance";

export const fetchDevices = () => deviceAPI.get("/devices");
export const deleteDevice = (id) => deviceAPI.delete(`/devices/${id}`);
export const toggleDevice = (id) => deviceAPI.patch(`/devices/${id}/toggle`);
export const saveFcmToken = (data) => deviceAPI.post("/devices/save-fcm-token", data);