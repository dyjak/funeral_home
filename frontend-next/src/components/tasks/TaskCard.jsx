import React, { useState } from 'react';

const TaskCard = ({ 
  task, 
  handleEditTask, 
  handleDeleteTask, 
  orders, 
  employees,
  cardBgClass 
}) => {
  const [isEditing, setIsEditing] = useState(false);
  const [editForm, setEditForm] = useState({
    taskName: task.taskName,
    description: task.description,
    dueDate: task.dueDate ? task.dueDate.substring(0, 16) : '',
    priority: task.priority,
    status: task.status,
    orderId: task.order?.id ? String(task.order.id) : '', // always string
    employeeId: task.assignedUser?.id ? String(task.assignedUser.id) : '' // always string
  });

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setEditForm({ ...editForm, [name]: value });
  };

  const handleSave = async () => {
    try {
      // Transform the data to match the expected format
      const taskData = {
        id: task.id,
        taskName: editForm.taskName,
        description: editForm.description,
        priority: editForm.priority,
        status: editForm.status,
        dueDate: editForm.dueDate ? editForm.dueDate + ":00" : null,
        order: editForm.orderId ? { id: parseInt(editForm.orderId) } : null,
        assignedUser: editForm.employeeId ? { id: parseInt(editForm.employeeId) } : null
      };

      await handleEditTask(taskData);
      setIsEditing(false);
    } catch (err) {
      console.error('Error saving task:', err);
      alert('Failed to save changes: ' + err.message);
    }
  };

  const handleCancel = () => {
    setIsEditing(false);
    // Reset form to original task values
    setEditForm({
      taskName: task.taskName,
      description: task.description,
      dueDate: task.dueDate ? task.dueDate.substring(0, 16) : '',
      priority: task.priority,
      status: task.status,
      orderId: task.order?.id ? String(task.order.id) : '', // always string
      employeeId: task.assignedUser?.id ? String(task.assignedUser.id) : '' // always string
    });
  };

  return (
    <div className={`${cardBgClass} rounded-lg shadow overflow-hidden`}>
      <div className="p-4">
        {/* Task info */}
        <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-2">
          <div>
            <p className="text-lg font-semibold">{task.taskName}</p>
            <p className="text-sm text-gray-400">
              {task.status === "pending" && "Oczekujące"}
              {task.status === "in_progress" && "W trakcie"}
              {task.status === "completed" && "Zakończone"}
              {task.status === "canceled" && "Anulowane"}
              {" | "}
              {task.priority === "low" && "Niski priorytet"}
              {task.priority === "medium" && "Średni priorytet"}
              {task.priority === "high" && "Wysoki priorytet"}
            </p>
            <p className="text-sm text-gray-400">{task.description}</p>
            <p className="text-sm text-gray-500">
              Termin: {task.dueDate ? new Date(task.dueDate).toLocaleString('pl-PL') : "nie określono"}
            </p>
            <p className="text-sm text-gray-500">
              Zamówienie: {(task.order?.cadaverFirstName && task.order?.cadaverLastName) ? 
                `${task.order.cadaverFirstName} ${task.order.cadaverLastName}` : 
                (task.order?.id ? `ID: ${task.order.id}` : "brak")}
            </p>
            <p className="text-sm text-gray-500">
              Przypisany: {task.assignedUser ? `${task.assignedUser.firstName} ${task.assignedUser.lastName}` : "brak"}
            </p>
          </div>
          <div className="flex gap-2 mt-2 md:mt-0">
            <button
              className="bg-blue-700 hover:bg-blue-600 text-white px-4 py-2 rounded text-sm"
              onClick={() => setIsEditing(!isEditing)}
            >
              {isEditing ? 'Anuluj' : 'Edytuj'}
            </button>
            <button
              className="bg-red-700 hover:bg-red-600 text-white px-4 py-2 rounded text-sm"
              onClick={() => handleDeleteTask(task.id)}
            >
              Usuń
            </button>
          </div>
        </div>

        {/* Edit form */}
        {isEditing && (
          <div className="mt-4 border-t border-gray-700 pt-4">
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              <div>
                <label className="block text-gray-400 mb-1 text-sm">Nazwa zadania</label>
                <input
                  type="text"
                  name="taskName"
                  className="w-full bg-gray-700 border border-gray-600 rounded p-2 text-gray-200"
                  value={editForm.taskName}
                  onChange={handleInputChange}
                />
              </div>
              <div>
                <label className="block text-gray-400 mb-1 text-sm">Opis</label>
                <input
                  type="text"
                  name="description"
                  className="w-full bg-gray-700 border border-gray-600 rounded p-2 text-gray-200"
                  value={editForm.description}
                  onChange={handleInputChange}
                />
              </div>
              <div>
                <label className="block text-gray-400 mb-1 text-sm">Termin</label>
                <input
                  type="datetime-local"
                  name="dueDate"
                  className="w-full bg-gray-700 border border-gray-600 rounded p-2 text-gray-200"
                  value={editForm.dueDate}
                  onChange={handleInputChange}
                />
              </div>
              <div>
                <label className="block text-gray-400 mb-1 text-sm">Priorytet</label>
                <select
                  name="priority"
                  className="w-full bg-gray-700 border border-gray-600 rounded p-2 text-gray-200"
                  value={editForm.priority}
                  onChange={handleInputChange}
                >
                  <option value="low">Niski</option>
                  <option value="medium">Średni</option>
                  <option value="high">Wysoki</option>
                </select>
              </div>
              <div>
                <label className="block text-gray-400 mb-1 text-sm">Status</label>
                <select
                  name="status"
                  className="w-full bg-gray-700 border border-gray-600 rounded p-2 text-gray-200"
                  value={editForm.status}
                  onChange={handleInputChange}
                >
                  <option value="pending">Oczekujące</option>
                  <option value="in_progress">W trakcie</option>
                  <option value="completed">Zakończone</option>
                  <option value="canceled">Anulowane</option>
                </select>
              </div>
              <div>
                <label className="block text-gray-400 mb-1 text-sm">Zamówienie</label>
                <select
                  name="orderId"
                  className="w-full bg-gray-700 border border-gray-600 rounded p-2 text-gray-200"
                  value={editForm.orderId}
                  onChange={handleInputChange}
                >
                  <option value="">Brak</option>
                  {orders.map((order) => (
                    <option key={order.id} value={order.id}>
                      {order.id} - {order.cadaverFirstName} {order.cadaverLastName}
                    </option>
                  ))}
                </select>
              </div>
              <div>
                <label className="block text-gray-400 mb-1 text-sm">Pracownik</label>
                <select
                  name="employeeId"
                  className="w-full bg-gray-700 border border-gray-600 rounded p-2 text-gray-200"
                  value={editForm.employeeId}
                  onChange={handleInputChange}
                >
                  <option value="">Brak</option>
                  {employees.map((employee) => (
                    <option key={employee.id} value={employee.id}>
                      {employee.firstName} {employee.lastName}
                    </option>
                  ))}
                </select>
              </div>
            </div>
            <div className="flex justify-end gap-2 mt-4">
              <button
                className="bg-gray-700 hover:bg-gray-600 text-white px-4 py-2 rounded"
                onClick={handleCancel}
              >
                Anuluj
              </button>
              <button
                className="bg-blue-700 hover:bg-blue-600 text-white px-4 py-2 rounded"
                onClick={handleSave}
              >
                Zapisz zmiany
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default TaskCard;