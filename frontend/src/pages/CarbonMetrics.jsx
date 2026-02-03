import React, { useState, useEffect } from 'react';
import LoadingSpinner from '../components/common/LoadingSpinner';
import CarbonChart from '../components/charts/CarbonChart';
import { carbonService } from '../services/carbonService';

const CarbonMetrics = () => {
  const [metrics, setMetrics] = useState(null);
  const [loading, setLoading] = useState(true);

  const carbonData = [
    { date: '2025-01-01', carbon: 5 },
    { date: '2025-01-07', carbon: 12 },
    { date: '2025-01-14', carbon: 18 },
    { date: '2025-01-21', carbon: 25 },
    { date: '2025-01-28', carbon: 32 },
  ];

  useEffect(() => {
    fetchMetrics();
  }, []);

  const fetchMetrics = async () => {
    try {
      // En production: const data = await carbonService.getMetrics();
      const data = {
        totalCarbon: 32,
        thisMonth: 7,
        avgPerSession: 2.3,
        trend: '+15%',
        comparedTo: 'mois dernier',
      };
      setMetrics(data);
    } catch (error) {
      console.error('Erreur chargement métriques:', error);
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
        🌍 Dashboard Métriques Carbone
      </h1>

      {/* KPIs */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
        <div className="bg-white p-6 rounded-xl shadow-lg">
          <p className="text-gray-600 text-sm mb-2">CO₂ Total Économisé</p>
          <p className="text-3xl font-bold text-primary">{metrics.totalCarbon} kg</p>
        </div>

        <div className="bg-white p-6 rounded-xl shadow-lg">
          <p className="text-gray-600 text-sm mb-2">Ce Mois</p>
          <p className="text-3xl font-bold text-secondary">{metrics.thisMonth} kg</p>
        </div>

        <div className="bg-white p-6 rounded-xl shadow-lg">
          <p className="text-gray-600 text-sm mb-2">Moyenne par Session</p>
          <p className="text-3xl font-bold text-accent">{metrics.avgPerSession} kg</p>
        </div>

        <div className="bg-white p-6 rounded-xl shadow-lg">
          <p className="text-gray-600 text-sm mb-2">Tendance</p>
          <p className="text-3xl font-bold text-green-600">{metrics.trend}</p>
          <p className="text-xs text-gray-500">{metrics.comparedTo}</p>
        </div>
      </div>

      {/* Graphique */}
      <CarbonChart data={carbonData} />

      {/* Équivalences */}
      <div className="bg-white p-8 rounded-xl shadow-lg mt-8">
        <h2 className="text-2xl font-bold mb-6">Équivalences</h2>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div className="text-center p-6 bg-blue-50 rounded-lg">
            <p className="text-4xl mb-3">✈️</p>
            <p className="text-2xl font-bold text-blue-600">
              {(metrics.totalCarbon / 0.25).toFixed(0)} km
            </p>
            <p className="text-gray-600 text-sm">en avion économisé</p>
          </div>

          <div className="text-center p-6 bg-green-50 rounded-lg">
            <p className="text-4xl mb-3">🚗</p>
            <p className="text-2xl font-bold text-green-600">
              {(metrics.totalCarbon / 0.12).toFixed(0)} km
            </p>
            <p className="text-gray-600 text-sm">en voiture économisé</p>
          </div>

          <div className="text-center p-6 bg-purple-50 rounded-lg">
            <p className="text-4xl mb-3">🏠</p>
            <p className="text-2xl font-bold text-purple-600">
              {(metrics.totalCarbon / 8).toFixed(0)} jours
            </p>
            <p className="text-gray-600 text-sm">d'électricité d'un foyer</p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default CarbonMetrics;