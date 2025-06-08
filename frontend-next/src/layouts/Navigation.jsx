// Navigation.jsx
import { Link, useNavigate, useLocation } from "react-router-dom";
import { useState, useEffect } from "react";

const Navigation = ({ token, handleLogout, userRole }) => {
  const navigate = useNavigate();
  const location = useLocation();
  const [loading, setLoading] = useState(true);
  const [currentRole, setCurrentRole] = useState(null);

  useEffect(() => {
    const fetchUserRole = async () => {
      if (!token) {
        setLoading(false);
        setCurrentRole(null);
        return;
      }

      try {
        const response = await fetch('http://localhost:8080/users/me', {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });

        if (!response.ok) throw new Error('Failed to fetch user data');
        
        const userData = await response.json();
        setCurrentRole(userData.role);
      } catch (err) {
        console.error('Error fetching user role:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchUserRole();
  }, [token]);

  const handleLogoutClick = () => {
    handleLogout();
    navigate("/log");
  };

  const linkClass = (path) =>
    `no-underline font-medium transition-colors ${
      location.pathname === path
        ? "text-white border-b-2 border-purple-400"
        : "text-purple-400 hover:text-purple-300"
    }`;

  if (loading) {
    return <div>Loading navigation...</div>;
  }

  return (
    <nav className="flex gap-4 p-5 bg-gray-800 border-b border-gray-700 justify-center items-center">
      <Link className={linkClass("/")} to="/">🏠 O projekcie</Link>

      {token && (
        <>
          <Link className={linkClass("/profile")} to="/profile">👤 Profile</Link>
          <Link className={linkClass("/tasks")} to="/tasks">📋 Zadania</Link>
          <Link className={linkClass("/recepcionist")} to="/recepcionist">📋 Recepcja</Link>
        </>
      )}

      {token && currentRole === 'ADMIN' && (
        <>
          <Link className={linkClass("/admin")} to="/admin">⚙️ Panel admina</Link>
          <Link className={linkClass("/status-requests")} to="/status-requests">📋 Wnioski</Link>
        </>
      )}

      {token && currentRole === 'USER' && (
        <>
          <span className="text-gray-500 cursor-not-allowed">⚙️ Panel admina</span>
          <span className="text-gray-500 cursor-not-allowed">📋 Wnioski</span>
        </>
      )}

      {!token && (
        <Link className={linkClass("/log")} to="/log">🔐 Logowanie</Link>
      )}

      {token && (
        <button
          className="bg-red-600 hover:bg-red-500 text-white border-none py-2 px-4 rounded cursor-pointer font-medium transition-colors"
          onClick={handleLogoutClick}
        >
          🚪 Wyloguj
        </button>
      )}
    </nav>
  );
};

export default Navigation;
