import React, { useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import Input from '../components/common/Input';
import Button from '../components/common/Button';

const UserProfile = () => {
  const { user, logout } = useAuth();
  const [isEditing, setIsEditing] = useState(false);
  const [formData, setFormData] = useState({
    name: user?.name || '',
    email: user?.email || '',
  });

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    // Logique de mise à jour
    console.log('Mise à jour du profil:', formData);
    setIsEditing(false);
  };

  const stats = {
    totalLearningTime: 145,
    coursesCompleted: 4,
    carbonSaved: 32,
    treesPlanted: 15,
    rank: 'Eco-Warrior',
    joinDate: '2024-09-15',
  };

  return (
    <div className="max-w-4xl mx-auto">
      <h1 className="text-4xl font-bold text-primary mb-8">👤 Mon Profil</h1>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Informations */}
        <div className="lg:col-span-2 bg-white p-8 rounded-xl shadow-lg">
          <div className="flex justify-between items-center mb-6">
            <h2 className="text-2xl font-bold">Informations Personnelles</h2>
            <Button
              variant="secondary"
              onClick={() => setIsEditing(!isEditing)}
            >
              {isEditing ? 'Annuler' : 'Modifier'}
            </Button>
          </div>

          {isEditing ? (
            <form onSubmit={handleSubmit}>
              <Input
                label="Nom complet"
                name="name"
                value={formData.name}
                onChange={handleChange}
              />
              <Input
                label="Email"
                type="email"
                name="email"
                value={formData.email}
                onChange={handleChange}
              />
              <Button type="submit" className="mt-4">
                Sauvegarder
              </Button>
            </form>
          ) : (
            <div className="space-y-4">
              <div>
                <p className="text-gray-600 text-sm">Nom</p>
                <p className="text-lg font-semibold">{user?.name}</p>
              </div>
              <div>
                <p className="text-gray-600 text-sm">Email</p>
                <p className="text-lg font-semibold">{user?.email}</p>
              </div>
              <div>
                <p className="text-gray-600 text-sm">Membre depuis</p>
                <p className="text-lg font-semibold">
                  {new Date(stats.joinDate).toLocaleDateString('fr-FR')}
                </p>
              </div>
              <div>
                <p className="text-gray-600 text-sm">Rang</p>
                <p className="text-lg font-semibold text-primary">
                  🏆 {stats.rank}
                </p>
              </div>
            </div>
          )}

          <div className="mt-8 pt-6 border-t">
            <Button variant="danger" onClick={logout}>
              Se déconnecter
            </Button>
          </div>
        </div>

        {/* Statistiques */}
        <div className="space-y-6">
          <div className="bg-gradient-to-br from-primary to-secondary text-white p-6 rounded-xl shadow-lg">
            <h3 className="text-xl font-bold mb-4">Statistiques</h3>
            <div className="space-y-4">
              <div>
                <p className="text-sm opacity-90">Temps d'apprentissage</p>
                <p className="text-2xl font-bold">{stats.totalLearningTime}h</p>
              </div>
              <div>
                <p className="text-sm opacity-90">Cours complétés</p>
                <p className="text-2xl font-bold">{stats.coursesCompleted}</p>
              </div>
              <div>
                <p className="text-sm opacity-90">CO₂ économisé</p>
                <p className="text-2xl font-bold">{stats.carbonSaved} kg</p>
              </div>
              <div>
                <p className="text-sm opacity-90">Arbres plantés</p>
                <p className="text-2xl font-bold">{stats.treesPlanted}</p>
              </div>
            </div>
          </div>

          <div className="bg-white p-6 rounded-xl shadow-lg">
            <h3 className="text-xl font-bold mb-4">Badges</h3>
            <div className="grid grid-cols-3 gap-3">
              <div className="text-center p-3 bg-yellow-50 rounded-lg">
                <p className="text-3xl">🥇</p>
                <p className="text-xs mt-1">Premier pas</p>
              </div>
              <div className="text-center p-3 bg-green-50 rounded-lg">
                <p className="text-3xl">🌱</p>
                <p className="text-xs mt-1">Éco-débutant</p>
              </div>
              <div className="text-center p-3 bg-blue-50 rounded-lg">
                <p className="text-3xl">💚</p>
                <p className="text-xs mt-1">Défenseur</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default UserProfile;