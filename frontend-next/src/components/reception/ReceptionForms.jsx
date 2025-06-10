import React, { useState } from 'react';
import { useAlert, AlertType } from '../../contexts/AlertContext';

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
  const { addAlert } = useAlert();

  // Name validation - only letters and spaces
  const handleNameInput = (e) => {
    const { name, value } = e.target;
    // Allow only letters (including Polish characters) and spaces
    if (value === '' || /^[A-Za-zĄąĆćĘęŁłŃńÓóŚśŹźŻż\s]*$/.test(value)) {
      handleClientChange(e);
    }
  };

  // Name validation for deceased person
  const handleDeceasedNameInput = (e) => {
    const { name, value } = e.target;
    // Allow only letters (including Polish characters) and spaces
    if (value === '' || /^[A-Za-zĄąĆćĘęŁłŃńÓóŚśŹźŻż\s]*$/.test(value)) {
      handleDeceasedChange(e);
    }
  };

  // Phone validation - only numbers, max 9 digits
  const handlePhoneInput = (e) => {
    const { name, value } = e.target;
    if (value === '' || (/^\d*$/.test(value) && value.length <= 9)) {
      handleClientChange(e);
    }
  };

  const validateForm = () => {
    // Email validation
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (formData.client.email && !emailRegex.test(formData.client.email)) {
      addAlert(AlertType.ERROR, "Nieprawidłowy format adresu email");
      return false;
    }

    // Date validation
    if (formData.deceased.birthDate && formData.deceased.deathDate) {
      const birthDate = new Date(formData.deceased.birthDate);
      const deathDate = new Date(formData.deceased.deathDate);
      
      if (deathDate < birthDate) {
        addAlert(AlertType.ERROR, "Data zgonu nie może być wcześniejsza niż data urodzenia");
        return false;
      }
    }

    return true;
  };

  const handleFormSubmit = async (e) => {
    e.preventDefault();
    if (!validateForm()) {
      return;
    }
    await handleSubmit(e);
  };

  return (
    <div className="mb-8">
      <h2 className="text-2xl font-bold mb-6">Nowe zlecenie</h2>
      <form onSubmit={handleFormSubmit} className="space-y-6">
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
                  onChange={handleNameInput}
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
                  onChange={handleNameInput}
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
                  onChange={handlePhoneInput}
                  pattern="[0-9]{9}"
                  className="w-full bg-gray-700 border border-gray-600 rounded p-2 text-gray-200"
                  required
                  placeholder="Wprowadź 9 cyfr"
                />
              </div>
              <div>
                <label className="block text-gray-400 mb-1 text-sm">Email</label>
                <input
                  type="email"
                  name="email"
                  value={formData.client.email}
                  onChange={handleClientChange} // Changed from handleEmailChange
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
                  onChange={handleDeceasedNameInput}
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
                  onChange={handleDeceasedNameInput}
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
                  max={new Date().toISOString().split('T')[0]}
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
                  max={new Date().toISOString().split('T')[0]}
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
