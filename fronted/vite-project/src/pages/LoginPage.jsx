import { useState } from "react";
import { useNavigate } from "react-router-dom";
import axiosInstance from "../api/axios";

function LoginPage() {
    const navigate = useNavigate();

    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");

        try {
            const response = await axiosInstance.post("/auth/login", {
                email,
                password,
            });

            localStorage.setItem("token", response.data.token);

            navigate("/cms/categories");
        } catch (err) {
            console.log(err);

            setError(
                err.response?.data || "Greška pri loginu."
            );
        }
    };

    return (
        <div>
            <h1>Login</h1>

            <form onSubmit={handleSubmit}>
                <input
                    type="email"
                    placeholder="Email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                />

                <input
                    type="password"
                    placeholder="Lozinka"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />

                <button type="submit">Prijavi se</button>
            </form>

            {error && <p>{error}</p>}
        </div>
    );
}

export default LoginPage;