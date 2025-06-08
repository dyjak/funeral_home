import React from 'react';
import TaskStatusSelect from './TaskStatusSelect';
import TaskProgress from './TaskProgress';

const TaskCard = ({ task, onStatusChangeRequest }) => {
    const getPriorityText = (priority) => {
        switch(priority) {
            case 'low': return 'Niski';
            case 'medium': return 'Średni';
            case 'high': return 'Wysoki';
            default: return priority;
        }
    };

    return (
        <div className="mt-4 p-4 bg-gray-700 rounded-lg">
            <div className="flex justify-between items-start">
                <div>
                    <h2 className="text-xl font-semibold">{task.taskName}</h2>
                    <p className="text-gray-400">{task.description}</p>
                </div>
                <TaskStatusSelect 
                    currentStatus={task.status}
                    taskId={task.id}
                    onStatusChangeRequest={onStatusChangeRequest}
                />
            </div>
            
            <TaskProgress task={task} />

            <div className="mt-2 text-sm text-gray-400">
                <p>Priorytet: {getPriorityText(task.priority)}</p>
                <p>Termin: {task.dueDate ? new Date(task.dueDate).toLocaleString('pl-PL') : 'Nie określono'}</p>
                {task.statusChangeRequest && (
                    <p className="mt-2 text-yellow-400">
                        Oczekująca zmiana statusu: {getPriorityText(task.statusChangeRequest.requestedStatus)}
                    </p>
                )}
            </div>
        </div>
    );
};

export default TaskCard;