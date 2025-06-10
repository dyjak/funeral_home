import React, { useState, useEffect } from 'react';

const UsersPanel = () => {
    const [users, setUsers] = useState([]);
    const [editingUserId, setEditingUserId] = useState(null);
    const [formData, setFormData] = useState({
        firstName: '',
        lastName: '',
        email: '',
        role: 'USER',
        password: ''
    });
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [isCreating, setIsCreating] = useState(false);
    const [validationErrors, setValidationErrors] = useState({});

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

    const validateForm = () => {
        const errors = {};

        if (!formData.firstName.trim()) {
            errors.firstName = 'Imię jest wymagane';
        }

        if (!formData.lastName.trim()) {
            errors.lastName = 'Nazwisko jest wymagane';
        }

        if (!formData.email.trim()) {
            errors.email = 'Email jest wymagany';
        } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
            errors.email = 'Nieprawidłowy format email';
        }

        if (isCreating && !formData.password.trim()) {
            errors.password = 'Hasło jest wymagane';
        }

        if (!formData.role) {
            errors.role = 'Rola jest wymagana';
        }

        setValidationErrors(errors);
        return Object.keys(errors).length === 0;
    };

    const handleEditClick = (user) => {
        setIsCreating(false);
        setEditingUserId(user.id);
        setFormData({
            firstName: user.firstName || '',
            lastName: user.lastName || '',
            email: user.email || '',
            role: user.role || 'USER',
            password: ''
        });
        setValidationErrors({});
    };

    const handleNewUserClick = () => {
        setIsCreating(true);
        setEditingUserId(null);
        setFormData({
            firstName: '',
            lastName: '',
            email: '',
            role: 'USER',
            password: ''
        });
        setValidationErrors({});
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });

        // Wyczyść błąd dla tego pola gdy użytkownik zaczyna pisać
        if (validationErrors[name]) {
            setValidationErrors({ ...validationErrors, [name]: '' });
        }
    };

    const handleSaveChanges = async () => {
        if (!validateForm()) {
            return;
        }

        try {
            const token = localStorage.getItem("token");
            const editingUser = users.find(u => u.id === editingUserId);
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
            const response = await fetch(`http://localhost:8080/users/${editingUserId}`, {
                method: 'DELETE',
                headers: { 'Authorization': `Bearer ${token}` }
            });

            if (!response.ok) throw new Error('Failed to delete user');

            setUsers(users.filter(user => user.id !== editingUserId));
            handleCancelEdit();
        } catch (err) {
            setError(err.message);
        }
    };

    const handleCancelEdit = () => {
        setEditingUserId(null);
        setIsCreating(false);
        setFormData({ firstName: '', lastName: '', email: '', role: 'USER', password: '' });
        setValidationErrors({});
    };

    const renderForm = (isForNewUser = false) => (
        <tr>
            <td colSpan="6" style={styles.formContainer}>
                <div style={styles.editForm}>
                    <h3>{isForNewUser ? 'Dodaj nowego użytkownika' : 'Edytuj dane użytkownika'}</h3>

                    <div style={styles.formRow}>
                        <div style={styles.inputGroup}>
                            <input
                                type="text"
                                name="firstName"
                                value={formData.firstName}
                                onChange={handleInputChange}
                                placeholder="Imię *"
                                style={{
                                    ...styles.input,
                                    ...(validationErrors.firstName ? styles.inputError : {})
                                }}
                            />
                            {validationErrors.firstName && (
                                <span style={styles.errorText}>{validationErrors.firstName}</span>
                            )}
                        </div>

                        <div style={styles.inputGroup}>
                            <input
                                type="text"
                                name="lastName"
                                value={formData.lastName}
                                onChange={handleInputChange}
                                placeholder="Nazwisko *"
                                style={{
                                    ...styles.input,
                                    ...(validationErrors.lastName ? styles.inputError : {})
                                }}
                            />
                            {validationErrors.lastName && (
                                <span style={styles.errorText}>{validationErrors.lastName}</span>
                            )}
                        </div>
                    </div>

                    <div style={styles.formRow}>
                        <div style={styles.inputGroup}>
                            <input
                                type="email"
                                name="email"
                                value={formData.email}
                                onChange={handleInputChange}
                                placeholder="Email *"
                                style={{
                                    ...styles.input,
                                    ...(validationErrors.email ? styles.inputError : {})
                                }}
                            />
                            {validationErrors.email && (
                                <span style={styles.errorText}>{validationErrors.email}</span>
                            )}
                        </div>
                    </div>

                    <div style={styles.formRow}>
                        <div style={styles.inputGroup}>
                            <input
                                type="password"
                                name="password"
                                value={formData.password}
                                onChange={handleInputChange}
                                placeholder={isForNewUser ? "Hasło *" : "Nowe hasło (opcjonalne)"}
                                style={{
                                    ...styles.input,
                                    ...(validationErrors.password ? styles.inputError : {})
                                }}
                            />
                            {validationErrors.password && (
                                <span style={styles.errorText}>{validationErrors.password}</span>
                            )}
                        </div>

                        <div style={styles.inputGroup}>
                            <select
                                name="role"
                                value={formData.role}
                                onChange={handleInputChange}
                                style={{
                                    ...styles.input,
                                    ...(validationErrors.role ? styles.inputError : {})
                                }}
                            >
                                <option value="USER">User</option>
                                <option value="ADMIN">Admin</option>
                            </select>
                            {validationErrors.role && (
                                <span style={styles.errorText}>{validationErrors.role}</span>
                            )}
                        </div>
                    </div>

                    <div style={styles.buttonGroup}>
                        <button style={styles.button} onClick={handleSaveChanges}>
                            {isForNewUser ? 'Dodaj' : 'Zapisz'}
                        </button>
                        <button style={styles.cancelButton} onClick={handleCancelEdit}>
                            Anuluj
                        </button>
                        {!isForNewUser && (
                            <button style={styles.deleteButton} onClick={handleDeleteUser}>
                                Usuń użytkownika
                            </button>
                        )}
                    </div>
                </div>
            </td>
        </tr>
    );

    if (loading) return <div style={styles.loading}>Loading...</div>;
    if (error) return <div style={styles.error}>Error: {error}</div>;

    return (
        <div style={styles.container}>
            <div style={styles.header}>
                <h1>Panel Użytkowników</h1>
                <button style={styles.button} onClick={handleNewUserClick}>
                    + Dodaj nowego użytkownika
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
                    <th style={styles.th}>Akcje</th>
                </tr>
                </thead>
                <tbody>
                {/* Formularz dodawania nowego użytkownika na górze */}
                {isCreating && renderForm(true)}

                {users.map(user => (
                    <React.Fragment key={user.id}>
                        <tr style={styles.tr}>
                            <td style={styles.td}>{user.id}</td>
                            <td style={styles.td}>{user.firstName}</td>
                            <td style={styles.td}>{user.lastName}</td>
                            <td style={styles.td}>{user.email}</td>
                            <td style={styles.td}>{user.role}</td>
                            <td style={styles.td}>
                                <button
                                    style={styles.smallButton}
                                    onClick={() => handleEditClick(user)}
                                >
                                    Edytuj
                                </button>
                                <button
                                    style={styles.smallDeleteButton}
                                    onClick={() => handleImmediateDelete(user.id)}
                                >
                                    Usuń
                                </button>
                            </td>
                        </tr>
                        {/* Formularz edycji pod konkretnym użytkownikiem */}
                        {editingUserId === user.id && renderForm(false)}
                    </React.Fragment>
                ))}
                </tbody>
            </table>
        </div>
    );
};

