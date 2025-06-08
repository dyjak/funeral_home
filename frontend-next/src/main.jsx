import { StrictMode, useState, useEffect } from "react";
import { createRoot } from "react-dom/client";
import { createBrowserRouter, RouterProvider, Navigate } from "react-router-dom";
import App from "./pages/App.jsx";
import Profile from "./pages/Profile.jsx";
import Raports from "./pages/Raports.jsx";
import Log from "./pages/Log.jsx";
import Admin from "./pages/admin-panel/Admin.jsx";
import RootLayout from "./layouts/RootLayout.jsx";
import "./styles/index.css";
import TaskPlans from "./pages/TaskPlans.jsx";
import Recepcionist from "./pages/Receptionist.jsx";
import TaskWork from "./pages/TaskWork.jsx";
import StatusRequests from "./pages/StatusRequests.jsx";

// 🔧 Komponent z tokenem globalnym
const Root = () => {
  const [token, setToken] = useState(localStorage.getItem("token"));
  const [userRole, setUserRole] = useState(localStorage.getItem("userRole"));
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchUserRole = async () => {
      if (!token) {
        setLoading(false);
        setUserRole(null);
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
        setUserRole(userData.role);
        localStorage.setItem("userRole", userData.role);
      } catch (err) {
        console.error('Error fetching user role:', err);
        setToken(null);
        setUserRole(null);
        localStorage.removeItem('token');
        localStorage.removeItem('userRole');
      } finally {
        setLoading(false);
      }
    };

    fetchUserRole();
  }, [token]);

  // Protected Route Component
  const ProtectedTaskRoute = () => {
    console.log('Current role:', userRole); // Debug log

    if (loading) {
      return <div>Loading...</div>;
    }

    if (!token) {
      return <Navigate to="/log" replace />;
    }

    // Make sure we have a role before deciding
    if (!userRole) {
      return <div>Loading user role...</div>;
    }

    if (userRole === 'ADMIN') {
      return <TaskPlans />;
    } else if (userRole === 'USER') {
      return <TaskWork />;
    }

    // If role doesn't match expected values, redirect to home
    return <Navigate to="/" replace />;
  };

  const router = createBrowserRouter([
    {
      path: "/",
      element: <RootLayout token={token} setToken={setToken} userRole={userRole} />,
      children: [
        {
          path: "/",
          element: <App token={token} setToken={setToken} />
        },
        {
          path: "/profile",
          element: token ? <Profile token={token} /> : <Navigate to="/log" replace />
        },
        {
          path: "/admin",
          element: userRole === 'ADMIN' ? <Admin token={token} /> : <Navigate to="/" replace />
        },
        {
          path: "/tasks",
          element: <ProtectedTaskRoute />
        },
        {
          path: "/status-requests",
          element: userRole === 'ADMIN' ? <StatusRequests /> : <Navigate to="/" replace />
        },
        {
          path: "/recepcionist",
          element: token ? <Recepcionist token={token} /> : <Navigate to="/log" replace />
        }
      ]
    },
    {
      path: "/log",
      element: <Log setToken={setToken} />
    },
    {
      path: "*",
      element: <h1>404 - Strona nie istnieje</h1>
    }
  ]);

  if (loading) {
    return <div>Loading...</div>;
  }

  return <RouterProvider router={router} />;
};

createRoot(document.getElementById("root")).render(
    <StrictMode>
      <Root />
    </StrictMode>
);