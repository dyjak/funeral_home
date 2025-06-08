import React from 'react';
import TaskCard from './TaskCard';

const TaskList = ({ 
  tasks, 
  handleEditTask, 
  handleDeleteTask, 
  orders, 
  employees 
}) => {
  return (
    <div className="space-y-4">
      <h2 className="text-xl font-semibold">
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
            />
          ))}
        </div>
      )}
    </div>
  );
};

export default TaskList;