import React, { useState, useEffect } from 'react';
import TaskForm from '../components/tasks/TaskForm';
import TaskFilters from '../components/tasks/TaskFilters';
import TaskList from '../components/tasks/TaskList';
import TaskEditForm from '../components/tasks/TaskEditForm';

const TaskPlans = () => {
  const [tasks, setTasks] = useState([]);
  const [orders, setOrders] = useState([]);
  const [employees, setEmployees] = useState([]);
  const [editingTask, setEditingTask] = useState(null);
  const [formData, setFormData] = useState({
    taskName: '',
    description: '',
    dueDate: '',
    priority: 'medium',
    status: 'pending',
    orderId: '',
    employeeId: ''
  });

  // Filter states
  const [filterOrderId, setFilterOrderId] = useState("");
  const [filterStatus, setFilterStatus] = useState("");
  const [filterPriority, setFilterPriority] = useState("");
  const [filterEmployee, setFilterEmployee] = useState("");
  const [filterDateFrom, setFilterDateFrom] = useState("");
  const [filterDateTo, setFilterDateTo] = useState("");
  const [filterName, setFilterName] = useState("");

  const fetchEmployees = async () => {
    try {
      const token = localStorage.getItem("token");
      const res = await fetch("http://localhost:8080/users", {
        headers: {
          "Authorization": `Bearer ${token}`
        }
      });
      if (!res.ok) throw new Error("Failed to fetch employees");
      const data = await res.json();
      setEmployees(data);
    } catch (err) {
      console.error("Error fetching employees:", err);
    }
  };

  const fetchTasks = async () => {
    try {
      const token = localStorage.getItem("token");
      const res = await fetch("http://localhost:8080/tasks", {
        headers: {
          "Authorization": `Bearer ${token}`,
          "Accept": "application/json"
        }
      });
      
      if (!res.ok) {
        throw new Error(`Failed to fetch tasks: ${res.status}`);
      }
      
      const text = await res.text();
      console.log('Raw response:', text); // Debug raw response
      
      try {
        const data = JSON.parse(text);
        console.log('Parsed data:', data); // Debug parsed data
        setTasks(data);
      } catch (parseError) {
        console.error('JSON Parse error:', parseError);
        console.log('Invalid JSON:', text);
      }
    } catch (err) {
      console.error("Error fetching tasks:", err);
    }
  };

  const fetchOrders = async () => {
    try {
      const token = localStorage.getItem("token");
      const response = await fetch("http://localhost:8080/orders", {
        headers: {
          "Authorization": `Bearer ${token}`
        }
      });
      if (!response.ok) throw new Error("Failed to fetch orders");
      const data = await response.json();
      setOrders(data);
    } catch (error) {
      console.error("Error fetching orders:", error);
    }
  };

  useEffect(() => {
    fetchTasks();
    fetchOrders();
    fetchEmployees();
  }, []);

  const resetForm = () => {
    setFormData({
      taskName: '',
      description: '',
      dueDate: '',
      priority: 'medium',
      status: 'pending',
      orderId: '',
      employeeId: ''
    });
  };
  
  const handleAddTask = async () => {
  const token = localStorage.getItem("token");

  try {
    // Format dueDate to always include seconds
    let dueDate = formData.dueDate;
    if (dueDate && dueDate.length === 16) {
      // "2025-06-09T16:24" -> "2025-06-09T16:24:00"
      dueDate = dueDate + ":00";
    }

    const taskData = {
      taskName: formData.taskName,
      description: formData.description,
      priority: formData.priority,
      status: formData.status,
      dueDate: dueDate,
      order: formData.orderId ? { id: parseInt(formData.orderId) } : null,
      assignedUser: formData.employeeId ? { id: parseInt(formData.employeeId) } : null
    };

    console.log('Sending task data:', taskData);

    const res = await fetch("http://localhost:8080/tasks", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify(taskData)
    });

    if (!res.ok) {
        const errorText = await res.text();
        console.error('Error response:', errorText);
        throw new Error(`Failed to add task: ${res.status} ${errorText}`);
    }

    const newTask = await res.json();
    console.log('New task response:', newTask);

    // Fetch the complete task data after creation
    const completeTaskRes = await fetch(`http://localhost:8080/tasks/${newTask.id}`, {
        headers: {
            "Authorization": `Bearer ${token}`,
            "Accept": "application/json"
        }
    });

    if (!completeTaskRes.ok) {
        throw new Error('Failed to fetch complete task data');
    }

    const completeTask = await completeTaskRes.json();
    setTasks(prev => [...prev, completeTask]);
    resetForm();
    return true; // Return true on success for the alert
  } catch (err) {
    console.error("Error adding task:", err);
    throw err; // Propagate error for the alert
  }
};
  
  
  const handleDeleteTask = async (taskId) => {
    const token = localStorage.getItem("token");
  
    try {
      const res = await fetch(`http://localhost:8080/tasks/${taskId}`, {
        method: "DELETE",
        headers: {
          "Authorization": `Bearer ${token}`,
        },
      });
  
      if (!res.ok) throw new Error("Błąd przy usuwaniu zadania");
  
      setTasks((prev) => prev.filter((task) => task.id !== taskId));
    } catch (err) {
      console.error("Błąd usuwania zadania:", err);
    }
  };
  
  const resetFilters = () => {
    setFilterOrderId("");
    setFilterStatus("");
    setFilterPriority("");
    setFilterEmployee("");
    setFilterDateFrom("");
    setFilterDateTo("");
    setFilterName("");
  };
  
  const generateReport = async () => {
    const token = localStorage.getItem("token");
  
    try {
      const response = await fetch("http://localhost:8080/reports/tasks", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify({
          filters: {
            orderId: filterOrderId ? Number(filterOrderId) : null,
            status: filterStatus || null,
            priority: filterPriority || null,
            employeeId: filterEmployee ? Number(filterEmployee) : null,
            dateFrom: filterDateFrom || null,
            dateTo: filterDateTo || null,
            name: filterName || null
          }
        }),
      });
  
      if (!response.ok) {
        throw new Error(`Błąd: ${response.status} ${response.statusText}`);
      }
  
      // Create blob and open PDF in new window
      const blob = await response.blob();
      const url = window.URL.createObjectURL(blob);
      window.open(url);
    } catch (err) {
      console.error("Błąd przy generowaniu raportu:", err);
      alert(`Wystąpił błąd podczas generowania raportu: ${err.message}`);
    }
  };

