import { useEffect, useState } from "react";
import axiosInstance from "../api/axios";

function NewsPage() {

    const [news, setNews] = useState([]);
    const [error, setError] = useState("");
    const [categories, setCategories] = useState([]);
    const [title, setTitle] = useState("");
    const [content, setContent] = useState("");
    const [categoryId, setCategoryId] = useState("");
    const [tags, setTags] = useState("");

    const [editingId, setEditingId] = useState(null);
    const [editTitle, setEditTitle] = useState("");
    const [editContent, setEditContent] = useState("");
    const [editCategoryId, setEditCategoryId] = useState("");
    const [editTags, setEditTags] = useState("");

    useEffect(() => {
        loadNews();
        loadCategories();
    }, []);

    const loadNews = async () => {

        try {

            const response =
                await axiosInstance.get("/cms/news");

            setNews(response.data);

        } catch (err) {

            setError("Greška pri učitavanju vesti.");
        }
    };

    const loadCategories = async () => {
        try {
            const response = await axiosInstance.get("/cms/categories");
            setCategories(response.data);
        } catch (err) {
            setError("Greška pri učitavanju kategorija.");
        }
    };

    const handleCreate = async (e) => {
        e.preventDefault();

        try {
            await axiosInstance.post("/cms/news", {
                title,
                content,
                categoryId: Number(categoryId),
                tags: tags.split(",").map((tag) => tag.trim()),
            });

            setTitle("");
            setContent("");
            setCategoryId("");
            setTags("");

            loadNews();

        } catch (err) {
            setError("Greška pri kreiranju vesti.");
        }
    };

    const handleDelete = async (id) => {
        try {
            await axiosInstance.delete(`/cms/news/${id}`);
            loadNews();
        } catch (err) {
            setError("Greška pri brisanju vesti.");
        }
    };

    const startEdit = (item) => {
        setEditingId(item.id);
        setEditTitle(item.title);
        setEditContent(item.content);
        setEditCategoryId(item.categoryId);
        setEditTags(item.tags ? item.tags.join(", ") : "");
    };

    const cancelEdit = () => {
        setEditingId(null);
        setEditTitle("");
        setEditContent("");
        setEditCategoryId("");
        setEditTags("");
    };

    const handleUpdate = async (id) => {
        try {
            await axiosInstance.put(`/cms/news/${id}`, {
                title: editTitle,
                content: editContent,
                categoryId: Number(editCategoryId),
                tags: editTags.split(",").map((tag) => tag.trim()).filter((tag) => tag.length > 0),
            });

            cancelEdit();
            loadNews();

        } catch (err) {
            setError("Greška pri izmeni vesti.");
        }
    };

    return (
        <div>

            <h1>CMS Vesti</h1>

            {error && <p>{error}</p>}

            <form onSubmit={handleCreate}>
                <input
                    type="text"
                    placeholder="Naslov"
                    value={title}
                    onChange={(e) => setTitle(e.target.value)}
                />

                <br />

                <textarea
                    placeholder="Tekst vesti"
                    value={content}
                    onChange={(e) => setContent(e.target.value)}
                />

                <br />

                <select
                    value={categoryId}
                    onChange={(e) => setCategoryId(e.target.value)}
                >
                    <option value="">Izaberi kategoriju</option>

                    {categories.map((category) => (
                        <option key={category.id} value={category.id}>
                            {category.name}
                        </option>
                    ))}
                </select>

                <br />

                <input
                    type="text"
                    placeholder="Tagovi odvojeni zarezom, npr. sport, tenis, novak"
                    value={tags}
                    onChange={(e) => setTags(e.target.value)}
                />

                <br />

                <button type="submit">Dodaj vest</button>
            </form>

            <table border="1">

                <thead>
                <tr>
                    <th>ID</th>
                    <th>Naslov</th>
                    <th>Broj pregleda</th>
                    <th>Kategorija</th>
                    <th>Akcije</th>

                </tr>
                </thead>

                <tbody>

                {news.map((item) => (

                    <tr key={item.id}>
                        <td>{item.id}</td>

                        <td>
                            {editingId === item.id ? (
                                <input
                                    value={editTitle}
                                    onChange={(e) => setEditTitle(e.target.value)}
                                />
                            ) : (
                                item.title
                            )}
                        </td>

                        <td>{item.visitCount}</td>

                        <td>
                            {editingId === item.id ? (
                                <select
                                    value={editCategoryId}
                                    onChange={(e) => setEditCategoryId(e.target.value)}
                                >
                                    {categories.map((category) => (
                                        <option key={category.id} value={category.id}>
                                            {category.name}
                                        </option>
                                    ))}
                                </select>
                            ) : (
                                item.categoryId
                            )}
                        </td>

                        <td>

                            {editingId === item.id ? (
                                <>
                <textarea
                    value={editContent}
                    onChange={(e) => setEditContent(e.target.value)}
                />

                                    <br />

                                    <input
                                        value={editTags}
                                        onChange={(e) => setEditTags(e.target.value)}
                                        placeholder="Tagovi, npr. sport, tenis"
                                    />


                                    <button onClick={() => handleUpdate(item.id)}>
                                        Sačuvaj
                                    </button>

                                    <button onClick={cancelEdit}>
                                        Otkaži
                                    </button>
                                </>
                            ) : (
                                <>
                                    <button onClick={() => startEdit(item)}>
                                        Izmeni
                                    </button>

                                    <button onClick={() => handleDelete(item.id)}>
                                        Obriši
                                    </button>
                                </>
                            )}
                        </td>
                    </tr>

                ))}

                </tbody>

            </table>

        </div>
    );
}

export default NewsPage;