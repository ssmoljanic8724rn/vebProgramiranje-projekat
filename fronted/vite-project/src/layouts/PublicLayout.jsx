import { Link, Outlet } from "react-router-dom";
import { useEffect, useState } from "react";
import axiosInstance from "../api/axios";
import MostReactedBox from "../components/MostReactedBox";

function PublicLayout() {
    const [categories, setCategories] = useState([]);

    useEffect(() => {
        loadCategories();
    }, []);

    const loadCategories = async () => {
        const response = await axiosInstance.get("/public/categories");
        setCategories(response.data);
    };

    return (
        <div>
            <nav style={{ display: "flex", gap: "20px", marginBottom: "20px" }}>
                <Link to="/">Početna</Link>
                <Link to="/most-read">Najčitanije</Link>
                <Link to="/search">Pretraga</Link>

                {categories.map((category) => (
                    <Link key={category.id} to={`/category/${category.id}`}>
                        {category.name}
                    </Link>
                ))}

                <Link to="/login">Login</Link>
            </nav>

            <div style={{ display: "flex", gap: "30px" }}>
                <main style={{ flex: 1 }}>
                    <Outlet />
                </main>

                <aside style={{ width: "250px" }}>
                    <MostReactedBox />
                </aside>
            </div>
        </div>
    );
}

export default PublicLayout;