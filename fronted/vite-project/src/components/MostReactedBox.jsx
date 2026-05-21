import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import axiosInstance from "../api/axios";

function MostReactedBox() {
    const [news, setNews] = useState([]);

    useEffect(() => {
        loadMostReacted();
    }, []);

    const loadMostReacted = async () => {
        const response = await axiosInstance.get("/public/news/most-reacted");
        setNews(response.data);
    };

    return (
        <div>
            <h3>Najviše reakcija</h3>

            {news.map((item) => (
                <div key={item.id}>
                    <Link to={`/news/${item.id}`}>
                        {item.title}
                    </Link>
                </div>
            ))}
        </div>
    );
}

export default MostReactedBox;