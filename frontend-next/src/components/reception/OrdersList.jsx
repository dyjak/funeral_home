import React, { useState } from 'react';

const OrdersList = ({ orders, generateReport, loading, generateBulkReports }) => {
  const [selectedOrders, setSelectedOrders] = useState([]);
  const [filters, setFilters] = useState({
    dateFrom: '',
    dateTo: '',
    clientName: '',
    status: ''
  });

  const handleFilterChange = (e) => {
    const { name, value } = e.target;
    setFilters(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const resetFilters = () => {
    setFilters({
      dateFrom: '',
      dateTo: '',
      clientName: '',
      status: ''
    });
  };

  const handleCheckboxChange = (orderId) => {
    setSelectedOrders(prev => {
      if (prev.includes(orderId)) {
        return prev.filter(id => id !== orderId);
      } else {
        return [...prev, orderId];
      }
    });
  };

  const filteredOrders = orders.filter(order => {
    if (filters.clientName && !`${order.client.firstName} ${order.client.lastName}`
      .toLowerCase()
      .includes(filters.clientName.toLowerCase())) {
      return false;
    }
    
    if (filters.dateFrom && new Date(order.orderDate) < new Date(filters.dateFrom)) {
      return false;
    }
    
    if (filters.dateTo && new Date(order.orderDate) > new Date(filters.dateTo)) {
      return false;
    }

    if (filters.status && order.status !== filters.status) {
      return false;
    }
    
    return true;
  });

  return (
    <div className="mt-8">
      <div className="bg-gray-800 p-4 rounded-lg mb-4">
        <h3 className="text-xl font-semibold mb-4">Filtry</h3>
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
          <div>
            <label className="block text-gray-400 mb-1 text-sm">Data od</label>
            <input
              type="date"
              name="dateFrom"
              value={filters.dateFrom}
              onChange={handleFilterChange}
              className="w-full bg-gray-700 border border-gray-600 rounded p-2 text-gray-200"
            />
          </div>
          <div>
            <label className="block text-gray-400 mb-1 text-sm">Data do</label>
            <input
              type="date"
              name="dateTo"
              value={filters.dateTo}
              onChange={handleFilterChange}
              className="w-full bg-gray-700 border border-gray-600 rounded p-2 text-gray-200"
            />
          </div>
          <div>
            <label className="block text-gray-400 mb-1 text-sm">Klient</label>
            <input
              type="text"
              name="clientName"
              value={filters.clientName}
              onChange={handleFilterChange}
              placeholder="Wyszukaj po nazwisku..."
              className="w-full bg-gray-700 border border-gray-600 rounded p-2 text-gray-200"
            />
          </div>
          <div>
            <label className="block text-gray-400 mb-1 text-sm">Status</label>
            <select
              name="status"
              value={filters.status}
              onChange={handleFilterChange}
              className="w-full bg-gray-700 border border-gray-600 rounded p-2 text-gray-200"
            >
              <option value="">Wszystkie</option>
              <option value="pending">Oczekujące</option>
              <option value="in_progress">W trakcie</option>
              <option value="completed">Zakończone</option>
              <option value="canceled">Anulowane</option>
            </select>
          </div>
        </div>
        <div className="mt-4 flex justify-end">
          <button
            onClick={resetFilters}
            className="bg-gray-700 hover:bg-gray-600 text-white px-4 py-2 rounded text-sm"
          >
            Resetuj filtry
          </button>
        </div>
      </div>

      <div className="flex justify-between items-center mb-4">
        <h2 className="text-xl font-semibold">Lista zamówień ({filteredOrders.length})</h2>
        {selectedOrders.length > 0 && (
          <button
            onClick={() => generateBulkReports(selectedOrders)}
            disabled={loading}
            className="bg-blue-700 hover:bg-blue-600 text-white px-4 py-2 rounded text-sm"
          >
            {loading ? 'Generowanie...' : `Generuj raporty (${selectedOrders.length})`}
          </button>
        )}
      </div>

      <div className="space-y-4">
        {filteredOrders.map(order => (
          <div key={order.id} className="bg-gray-800 p-4 rounded-lg flex items-center justify-between">
            <div className="flex items-center">
              <input
                type="checkbox"
                checked={selectedOrders.includes(order.id)}
                onChange={() => handleCheckboxChange(order.id)}
                className="mr-4 h-5 w-5"
              />
              <div>
                <p className="font-semibold">
                  Zamówienie #{order.id} - {order.client.firstName} {order.client.lastName}
                </p>
                <p className="text-sm text-gray-400">
                  Zmarły: {order.cadaverFirstName} {order.cadaverLastName}
                </p>
                <p className="text-sm text-gray-500">
                  Data: {new Date(order.orderDate).toLocaleString('pl-PL')}
                </p>
              </div>
            </div>
            <button
              onClick={() => generateReport(order.id)}
              disabled={loading}
              className="bg-blue-700 hover:bg-blue-600 text-white px-4 py-2 rounded text-sm"
            >
              {loading ? 'Generowanie...' : 'Generuj raport'}
            </button>
          </div>
        ))}
      </div>
    </div>
  );
};

export default OrdersList;