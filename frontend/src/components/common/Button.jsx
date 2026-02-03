import React from 'react';

const Button = ({ 
  children, 
  onClick, 
  type = 'button', 
  variant = 'primary',
  disabled = false,
  className = '',
  ariaLabel,
}) => {
  const baseStyles = 'px-6 py-3 rounded-lg font-semibold transition duration-200 focus:outline-none focus:ring-4';
  
  const variants = {
    primary: 'bg-primary text-white hover:bg-secondary disabled:bg-gray-400 focus:ring-primary focus:ring-opacity-50',
    secondary: 'bg-gray-200 text-gray-800 hover:bg-gray-300 focus:ring-gray-300',
    danger: 'bg-red-500 text-white hover:bg-red-600 focus:ring-red-300',
  };

  return (
    <button
      type={type}
      onClick={onClick}
      disabled={disabled}
      className={`${baseStyles} ${variants[variant]} ${className}`}
      aria-label={ariaLabel}
      aria-disabled={disabled}
    >
      {children}
    </button>
  );
};

export default Button;