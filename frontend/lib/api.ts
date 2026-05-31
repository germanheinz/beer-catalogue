const BASE_URL = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080/api/v1";

function basicAuth(username: string, password: string) {
  return "Basic " + btoa(`${username}:${password}`);
}

async function request<T>(
  path: string,
  options: RequestInit = {},
  credentials?: { username: string; password: string }
): Promise<T> {
  const headers: Record<string, string> = {
    "Content-Type": "application/json",
    ...(options.headers as Record<string, string>),
  };

  if (credentials) {
    headers["Authorization"] = basicAuth(credentials.username, credentials.password);
  }

  const res = await fetch(`${BASE_URL}${path}`, { ...options, headers });

  if (!res.ok) {
    const error = await res.json().catch(() => ({ message: res.statusText }));
    throw new Error(error.message ?? "Request failed");
  }

  if (res.status === 204) return undefined as T;
  return res.json();
}

export const api = {
  get: <T>(path: string, credentials?: { username: string; password: string }) =>
    request<T>(path, { method: "GET" }, credentials),

  post: <T>(path: string, body: unknown, credentials?: { username: string; password: string }) =>
    request<T>(path, { method: "POST", body: JSON.stringify(body) }, credentials),

  put: <T>(path: string, body: unknown, credentials?: { username: string; password: string }) =>
    request<T>(path, { method: "PUT", body: JSON.stringify(body) }, credentials),

  delete: <T>(path: string, credentials?: { username: string; password: string }) =>
    request<T>(path, { method: "DELETE" }, credentials),
};
