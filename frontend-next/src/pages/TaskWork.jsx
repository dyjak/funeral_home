import React, { useState, useEffect } from 'react';
import { useAlert, AlertType } from '../contexts/AlertContext';
import TaskCard from '../components/worker/TaskCard';
import TaskList from '../components/worker/TaskList';

const TaskWork = () => {
    const { addAlert } = useAlert();
    const [tasks, setTasks] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [statusChangeRequests, setStatusChangeRequests] = useState({});

    const fetchTasks = async () => {
        try {
            const token = localStorage.getItem("token");
            if (!token) {
                throw new Error('No token found');
            }

            console.log('Fetching assigned tasks...');
            const response = await fetch('http://localhost:8080/tasks/assigned', {
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Accept': 'application/json'
                }
            });
                
            if (!response.ok) {
                const text = await response.text();
                console.error('Server response:', text);
                throw new Error(`Failed to fetch tasks: ${response.status}`);
            }

            const data = await response.json();
            console.log('Received tasks:', data); // Debug log
            setTasks(data);
            setError(null);
        } catch (err) {
            console.error('Error fetching tasks:', err);
            setError(err.message);
            addAlert(AlertType.ERROR, `Błąd podczas pobierania zadań: ${err.message}`);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchTasks();
    }, []);

    // Debug log whenever tasks change
    useEffect(() => {
        console.log('Tasks state updated:', tasks);
    }, [tasks]);

    const handleStatusChangeRequest = async (taskId, requestData) => {
        try {
            // Always fetch fresh user data from API to ensure consistency
            const token = localStorage.getItem('token');
            if (!token) throw new Error('No token found');

            const response = await fetch('http://localhost:8080/users/me', {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });

            if (!response.ok) throw new Error('Failed to fetch user data');
            
            const currentUser = await response.json();
            console.log('Fetched user data:', currentUser); // Debug log

            // Store updated user data
            localStorage.setItem('currentUser', JSON.stringify(currentUser));

            // Create new request object with verified user data
            const newRequest = {
                id: Date.now(),
                task: tasks.find(t => t.id === taskId),
                user: {
                    id: currentUser.id,
                    firstName: currentUser.firstName,
                    lastName: currentUser.lastName,
                    email: currentUser.email,
                    role: currentUser.role
                },
                currentStatus: requestData.currentStatus,
                requestedStatus: requestData.requestedStatus,
                reason: requestData.reason,
                requestDate: new Date().toISOString()
            };

            // Save request to localStorage
            const STORAGE_KEY = 'statusChangeRequests';
            const stored = localStorage.getItem(STORAGE_KEY);
            let existingData = [];
            
            if (stored) {
                const { data, timestamp } = JSON.parse(stored);
                const now = new Date().getTime();
                const expirationTime = 24 * 60 * 60 * 1000; // 24 hours

                // Only keep non-expired requests
                if (now - timestamp < expirationTime) {
                    existingData = data;
                }
            }

            // Add new request
            const updatedRequests = [...existingData, newRequest];
            
            // Save with new timestamp
            const requestsWithExpiration = {
                data: updatedRequests,
                timestamp: new Date().getTime()
            };
            localStorage.setItem(STORAGE_KEY, JSON.stringify(requestsWithExpiration));

            // Update local state
            setStatusChangeRequests(prev => ({
                ...prev,
                [taskId]: requestData
            }));

            // Update task with pending request
            setTasks(prev => prev.map(task => 
                task.id === taskId 
                    ? { ...task, statusChangeRequest: requestData }
                    : task
            ));

            addAlert(AlertType.SUCCESS, 'Wniosek o zmianę statusu został wysłany');
        } catch (err) {
            console.error('Error submitting status change request:', err);
            addAlert(AlertType.ERROR, `Błąd podczas wysyłania wniosku: ${err.message}`);
        }
    };

    // Sort tasks by due date
    const sortedTasks = [...tasks].sort((a, b) => {
        if (!a.dueDate) return 1;
        if (!b.dueDate) return -1;
        return new Date(a.dueDate) - new Date(b.dueDate);
    });

    if (loading) return <div className="text-center p-4">Loading...</div>;
    if (error) return <div className="text-center p-4 text-red-500">{error}</div>;

    return (
        <div className="container mx-auto p-4 bg-gray-900 text-gray-100">
            <h1 className="text-2xl font-bold mb-6">Moje zadania (Pracownik)</h1>
            <div className="bg-gray-800 p-4 rounded-lg">
                {sortedTasks.length === 0 ? (
                    <div className="mt-4 p-4 bg-gray-700 rounded-lg">
                        <p className="text-gray-400">Brak przypisanych zadań.</p>
                    </div>
                ) : (
                    sortedTasks.map(task => (
                        <TaskCard
                            key={task.id}
                            task={task}
                            onStatusChangeRequest={handleStatusChangeRequest}
                        />
                    ))
                )}
            </div>
        </div>
    );
};

export default TaskWork;