import api from './api';

export const learningService = {
  generatePath: async (topic, level) => {
    const response = await api.post('/learning/generate', { topic, level });
    return response.data;
  },

  personalizePath: async (pathId, preferences) => {
    const response = await api.post(`/learning/personalize/${pathId}`, preferences);
    return response.data;
  },

  getUserPaths: async () => {
    const response = await api.get('/learning/paths');
    return response.data;
  },

  getPathById: async (pathId) => {
    const response = await api.get(`/learning/paths/${pathId}`);
    return response.data;
  },
};