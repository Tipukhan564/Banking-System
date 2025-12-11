import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export const authAPI = {
  login: async (email, password) => {
    try {
      const res = await api.post('/auth/login', { email, password });
      return res.data;
    } catch (err) {
      return err.response?.data || { success: false, error: "Login error" };
    }
  },

  register: async (data) => {
    try {
      const res = await api.post('/auth/register', data);
      return res.data;
    } catch (err) {
      return err.response?.data || { success: false, error: "Registration error" };
    }
  }
};

export const accountAPI = {
  createAccount: (data) => api.post('/accounts/create', data),
  getAccount: (accountNumber) => api.get(`/accounts/${accountNumber}`),
  getCustomerAccounts: (customerId) => api.get(`/accounts/customer/${customerId}`),
  freezeAccount: (accountNumber) => api.post(`/accounts/${accountNumber}/freeze`),
  unfreezeAccount: (accountNumber) => api.post(`/accounts/${accountNumber}/unfreeze`),
};

export const transactionAPI = {
  transfer: (data) => api.post('/transactions/transfer', data),
  deposit: (data) => api.post('/transactions/deposit', data),
  withdrawal: (data) => api.post('/transactions/withdrawal', data),
  getTransaction: (reference) => api.get(`/transactions/${reference}`),
  getAccountTransactions: (accountNumber) => api.get(`/transactions/account/${accountNumber}`),
};

export const ibftAPI = {
  processIBFT: (data) => api.post('/ibft/transfer', data),
};

export const raastAPI = {
  processRaast: (data) => api.post('/raast/transfer', data),
};

export const reportAPI = {
  generatePDF: (accountId, startDate, endDate) =>
    api.get('/reports/transactions/pdf', {
      params: { accountId, startDate, endDate },
      responseType: 'blob',
    }),
    
  generateExcel: (accountId, startDate, endDate) =>
    api.get('/reports/transactions/excel', {
      params: { accountId, startDate, endDate },
      responseType: 'blob',
    }),
};

export const csvAPI = {
  bulkAccountOpening: (file) => {
    const formData = new FormData();
    formData.append('file', file);
    return api.post('/csv/bulk-account-opening', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },
};

export default api;
