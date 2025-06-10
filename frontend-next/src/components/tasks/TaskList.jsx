import React from 'react';
import TaskCard from './TaskCard';

const TaskList = ({ 
  tasks, 
  handleEditTask, 
  handleDeleteTask, 
  orders, 
  employees 
}) => {
  // Helper to determine background color and text
  const getCardBgClass = (status) => {
    if (status === "canceled") return "bg-red-900 bg-opacity-60 text-red-200 border border-red-700";
    if (status === "completed") return "bg-green-900 bg-opacity-60 text-green-200 border border-green-700";
    return "bg-gray-800 text-gray-100 border border-gray-700";
  };

  return (
    <div className="space-y-4">
      <h2 className="text-xl font-semibold mb-4">
        Lista zadań ({tasks.length})
      </h2>
      {tasks.length === 0 ? (
        <div className="bg-gray-800 p-4 rounded-lg text-center">
          <p>Brak zadań spełniających kryteria filtrowania</p>
        </div>
      ) : (
        <div className="space-y-4">
          {tasks.map((task) => (
            <TaskCard
              key={task.id}
              task={task}
              handleEditTask={handleEditTask}
              handleDeleteTask={handleDeleteTask}
              orders={orders}
              employees={employees}
              cardBgClass={getCardBgClass(task.status)}
            />
          ))}
        </div>
      )}
    </div>
  );
};

export default TaskList;