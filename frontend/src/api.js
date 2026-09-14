const API_URL = import.meta.env.VITE_API_URL ?? "http://localhost:8080";

export async function api(path, options = {}) {
  const token =
    localStorage.getItem("shoeverse_token") ||
    localStorage.getItem("shopverse_token");
  const response = await fetch(`${API_URL}${path}`, {
    ...options,
    headers: {
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...options.headers,
    },
  });
  const body = await response.json().catch(() => ({}));
  if (!response.ok || body.success === false)
    throw new Error(body.message || "Something went wrong");
  return body.data;
}

export const apiUrl = API_URL;

export function saveSession(auth) {
  localStorage.setItem("shoeverse_token", auth.token);
  localStorage.setItem("shoeverse_user", JSON.stringify(auth.user));
  localStorage.removeItem("shopverse_token");
  localStorage.removeItem("shopverse_user");
}

export function clearSession() {
  localStorage.removeItem("shoeverse_token");
  localStorage.removeItem("shoeverse_user");
  localStorage.removeItem("shopverse_token");
  localStorage.removeItem("shopverse_user");
}
