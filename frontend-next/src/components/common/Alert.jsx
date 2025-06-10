import React from 'react';
import { AlertType } from '../../contexts/AlertContext';

const Alert = ({ alert, onClose, onConfirm, onCancel }) => {
    const getAlertStyles = () => {
        switch (alert.type) {
            case AlertType.SUCCESS:
                return 'bg-green-900 bg-opacity-60 text-green-200 border border-green-700';
            case AlertType.ERROR:
                return 'bg-red-900 bg-opacity-60 text-red-200 border border-red-700';
            case AlertType.WARNING:
                return 'bg-yellow-900 bg-opacity-60 text-yellow-200 border border-yellow-700';
            case AlertType.INFO:
                return 'bg-blue-900 bg-opacity-60 text-blue-200 border border-blue-700';
            case AlertType.CONFIRM:
                return 'bg-gray-900 bg-opacity-60 text-gray-200 border border-gray-700';
            default:
                return 'bg-gray-900 bg-opacity-60 text-gray-200 border border-gray-700';
        }
    };

    return (
        <div className={`p-4 rounded-lg shadow-lg ${getAlertStyles()} mb-4 animate-fade-in`}>
            <div className="flex justify-between items-center">
                <div>{alert.message}</div>
                <div className="flex gap-2">
                    {alert.type === AlertType.CONFIRM ? (
                        <>
                            <button
                                onClick={() => onConfirm(alert.id)}
                                className="px-4 py-2 bg-green-700 hover:bg-green-600 rounded text-white"
                            >
                                Potwierdź
                            </button>
                            <button
                                onClick={() => onCancel(alert.id)}
                                className="px-4 py-2 bg-gray-700 hover:bg-gray-600 rounded text-white"
                            >
                                Anuluj
                            </button>
                        </>
                    ) : (
                        <button
                            onClick={() => onClose(alert.id)}
                            className="text-gray-300 hover:text-white"
                        >
                            ×
                        </button>
                    )}
                </div>
            </div>
        </div>
    );
};

export default Alert;