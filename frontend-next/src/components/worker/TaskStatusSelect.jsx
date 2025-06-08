import React, { useState } from 'react';

const TaskStatusSelect = ({ currentStatus, taskId, onStatusChangeRequest }) => {
    const [isRequesting, setIsRequesting] = useState(false);
    const [requestedStatus, setRequestedStatus] = useState('');
    const [reason, setReason] = useState('');

    const handleRequestSubmit = () => {
        onStatusChangeRequest(taskId, {
            currentStatus,
            requestedStatus,
            reason,
            requestDate: new Date().toISOString()
        });
        setIsRequesting(false);
        setReason('');
    };

    const getStatusColor = (status) => {
        switch (status) {
            case 'completed': return 'bg-green-600';
            case 'in_progress': return 'bg-blue-600';
            case 'canceled': return 'bg-red-600';
            default: return 'bg-yellow-600';
        }
    };

    return (
        <div className="relative">
            {isRequesting ? (
                <div className="absolute right-0 w-64 p-4 bg-gray-800 rounded-lg shadow-lg z-10">
                    <h3 className="text-sm font-semibold mb-2">Wnioskuj o zmianę statusu</h3>
                    <select
                        className="w-full mb-2 bg-gray-700 text-white rounded p-1"
                        value={requestedStatus}
                        onChange={(e) => setRequestedStatus(e.target.value)}
                    >
                        <option value="">Wybierz status</option>
                        <option value="pending">Oczekujące</option>
                        <option value="in_progress">W trakcie</option>
                        <option value="completed">Zakończone</option>
                        <option value="canceled">Anulowane</option>
                    </select>
                    <textarea
                        className="w-full mb-2 bg-gray-700 text-white rounded p-1"
                        placeholder="Powód zmiany statusu..."
                        value={reason}
                        onChange={(e) => setReason(e.target.value)}
                    />
                    <div className="flex justify-end gap-2">
                        <button
                            className="px-2 py-1 bg-gray-600 rounded text-sm"
                            onClick={() => setIsRequesting(false)}
                        >
                            Anuluj
                        </button>
                        <button
                            className="px-2 py-1 bg-blue-600 rounded text-sm"
                            onClick={handleRequestSubmit}
                            disabled={!requestedStatus || !reason}
                        >
                            Wyślij
                        </button>
                    </div>
                </div>
            ) : (
                <button
                    className={`${getStatusColor(currentStatus)} text-white px-2 py-1 rounded`}
                    onClick={() => setIsRequesting(true)}
                >
                    {currentStatus === 'pending' ? 'Oczekujące' :
                     currentStatus === 'in_progress' ? 'W trakcie' :
                     currentStatus === 'completed' ? 'Zakończone' :
                     currentStatus === 'canceled' ? 'Anulowane' : currentStatus}
                </button>
            )}
        </div>
    );
};

export default TaskStatusSelect;