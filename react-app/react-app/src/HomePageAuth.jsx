import React from 'react';
import './HomePageAuth.css'; 
const HomePageAuth = () => {
  return (
    <div className="home-page">
      <header className="header">
        <h1>Welcome to the Assignment Review App</h1>
        <hr />
        <nav>
          <ul>
            <li><a href="/dashboard" className="button">Dashboard</a></li>
          </ul>
        </nav>
      </header>
      <footer>
        <p>&copy; 2023 BloomCodeCamp. All rights reserved.</p>
      </footer>
    </div>
  );
};

export default HomePageAuth;