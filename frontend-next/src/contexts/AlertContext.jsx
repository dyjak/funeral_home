import React, { createContext, useContext, useState } from 'react';

export const AlertType = {
    SUCCESS: 'success',
    ERROR: 'error',
    WARNING: 'warning',
    INFO: 'info',
    CONFIRM: 'confirm'
};

const AlertContext = createContext(null);

export const AlertProvider = ({ children }) => {
    const [alerts, setAlerts] = useState([]);
    const [confirmCallback, setConfirmCallback] = useState(null);

    const addAlert = (type, message, timeout = 5000) => {
        const id = Date.now();
        setAlerts(prev => [...prev, { id, type, message }]);
        
        if (type !== AlertType.CONFIRM && timeout > 0) {
            setTimeout(() => removeAlert(id), timeout);
        }
        return id;
    };

    const removeAlert = (id) => {
        setAlerts(prev => prev.filter(alert => alert.id !== id));
    };

    const showConfirmation = (message, onConfirm) => {
        setConfirmCallback(() => onConfirm);
        addAlert(AlertType.CONFIRM, message, 0);
    };

    const handleConfirm = (id) => {
        if (confirmCallback) {
            confirmCallback();
            setConfirmCallback(null);
        }
        removeAlert(id);
    };

    const handleCancel = (id) => {
        setConfirmCallback(null);
        removeAlert(id);
    };

    return (
        <AlertContext.Provider value={{
            alerts,
            addAlert,
            removeAlert,
            showConfirmation,
            handleConfirm,
            handleCancel
        }}>
            {children}
        </AlertContext.Provider>
    );
};

export const useAlert = () => {
    const context = useContext(AlertContext);
    if (!context) {
        throw new Error('useAlert must be used within an AlertProvider');
    }
    return context;
};