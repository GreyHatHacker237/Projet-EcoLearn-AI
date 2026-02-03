import React, { useState, useEffect } from 'react';
import LoadingSpinner from '../components/common/LoadingSpinner';
import { carbonService } from '../services/carbonService';

const PlantationHistory = () => {
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchHistory();
  }, []);

  const fetchHistory = async () => {
    try {
      // En production: const data = await carbonService.getHistory();
      // Simuler les données
      const data = [
        {
          id: 1,
          date: '2025-01-28',
          trees: 3,
          carbonOffset: 2.5,
          location: 'Amazonie, Brésil',
          status: 'Planté',
        },
        {
          id: 2,
          date: '2025-01-20',
          trees: 5,
          carbonOffset: 4.2,
          location: 'Madagascar',
          status: 'Planté',
        },
        {
          id: 3,
          date: '2025-01-15',
          trees: 2,
          carbonOffset: 1.8,
          location: 'Kenya',
          status: 'Planté',
        },
        {
          id: 4,
          date: '2025-01-10',
          trees: 4,
          carbonOffset: 3.3,
          location: 'Indonésie',
          status: 'En cours',
        },
      ];
      setHistory(data);
    } catch (error) {
      console.error('Erreur chargement historique:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-96">
        <LoadingSpinner size="lg" />
      </div>
    );
  }

  return (
    <div>
      <h1 className="text-4xl font-bold text-primary mb-8">
        🌳 Historique des Plantations
      </h1>

      {/* Résumé */}
      <div className="bg-gradient-to-r from-primary to-secondary text-white p-8 rounded-xl shadow-lg mb-8">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 text-center">
          <div>
            <p className="text-5xl font-bold mb-2">
              {history.reduce((sum, item) => sum + item.trees, 0)}
            </p>
            <p className="text-lg">Arbres Total</p>
          </div>
          <div>
            <p className="text-5xl font-bold mb-2">
              {history.reduce((sum, item) => sum + item.carbonOffset, 0).toFixed(1)} kg
            </p>
            <p className="text-lg">CO₂ Compensé</p>
          </div>
          <div>
            <p className="text-5xl font-bold mb-2">{history.length}</p>
            <p className="text-lg">Contributions</p>
          </div>
        </div>
      </div>

      {/* Liste */}
      <div className="space-y-4">
        {history.map((item) => (
          <div
            key={item.id}
            className="bg-white p-6 rounded-xl shadow-lg hover:shadow-xl transition"
          >
            <div className="flex items-center justify-between">
              <div className="flex-1">
                <div className="flex items-center space-x-3 mb-2">
                  <span className="text-3xl">🌳</span>
                  <div>
                    <h3 className="text-xl font-bold text-gray-800">
                      {item.trees} arbres plantés
                    </h3>
                    <p className="text-gray-600 text-sm">{item.location}</p>
                  </div>
                </div>
                <div className="flex items-center space-x-6 text-sm text-gray-600 mt-3">
                  <span>📅 {new Date(item.date).toLocaleDateString('fr-FR')}</span>
                  <span>🌍 {item.carbonOffset} kg CO₂ compensé</span>
                  <span
                    className={`px-3 py-1 rounded-full text-white ${
                      item.status === 'Planté' ? 'bg-green-500' : 'bg-yellow-500'
                    }`}
                  >
                    {item.status}
                  </span>
                </div>
              </div>
              <div className="text-right">
                <button className="text-primary hover:underline font-semibold">
                  Voir détails →
                </button>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default PlantationHistory;