import axios from 'axios';
import { useAuthStore } from '@/stores/authStore';

const api = axios.create();

api.interceptors.request.use(
  (config) => {
    const accessToken = localStorage.getItem('access_token');
    // _anonymous: replay of a request as a guest, see the response interceptor.
    if (accessToken && !config._anonymous) {
      config.headers['Authorization'] = `Bearer ${accessToken}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

function logOutLocally() {
  localStorage.removeItem('access_token');
  localStorage.removeItem('refresh_token');
  useAuthStore().setToLoggedOut();
}

async function refreshTokens() {
  const refreshToken = localStorage.getItem('refresh_token');
  if (!refreshToken) throw new Error('No refresh token');

  const response = await axios.post('/api/v1/auth/token/refresh', {}, {
    headers: { 'Authorization': `Bearer ${refreshToken}` }
  });
  const { access_token, refresh_token } = response.data;
  localStorage.setItem('access_token', access_token);
  localStorage.setItem('refresh_token', refresh_token);
}

// On 401:
//   1. Try to refresh the tokens and replay the request.
//   2. If that fails the session is over: log out locally and replay the request once more
//      WITHOUT a token. The backend answers 401 to an expired token even on public endpoints,
//      so a guest replay is what tells "public page, stale session" (works now) apart from
//      "really needs a login" (401 again).
//   3. Only a 401 for the guest request redirects to the login page.
api.interceptors.response.use(
  (response) => response, // Pass through successful responses immediately
  async (error) => {
    const originalRequest = error.config;
    if (!error.response || error.response.status !== 401 || !originalRequest) {
      return Promise.reject(error);
    }

    const sentToken = Boolean(originalRequest.headers?.['Authorization']);
    if (originalRequest._anonymous || !sentToken) {
      // Already a guest request: the endpoint really requires a login.
      logOutLocally();
      window.location.href = '/login';
      return Promise.reject(error);
    }

    if (!originalRequest._retry) {
      originalRequest._retry = true; // refresh at most once per request
      try {
        await refreshTokens();
        return api(originalRequest); // the request interceptor attaches the new token
      } catch {
        // fall through to the guest replay
      }
    }

    logOutLocally();
    originalRequest._anonymous = true;
    delete originalRequest.headers['Authorization'];
    return api(originalRequest);
  }
);

export default api;
