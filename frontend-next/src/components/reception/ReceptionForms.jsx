import React from 'react';

const ReceptionForms = ({
  currentUser,
  formData,
  handleClientChange,
  handleDeceasedChange,
  handleSubmit,
  loading,
  error,
  success
}) => {
  return (
    <div className="mb-8">
      <h2 className="text-2xl font-bold mb-6">Nowe zlecenie</h2>
      <form onSubmit={handleSubmit} className="space-y-6">
        {error && (
          <div className="p-4 bg-red-900 text-red-100 rounded-lg">
            {error}
          </div>
        )}
        {success && (
          <div className="p-4 bg-green-900 text-green-100 rounded-lg">
            {success}
          </div>
        )}

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {/* Client Data Section */}
          <div className="bg-gray-800 p-6 rounded-lg">
            <h2 className="text-xl font-semibold mb-4 pb-2 border-b border-gray-700">
              Dane klienta
            </h2>
            <div className="space-y-4">
              <div>
                <label className="block text-gray-400 mb-1 text-sm">Imię</label>
                <input
                  type="text"
                  name="firstName"
                  value={formData.client.firstName}
                  onChange={handleClientChange}
                  className="w-full bg-gray-700 border border-gray-600 rounded p-2 text-gray-200"
                  required
                />
              </div>
              <div>
                <label className="block text-gray-400 mb-1 text-sm">Nazwisko</label>
                <input
                  type="text"
                  name="lastName"
                  value={formData.client.lastName}
                  onChange={handleClientChange}
                  className="w-full bg-gray-700 border border-gray-600 rounded p-2 text-gray-200"
                  required
                />
              </div>
              <div>
                <label className="block text-gray-400 mb-1 text-sm">Telefon</label>
                <input
                  type="tel"
                  name="phone"
                  value={formData.client.phone}
                  onChange={handleClientChange}
                  className="w-full bg-gray-700 border border-gray-600 rounded p-2 text-gray-200"
                  required
                />
              </div>
              <div>
                <label className="block text-gray-400 mb-1 text-sm">Email</label>
                <input
                  type="email"
                  name="email"
                  value={formData.client.email}
                  onChange={handleClientChange}
                  className="w-full bg-gray-700 border border-gray-600 rounded p-2 text-gray-200"
                />
              </div>
            </div>
          </div>

          {/* Deceased Data Section */}
          <div className="bg-gray-800 p-6 rounded-lg">
            <h2 className="text-xl font-semibold mb-4 pb-2 border-b border-gray-700">
              Dane osoby zmarłej
            </h2>
            <div className="space-y-4">
              <div>
                <label className="block text-gray-400 mb-1 text-sm">Imię</label>
                <input
                  type="text"
                  name="firstName"
                  value={formData.deceased.firstName}
                  onChange={handleDeceasedChange}
                  className="w-full bg-gray-700 border border-gray-600 rounded p-2 text-gray-200"
                  required
                />
              </div>
              <div>
                <label className="block text-gray-400 mb-1 text-sm">Nazwisko</label>
                <input
                  type="text"
                  name="lastName"
                  value={formData.deceased.lastName}
                  onChange={handleDeceasedChange}
                  className="w-full bg-gray-700 border border-gray-600 rounded p-2 text-gray-200"
                  required
                />
              </div>
              <div>
                <label className="block text-gray-400 mb-1 text-sm">Data urodzenia</label>
                <input
                  type="date"
                  name="birthDate"
                  value={formData.deceased.birthDate}
                  onChange={handleDeceasedChange}
                  className="w-full bg-gray-700 border border-gray-600 rounded p-2 text-gray-200"
                  required
                />
              </div>
              <div>
                <label className="block text-gray-400 mb-1 text-sm">Data zgonu</label>
                <input
                  type="date"
                  name="deathDate"
                  value={formData.deceased.deathDate}
                  onChange={handleDeceasedChange}
                  className="w-full bg-gray-700 border border-gray-600 rounded p-2 text-gray-200"
                  required
                />
              </div>
              <div>
                <label className="block text-gray-400 mb-1 text-sm">Numer aktu zgonu</label>
                <input
                  type="text"
                  name="deathCertificateNumber"
                  value={formData.deceased.deathCertificateNumber}
                  onChange={handleDeceasedChange}
                  className="w-full bg-gray-700 border border-gray-600 rounded p-2 text-gray-200"
                  required
                />
              </div>
            </div>
          </div>
        </div>

        <div className="flex justify-end mt-6">
          <button
            type="submit"
            disabled={loading}
            className="bg-blue-700 hover:bg-blue-600 text-white px-6 py-2 rounded disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {loading ? 'Przetwarzanie...' : 'Utwórz zlecenie'}
          </button>
        </div>
      </form>
    </div>
  );
};

export default ReceptionForms;
