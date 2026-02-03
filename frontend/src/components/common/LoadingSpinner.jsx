import React from 'react';

const LoadingSpinner = ({ size = 'md' }) => {
  const sizes = {
    sm: 'w-8 h-8',
    md: 'w-12 h-12',
    lg: 'w-16 h-16',
  };

  return (
    <div className="flex justify-center items-center">
      <div className={`${sizes[size]} border-4 border-primary border-t-transparent rounded-full animate-spin`}></div>
    </div>
  );
};

export default LoadingSpinner;