const filteredTasks = tasks.filter(task => {
    // Filter by name
    if (filterName && !task.taskName.toLowerCase().includes(filterName.toLowerCase())) {
        return false;
    }

    // Filter by order
    if (filterOrderId && (!task.order || task.order.id !== parseInt(filterOrderId))) {
        return false;
    }

    // Filter by status
    if (filterStatus && task.status !== filterStatus) {
        return false;
    }

    // Filter by priority
    if (filterPriority && task.priority !== filterPriority) {
        return false;
    }

    // Filter by employee
    if (filterEmployee && (!task.assignedUser || task.assignedUser.id !== parseInt(filterEmployee))) {
        return false;
    }

    // Filter by date range
    if (filterDateFrom && task.dueDate) {
        const taskDate = new Date(task.dueDate);
        const fromDate = new Date(filterDateFrom);
        if (taskDate < fromDate) {
            return false;
        }
    }

    if (filterDateTo && task.dueDate) {
        const taskDate = new Date(task.dueDate);
        const toDate = new Date(filterDateTo);
        toDate.setHours(23, 59, 59); // Include the entire day
        if (taskDate > toDate) {
            return false;
        }
    }

    return true;
});

  const handleEditClick = (task) => {
    console.log('Starting edit for task:', task);
    setEditingTask(task);
    
    // Convert dates to proper format for input fields
    const formattedDueDate = task.dueDate ? 
        new Date(task.dueDate).toISOString().substring(0, 16) : '';

    const newFormData = {
        taskName: task.taskName,
        description: task.description || '',
        dueDate: formattedDueDate,
        priority: task.priority,
        status: task.status,
        orderId: task.order?.id?.toString() || '',
        employeeId: task.assignedUser?.id?.toString() || ''
    };
    
    console.log('Setting form data to:', newFormData);
    setFormData(newFormData);
};

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    console.log(`Field ${name} changing to:`, value);
    console.log('Previous form state:', formData);
    
    setFormData(prev => {
        const newState = {
            ...prev,
            [name]: value
        };
        console.log('New form state:', newState);
        return newState;
    });
  };

  const handleEditTask = async (taskData) => {
    const token = localStorage.getItem("token");
    try {
      console.log('Received task data for update:', taskData);

      const transformedData = {
        id: taskData.id,
        taskName: taskData.taskName,
        description: taskData.description,
        dueDate: taskData.dueDate,
        priority: taskData.priority,
        status: taskData.status,
        order: taskData.order ? taskData.order : (taskData.orderId ? { id: parseInt(taskData.orderId) } : null),
        assignedUser: taskData.assignedUser ? taskData.assignedUser : (taskData.employeeId ? { id: parseInt(taskData.employeeId) } : null)
      };

      console.log('Sending to API:', transformedData);

      const res = await fetch(`http://localhost:8080/tasks/${taskData.id}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify(transformedData)
      });

      console.log('Response status:', res.status);

      if (!res.ok) {
        const errorText = await res.text();
        console.error('Error response:', errorText);
        throw new Error(`Failed to update task: ${errorText}`);
      }

      const updatedTask = await res.json();
      console.log('Received updated task:', updatedTask);

      setTasks(prev => prev.map(task =>
        task.id === updatedTask.id ? updatedTask : task
      ));
      setEditingTask(null);
      return true; // Return true on success for the alert
    } catch (err) {
      console.error("Error updating task:", err);
      throw err; // Propagate error for the alert
    }
  };


  const handleCancelEdit = () => {
    setEditingTask(null);
    setFormData({
      taskName: '',
      description: '',
      dueDate: '',
      priority: 'medium',
      status: 'pending',
      orderId: '',
      employeeId: ''
    });
  };

  // Update the order selection in the form JSX
  return (
    <div className="container mx-auto p-4 bg-gray-900 text-gray-100">
      {editingTask ? (
        <TaskEditForm
          task={editingTask}
          onSubmit={handleEditTask}
          onCancel={handleCancelEdit}
          orders={orders}
          employees={employees}
        />
      ) : (
        <TaskForm
          formData={formData}
          handleInputChange={handleInputChange}
          handleAddTask={handleAddTask}
          orders={orders}
          employees={employees}
        />
      )}

      <TaskFilters
        filters={{
          filterName,
          filterOrderId,
          filterStatus,
          filterPriority,
          filterEmployee,
          filterDateFrom,
          filterDateTo
        }}
        setFilters={{
          setFilterName,
          setFilterOrderId,
          setFilterStatus,
          setFilterPriority,
          setFilterEmployee,
          setFilterDateFrom,
          setFilterDateTo
        }}
        resetFilters={resetFilters}
        generateReport={generateReport}
        orders={orders}
        employees={employees}
      />

      <TaskList
        tasks={filteredTasks}
        handleEditTask={handleEditTask} // <-- pass the API update handler for inline edits
        handleEditClick={handleEditClick} // <-- pass this only if you want to trigger full-page edit
        handleDeleteTask={handleDeleteTask}
        orders={orders}
        employees={employees}
      />
    </div>
  );
};

export default TaskPlans;