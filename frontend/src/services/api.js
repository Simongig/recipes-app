import axios from 'axios';

const api = axios.create();

api.interceptors.request.use(
  (config) => {
    const accessToken = localStorage.getItem('access_token');
    if (accessToken) {
      config.headers['Authorization'] = `Bearer ${accessToken}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

api.interceptors.response.use(
  (response) => response, // Pass through successful responses immediately
  async (error) => {
    const originalRequest = error.config;

    // Check if the error is 401 (Unauthorized) and we haven't tried retrying yet
    if (error.response && error.response.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true; // Mark request so we don't enter an infinite loop if refresh fails

      try {
        const refreshToken = localStorage.getItem('refresh_token');
        const response = await axios.post('/api/v1/auth/token/refresh', {}, {
          headers: { 'Authorization': `Bearer ${refreshToken}` }
        });

        const { access_token, refresh_token } = response.data;

        // Save them back to client storage
        localStorage.setItem('access_token', access_token);
        localStorage.setItem('refresh_token', refresh_token);

        // Update the authorization header of the original, failed request
        originalRequest.headers['Authorization'] = `Bearer ${access_token}`;

        // Replay the original request with the new token and return its result
        return api(originalRequest);
        
      } catch (refreshError) {
        // If the refresh token itself is expired or invalid, log the user out completely
        localStorage.removeItem('access_token');
        localStorage.removeItem('refresh_token');
        
        // Redirect to login (or clear your Vue Pinia/Vuex store here)
        window.location.href = '/login'; 
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

export default api;