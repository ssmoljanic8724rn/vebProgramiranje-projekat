import { useEffect, useState } from "react";
import axiosInstance from "../api/axios";

function CategoriesPage() {
    const [categories, setCategories] = useState([]);
    const [error, setError] = useState("");
    const [name, setName] = useState("");
    const [description, setDescription] = useState("");
    const [editingId, setEditingId] = useState(null);
    const [editName, setEditName] = useState("");
    const [editDescription, setEditDescription] = useState("");

    useEffect(() => {
        loadCategories();
    }, []);

    const loadCategories = async () => {
        try {
            const response = await axiosInstance.get("/cms/categories");
            setCategories(response.data);
        } catch (err) {
            setError("Greška pri učitavanju kategorija.");
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        try {
            await axiosInstance.post("/cms/categories", {
                name,
                description,
            });

            setName("");
            setDescription("");

            loadCategories();

        } catch (err) {
            setError("Greška pri kreiranju kategorije.");
        }
    };

    const handleDelete = async (id) => {

        try {

            await axiosInstance.delete(
                `/cms/categories/${id}`
            );

            loadCategories();

        } catch (err) {

            setError("Greška pri brisanju kategorije.");
        }
    };

    const startEdit = (category) => {
        setEditingId(category.id);
        setEditName(category.name);
        setEditDescription(category.description);
    };

    const cancelEdit = () => {
        setEditingId(null);
        setEditName("");
        setEditDescription("");
    };

    const handleUpdate = async (id) => {
        try {
            await axiosInstance.put(`/cms/categories/${id}`, {
                name: editName,
                description: editDescription,
            });

            cancelEdit();
            loadCategories();

        } catch (err) {
            setError("Greška pri izmeni kategorije.");
        }
    };

    return (
        <div>
            <h1>Kategorije CMS</h1>

            {error && <p>{error}</p>}

            <form onSubmit={handleSubmit}>

                <input
                    type="text"
                    placeholder="Naziv kategorije"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                />

                <input
                    type="text"
                    placeholder="Opis"
                    value={description}
                    onChange={(e) => setDescription(e.target.value)}
                />

                <button type="submit">
                    Dodaj kategoriju
                </button>

            </form>

            <table border="1">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Ime</th>
                    <th>Opis</th>
                    <th>Akcije</th>
                </tr>
                </thead>

                <tbody>
                {categories.map((category) => (
                    <tr key={category.id}>
                        <td>{category.id}</td>

                        <td>
                            {editingId === category.id ? (
                                <input
                                    value={editName}
                                    onChange={(e) => setEditName(e.target.value)}
                                />
                            ) : (
                                category.name
                            )}
                        </td>

                        <td>
                            {editingId === category.id ? (
                                <input
                                    value={editDescription}
                                    onChange={(e) => setEditDescription(e.target.value)}
                                />
                            ) : (
                                category.description
                            )}
                        </td>

                        <td>
                            {editingId === category.id ? (
                                <>
                                    <button onClick={() => handleUpdate(category.id)}>
                                        Sačuvaj
                                    </button>

                                    <button onClick={cancelEdit}>
                                        Otkaži
                                    </button>
                                </>
                            ) : (
                                <>
                                    <button onClick={() => startEdit(category)}>
                                        Izmeni
                                    </button>

                                    <button onClick={() => handleDelete(category.id)}>
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

export default CategoriesPage;