import React from 'react';

const SentinelLogo = ({ size = 40, className = "" }) => {
  return (
    <img 
      src="/sentinel-logo.png"
      alt="Sentinel Logo"
      width={size}
      height={size}
      className={className}
    />
  );
};

export default SentinelLogo;
