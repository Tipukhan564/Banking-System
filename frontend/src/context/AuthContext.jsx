import React, { createContext, useState, useContext, useEffect } from 'react';
import axios from 'axios';

const AuthContext = createContext();

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within AuthProvider");
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(localStorage.getItem("token"));
  const [loading, setLoading] = useState(true);

  // Set base URL for all backend requests
  axios.defaults.baseURL = "http://localhost:8080";

  useEffect(() => {
    // Initialize auth state from localStorage
    const storedToken = localStorage.getItem("token");
    const storedUser = localStorage.getItem("user");

    if (storedToken) {
      setToken(storedToken);
      axios.defaults.headers.common["Authorization"] = `Bearer ${storedToken}`;

      if (storedUser) {
        try {
          setUser(JSON.parse(storedUser));
        } catch (error) {
          console.error("Failed to parse user data:", error);
          localStorage.removeItem("user");
        }
      }
    } else {
      delete axios.defaults.headers.common["Authorization"];
    }

    setLoading(false);
  }, []);

  // ============================
  // LOGIN
  // ============================
  const login = async (email, password) => {
    try {
      const response = await axios.post(
        "/api/auth/login",
        { email, password }
      );

      const { token, ...userData } = response.data;

      // Save token & user
      setToken(token);
      setUser(userData);

      localStorage.setItem("token", token);
      localStorage.setItem("user", JSON.stringify(userData));

      axios.defaults.headers.common["Authorization"] = `Bearer ${token}`;

      return { success: true };
    } catch (error) {
      return {
        success: false,
        error: error.response?.data?.message || "Login failed",
      };
    }
  };

  // ============================
  // REGISTER
  // ============================
  const register = async (data) => {
    try {
      const response = await axios.post("/api/auth/register", data);

      const { token, ...userData } = response.data;

      setToken(token);
      setUser(userData);

      localStorage.setItem("token", token);
      localStorage.setItem("user", JSON.stringify(userData));

      axios.defaults.headers.common["Authorization"] = `Bearer ${token}`;

      return { success: true };
    } catch (error) {
      return {
        success: false,
        error: error.response?.data?.message || "Registration failed",
      };
    }
  };

  // ============================
  // LOGOUT
  // ============================
  const logout = () => {
    setToken(null);
    setUser(null);

    localStorage.removeItem("token");
    localStorage.removeItem("user");

    delete axios.defaults.headers.common["Authorization"];
  };

  const value = {
    user,
    token,
    login,
    register,
    logout,
    isAuthenticated: !!token,
    loading,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};
