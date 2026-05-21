import { Link, Outlet, useNavigate } from "react-router-dom";

function CmsLayout() {

    const navigate = useNavigate();

    const logout = () => {

        localStorage.removeItem("token");

        navigate("/login");
    };

    return (
        <div>

            <nav
                style={{
                    display: "flex",
                    gap: "20px",
                    marginBottom: "20px",
                }}
            >

                <Link to="/cms/categories">
                    Kategorije
                </Link>

                <Link to="/cms/news">
                    Vesti
                </Link>

                <Link to="/cms/users">
                    Korisnici
                </Link>

                <button onClick={logout}>
                    Logout
                </button>

            </nav>

            <Outlet />

        </div>
    );
}

export default CmsLayout;