import React, { useState, useEffect } from 'react';
import { learningService } from '../services/learningService';
import LoadingSpinner from '../components/common/LoadingSpinner';
import LessonCard from '../components/learning/LessonCard';
import Button from '../components/common/Button';

const Learning = () => {
  const [paths, setPaths] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showNewPathForm, setShowNewPathForm] = useState(false);
  const [topic, setTopic] = useState('');
  const [level, setLevel] = useState('beginner');

  useEffect(() => {
    fetchPaths();
  }, []);

  const fetchPaths = async () => {
    try {
      // En production: const data = await learningService.getUserPaths();
      // Simuler des données pour le développement
      const data = [
        {
          id: 1,
          title: 'Énergies Renouvelables',
          description: 'Découvrez les différentes sources d\'énergie propre',
          icon: '⚡',
          progress: 65,
          duration: 45,
          carbonSaved: 3.2,
        },
        {
          id: 2,
          title: 'Agriculture Durable',
          description: 'Apprenez les techniques d\'agriculture respectueuse',
          icon: '🌾',
          progress: 30,
          duration: 60,
          carbonSaved: 2.8,
        },
        {
          id: 3,
          title: 'Gestion des Déchets',
          description: 'Maîtrisez le recyclage et la réduction des déchets',
          icon: '♻️',
          progress: 100,
          duration: 30,
          carbonSaved: 4.5,
        },
      ];
      setPaths(data);
    } catch (error) {
      console.error('Erreur chargement parcours:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleGeneratePath = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      // En production: await learningService.generatePath(topic, level);
      await new Promise(resolve => setTimeout(resolve, 1000)); // Simuler un appel API
      await fetchPaths();
      setShowNewPathForm(false);
      setTopic('');
    } catch (error) {
      console.error('Erreur génération parcours:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading && paths.length === 0) {
    return (
      <div className="flex justify-center items-center h-96">
        <LoadingSpinner size="lg" />
      </div>
    );
  }

  return (
    <div>
      <div className="flex justify-between items-center mb-8">
        <h1 className="text-4xl font-bold text-primary">
          📚 Mes Parcours d'Apprentissage
        </h1>
        <Button onClick={() => setShowNewPathForm(!showNewPathForm)}>
          ➕ Nouveau Parcours
        </Button>
      </div>

      {/* Formulaire de création */}
      {showNewPathForm && (
        <div className="bg-white p-6 rounded-xl shadow-lg mb-8">
          <h2 className="text-2xl font-bold mb-4">Créer un nouveau parcours</h2>
          <form onSubmit={handleGeneratePath} className="space-y-4">
            <div>
              <label className="block text-gray-700 font-semibold mb-2">
                Sujet d'apprentissage
              </label>
              <input
                type="text"
                value={topic}
                onChange={(e) => setTopic(e.target.value)}
                placeholder="Ex: Énergies renouvelables, Agriculture durable..."
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
                required
              />
            </div>

            <div>
              <label className="block text-gray-700 font-semibold mb-2">
                Niveau
              </label>
              <select
                value={level}
                onChange={(e) => setLevel(e.target.value)}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
              >
                <option value="beginner">Débutant</option>
                <option value="intermediate">Intermédiaire</option>
                <option value="advanced">Avancé</option>
              </select>
            </div>

            <div className="flex space-x-4">
              <Button type="submit" disabled={loading}>
                {loading ? 'Génération...' : 'Générer avec IA'}
              </Button>
              <Button 
                type="button" 
                variant="secondary"
                onClick={() => setShowNewPathForm(false)}
              >
                Annuler
              </Button>
            </div>
          </form>
        </div>
      )}

      {/* Liste des parcours */}
      {paths.length === 0 ? (
        <div className="text-center py-20 bg-white rounded-xl">
          <p className="text-xl text-gray-600 mb-4">
            Aucun parcours pour le moment
          </p>
          <p className="text-gray-500">
            Créez votre premier parcours d'apprentissage !
          </p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {paths.map((path) => (
            <LessonCard key={path.id} lesson={path} />
          ))}
        </div>
      )}
    </div>
  );
};

export default Learning;