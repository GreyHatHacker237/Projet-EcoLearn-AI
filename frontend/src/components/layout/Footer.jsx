import React from 'react';

const Footer = () => {
  return (
    <footer className="bg-dark text-white py-6 mt-12">
      <div className="container mx-auto px-4">
        <div className="flex flex-col md:flex-row justify-between items-center">
          <div className="text-center md:text-left mb-4 md:mb-0">
            <p className="font-semibold">&copy; 2026 EcoLearn AI - Apprendre pour la planète 🌍</p>
            <p className="text-sm text-gray-300 mt-1">
              Réduisez votre empreinte carbone en apprenant
            </p>
          </div>
          
          <div className="flex space-x-6">
            <a href="#" className="hover:text-accent transition">À propos</a>
            <a href="#" className="hover:text-accent transition">Contact</a>
            <a href="#" className="hover:text-accent transition">Confidentialité</a>
          </div>
        </div>
      </div>
    </footer>
  );
};

export default Footer;