importScripts("https://www.gstatic.com/firebasejs/9.22.1/firebase-app-compat.js");
importScripts("https://www.gstatic.com/firebasejs/9.22.1/firebase-messaging-compat.js");

firebase.initializeApp({
    apiKey: "AIzaSyAnb-Y23o0HWNlaInCAhzKMGcOp8_5--lI",
    authDomain: "todolist-5c904.firebaseapp.com",
    projectId: "todolist-5c904",
    storageBucket: "todolist-5c904.firebasestorage.app",
    messagingSenderId: "938492716103",
    appId: "1:938492716103:web:d7adacf72a91f8383e3d62"
});

const messaging = firebase.messaging();

messaging.onBackgroundMessage(function(payload) {
    console.log("[firebase-messaging-sw.js] Received background message ", payload);
    const notificationTitle = payload.notification.title;
    const notificationOptions = {
        body: payload.notification.body,
    };

    self.registration.showNotification(notificationTitle, notificationOptions);
});
