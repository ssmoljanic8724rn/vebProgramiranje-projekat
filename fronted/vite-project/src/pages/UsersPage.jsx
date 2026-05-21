import { useEffect, useState } from "react";
import axiosInstance from "../api/axios";

function UsersPage() {
    const [users, setUsers] = useState([]);
    const [error, setError] = useState("");

    const [email, setEmail] = useState("");
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [role, setRole] = useState("CONTENT_CREATOR");
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");

    const [editingId, setEditingId] = useState(null);
    const [editEmail, setEditEmail] = useState("");
    const [editFirstName, setEditFirstName] = useState("");
    const [editLastName, setEditLastName] = useState("");
    const [editRole, setEditRole] = useState("CONTENT_CREATOR");

    useEffect(() => {
        loadUsers();
    }, []);

    const loadUsers = async () => {
        try {
            const response = await axiosInstance.get("/cms/users");
            setUsers(response.data);
        } catch (err) {
            setError("Nemate dozvolu ili je došlo do greške.");
        }
    };

    const handleCreate = async (e) => {
        e.preventDefault();

        try {
            await axiosInstance.post("/cms/users", {
                email,
                firstName,
                lastName,
                role,
                password,
                confirmPassword,
            });

            setEmail("");
            setFirstName("");
            setLastName("");
            setRole("CONTENT_CREATOR");
            setPassword("");
            setConfirmPassword("");

            loadUsers();
        } catch (err) {
            setError(err.response?.data || "Greška pri kreiranju korisnika.");
        }
    };

    const toggleActive = async (user) => {
        try {
            await axiosInstance.patch(
                `/cms/users/${user.id}/active?active=${!user.active}`
            );

            loadUsers();
        } catch (err) {
            setError(err.response?.data || "Greška pri promeni statusa.");
        }
    };

    const startEdit = (user) => {
        setEditingId(user.id);
        setEditEmail(user.email);
        setEditFirstName(user.firstName);
        setEditLastName(user.lastName);
        setEditRole(user.role);
    };

    const cancelEdit = () => {
        setEditingId(null);
    };

    const handleUpdate = async (id) => {
        try {
            await axiosInstance.put(`/cms/users/${id}`, {
                email: editEmail,
                firstName: editFirstName,
                lastName: editLastName,
                role: editRole,
            });

            cancelEdit();
            loadUsers();
        } catch (err) {
            setError(err.response?.data || "Greška pri izmeni korisnika.");
        }
    };

    return (
        <div>

            <h1>Korisnici</h1>

            {error && <p>{error}</p>}

            <form
                onSubmit={handleCreate}
                style={{
                    marginBottom: "20px",
                }}
            >

                <input
                    placeholder="Email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                />

                <input
                    placeholder="Ime"
                    value={firstName}
                    onChange={(e) => setFirstName(e.target.value)}
                />

                <input
                    placeholder="Prezime"
                    value={lastName}
                    onChange={(e) => setLastName(e.target.value)}
                />

                <select
                    value={role}
                    onChange={(e) => setRole(e.target.value)}
                >
                    <option value="CONTENT_CREATOR">
                        Content creator
                    </option>

                    <option value="ADMIN">
                        Admin
                    </option>
                </select>

                <input
                    type="password"
                    placeholder="Lozinka"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />

                <input
                    type="password"
                    placeholder="Potvrda lozinke"
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                />

                <button type="submit">
                    Dodaj korisnika
                </button>

            </form>

            <hr />

            <table border="1">

                <thead>

                <tr>
                    <th>ID</th>
                    <th>Ime i prezime</th>
                    <th>Email</th>
                    <th>Tip</th>
                    <th>Aktivan</th>
                    <th>Akcije</th>
                </tr>

                </thead>

                <tbody>

                {users.map((user) => (

                    <tr key={user.id}>

                        <td>{user.id}</td>

                        <td>

                            {editingId === user.id ? (

                                <div>

                                    <input
                                        value={editFirstName}
                                        onChange={(e) =>
                                            setEditFirstName(e.target.value)
                                        }
                                    />

                                    <input
                                        value={editLastName}
                                        onChange={(e) =>
                                            setEditLastName(e.target.value)
                                        }
                                    />

                                </div>

                            ) : (

                                `${user.firstName} ${user.lastName}`

                            )}

                        </td>

                        <td>

                            {editingId === user.id ? (

                                <input
                                    value={editEmail}
                                    onChange={(e) =>
                                        setEditEmail(e.target.value)
                                    }
                                />

                            ) : (

                                user.email

                            )}

                        </td>

                        <td>

                            {editingId === user.id ? (

                                <select
                                    value={editRole}
                                    onChange={(e) =>
                                        setEditRole(e.target.value)
                                    }
                                >

                                    <option value="CONTENT_CREATOR">
                                        Content creator
                                    </option>

                                    <option value="ADMIN">
                                        Admin
                                    </option>

                                </select>

                            ) : (

                                user.role

                            )}

                        </td>

                        <td>
                            {user.active ? "Da" : "Ne"}
                        </td>

                        <td>

                            {editingId === user.id ? (

                                <>

                                    <button
                                        onClick={() =>
                                            handleUpdate(user.id)
                                        }
                                    >
                                        Sačuvaj
                                    </button>

                                    <button
                                        onClick={cancelEdit}
                                    >
                                        Otkaži
                                    </button>

                                </>

                            ) : (

                                <>

                                    <button
                                        onClick={() =>
                                            startEdit(user)
                                        }
                                    >
                                        Izmeni
                                    </button>

                                    {user.role === "CONTENT_CREATOR" && (

                                        <button
                                            onClick={() =>
                                                toggleActive(user)
                                            }
                                        >
                                            {user.active
                                                ? "Deaktiviraj"
                                                : "Aktiviraj"}
                                        </button>

                                    )}

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

export default UsersPage;