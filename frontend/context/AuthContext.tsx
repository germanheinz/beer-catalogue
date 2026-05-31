"use client";

import { createContext, useContext, useEffect, useState } from "react";
import { AuthUser } from "@/types";
import { api } from "@/lib/api";

interface AuthState {
  user: AuthUser | null;
  username: string;
  password: string;
  login: (username: string, password: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthState | null>(null);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(null);
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  useEffect(() => {
    const stored = localStorage.getItem("auth");
    if (stored) {
      const { username, password } = JSON.parse(stored);
      api.get<AuthUser>("/auth/me", { username, password })
        .then((u) => {
          setUser(u);
          setUsername(username);
          setPassword(password);
        })
        .catch(() => localStorage.removeItem("auth"));
    }
  }, []);

  async function login(username: string, password: string) {
    const u = await api.get<AuthUser>("/auth/me", { username, password });
    setUser(u);
    setUsername(username);
    setPassword(password);
    localStorage.setItem("auth", JSON.stringify({ username, password }));
  }

  function logout() {
    setUser(null);
    setUsername("");
    setPassword("");
    localStorage.removeItem("auth");
  }

  return (
    <AuthContext.Provider value={{ user, username, password, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within AuthProvider");
  return ctx;
}
