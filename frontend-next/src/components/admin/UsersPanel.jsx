import React, { useState, useEffect } from 'react';

const UsersPanel = () => {
    const [users, setUsers] = useState([]);
    const [editingUser, setEditingUser] = useState(null);
    const [formData, setFormData] = useState({ 
        firstName: '', 
        lastName: '', 
        email: '', 
        role: 'USER',
        password: '' // Added for new user creation
    });
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [isCreating, setIsCreating] = useState(false);

    useEffect(() => {
        fetchUsers();
    }, []);

    const fetchUsers = async () => {
        try {
            const token = localStorage.getItem("token");
            const response = await fetch('http://localhost:8080/users', {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            if (!response.ok) throw new Error('Failed to fetch users');
            const data = await response.json();
            setUsers(data);
            setLoading(false);
        } catch (err) {
            setError(err.message);
            setLoading(false);
        }
    };

    const handleEditClick = (user) => {
        setIsCreating(false);
        setEditingUser(user);
        setFormData({ 
            firstName: user.firstName || '', 
            lastName: user.lastName || '',
            email: user.email || '', 
            role: user.role || 'USER',
            password: '' // Clear password when editing
        });
    };

    const handleNewUserClick = () => {
        setIsCreating(true);
        setEditingUser(null);
        setFormData({
            firstName: '',
            lastName: '',
            email: '',
            role: 'USER',
            password: ''
        });
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

   const handleSaveChanges = async () => {
    try {
        const token = localStorage.getItem("token");
        const url = editingUser 
            ? `http://localhost:8080/users/${editingUser.id}`
            : 'http://localhost:8080/users';

        const userToSend = {
            firstName: formData.firstName,
            lastName: formData.lastName,
            email: formData.email,
            role: formData.role,
            ...(formData.password && { passwordHash: formData.password })
        };

        console.log("📦 Dane wysyłane do backendu:", userToSend);

        const response = await fetch(url, {
            method: editingUser ? 'PUT' : 'POST',
            headers: { 
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(userToSend)
        });

        if (!response.ok) throw new Error(`Failed to ${editingUser ? 'update' : 'create'} user`);

        const updatedUser = await response.json();

        if (editingUser) {
            setUsers(users.map(user => user.id === updatedUser.id ? updatedUser : user));
        } else {
            setUsers([...users, updatedUser]);
        }

        handleCancelEdit();
    } catch (err) {
        setError(err.message);
    }
};


    const handleImmediateDelete = async (userId) => {
    const confirmDelete = window.confirm('Czy na pewno chcesz usunąć tego użytkownika?');
    if (!confirmDelete) return;

    try {
        const token = localStorage.getItem("token");
        const response = await fetch(`http://localhost:8080/users/${userId}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) throw new Error('Nie udało się usunąć użytkownika.');

        setUsers(users.filter(user => user.id !== userId));
    } catch (error) {
        alert(error.message);
        console.error("Błąd usuwania:", error);
    }
};

    const handleDeleteUser = async () => {
        const confirm = window.confirm('Czy na pewno chcesz usunąć tego użytkownika?');
        if (!confirm) return;

        try {
            const token = localStorage.getItem("token");
            const response = await fetch(`http://localhost:8080/users/${editingUser.id}`, {
                method: 'DELETE',
                headers: { 'Authorization': `Bearer ${token}` }
            });
            
            if (!response.ok) throw new Error('Failed to delete user');
            
            setUsers(users.filter(user => user.id !== editingUser.id));
            handleCancelEdit();
        } catch (err) {
            setError(err.message);
        }
    };

    const handleCancelEdit = () => {
        setEditingUser(null);
        setIsCreating(false);
        setFormData({ firstName: '', lastName: '', email: '', role: 'USER', password: '' });
    };

    if (loading) return <div style={styles.loading}>Loading...</div>;
    if (error) return <div style={styles.error}>Error: {error}</div>;

    return (
        <div style={styles.container}>
            <div style={styles.header}>
                <h1>Panel Użytkowników</h1>
                <button style={styles.button} onClick={handleNewUserClick}>
                    Dodaj nowego użytkownika
                </button>
            </div>

            <table style={styles.table}>
                <thead>
                    <tr>
                        <th style={styles.th}>ID</th>
                        <th style={styles.th}>Imię</th>
                        <th style={styles.th}>Nazwisko</th>
                        <th style={styles.th}>Email</th>
                        <th style={styles.th}>Rola</th>
                        <th style={styles.th}>Akcja</th>
                    </tr>
                </thead>
                <tbody>
                    {users.map(user => (
                        <tr key={user.id} style={styles.tr}>
                            <td style={styles.td}>{user.id}</td>
                            <td style={styles.td}>{user.firstName}</td>
                            <td style={styles.td}>{user.lastName}</td>
                            <td style={styles.td}>{user.email}</td>
                            <td style={styles.td}>{user.role}</td>
                            <td style={styles.td}>
                                <td style={styles.td}>
    <button style={styles.button} onClick={() => handleEditClick(user)}>
        Edytuj
    </button>
    <button 
        style={styles.deleteButton}
        onClick={() => handleImmediateDelete(user.id)}
    >
        Usuń
    </button>
</td>

                            </td>
                            
                        </tr>
                    ))}
                </tbody>
            </table>

            {(editingUser || isCreating) && (
                <div style={styles.editForm}>
                    <h2>{isCreating ? 'Dodaj nowego użytkownika' : 'Edytuj dane użytkownika'}</h2>
                    <input
                        type="text"
                        name="firstName"
                        value={formData.firstName}
                        onChange={handleInputChange}
                        placeholder="Imię"
                        style={styles.input}
                        required
                    />
                    <input
                        type="text"
                        name="lastName"
                        value={formData.lastName}
                        onChange={handleInputChange}
                        placeholder="Nazwisko"
                        style={styles.input}
                        required
                    />
                    <input
                        type="email"
                        name="email"
                        value={formData.email}
                        onChange={handleInputChange}
                        placeholder="Email"
                        style={styles.input}
                        required
                    />
                    {(isCreating || editingUser) && (
                        <input
                            type="password"
                            name="password"
                            value={formData.password}
                            onChange={handleInputChange}
                            placeholder={isCreating ? "Hasło" : "Nowe hasło (opcjonalne)"}
                            style={styles.input}
                            required={isCreating}
                        />
                    )}
                    <select
                        name="role"
                        value={formData.role}
                        onChange={handleInputChange}
                        style={styles.input}
                    >
                        <option value="ADMIN">Admin</option>
                        <option value="USER">User</option>
                    </select>
                    <div>
                        <button style={styles.button} onClick={handleSaveChanges}>
                            {isCreating ? 'Dodaj' : 'Zapisz'}
                        </button>
                        <button style={styles.button} onClick={handleCancelEdit}>
                            Anuluj
                        </button>
                        {editingUser && (
                            <button style={styles.deleteButton} onClick={handleDeleteUser}>
                                Usuń użytkownika
                            </button>
                        )}
                    </div>
                </div>
            )}
        </div>
    );
};

const styles = {
    container: {
        backgroundColor: '#1F2937', // bg-gray-800 (lighter than parent)
        borderRadius: '8px',
        padding: '20px'
    },
    table: {
        width: '100%',
        borderCollapse: 'collapse',
        marginTop: '20px',
        backgroundColor: '#374151' // bg-gray-700 (even lighter)
    },
    th: {
        padding: '12px',
        textAlign: 'left',
        backgroundColor: '#374151', // bg-gray-700 instead of #2f0031
        color: 'white',
        fontWeight: 'bold'
    },
    td: {
        padding: '12px',
        textAlign: 'left',
        borderBottom: '1px solid #4B5563' // bg-gray-600
    },
    tr: {
        transition: 'background-color 0.3s',
        '&:hover': {
            backgroundColor: '#374151' // bg-gray-700
        }
    },
    button: {
        padding: '8px 16px',
        backgroundColor: '#7C3AED', // bg-purple-600 instead of #9900ff
        color: 'white',
        border: 'none',
        borderRadius: '4px',
        cursor: 'pointer',
        fontSize: '14px',
        margin: '5px',
        '&:hover': {
            backgroundColor: '#6D28D9' // bg-purple-700
        }
    },
    deleteButton: {
        padding: '8px 16px',
        backgroundColor: '#DC2626', // bg-red-600 instead of #ff4444
        color: 'white',
        border: 'none',
        borderRadius: '4px',
        cursor: 'pointer',
        fontSize: '14px',
        margin: '5px',
        '&:hover': {
            backgroundColor: '#B91C1C' // bg-red-700
        }
    },
    editForm: {
        backgroundColor: '#1F2937', // bg-gray-800 instead of #2a2828
        color: 'white',
        borderRadius: '8px',
        boxShadow: '0 2px 5px rgba(0, 0, 0, 0.1)',
        maxWidth: '500px',
        margin: '20px auto',
        padding: '20px'
    },
    input: {
        width: '100%',
        padding: '10px',
        marginBottom: '10px',
        borderRadius: '4px',
        border: '1px solid #4B5563', // bg-gray-600
        fontSize: '16px',
        backgroundColor: '#374151', // bg-gray-700 instead of #444
        color: 'white'
    },
    header: {
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        marginBottom: '20px',
        color: 'white'
    }
};

export default UsersPanel;