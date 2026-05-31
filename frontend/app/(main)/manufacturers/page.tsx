"use client";

import { useEffect, useState } from "react";
import { useAuth } from "@/context/AuthContext";
import { api } from "@/lib/api";
import { ManufacturerResponse, Page } from "@/types";

const EMPTY_FORM = { name: "", country: "" };

export default function ManufacturersPage() {
  const { user, username, password } = useAuth();
  const isAdmin = user?.role === "ADMIN";

  const [manufacturers, setManufacturers] = useState<ManufacturerResponse[]>([]);
  const [totalPages, setTotalPages] = useState(0);
  const [page, setPage] = useState(0);
  const [nameFilter, setNameFilter] = useState("");
  const [search, setSearch] = useState("");

  const [form, setForm] = useState(EMPTY_FORM);
  const [editing, setEditing] = useState<ManufacturerResponse | null>(null);
  const [showForm, setShowForm] = useState(false);
  const [error, setError] = useState("");

  async function load(p = 0, name = search) {
    const params = new URLSearchParams({ page: String(p), size: "10", sort: "name,asc" });
    if (name) params.set("name", name);
    const data = await api.get<Page<ManufacturerResponse>>(`/manufacturers?${params}`);
    setManufacturers(data.content);
    setTotalPages(data.totalPages);
    setPage(p);
  }

  useEffect(() => { load(0, ""); }, []);

  function handleSearch(e: React.FormEvent) {
    e.preventDefault();
    setSearch(nameFilter);
    load(0, nameFilter);
  }

  function openCreate() {
    setEditing(null);
    setForm(EMPTY_FORM);
    setError("");
    setShowForm(true);
  }

  function openEdit(m: ManufacturerResponse) {
    setEditing(m);
    setForm({ name: m.name, country: m.country });
    setError("");
    setShowForm(true);
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setError("");
    const creds = { username, password };
    try {
      if (editing) {
        await api.put(`/manufacturers/${editing.id}`, form, creds);
      } else {
        await api.post("/manufacturers", form, creds);
      }
      setShowForm(false);
      load(page, search);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : "Error");
    }
  }

  async function handleDelete(id: number) {
    if (!confirm("Delete this manufacturer?")) return;
    try {
      await api.delete(`/manufacturers/${id}`, { username, password });
      load(page, search);
    } catch (err: unknown) {
      alert(err instanceof Error ? err.message : "Error");
    }
  }

  return (
    <div className="flex flex-col gap-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold">Manufacturers</h1>
        {isAdmin && (
          <button
            onClick={openCreate}
            className="px-4 py-2 bg-gray-900 text-white text-sm rounded hover:bg-gray-700"
          >
            + New
          </button>
        )}
      </div>

      <form onSubmit={handleSearch} className="flex gap-2">
        <input
          type="text"
          placeholder="Search by name..."
          value={nameFilter}
          onChange={(e) => setNameFilter(e.target.value)}
          className="border border-gray-300 rounded px-3 py-2 text-sm flex-1 focus:outline-none focus:border-gray-500"
        />
        <button
          type="submit"
          className="px-4 py-2 border border-gray-300 rounded text-sm hover:bg-gray-100"
        >
          Search
        </button>
      </form>

      {showForm && (
        <form
          onSubmit={handleSubmit}
          className="border border-gray-200 rounded p-6 bg-white flex flex-col gap-4"
        >
          <h2 className="font-semibold">{editing ? "Edit Manufacturer" : "New Manufacturer"}</h2>
          <div className="flex gap-4">
            <div className="flex flex-col gap-1 flex-1">
              <label className="text-sm text-gray-600">Name</label>
              <input
                type="text"
                value={form.name}
                onChange={(e) => setForm({ ...form, name: e.target.value })}
                className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-gray-500"
                required
              />
            </div>
            <div className="flex flex-col gap-1 flex-1">
              <label className="text-sm text-gray-600">Country</label>
              <input
                type="text"
                value={form.country}
                onChange={(e) => setForm({ ...form, country: e.target.value })}
                className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-gray-500"
                required
              />
            </div>
          </div>
          {error && <p className="text-sm text-red-500">{error}</p>}
          <div className="flex gap-2">
            <button
              type="submit"
              className="px-4 py-2 bg-gray-900 text-white text-sm rounded hover:bg-gray-700"
            >
              Save
            </button>
            <button
              type="button"
              onClick={() => setShowForm(false)}
              className="px-4 py-2 border border-gray-300 text-sm rounded hover:bg-gray-100"
            >
              Cancel
            </button>
          </div>
        </form>
      )}

      <table className="w-full text-sm border border-gray-200 rounded overflow-hidden">
        <thead className="bg-gray-50 text-gray-600 text-left">
          <tr>
            <th className="px-4 py-3">Name</th>
            <th className="px-4 py-3">Country</th>
            {isAdmin && <th className="px-4 py-3 w-24">Actions</th>}
          </tr>
        </thead>
        <tbody>
          {manufacturers.map((m) => (
            <tr key={m.id} className="border-t border-gray-100 hover:bg-gray-50">
              <td className="px-4 py-3">{m.name}</td>
              <td className="px-4 py-3">{m.country}</td>
              {isAdmin && (
                <td className="px-4 py-3 flex gap-2">
                  <button
                    onClick={() => openEdit(m)}
                    className="text-gray-600 hover:text-gray-900 text-xs underline"
                  >
                    Edit
                  </button>
                  <button
                    onClick={() => handleDelete(m.id)}
                    className="text-red-500 hover:text-red-700 text-xs underline"
                  >
                    Delete
                  </button>
                </td>
              )}
            </tr>
          ))}
          {manufacturers.length === 0 && (
            <tr>
              <td colSpan={isAdmin ? 3 : 2} className="px-4 py-6 text-center text-gray-400">
                No manufacturers found
              </td>
            </tr>
          )}
        </tbody>
      </table>

      {totalPages > 1 && (
        <div className="flex gap-2 justify-center">
          {Array.from({ length: totalPages }, (_, i) => (
            <button
              key={i}
              onClick={() => load(i, search)}
              className={`px-3 py-1 text-sm rounded border ${
                i === page
                  ? "bg-gray-900 text-white border-gray-900"
                  : "border-gray-300 hover:bg-gray-100"
              }`}
            >
              {i + 1}
            </button>
          ))}
        </div>
      )}
    </div>
  );
}
