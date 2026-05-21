import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import axiosInstance from "../api/axios";

function TagNewsPage() {

    const { tagName } = useParams();

    const [news, setNews] = useState([]);

    useEffect(() => {
        loadNewsByTag();
    }, [tagName]);

    const loadNewsByTag = async () => {

        try {

            const response =
                await axiosInstance.get(
                    `/public/news/tag/${tagName}`
                );

            setNews(response.data);

        } catch (err) {

            console.log(err);
        }
    };

    return (
        <div>

            <h1>Tag: {tagName}</h1>

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

                    <hr />

                </div>

            ))}

        </div>
    );
}

export default TagNewsPage;