import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import axiosInstance from "../api/axios";

function MostReadPage() {
    const [news, setNews] = useState([]);

    useEffect(() => {
        loadMostRead();
    }, []);

    const loadMostRead = async () => {
        const response = await axiosInstance.get("/public/news/most-read");
        setNews(response.data);
    };

    return (
        <div>
            <h1>Najčitanije vesti</h1>

            {news.map((item) => (
                <div key={item.id}>
                    <h2>
                        <Link to={`/news/${item.id}`}>
                            {item.title}
                        </Link>
                    </h2>

                    <p>
                        {item.content.length > 150
                            ? item.content.substring(0, 150) + "..."
                            : item.content}
                    </p>

                    <p>Pregledi: {item.visitCount}</p>

                    <hr />
                </div>
            ))}
        </div>
    );
}

export default MostReadPage;