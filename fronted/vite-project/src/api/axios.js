import axios from "axios";

const axiosInstance = axios.create({
    baseURL: "http://localhost:8080/api",
});

function getSessionId() {

    let sessionId =
        localStorage.getItem("sessionId");

    if (!sessionId) {

        sessionId = crypto.randomUUID();

        localStorage.setItem(
            "sessionId",
            sessionId
        );
    }

    return sessionId;
}

axiosInstance.interceptors.request.use((config) => {

    const token = localStorage.getItem("token");

    if (token) {
        config.headers.Authorization =
            `Bearer ${token}`;
    }

    config.headers["X-Session-Id"] =
        getSessionId();

    return config;
});

export default axiosInstance;