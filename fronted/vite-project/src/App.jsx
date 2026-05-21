import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";

import LoginPage from "./pages/LoginPage";

import CategoriesPage from "./pages/CategoriesPage";
import NewsPage from "./pages/NewsPage";

import HomePage from "./pages/HomePage";
import NewsDetailsPage from "./pages/NewsDetailsPage";
import MostReadPage from "./pages/MostReadPage";
import UsersPage from "./pages/UsersPage";
import SearchPage from "./pages/SearchPage";
import CategoryNewsPage from "./pages/CategoryNewsPage";
import TagNewsPage from "./pages/TagNewsPage";

import CmsLayout from "./layouts/CmsLayout";
import PublicLayout from "./layouts/PublicLayout";



import ProtectedRoute from "./routes/ProtectedRoute";

function App() {

    return (
        <BrowserRouter>

            <Routes>

                <Route path="/" element={<PublicLayout />}>

                    <Route
                        index
                        element={<HomePage />}
                    />

                    <Route
                        path="news/:id"
                        element={<NewsDetailsPage />}
                    />

                    <Route
                        path="most-read"
                        element={<MostReadPage />}
                    />

                    <Route
                        path="search"
                        element={<SearchPage />}
                    />

                    <Route
                        path="category/:categoryId"
                        element={<CategoryNewsPage />}
                    />

                    <Route
                        path="tag/:tagName"
                        element={<TagNewsPage />}
                    />


                </Route>

                <Route
                    path="/login"
                    element={<LoginPage />}
                />




                <Route
                    path="/cms"
                    element={
                        <ProtectedRoute>
                            <CmsLayout />
                        </ProtectedRoute>
                    }
                >

                    <Route path="users"
                           element={<UsersPage />}
                    />

                    <Route
                        path="categories"
                        element={<CategoriesPage />}
                    />

                    <Route
                        path="news"
                        element={<NewsPage />}
                    />

                </Route>

                <Route
                    path="*"
                    element={<Navigate to="/" />}
                />

            </Routes>

        </BrowserRouter>
    );
}

export default App;