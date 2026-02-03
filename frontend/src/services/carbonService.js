import api from './api';

export const carbonService = {
  calculateCarbon: async (sessionData) => {
    const response = await api.post('/carbon/calculate', sessionData);
    return response.data;
  },

  offsetCarbon: async (amount) => {
    const response = await api.post('/carbon/offset', { amount });
    return response.data;
  },

  getMetrics: async () => {
    const response = await api.get('/carbon/metrics');
    return response.data;
  },

  getHistory: async () => {
    const response = await api.get('/carbon/history');
    return response.data;
  },
};