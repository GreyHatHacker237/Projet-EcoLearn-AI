import React from 'react';
import { Link } from 'react-router-dom';
import Button from '../components/common/Button';

const NotFound = () => {
  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-primary to-secondary">
      <div className="text-center text-white">
        <h1 className="text-9xl font-bold mb-4">404</h1>
        <p className="text-3xl font-semibold mb-8">Page non trouvée</p>
        <p className="text-xl mb-12 opacity-90">
          Oups ! La page que vous cherchez n'existe pas.
        </p>
        <Link to="/dashboard">
          <Button variant="primary" className="bg-white text-primary hover:bg-gray-100">
            Retour au Dashboard
          </Button>
        </Link>
      </div>
    </div>
  );
};

export default NotFound;