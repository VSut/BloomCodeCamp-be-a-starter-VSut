import React, { useEffect, useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import HomePagePublic from './HomePagePublic';
import HomePageAuth from './HomePageAuth'; // Import the HomePageAuth component
import LoginPage from './LoginPage';

const App = () => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const validateToken = async () => {
      const token = localStorage.getItem('token');
      if (!token) {
        setLoading(false);
        return;
      }

      try {
        console.log(token);
        const response = await fetch('http://localhost:8080/api/auth/validate', {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`,
          },
        });

        if (response.ok) {
          setIsAuthenticated(true);
        } else {
          localStorage.removeItem('token');
        }
      } catch (error) {
        console.error('Token validation failed:', error);
        localStorage.removeItem('token');
      } finally {
        setLoading(false);
      }
    };

    validateToken();
  }, []);

  if (loading) {
    return <div>Loading...</div>;
  }

  return (
    <Router>
      <Routes>
        <Route path="/" element={isAuthenticated ? <HomePageAuth /> : <HomePagePublic />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/dashboard" element={isAuthenticated ? <HomePageAuth /> : <Navigate to="/login" />} />
        {/* Add other routes here */}
      </Routes>
    </Router>
  );
};

export default App;