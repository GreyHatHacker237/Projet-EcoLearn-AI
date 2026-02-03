import React, { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import LoadingSpinner from '../components/common/LoadingSpinner';
import CarbonChart from '../components/charts/CarbonChart';
import TreesChart from '../components/charts/TreesChart';
import { carbonService } from '../services/carbonService';

const Dashboard = () => {
  const { user } = useAuth();
  const [metrics, setMetrics] = useState(null);
  const [loading, setLoading] = useState(true);

  // Données simulées pour les graphiques
  const carbonData = [
    { date: '2025-01-01', carbon: 5 },
    { date: '2025-01-07', carbon: 12 },
    { date: '2025-01-14', carbon: 18 },
    { date: '2025-01-21', carbon: 25 },
    { date: '2025-01-28', carbon: 32 },
  ];

  const treesData = [
    { month: 'Sept', trees: 3 },
    { month: 'Oct', trees: 5 },
    { month: 'Nov', trees: 8 },
    { month: 'Déc', trees: 12 },
    { month: 'Jan', trees: 15 },
  ];

  useEffect(() => {
    const fetchMetrics = async () => {
      try {
        // En production, utiliser : const data = await carbonService.getMetrics();
        // Simuler les métriques pour le développement
        const data = {
          totalCarbonSaved: 32,
          treesPlanted: 15,
          completedPaths: 4,
        };
        setMetrics(data);
      } catch (error) {
        console.error('Erreur chargement métriques:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchMetrics();
  }, []);

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
        Bienvenue, {user?.name || 'Utilisateur'} ! 👋
      </h1>

      {/* Statistiques rapides */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-12">
        <div className="bg-white p-6 rounded-xl shadow-lg transform hover:scale-105 transition">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-gray-600 text-sm">CO₂ Économisé</p>
              <p className="text-3xl font-bold text-primary">
                {metrics?.totalCarbonSaved || 0} kg
              </p>
            </div>
            <div className="text-5xl">🌍</div>
          </div>
        </div>

        <div className="bg-white p-6 rounded-xl shadow-lg transform hover:scale-105 transition">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-gray-600 text-sm">Arbres Plantés</p>
              <p className="text-3xl font-bold text-secondary">
                {metrics?.treesPlanted || 0}
              </p>
            </div>
            <div className="text-5xl">🌳</div>
          </div>
        </div>

        <div className="bg-white p-6 rounded-xl shadow-lg transform hover:scale-105 transition">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-gray-600 text-sm">Parcours Complétés</p>
              <p className="text-3xl font-bold text-accent">
                {metrics?.completedPaths || 0}
              </p>
            </div>
            <div className="text-5xl">📚</div>
          </div>
        </div>
      </div>

      {/* Graphiques D3.js */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        <CarbonChart data={carbonData} />
        <TreesChart data={treesData} />
      </div>
    </div>
  );
};

export default Dashboard;