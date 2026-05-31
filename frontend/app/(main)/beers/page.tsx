"use client";

import { useEffect, useState } from "react";
import { useAuth } from "@/context/AuthContext";
import { api } from "@/lib/api";
import { BeerResponse, BeerType, ManufacturerResponse, Page } from "@/types";

const BEER_TYPES: BeerType[] = ["IPA", "LAGER", "STOUT", "ALE", "PILSNER", "PORTER", "WHEAT", "SOUR"];

const EMPTY_FORM = {
  name: "",
  abv: "",
  type: "" as BeerType | "",
  description: "",
  manufacturerId: "",
};

export default function BeersPage() {
  const { user, username, password } = useAuth();
  const canWrite = user?.role === "ADMIN" || user?.role === "MANUFACTURER";

  const [beers, setBeers] = useState<BeerResponse[]>([]);
  const [manufacturers, setManufacturers] = useState<ManufacturerResponse[]>([]);
  const [totalPages, setTotalPages] = useState(0);
  const [page, setPage] = useState(0);

  const [filters, setFilters] = useState({ name: "", type: "" as BeerType | "", minAbv: "", maxAbv: "" });
  const [activeFilters, setActiveFilters] = useState(filters);

  const [form, setForm] = useState(EMPTY_FORM);
  const [editing, setEditing] = useState<BeerResponse | null>(null);
  const [showForm, setShowForm] = useState(false);
  const [error, setError] = useState("");

  async function load(p = 0, f = activeFilters) {
    const params = new URLSearchParams({ page: String(p), size: "10", sort: "name,asc" });
    if (f.name) params.set("name", f.name);
    if (f.type) params.set("type", f.type);
    if (f.minAbv) params.set("minAbv", f.minAbv);
    if (f.maxAbv) params.set("maxAbv", f.maxAbv);

    const hasFilter = f.name || f.type || f.minAbv || f.maxAbv;
    const path = hasFilter ? `/beers/search?${params}` : `/beers?${params}`;
    const data = await api.get<Page<BeerResponse>>(path);
    setBeers(data.content);
    setTotalPages(data.totalPages);
    setPage(p);
  }

  useEffect(() => {
    load(0, { name: "", type: "", minAbv: "", maxAbv: "" });
    api.get<Page<ManufacturerResponse>>("/manufacturers?size=100").then((d) =>
      setManufacturers(d.content)
    );
  }, []);

  function handleSearch(e: React.FormEvent) {
    e.preventDefault();
    setActiveFilters(filters);
    load(0, filters);
  }

  function openCreate() {
    setEditing(null);
    setForm({ ...EMPTY_FORM, manufacturerId: user?.manufacturerId ? String(user.manufacturerId) : "" });
    setError("");
    setShowForm(true);
  }

  function openEdit(b: BeerResponse) {
    setEditing(b);
    setForm({
      name: b.name,
      abv: String(b.abv),
      type: b.type,
      description: b.description,
      manufacturerId: String(b.manufacturer.id),
    });
    setError("");
    setShowForm(true);
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setError("");
    const creds = { username, password };
    const body = {
      name: form.name,
      abv: parseFloat(form.abv),
      type: form.type,
      description: form.description,
      manufacturerId: parseInt(form.manufacturerId),
    };
    try {
      if (editing) {
        await api.put(`/beers/${editing.id}`, body, creds);
      } else {
        await api.post("/beers", body, creds);
      }
      setShowForm(false);
      load(page, activeFilters);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : "Error");
    }
  }

  async function handleDelete(id: number) {
    if (!confirm("Delete this beer?")) return;
    try {
      await api.delete(`/beers/${id}`, { username, password });
      load(page, activeFilters);
    } catch (err: unknown) {
      alert(err instanceof Error ? err.message : "Error");
    }
  }

  const canEdit = (beer: BeerResponse) =>
    user?.role === "ADMIN" || user?.manufacturerId === beer.manufacturer.id;

  return (
    <div className="flex flex-col gap-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold">Beers</h1>
        {canWrite && (
          <button
            onClick={openCreate}
            className="px-4 py-2 bg-gray-900 text-white text-sm rounded hover:bg-gray-700"
          >
            + New
          </button>
        )}
      </div>

      <form onSubmit={handleSearch} className="flex flex-wrap gap-2">
        <input
          type="text"
          placeholder="Name..."
          value={filters.name}
          onChange={(e) => setFilters({ ...filters, name: e.target.value })}
          className="border border-gray-300 rounded px-3 py-2 text-sm w-40 focus:outline-none focus:border-gray-500"
        />
        <select
          value={filters.type}
          onChange={(e) => setFilters({ ...filters, type: e.target.value as BeerType | "" })}
          className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-gray-500"
        >
          <option value="">All types</option>
          {BEER_TYPES.map((t) => <option key={t} value={t}>{t}</option>)}
        </select>
        <input
          type="number"
          placeholder="Min ABV"
          step="0.1"
          value={filters.minAbv}
          onChange={(e) => setFilters({ ...filters, minAbv: e.target.value })}
          className="border border-gray-300 rounded px-3 py-2 text-sm w-24 focus:outline-none focus:border-gray-500"
        />
        <input
          type="number"
          placeholder="Max ABV"
          step="0.1"
          value={filters.maxAbv}
          onChange={(e) => setFilters({ ...filters, maxAbv: e.target.value })}
          className="border border-gray-300 rounded px-3 py-2 text-sm w-24 focus:outline-none focus:border-gray-500"
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
          <h2 className="font-semibold">{editing ? "Edit Beer" : "New Beer"}</h2>
          <div className="grid grid-cols-2 gap-4">
            <div className="flex flex-col gap-1">
              <label className="text-sm text-gray-600">Name</label>
              <input
                type="text"
                value={form.name}
                onChange={(e) => setForm({ ...form, name: e.target.value })}
                className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-gray-500"
                required
              />
            </div>
            <div className="flex flex-col gap-1">
              <label className="text-sm text-gray-600">ABV (%)</label>
              <input
                type="number"
                step="0.1"
                min="0"
                max="99.9"
                value={form.abv}
                onChange={(e) => setForm({ ...form, abv: e.target.value })}
                className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-gray-500"
                required
              />
            </div>
            <div className="flex flex-col gap-1">
              <label className="text-sm text-gray-600">Type</label>
              <select
                value={form.type}
                onChange={(e) => setForm({ ...form, type: e.target.value as BeerType })}
                className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-gray-500"
                required
              >
                <option value="">Select type</option>
                {BEER_TYPES.map((t) => <option key={t} value={t}>{t}</option>)}
              </select>
            </div>
            <div className="flex flex-col gap-1">
              <label className="text-sm text-gray-600">Manufacturer</label>
              <select
                value={form.manufacturerId}
                onChange={(e) => setForm({ ...form, manufacturerId: e.target.value })}
                className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-gray-500"
                required
                disabled={user?.role === "MANUFACTURER"}
              >
                <option value="">Select manufacturer</option>
                {manufacturers.map((m) => (
                  <option key={m.id} value={m.id}>{m.name}</option>
                ))}
              </select>
            </div>
            <div className="flex flex-col gap-1 col-span-2">
              <label className="text-sm text-gray-600">Description</label>
              <textarea
                value={form.description}
                onChange={(e) => setForm({ ...form, description: e.target.value })}
                className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-gray-500"
                rows={3}
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
            <th className="px-4 py-3">Type</th>
            <th className="px-4 py-3">ABV</th>
            <th className="px-4 py-3">Manufacturer</th>
            {canWrite && <th className="px-4 py-3 w-24">Actions</th>}
          </tr>
        </thead>
        <tbody>
          {beers.map((b) => (
            <tr key={b.id} className="border-t border-gray-100 hover:bg-gray-50">
              <td className="px-4 py-3">{b.name}</td>
              <td className="px-4 py-3">
                <span className="text-xs bg-gray-100 px-2 py-0.5 rounded">{b.type}</span>
              </td>
              <td className="px-4 py-3">{b.abv}%</td>
              <td className="px-4 py-3">{b.manufacturer.name}</td>
              {canWrite && (
                <td className="px-4 py-3 flex gap-2">
                  {canEdit(b) && (
                    <>
                      <button
                        onClick={() => openEdit(b)}
                        className="text-gray-600 hover:text-gray-900 text-xs underline"
                      >
                        Edit
                      </button>
                      <button
                        onClick={() => handleDelete(b.id)}
                        className="text-red-500 hover:text-red-700 text-xs underline"
                      >
                        Delete
                      </button>
                    </>
                  )}
                </td>
              )}
            </tr>
          ))}
          {beers.length === 0 && (
            <tr>
              <td colSpan={canWrite ? 5 : 4} className="px-4 py-6 text-center text-gray-400">
                No beers found
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
              onClick={() => load(i, activeFilters)}
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
