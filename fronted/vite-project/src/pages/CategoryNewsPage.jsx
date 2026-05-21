import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import axiosInstance from "../api/axios";

function CategoryNewsPage() {
    const { categoryId } = useParams();

    const [news, setNews] = useState([]);

    useEffect(() => {
        loadNewsByCategory();
    }, [categoryId]);

    const loadNewsByCategory = async () => {
        const response = await axiosInstance.get(
            `/public/news/category/${categoryId}`
        );

        setNews(response.data);
    };

    return (
        <div>
            <h1>Vesti po kategoriji</h1>

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

                    <p>Datum: {item.createdAt}</p>

                    <hr />
                </div>
            ))}
        </div>
    );
}

export default CategoryNewsPage;