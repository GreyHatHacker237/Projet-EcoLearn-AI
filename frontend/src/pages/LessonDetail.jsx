import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { learningService } from '../services/learningService';
import LoadingSpinner from '../components/common/LoadingSpinner';
import Button from '../components/common/Button';
import ProgressBar from '../components/learning/ProgressBar';

const LessonDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [lesson, setLesson] = useState(null);
  const [loading, setLoading] = useState(true);
  const [currentSection, setCurrentSection] = useState(0);

  useEffect(() => {
    fetchLesson();
  }, [id]);

  const fetchLesson = async () => {
    try {
      // En production: const data = await learningService.getPathById(id);
      // Simuler le chargement
      const data = {
        id,
        title: 'Introduction aux Énergies Renouvelables',
        sections: [
          {
            title: 'Qu\'est-ce que l\'énergie renouvelable ?',
            content: 'Les énergies renouvelables sont des sources d\'énergie qui se renouvellent naturellement à l\'échelle humaine. Contrairement aux énergies fossiles (pétrole, charbon, gaz naturel) qui mettent des millions d\'années à se former, les énergies renouvelables sont continuellement disponibles. Ces sources d\'énergie comprennent principalement : l\'énergie solaire issue du rayonnement du soleil, l\'énergie éolienne provenant du vent, l\'énergie hydraulique générée par le mouvement de l\'eau, la biomasse provenant de matières organiques, et l\'énergie géothermique extraite de la chaleur de la Terre.',
            carbonImpact: 0.5,
          },
          {
            title: 'Les différents types d\'énergies renouvelables',
            content: 'Il existe plusieurs types d\'énergies renouvelables, chacune avec ses avantages spécifiques. L\'énergie solaire utilise des panneaux photovoltaïques pour convertir la lumière en électricité. L\'énergie éolienne exploite la force du vent via des turbines. L\'énergie hydraulique transforme la force de l\'eau en mouvement en électricité grâce à des barrages ou des turbines marines. La biomasse convertit les déchets organiques en énergie. Enfin, l\'énergie géothermique capte la chaleur naturelle du sous-sol pour produire de l\'électricité ou chauffer des bâtiments.',
            carbonImpact: 0.3,
          },
          {
            title: 'Avantages et défis',
            content: 'Les énergies renouvelables présentent de nombreux avantages : elles réduisent considérablement les émissions de gaz à effet de serre, sont inépuisables, créent des emplois locaux et réduisent la dépendance aux énergies fossiles. Cependant, elles font face à plusieurs défis : l\'intermittence (le soleil ne brille pas toujours, le vent ne souffle pas constamment), le coût initial d\'installation encore élevé, le besoin de solutions de stockage d\'énergie performantes, et l\'impact environnemental de la fabrication des équipements. Malgré ces défis, les progrès technologiques rendent ces énergies de plus en plus compétitives et efficaces.',
            carbonImpact: 0.4,
          },
        ],
        totalSections: 3,
        completedSections: 0,
      };
      setLesson(data);
    } catch (error) {
      console.error('Erreur chargement leçon:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleNext = () => {
    if (currentSection < lesson.sections.length - 1) {
      setCurrentSection(currentSection + 1);
    } else {
      navigate('/learning');
    }
  };

  const handlePrevious = () => {
    if (currentSection > 0) {
      setCurrentSection(currentSection - 1);
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-96">
        <LoadingSpinner size="lg" />
      </div>
    );
  }

  const section = lesson.sections[currentSection];

  return (
    <div className="max-w-4xl mx-auto">
      <Button 
        variant="secondary" 
        onClick={() => navigate('/learning')}
        className="mb-6"
      >
        ← Retour aux parcours
      </Button>

      <div className="bg-white p-8 rounded-xl shadow-lg">
        <h1 className="text-3xl font-bold text-primary mb-6">
          {lesson.title}
        </h1>

        <ProgressBar
          current={currentSection + 1}
          total={lesson.totalSections}
          label="Progression du cours"
        />

        {/* Contenu de la section */}
        <div className="mb-8">
          <h2 className="text-2xl font-bold text-gray-800 mb-4">
            {section.title}
          </h2>
          <div className="prose max-w-none">
            <p className="text-gray-700 leading-relaxed text-lg">
              {section.content}
            </p>
          </div>

          {/* Impact carbone */}
          <div className="mt-6 p-4 bg-green-50 border-l-4 border-primary rounded">
            <p className="text-primary font-semibold">
              🌱 Impact de cette section : {section.carbonImpact} kg CO₂ économisé
            </p>
          </div>
        </div>

        {/* Navigation */}
        <div className="flex justify-between items-center pt-6 border-t">
          <Button
            variant="secondary"
            onClick={handlePrevious}
            disabled={currentSection === 0}
          >
            ← Précédent
          </Button>

          <span className="text-gray-600">
            Section {currentSection + 1} / {lesson.totalSections}
          </span>

          <Button onClick={handleNext}>
            {currentSection === lesson.sections.length - 1 ? 'Terminer' : 'Suivant →'}
          </Button>
        </div>
      </div>
    </div>
  );
};

export default LessonDetail;