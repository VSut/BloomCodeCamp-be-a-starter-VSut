import React from 'react';
import './HomePagePublic.css';

const HomePagePublic = () => {
  return (
    <div className="home-page">
      <header className="header">
        <h1>Welcome to the <br/> Assignment Review App</h1>
        <hr />
        <nav>
          <ul>
            <li><a href="/login" className="button">Login</a></li>
          </ul>
        </nav>
      </header>
      <footer>
        <p>&copy; 2023 BloomCodeCamp. All rights reserved.</p>
      </footer>
    </div>
  );
};

export default HomePagePublic;