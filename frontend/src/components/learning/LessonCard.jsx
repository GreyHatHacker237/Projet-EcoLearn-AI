import React from 'react';
import { useNavigate } from 'react-router-dom';

const LessonCard = ({ lesson }) => {
  const navigate = useNavigate();

  return (
    <div 
      className="bg-white p-6 rounded-xl shadow-lg hover:shadow-xl transition cursor-pointer hover-lift"
      onClick={() => navigate(`/learning/${lesson.id}`)}
    >
      <div className="flex items-start justify-between mb-4">
        <div className="flex-1">
          <h3 className="text-xl font-bold text-gray-800 mb-2">
            {lesson.title}
          </h3>
          <p className="text-gray-600 text-sm mb-3">
            {lesson.description}
          </p>
        </div>
        <div className="text-4xl ml-4">{lesson.icon || '📚'}</div>
      </div>

      {/* Progress Bar */}
      <div className="mb-3">
        <div className="flex justify-between text-sm text-gray-600 mb-1">
          <span>Progression</span>
          <span>{lesson.progress || 0}%</span>
        </div>
        <div className="w-full bg-gray-200 rounded-full h-2">
          <div
            className="bg-primary h-2 rounded-full transition-all duration-500"
            style={{ width: `${lesson.progress || 0}%` }}
          ></div>
        </div>
      </div>

      {/* Meta Info */}
      <div className="flex items-center justify-between text-sm text-gray-500">
        <span>⏱️ {lesson.duration || '30'} min</span>
        <span>🌱 {lesson.carbonSaved || 0} kg CO₂</span>
      </div>
    </div>
  );
};

export default LessonCard;