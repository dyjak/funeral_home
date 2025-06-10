import React from 'react';
import { useAlert, AlertType } from '../../contexts/AlertContext';

const TaskForm = ({ 
  formData, 
  handleInputChange, 
  handleAddTask, 
  orders, 
  employees
}) => {
  const { addAlert } = useAlert();

  // Get current date-time in ISO format for min attribute
  const now = new Date();
  const minDateTime = now.toISOString().slice(0, 16);

  const handleSubmit = async () => {
    try {
      // Validate due date if one is set
      if (formData.dueDate) {
        const selectedDate = new Date(formData.dueDate);
        if (selectedDate < now) {
          addAlert(AlertType.ERROR, "Nie można ustawić terminu w przeszłości");
          return;
        }
      }

      await handleAddTask();
      addAlert(AlertType.SUCCESS, "Zadanie zostało utworzone");
    } catch (err) {
      addAlert(AlertType.ERROR, `Błąd podczas tworzenia zadania: ${err.message}`);
    }
  };

  // Update the date input to include min attribute
  return (
    <div className="mb-8 bg-gray-800 p-4 rounded-lg">
      <h2 className="text-xl font-semibold mb-4">Dodaj nowe zadanie</h2>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 mb-4">
        <div>
          <label className="block text-gray-400 mb-1 text-sm">Nazwa zadania</label>
          <input
            type="text"
            className="w-full bg-gray-700 border border-gray-600 rounded p-2 text-gray-200"
            name="taskName"
            value={formData.taskName}
            onChange={handleInputChange}
          />
        </div>
        <div>
          <label className="block text-gray-400 mb-1 text-sm">Opis zadania</label>
          <input
            type="text"
            className="w-full bg-gray-700 border border-gray-600 rounded p-2 text-gray-200"
            name="description"
            value={formData.description}
            onChange={handleInputChange}
          />
        </div>
        <div>
          <label className="block text-gray-400 mb-1 text-sm">Termin wykonania</label>
          <input
            type="datetime-local"
            className="w-full bg-gray-700 border border-gray-600 rounded p-2 text-gray-200"
            name="dueDate"
            value={formData.dueDate}
            onChange={handleInputChange}
            min={minDateTime}
          />
        </div>
        <div>
          <label className="block text-gray-400 mb-1 text-sm">Priorytet</label>
          <select
            className="w-full bg-gray-700 border border-gray-600 rounded p-2 text-gray-200"
            name="priority"
            value={formData.priority}
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
            className="w-full bg-gray-700 border border-gray-600 rounded p-2 text-gray-200"
            name="status"
            value={formData.status}
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
            className="w-full bg-gray-700 border border-gray-600 rounded p-2 text-gray-200"
            name="orderId"
            value={formData.orderId}
            onChange={handleInputChange}
          >
            <option value="">Brak</option>
            {orders.map((order) => (
              <option key={order.id} value={order.id}>
                {order.id} - {order.cadaverFirstName} {order.cadaverLastName}
                {order.deathCertificateNumber && ` (${order.deathCertificateNumber})`}
              </option>
            ))}
          </select>
        </div>
        <div>
          <label className="block text-gray-400 mb-1 text-sm">Pracownik</label>
          <select
            className="w-full bg-gray-700 border border-gray-600 rounded p-2 text-gray-200"
            name="employeeId"
            value={formData.employeeId}
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
      <div className="flex justify-end">
        <button
          onClick={handleSubmit}
          className="bg-green-700 hover:bg-green-600 text-white px-6 py-2 rounded"
        >
          Dodaj zadanie
        </button>
      </div>
    </div>
  );
};

export default TaskForm;