const styles = {
    container: {
        backgroundColor: '#1F2937',
        borderRadius: '8px',
        padding: '20px'
    },
    header: {
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        marginBottom: '20px',
        color: 'white'
    },
    table: {
        width: '100%',
        borderCollapse: 'collapse',
        backgroundColor: '#374151'
    },
    th: {
        padding: '12px',
        textAlign: 'left',
        backgroundColor: '#374151',
        color: 'white',
        fontWeight: 'bold'
    },
    td: {
        padding: '12px',
        textAlign: 'left',
        borderBottom: '1px solid #4B5563'
    },
    tr: {
        transition: 'background-color 0.3s',
        '&:hover': {
            backgroundColor: '#374151'
        }
    },
    formContainer: {
        padding: '0',
        backgroundColor: '#1F2937'
    },
    editForm: {
        backgroundColor: '#2D3748',
        color: 'white',
        borderRadius: '8px',
        padding: '20px',
        margin: '10px'
    },
    formRow: {
        display: 'flex',
        gap: '15px',
        marginBottom: '15px'
    },
    inputGroup: {
        flex: 1
    },
    input: {
        width: '100%',
        padding: '10px',
        borderRadius: '4px',
        border: '1px solid #4B5563',
        fontSize: '14px',
        backgroundColor: '#374151',
        color: 'white',
        boxSizing: 'border-box'
    },
    inputError: {
        borderColor: '#DC2626',
        backgroundColor: '#FEE2E2',
        color: '#DC2626'
    },
    errorText: {
        color: '#DC2626',
        fontSize: '12px',
        marginTop: '4px',
        display: 'block'
    },
    buttonGroup: {
        display: 'flex',
        gap: '10px',
        marginTop: '15px'
    },
    button: {
        padding: '10px 20px',
        backgroundColor: '#7C3AED',
        color: 'white',
        border: 'none',
        borderRadius: '4px',
        cursor: 'pointer',
        fontSize: '14px',
        fontWeight: '500'
    },
    cancelButton: {
        padding: '10px 20px',
        backgroundColor: '#6B7280',
        color: 'white',
        border: 'none',
        borderRadius: '4px',
        cursor: 'pointer',
        fontSize: '14px'
    },
    deleteButton: {
        padding: '10px 20px',
        backgroundColor: '#DC2626',
        color: 'white',
        border: 'none',
        borderRadius: '4px',
        cursor: 'pointer',
        fontSize: '14px'
    },
    smallButton: {
        padding: '6px 12px',
        backgroundColor: '#7C3AED',
        color: 'white',
        border: 'none',
        borderRadius: '4px',
        cursor: 'pointer',
        fontSize: '12px',
        marginRight: '5px'
    },
    smallDeleteButton: {
        padding: '6px 12px',
        backgroundColor: '#DC2626',
        color: 'white',
        border: 'none',
        borderRadius: '4px',
        cursor: 'pointer',
        fontSize: '12px'
    },
    loading: {
        color: 'white',
        textAlign: 'center',
        padding: '20px'
    },
    error: {
        color: '#DC2626',
        textAlign: 'center',
        padding: '20px'
    }
};

export default UsersPanel;