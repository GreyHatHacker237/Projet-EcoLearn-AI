import React, { useEffect } from 'react';

const Toast = ({ message, type = 'success', onClose, duration = 3000 }) => {
  useEffect(() => {
    const timer = setTimeout(() => {
      onClose();
    }, duration);

    return () => clearTimeout(timer);
  }, [duration, onClose]);

  const types = {
    success: 'bg-green-500',
    error: 'bg-red-500',
    info: 'bg-blue-500',
    warning: 'bg-yellow-500',
  };

  const icons = {
    success: '✓',
    error: '✕',
    info: 'ℹ',
    warning: '⚠',
  };

  return (
    <div className="fixed top-4 right-4 z-50 animate-fade-in-down">
      <div className={`${types[type]} text-white px-6 py-4 rounded-lg shadow-lg flex items-center space-x-3`}>
        <span className="text-2xl">{icons[type]}</span>
        <p className="font-semibold">{message}</p>
        <button onClick={onClose} className="ml-4 hover:opacity-75">
          ✕
        </button>
      </div>
    </div>
  );
};

export default Toast;