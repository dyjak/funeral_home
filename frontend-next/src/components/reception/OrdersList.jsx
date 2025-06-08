import React from "react";

const OrdersList = ({ orders, generateReport, loading }) => {
  return (
    <div className="mt-8">
      <h2 className="text-2xl font-bold mb-6">
        Lista zamówień ({orders.length})
      </h2>
      <div className="space-y-4">
        {orders.length === 0 ? (
          <div className="bg-gray-800 p-4 rounded-lg text-center">
            <p>Brak zamówień spełniających kryteria filtrowania</p>
          </div>
        ) : (
          orders.map((order) => (
            <div
              key={order.id}
              className="bg-gray-800 rounded-lg shadow overflow-hidden"
            >
              <div className="p-4 flex flex-col md:flex-row md:items-center md:justify-between gap-2">
                <div>
                  <p className="text-lg font-semibold">
                    Zamówienie #{order.id}
                  </p>
                  <p className="text-sm text-gray-400">
                    Klient: {order.client.firstName} {order.client.lastName}
                  </p>
                  <p className="text-sm text-gray-400">
                    Zmarły: {order.cadaverFirstName} {order.cadaverLastName}
                  </p>
                  <p className="text-sm text-gray-500">
                    Data zamówienia:{" "}
                    {new Date(order.orderDate).toLocaleString("pl-PL")}
                  </p>
                </div>
                <div className="flex gap-2">
                  <button
                    onClick={() => generateReport(order.id)}
                    disabled={loading}
                    className="bg-blue-700 hover:bg-blue-600 text-white px-4 py-2 rounded text-sm disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    {loading ? "Generowanie..." : "Generuj raport"}
                  </button>
                </div>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default OrdersList;
