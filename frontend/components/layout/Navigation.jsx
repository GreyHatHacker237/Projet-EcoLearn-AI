import React from 'react';
import { Link } from 'react-router-dom';

const Navigation = () => {
  return (
    <nav className="bg-primary text-white shadow-lg">
      <div className="container mx-auto px-4">
        <div className="flex justify-between items-center h-16">
          <Link to="/" className="text-2xl font-bold flex items-center">
            🌱 EcoLearn AI
          </Link>
          
          <div className="flex space-x-6">
            <Link to="/dashboard" className="hover:text-accent transition">
              Dashboard
            </Link>
            <Link to="/learning" className="hover:text-accent transition">
              Parcours
            </Link>
            <Link to="/impact" className="hover:text-accent transition">
              Impact
            </Link>
            <Link to="/profile" className="hover:text-accent transition">
              Profil
            </Link>
          </div>

          <div className="flex space-x-4">
            <Link to="/login" className="hover:text-accent transition">
              Connexion
            </Link>
            <Link 
              to="/register" 
              className="bg-white text-primary px-4 py-2 rounded-lg hover:bg-light transition"
            >
              S'inscrire
            </Link>
          </div>
        </div>
      </div>
    </nav>
  );
};

export default Navigation;