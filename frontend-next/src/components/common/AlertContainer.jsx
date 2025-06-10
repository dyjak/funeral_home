import React from 'react';
import Alert from './Alert';
import { useAlert } from '../../contexts/AlertContext';

const AlertContainer = () => {
    const { alerts, removeAlert, handleConfirm, handleCancel } = useAlert();

    return (
        <div className="fixed top-4 right-4 z-50 w-96 space-y-2">
            {alerts.map(alert => (
                <Alert
                    key={alert.id}
                    alert={alert}
                    onClose={removeAlert}
                    onConfirm={handleConfirm}
                    onCancel={handleCancel}
                />
            ))}
        </div>
    );
};

export default AlertContainer;