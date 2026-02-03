import React from 'react';

const ProgressBar = ({ current, total, label }) => {
  const percentage = Math.round((current / total) * 100);

  return (
    <div className="mb-6">
      <div className="flex justify-between items-center mb-2">
        <span className="text-gray-700 font-semibold">
          {label || 'Progression'}
        </span>
        <span className="text-gray-600">
          {current} / {total} ({percentage}%)
        </span>
      </div>
      <div className="w-full bg-gray-200 rounded-full h-4 overflow-hidden">
        <div
          className="bg-gradient-to-r from-primary to-secondary h-4 rounded-full transition-all duration-500 flex items-center justify-end pr-2"
          style={{ width: `${percentage}%` }}
        >
          {percentage > 10 && (
            <span className="text-white text-xs font-bold">{percentage}%</span>
          )}
        </div>
      </div>
    </div>
  );
};

export default ProgressBar;