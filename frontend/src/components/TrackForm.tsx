"use client";

import { useState } from "react";
import { trackRequest, type PublicRequestView, type ApiError } from "@/lib/api";
import StatusBadge from "./StatusBadge";

export default function TrackForm() {
  const [reference, setReference] = useState("");
  const [result, setResult] = useState<PublicRequestView | null>(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setError("");
    setResult(null);
    setLoading(true);

    try {
      const view = await trackRequest(reference.trim());
      setResult(view);
    } catch (err) {
      const apiErr = err as ApiError;
      if (apiErr.status === 404) {
        setError("No request found with that tracking code.");
      } else {
        setError(apiErr.detail ?? apiErr.title);
      }
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="space-y-5">
      <form onSubmit={handleSubmit} className="flex gap-3">
        <input
          type="text"
          value={reference}
          onChange={(e) => setReference(e.target.value.toUpperCase())}
          placeholder="SR-XXXXXXXX"
          className="flex-1 rounded-lg border border-gray-300 px-3 py-2 font-mono text-sm focus:border-blue-500 focus:ring-1 focus:ring-blue-500"
        />
        <button
          type="submit"
          disabled={loading || !reference.trim()}
          className="rounded-lg bg-blue-600 px-5 py-2 text-sm font-medium text-white hover:bg-blue-700 disabled:opacity-50"
        >
          {loading ? "Searching..." : "Track"}
        </button>
      </form>

      {error && (
        <p className="rounded-lg bg-red-50 p-3 text-sm text-red-700">
          {error}
        </p>
      )}

      {result && (
        <div className="rounded-lg border border-gray-200 bg-white p-5">
          <div className="mb-3 flex items-center justify-between">
            <h3 className="font-mono text-lg font-bold">{result.reference}</h3>
            <StatusBadge status={result.status} />
          </div>
          <dl className="space-y-2 text-sm">
            <div className="flex justify-between">
              <dt className="text-gray-500">Category</dt>
              <dd className="font-medium">
                {result.category.replace("_", " ")}
              </dd>
            </div>
            <div className="flex justify-between">
              <dt className="text-gray-500">Title</dt>
              <dd className="font-medium">{result.title}</dd>
            </div>
            <div className="flex justify-between">
              <dt className="text-gray-500">Submitted</dt>
              <dd>{new Date(result.createdAt).toLocaleString()}</dd>
            </div>
            <div className="flex justify-between">
              <dt className="text-gray-500">Last Updated</dt>
              <dd>{new Date(result.updatedAt).toLocaleString()}</dd>
            </div>
          </dl>
        </div>
      )}
    </div>
  );
}
