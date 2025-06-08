import React from 'react';

    const TaskFilters = ({ 
    filters: {  // Destructure the filter values
        filterName,
        filterOrderId,
        filterStatus,
        filterPriority,
        filterEmployee,
        filterDateFrom,
        filterDateTo
    },
    setFilters: {  // Destructure the setters
        setFilterName,
        setFilterOrderId,
        setFilterStatus,
        setFilterPriority,
        setFilterEmployee,
        setFilterDateFrom,
        setFilterDateTo
    }, 
    resetFilters, 
    generateReport, 
    orders, 
    employees 
}) => {

  const handleFilterChange = (e) => {
    const { name, value } = e.target;
    // Use the appropriate setter based on the input name
    switch(name) {
      case 'name':
        setFilterName(value);
        break;
      case 'orderId':
        setFilterOrderId(value);
        break;
      case 'status':
        setFilterStatus(value);
        break;
      case 'priority':
        setFilterPriority(value);
        break;
      case 'employeeId':
        setFilterEmployee(value);
        break;
      case 'dateFrom':
        setFilterDateFrom(value);
        break;
      case 'dateTo':
        setFilterDateTo(value);
        break;
    }
  };

  return (
    <div className="mb-8 bg-gray-800 p-4 rounded-lg">
      <div className="flex justify-between items-center mb-4">
        <h2 className="text-xl font-semibold">Filtrowanie zaawansowane</h2>
        <div className="flex gap-2">
          <button
            onClick={resetFilters}
            className="bg-gray-700 hover:bg-gray-600 text-white px-4 py-2 rounded text-sm"
          >
            Resetuj filtry
          </button>
          <button
            onClick={generateReport}
            className="bg-green-700 hover:bg-green-600 text-white px-4 py-2 rounded text-sm"
          >
            Generuj PDF
          </button>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 mb-4">
        <div>
          <label className="block text-gray-400 mb-1 text-sm">Nazwa zadania</label>
          <input
            type="text"
            className="w-full bg-gray-700 border border-gray-600 rounded p-2 text-gray-200"
            name="name"
            value={filterName}
            onChange={handleFilterChange}
            placeholder="Filtruj po nazwie..."
          />
        </div>
        
        <div>
          <label className="block text-gray-400 mb-1 text-sm">Zamówienie</label>
          <select
            className="w-full bg-gray-700 border border-gray-600 rounded p-2 text-gray-200"
            name="orderId"
            value={filterOrderId}
            onChange={handleFilterChange}
          >
            <option value="">Wszystkie</option>
            {orders.map((order) => (
              <option key={order.id} value={order.id}>
                {order.id} - {order.cadaverFirstName} {order.cadaverLastName}
                {order.deathCertificateNumber && ` (${order.deathCertificateNumber})`}
              </option>
            ))}
          </select>
        </div>

        <div>
          <label className="block text-gray-400 mb-1 text-sm">Status</label>
          <select
            className="w-full bg-gray-700 border border-gray-600 rounded p-2 text-gray-200"
            name="status"
            value={filterStatus}
            onChange={handleFilterChange}
          >
            <option value="">Wszystkie</option>
            <option value="pending">Oczekujące</option>
            <option value="in_progress">W trakcie</option>
            <option value="completed">Zakończone</option>
            <option value="canceled">Anulowane</option>
          </select>
        </div>

        <div>
          <label className="block text-gray-400 mb-1 text-sm">Priorytet</label>
          <select
            className="w-full bg-gray-700 border border-gray-600 rounded p-2 text-gray-200"
            name="priority"
            value={filterPriority}
            onChange={handleFilterChange}
          >
            <option value="">Wszystkie</option>
            <option value="low">Niski</option>
            <option value="medium">Średni</option>
            <option value="high">Wysoki</option>
          </select>
        </div>

        <div>
          <label className="block text-gray-400 mb-1 text-sm">Pracownik</label>
          <select
            className="w-full bg-gray-700 border border-gray-600 rounded p-2 text-gray-200"
            name="employeeId"
            value={filterEmployee}
            onChange={handleFilterChange}
          >
            <option value="">Wszyscy</option>
            {employees.map((employee) => (
              <option key={employee.id} value={employee.id}>
                {employee.firstName} {employee.lastName}
              </option>
            ))}
          </select>
        </div>

        <div className="flex gap-2">
          <div className="flex-1">
            <label className="block text-gray-400 mb-1 text-sm">Data od</label>
            <input
              type="date"
              className="w-full bg-gray-700 border border-gray-600 rounded p-2 text-gray-200"
              name="dateFrom"
              value={filterDateFrom}
              onChange={handleFilterChange}
            />
          </div>
          <div className="flex-1">
            <label className="block text-gray-400 mb-1 text-sm">Data do</label>
            <input
              type="date"
              className="w-full bg-gray-700 border border-gray-600 rounded p-2 text-gray-200"
              name="dateTo"
              value={filterDateTo}
              onChange={handleFilterChange}
            />
          </div>
        </div>
      </div>
    </div>
  );
};

export default TaskFilters;