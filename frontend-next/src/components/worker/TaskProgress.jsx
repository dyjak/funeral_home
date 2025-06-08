import React from 'react';

const TaskProgress = ({ task }) => {
    const calculateProgress = (task) => {
        if (!task.dueDate || !task.assignedAt) return 0;
        
        const now = new Date();
        const due = new Date(task.dueDate);
        const start = new Date(task.assignedAt);
        
        if (now < start) return 0;
        if (now > due) return 100;
        
        const total = due - start;
        const elapsed = now - start;
        return Math.round((elapsed / total) * 100);
    };

    return (
        <div className="mt-4">
            <div className="w-full bg-gray-600 rounded-full h-2.5">
                <div
                    className="bg-blue-600 h-2.5 rounded-full transition-all duration-300"
                    style={{ width: `${calculateProgress(task)}%` }}
                />
            </div>
            <p className="text-xs text-gray-400 mt-1">
                Pozostały czas: {calculateProgress(task)}%
            </p>
        </div>
    );
};

export default TaskProgress;