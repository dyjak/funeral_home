import React, { useState, useEffect } from 'react';
import { useAlert, AlertType } from '../contexts/AlertContext';

const StatusRequests = () => {
    const { addAlert, showConfirmation } = useAlert();
    const [requests, setRequests] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // Helper functions for localStorage
    const STORAGE_KEY = 'statusChangeRequests';
    const EXPIRATION_HOURS = 24; // Requests expire after 24 hours

    const saveRequests = (requests) => {
        const requestsWithExpiration = {
            data: requests,
            timestamp: new Date().getTime()
        };
        localStorage.setItem(STORAGE_KEY, JSON.stringify(requestsWithExpiration));
    };

    const getRequests = () => {
        const stored = localStorage.getItem(STORAGE_KEY);
        if (!stored) return [];

        const { data, timestamp } = JSON.parse(stored);
        const now = new Date().getTime();
        const expirationTime = EXPIRATION_HOURS * 60 * 60 * 1000; // Convert hours to milliseconds

        // Check if data has expired
        if (now - timestamp > expirationTime) {
            localStorage.removeItem(STORAGE_KEY);
            return [];
        }

        return data;
    };

    const fetchRequests = () => {
        try {
            const storedRequests = getRequests();
            setRequests(storedRequests);
        } catch (err) {
            console.error('Error fetching requests:', err);
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchRequests();
        // Set up periodic cleanup
        const cleanup = setInterval(fetchRequests, 1000 * 60 * 60); // Check every hour
        return () => clearInterval(cleanup);
    }, []);

    const handleRequestAction = async (taskId, requestId, action) => {
        try {
            if (action === 'approve') {
                const request = requests.find(r => r.id === requestId);
                const taskData = {
                    id: taskId,
                    status: request.requestedStatus
                };

                showConfirmation(
                    `Czy na pewno chcesz ${action === 'approve' ? 'zatwierdzić' : 'odrzucić'} ten wniosek?`,
                    async () => {
                        try {
                            if (action === 'approve') {
                                const res = await fetch(`http://localhost:8080/tasks/${taskId}`, {
                                    method: "PUT",
                                    headers: {
                                        "Content-Type": "application/json",
                                        "Authorization": `Bearer ${localStorage.getItem('token')}`
                                    },
                                    body: JSON.stringify(taskData)
                                });

                                if (!res.ok) {
                                    const errorText = await res.text();
                                    throw new Error(`Failed to update task status: ${errorText}`);
                                }
                            }

                            // Remove the request
                            const updatedRequests = requests.filter(req => req.id !== requestId);
                            setRequests(updatedRequests);
                            saveRequests(updatedRequests);

                            addAlert(
                                AlertType.SUCCESS, 
                                `Wniosek został ${action === 'approve' ? 'zatwierdzony' : 'odrzucony'}`
                            );
                        } catch (err) {
                            addAlert(
                                AlertType.ERROR, 
                                `Błąd podczas ${action === 'approve' ? 'zatwierdzania' : 'odrzucania'} wniosku: ${err.message}`
                            );
                        }
                    }
                );
            }
        } catch (err) {
            addAlert(AlertType.ERROR, `Wystąpił błąd: ${err.message}`);
        }
    };

    const formatDate = (dateString) => {
        return new Date(dateString).toLocaleString('pl-PL');
    };

    const getStatusBadgeColor = (status) => {
        switch (status) {
            case 'pending': return 'bg-yellow-600';
            case 'in_progress': return 'bg-blue-600';
            case 'completed': return 'bg-green-600';
            case 'canceled': return 'bg-red-600';
            default: return 'bg-gray-600';
        }
    };

    if (loading) return <div className="text-center p-4">Loading...</div>;
    if (error) return <div className="text-center p-4 text-red-500">{error}</div>;

    return (
        <div className="container mx-auto p-4 bg-gray-900 text-gray-100">
            <h1 className="text-2xl font-bold mb-6">Wnioski o zmianę statusu</h1>
            
            {requests.length === 0 ? (
                <div className="bg-gray-800 p-4 rounded-lg text-center">
                    <p>Brak oczekujących wniosków</p>
                </div>
            ) : (
                <div className="space-y-4">
                    {requests.map(request => (
                        <div key={request.id} className="bg-gray-800 p-4 rounded-lg shadow-lg">
                            <div className="flex justify-between items-start">
                                <div>
                                    <h3 className="text-lg font-semibold mb-2">
                                        {request.task.taskName}
                                    </h3>
                                    <p className="text-gray-400 text-sm mb-1">
                                        Pracownik: {request.user.firstName} {request.user.lastName}
                                    </p>
                                    <div className="flex items-center gap-2 mb-1">
                                        <span className="text-gray-400 text-sm">Proponowana zmiana:</span>
                                        <span className={`${getStatusBadgeColor(request.currentStatus)} px-2 py-1 rounded text-xs`}>
                                            {request.currentStatus}
                                        </span>
                                        <span className="text-gray-400 text-sm">→</span>
                                        <span className={`${getStatusBadgeColor(request.requestedStatus)} px-2 py-1 rounded text-xs`}>
                                            {request.requestedStatus}
                                        </span>
                                    </div>
                                    <p className="text-gray-400 text-sm">
                                        Data wniosku: {formatDate(request.requestDate)}
                                    </p>
                                    <div className="mt-2 p-2 bg-gray-700 rounded">
                                        <p className="text-sm">Uzasadnienie: {request.reason}</p>
                                    </div>
                                </div>
                                <div className="flex gap-2">
                                    <button
                                        onClick={() => handleRequestAction(request.task.id, request.id, 'approve')}
                                        className="bg-green-600 hover:bg-green-500 text-white px-4 py-2 rounded text-sm"
                                    >
                                        Zatwierdź
                                    </button>
                                    <button
                                        onClick={() => handleRequestAction(request.task.id, request.id, 'reject')}
                                        className="bg-red-600 hover:bg-red-500 text-white px-4 py-2 rounded text-sm"
                                    >
                                        Odrzuć
                                    </button>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default StatusRequests;