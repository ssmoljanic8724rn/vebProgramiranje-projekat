import { useEffect, useState } from "react";
import axiosInstance from "../api/axios";

function HomePage() {
    const [news, setNews] = useState([]);
    const [error, setError] = useState("");

    useEffect(() => {
        loadLatestNews();
    }, []);

    const loadLatestNews = async () => {
        try {
            const response = await axiosInstance.get("/public/news/latest");
            setNews(response.data);
        } catch (err) {
            setError("Greška pri učitavanju vesti.");
        }
    };

    return (
        <div>
            <h1>RAF Novosti</h1>

            {error && <p>{error}</p>}

            {news.map((item) => (
                <div key={item.id}>
                    <h2>{item.title}</h2>

                    <p>
                        {item.content.length > 150
                            ? item.content.substring(0, 150) + "..."
                            : item.content}
                    </p>

                    <p>Datum: {item.createdAt}</p>

                    <a href={`/news/${item.id}`}>
                        Pročitaj više
                    </a>

                    <hr />
                </div>
            ))}
        </div>
    );
}

export default HomePage;