import React from 'react';

const TaskList = ({ 
  tasks, 
  handleEditTask, 
  handleDeleteTask, 
  orders, 
  employees 
}) => {
  const getPriorityText = (priority) => {
    switch(priority) {
      case 'low': return 'Niski';
      case 'medium': return 'Średni';
      case 'high': return 'Wysoki';
      default: return priority;
    }
  };

  const getStatusText = (status) => {
    switch(status) {
      case 'pending': return 'Oczekujące';
      case 'in_progress': return 'W trakcie';
      case 'completed': return 'Zakończone';
      case 'canceled': return 'Anulowane';
      default: return status;
    }
  };

  const getStatusColor = (status) => {
    switch(status) {
      case 'pending': return 'bg-yellow-600';
      case 'in_progress': return 'bg-blue-600';
      case 'completed': return 'bg-green-600';
      case 'canceled': return 'bg-red-600';
      default: return 'bg-gray-600';
    }
  };

  return (
    <div className="space-y-4">
      <h2 className="text-xl font-semibold mb-4">Lista zadań ({tasks.length})</h2>
      {tasks.length === 0 ? (
        <div className="bg-gray-800 p-4 rounded-lg text-center">
          <p>Brak zadań spełniających kryteria filtrowania</p>
        </div>
      ) : (
        <div className="grid gap-4">
          {tasks.map((task) => (
            <div key={task.id} className="bg-gray-800 p-4 rounded-lg shadow-lg">
              <div className="flex justify-between items-start mb-4">
                <div>
                  <h3 className="text-lg font-semibold">{task.taskName}</h3>
                  <p className="text-gray-400">{task.description}</p>
                </div>
                <div className="flex gap-2">
                  <button
                    onClick={() => handleEditTask(task)}
                    className="px-3 py-1 bg-blue-600 hover:bg-blue-500 text-white rounded"
                  >
                    Edytuj
                  </button>
                  <button
                    onClick={() => handleDeleteTask(task.id)}
                    className="px-3 py-1 bg-red-600 hover:bg-red-500 text-white rounded"
                  >
                    Usuń
                  </button>
                </div>
              </div>

              <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm">
                <div>
                  <span className="text-gray-400">Status:</span>
                  <span className={`ml-2 px-2 py-1 rounded text-white ${getStatusColor(task.status)}`}>
                    {getStatusText(task.status)}
                  </span>
                </div>
                <div>
                  <span className="text-gray-400">Priorytet:</span>
                  <span className="ml-2">{getPriorityText(task.priority)}</span>
                </div>
                <div>
                  <span className="text-gray-400">Pracownik:</span>
                  <span className="ml-2">
                    {task.assignedUser ? 
                      `${task.assignedUser.firstName} ${task.assignedUser.lastName}` : 
                      'Nie przypisano'}
                  </span>
                </div>
                <div>
                  <span className="text-gray-400">Zamówienie:</span>
                  <span className="ml-2">
                    {task.order ? `ID: ${task.order.id}` : 'Brak'}
                  </span>
                </div>
              </div>

              {task.dueDate && (
                <div className="mt-2 text-sm">
                  <span className="text-gray-400">Termin:</span>
                  <span className="ml-2">
                    {new Date(task.dueDate).toLocaleString('pl-PL')}
                  </span>
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default TaskList;