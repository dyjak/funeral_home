import React, { useState, useEffect } from 'react';
import { useAlert, AlertType } from '../contexts/AlertContext';
import ReceptionForms from '../components/reception/ReceptionForms';
import OrdersList from '../components/reception/OrdersList';

const Receptionist = () => {
  const { addAlert } = useAlert();
  const [formData, setFormData] = useState({
    client: {
      firstName: '',
      lastName: '',
      phone: '',
      email: ''
    },
    deceased: {
      firstName: '',
      lastName: '',
      birthDate: '',
      deathDate: '',
      deathCertificateNumber: ''
    }
  });
  const [currentUser, setCurrentUser] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);
  const [orders, setOrders] = useState([]); // Add orders state

  useEffect(() => {
    const fetchCurrentUser = async () => {
      const token = localStorage.getItem('token');
      if (!token) return;

      try {
        const response = await fetch('http://localhost:8080/users/me', {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });

        if (!response.ok) throw new Error('Failed to fetch user data');
        const userData = await response.json();
        setCurrentUser(userData);
      } catch (err) {
        console.error('Error fetching user data:', err);
      }
    };

    fetchCurrentUser();
  }, []);

  // First, extract fetchOrders to a separate function at the top level of the component
  const fetchOrders = async () => {
    const token = localStorage.getItem('token');
    if (!token) return;

    try {
      const response = await fetch('http://localhost:8080/orders', {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Accept': 'application/json'
        }
      });

      if (!response.ok) throw new Error('Failed to fetch orders');
      const data = await response.json();
      setOrders(data);
    } catch (err) {
      console.error('Error fetching orders:', err);
      setError('Failed to fetch orders');
    }
  };

  // Use the extracted function in useEffect
  useEffect(() => {
    fetchOrders();
  }, []); // Empty dependency array means this runs once on mount

  const handleClientChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      client: { ...prev.client, [name]: value }
    }));
  };

  const handleDeceasedChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      deceased: { ...prev.deceased, [name]: value }
    }));
  };

  const generateBulkReports = async (orderIds) => {
    setLoading(true);
    try {
      const token = localStorage.getItem('token');
      
      const response = await fetch(`http://localhost:8080/reports/orders/bulk`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(orderIds)
      });

      if (!response.ok) {
        throw new Error('Nie udało się wygenerować raportów');
      }

      const blob = await response.blob();
      const url = window.URL.createObjectURL(blob);
      window.open(url);
      
      addAlert(AlertType.SUCCESS, 'Raporty zostały wygenerowane pomyślnie');
    } catch (err) {
      console.error('Error generating reports:', err);
      addAlert(AlertType.ERROR, `Błąd podczas generowania raportów: ${err.message}`);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    const token = localStorage.getItem('token');

    try {
      // Create Client
      const clientResponse = await fetch('http://localhost:8080/clients', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(formData.client)
      });

      if (!clientResponse.ok) throw new Error('Błąd podczas tworzenia klienta');
      const client = await clientResponse.json();

      // Helper function to convert date to datetime
      const convertToDateTime = (dateStr) => {
        if (!dateStr) return null;
        const date = new Date(dateStr);
        // Set time to noon (12:00:00) to avoid timezone issues
        date.setHours(12, 0, 0, 0);
        return date.toISOString();
      };

      // Create Order with all dates properly formatted
      const orderData = {
        cadaverFirstName: formData.deceased.firstName,
        cadaverLastName: formData.deceased.lastName,
        deathCertificateNumber: formData.deceased.deathCertificateNumber,
        birthDate: convertToDateTime(formData.deceased.birthDate),
        deathDate: convertToDateTime(formData.deceased.deathDate),
        client: { id: client.id },
        status: 'pending',
        orderDate: new Date().toISOString(),
        user: { id: currentUser.id }
      };

      const orderResponse = await fetch('http://localhost:8080/orders', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(orderData)
      });

      if (!orderResponse.ok) throw new Error('Błąd podczas tworzenia zlecenia');
      
      addAlert(AlertType.SUCCESS, 'Zlecenie zostało pomyślnie utworzone');
      
      // Reset form
      setFormData({
        client: {
          firstName: '',
          lastName: '',
          phone: '',
          email: ''
        },
        deceased: {
          firstName: '',
          lastName: '',
          birthDate: '',
          deathDate: '',
          deathCertificateNumber: ''
        }
      });

      // Refresh orders list
      await fetchOrders();

    } catch (err) {
      addAlert(AlertType.ERROR, `Błąd: ${err.message}`);
    } finally {
      setLoading(false);
    }
  };

  // Generate report function
  const generateOrderReport = async (orderId) => {
    setLoading(true);
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`http://localhost:8080/reports/orders/${orderId}`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });

      if (!response.ok) throw new Error('Nie udało się wygenerować raportu');

      const blob = await response.blob();
      const url = window.URL.createObjectURL(blob);
      window.open(url);
      
      addAlert(AlertType.SUCCESS, 'Raport został wygenerowany pomyślnie');
    } catch (err) {
      console.error('Error generating report:', err);
      addAlert(AlertType.ERROR, `Błąd podczas generowania raportu: ${err.message}`);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container mx-auto p-4 bg-gray-900 text-gray-100">
      <h1 className="text-3xl font-bold mb-8">Panel recepcji</h1>
      
      <ReceptionForms 
        currentUser={currentUser}
        formData={formData}
        handleClientChange={handleClientChange}
        handleDeceasedChange={handleDeceasedChange}
        handleSubmit={handleSubmit}
        loading={loading}
        error={error}
        success={success}
      />

      <OrdersList 
        orders={orders}
        generateReport={generateOrderReport}
        generateBulkReports={generateBulkReports}
        loading={loading}
      />
    </div>
  );
};

export default Receptionist;