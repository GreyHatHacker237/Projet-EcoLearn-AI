import React from 'react';
import { BrowserRouter as Router } from 'react-router-dom';
import Layout from './components/layout/Layout';

function App() {
  return (
    <Router>
      <Layout>
        <div className="text-center py-20">
          <h1 className="text-4xl font-bold text-primary mb-4">
            🌱 Bienvenue sur EcoLearn AI
          </h1>
          <p className="text-xl text-gray-600">
            Apprenez tout en réduisant votre empreinte carbone
          </p>
        </div>
      </Layout>
    </Router>
  );
}

export default App;