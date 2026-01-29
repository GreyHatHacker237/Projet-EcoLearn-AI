# 🌱 EcoLearn AI - Frontend

Application React pour l'apprentissage écologique avec Intelligence Artificielle.

## 🎯 Objectif du Projet

EcoLearn AI permet aux utilisateurs d'apprendre sur des sujets environnementaux tout en réduisant leur empreinte carbone. Chaque session d'apprentissage contribue à la plantation d'arbres.

## 🚀 Technologies Utilisées

- **React 18** - Framework JavaScript
- **React Router v6** - Routing
- **Tailwind CSS** - Framework CSS
- **D3.js** - Visualisations de données
- **Axios** - Requêtes HTTP
- **Context API** - Gestion d'état

## 📋 Prérequis

- Node.js >= 14.x
- npm >= 6.x

## 🔧 Installation
```bash
# Cloner le repository
git clone git@github.com:USERNAME/ecolearn-frontend.git

# Accéder au dossier
cd ecolearn-frontend

# Installer les dépendances
npm install

# Créer le fichier .env
cp .env.example .env

# Lancer l'application
npm start
```

## 🌐 Variables d'Environnement

Créer un fichier `.env` à la racine :
```env
REACT_APP_API_URL=http://localhost:8000/api
```

## 📁 Structure du Projet
```
src/
├── components/      # Composants réutilisables
├── pages/          # Pages de l'application
├── services/       # Services API
├── contexts/       # Contextes React
├── hooks/          # Hooks personnalisés
├── utils/          # Utilitaires
└── styles/         # Styles globaux
```

## 🎨 Scripts Disponibles
```bash
npm start          # Lancer en mode développement
npm run build      # Créer le build de production
npm test           # Lancer les tests
```

## 🤝 Contribution

1. Fork le projet
2. Créer une branche (`git checkout -b feature/AmazingFeature`)
3. Commit les changements (`git commit -m 'feat: Add AmazingFeature'`)
4. Push vers la branche (`git push origin feature/AmazingFeature`)
5. Ouvrir une Pull Request

## 📝 Convention de Commits

Utiliser le format : `type: description`

Types :
- `feat`: Nouvelle fonctionnalité
- `fix`: Correction de bug
- `docs`: Documentation
- `style`: Formatage
- `refactor`: Refactorisation
- `test`: Tests
- `chore`: Maintenance

## 👥 Équipe

- **Personne 3** - Frontend Lead

## 📄 Licence

Ce projet est sous licence MIT.

## 🌍 Contact

Pour toute question : contact@ecolearn.ai