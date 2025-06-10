import React, { useState, useEffect } from 'react';
import { useAlert, AlertType } from '../../contexts/AlertContext';

const OrdersPanel = () => {
    const { addAlert, showConfirmation } = useAlert();
    const [orders, setOrders] = useState([]);
    const [editingOrder, setEditingOrder] = useState(null);
    const [formData, setFormData] = useState({
        status: 'pending',
        cadaverFirstName: '',
        cadaverLastName: '',
        deathCertificateNumber: '',
        birthDate: '',
        deathDate: ''
    });
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchOrders = async () => {
            try {
                const token = localStorage.getItem("token");
                const response = await fetch('http://localhost:8080/orders', {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
                if (!response.ok) throw new Error('Failed to fetch orders');
                const data = await response.json();
                setOrders(data);
                setLoading(false);
            } catch (err) {
                setError(err.message);
                setLoading(false);
            }
        };
        fetchOrders();
    }, []);

    const handleEditClick = (order) => {
        setEditingOrder(order);
        setFormData({
            status: order.status || 'pending',
            cadaverFirstName: order.cadaverFirstName || '',
            cadaverLastName: order.cadaverLastName || '',
            deathCertificateNumber: order.deathCertificateNumber || '',
            birthDate: order.birthDate ? new Date(order.birthDate).toISOString().split('T')[0] : '',
            deathDate: order.deathDate ? new Date(order.deathDate).toISOString().split('T')[0] : ''
        });
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const handleSaveChanges = async () => {
        try {
            const token = localStorage.getItem("token");
            const response = await fetch(`http://localhost:8080/orders/${editingOrder.id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(formData)
            });
            
            if (!response.ok) throw new Error('Nie udało się zaktualizować zamówienia');
            
            const updatedOrder = await response.json();
            setOrders(orders.map(order => order.id === updatedOrder.id ? updatedOrder : order));
            setEditingOrder(null);
            addAlert(AlertType.SUCCESS, 'Zamówienie zostało zaktualizowane');
        } catch (err) {
            addAlert(AlertType.ERROR, `Błąd podczas aktualizacji zamówienia: ${err.message}`);
        }
    };

    const handleDeleteOrder = async () => {
        showConfirmation(
            "Czy na pewno chcesz usunąć to zamówienie?",
            async () => {
                try {
                    const token = localStorage.getItem("token");
                    const response = await fetch(`http://localhost:8080/orders/${editingOrder.id}`, {
                        method: 'DELETE',
                        headers: { 'Authorization': `Bearer ${token}` }
                    });
                    
                    if (!response.ok) throw new Error('Nie udało się usunąć zamówienia');
                    
                    setOrders(orders.filter(order => order.id !== editingOrder.id));
                    setEditingOrder(null);
                    addAlert(AlertType.SUCCESS, 'Zamówienie zostało usunięte');
                } catch (err) {
                    addAlert(AlertType.ERROR, `Błąd podczas usuwania zamówienia: ${err.message}`);
                }
            }
        );
    };

    const handleGenerateReport = async (orderId) => {
        try {
            // ...existing report generation code...
            addAlert(AlertType.SUCCESS, 'Raport został wygenerowany pomyślnie');
        } catch (err) {
            addAlert(AlertType.ERROR, `Błąd podczas generowania raportu: ${err.message}`);
        }
    };

    const handleCancelEdit = () => {
        setEditingOrder(null);
        setFormData({
            status: 'pending',
            cadaverFirstName: '',
            cadaverLastName: '',
            deathCertificateNumber: '',
            birthDate: '',
            deathDate: ''
        });
    };

    if (loading) return <div style={styles.loading}>Loading...</div>;
    if (error) return <div style={styles.error}>Error: {error}</div>;

    return (
        <div style={styles.container}>
            <h1 style={styles.header}>Panel Zamówień</h1>
            <table style={styles.table}>
                <thead>
                    <tr>
                        <th style={styles.th}>ID</th>
                        <th style={styles.th}>Zmarły</th>
                        <th style={styles.th}>Status</th>
                        <th style={styles.th}>Nr. Aktu Zgonu</th>
                        <th style={styles.th}>Data Urodzenia</th>
                        <th style={styles.th}>Data Śmierci</th>
                        <th style={styles.th}>Akcja</th>
                    </tr>
                </thead>
                <tbody>
                    {orders.map(order => (
                        <tr key={order.id} style={styles.tr}>
                            <td style={styles.td}>{order.id}</td>
                            <td style={styles.td}>{`${order.cadaverFirstName} ${order.cadaverLastName}`}</td>
                            <td style={styles.td}>{order.status}</td>
                            <td style={styles.td}>{order.deathCertificateNumber}</td>
                            <td style={styles.td}>{new Date(order.birthDate).toLocaleDateString('pl-PL')}</td>
                            <td style={styles.td}>{new Date(order.deathDate).toLocaleDateString('pl-PL')}</td>
                            <td style={styles.td}>
                                <button style={styles.button} onClick={() => handleEditClick(order)}>
                                    Edytuj dane
                                </button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>

            {editingOrder && (
                <div style={styles.editForm}>
                    <h2>Edytuj dane zamówienia</h2>
                    <input
                        type="text"
                        name="cadaverFirstName"
                        value={formData.cadaverFirstName}
                        onChange={handleInputChange}
                        placeholder="Imię zmarłego"
                        style={styles.input}
                    />
                    <input
                        type="text"
                        name="cadaverLastName"
                        value={formData.cadaverLastName}
                        onChange={handleInputChange}
                        placeholder="Nazwisko zmarłego"
                        style={styles.input}
                    />
                    <input
                        type="text"
                        name="deathCertificateNumber"
                        value={formData.deathCertificateNumber}
                        onChange={handleInputChange}
                        placeholder="Numer aktu zgonu"
                        style={styles.input}
                    />
                    <input
                        type="date"
                        name="birthDate"
                        value={formData.birthDate}
                        onChange={handleInputChange}
                        style={styles.input}
                    />
                    <input
                        type="date"
                        name="deathDate"
                        value={formData.deathDate}
                        onChange={handleInputChange}
                        style={styles.input}
                    />
                    <select
                        name="status"
                        value={formData.status}
                        onChange={handleInputChange}
                        style={styles.input}
                    >
                        <option value="pending">W oczekiwaniu</option>
                        <option value="in_progress">W trakcie</option>
                        <option value="completed">Zakończone</option>
                        <option value="canceled">Anulowane</option>
                    </select>
                    <div>
                        <button style={styles.button} onClick={handleSaveChanges}>Zapisz</button>
                        <button style={styles.button} onClick={handleCancelEdit}>Anuluj</button>
                        <button style={styles.deleteButton} onClick={handleDeleteOrder}>Usuń zamówienie</button>
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

export default OrdersPanel;