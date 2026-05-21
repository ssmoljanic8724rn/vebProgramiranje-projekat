import { useState } from "react";
import { Link } from "react-router-dom";
import axiosInstance from "../api/axios";

function SearchPage() {

    const [query, setQuery] = useState("");
    const [news, setNews] = useState([]);

    const handleSearch = async (e) => {

        e.preventDefault();

        try {

            const response =
                await axiosInstance.get(
                    `/public/news/search?q=${query}`
                );

            setNews(response.data);

        } catch (err) {

            console.log(err);
        }
    };

    return (
        <div>

            <h1>Pretraga vesti</h1>

            <form onSubmit={handleSearch}>

                <input
                    type="text"
                    placeholder="Pretraži vesti..."
                    value={query}
                    onChange={(e) => setQuery(e.target.value)}
                />

                <button type="submit">
                    Pretraži
                </button>

            </form>

            <hr />

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

export default SearchPage;