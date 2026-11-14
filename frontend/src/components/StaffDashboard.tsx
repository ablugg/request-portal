"use client";

import { useCallback, useEffect, useState } from "react";
import {
  listRequests,
  changeStatus,
  type StaffRequestView,
  type ApiError,
} from "@/lib/api";
import StatusBadge from "./StatusBadge";

const STATUSES = ["ALL", "SUBMITTED", "IN_PROGRESS", "RESOLVED", "CLOSED"];

const ALLOWED_MOVES: Record<string, string[]> = {
  SUBMITTED: ["IN_PROGRESS", "CLOSED"],
  IN_PROGRESS: ["RESOLVED", "CLOSED"],
  RESOLVED: ["IN_PROGRESS", "CLOSED"],
  CLOSED: [],
};

export default function StaffDashboard() {
  const [filter, setFilter] = useState("ALL");
  const [requests, setRequests] = useState<StaffRequestView[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [expanded, setExpanded] = useState<string | null>(null);
  const [updating, setUpdating] = useState<string | null>(null);

  const fetchRequests = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const data = await listRequests(
        filter === "ALL" ? undefined : filter
      );
      setRequests(data);
    } catch (err) {
      const apiErr = err as ApiError;
      setError(apiErr.detail ?? apiErr.title);
    } finally {
      setLoading(false);
    }
  }, [filter]);

  useEffect(() => {
    fetchRequests();
  }, [fetchRequests]);

  async function handleStatusChange(reference: string, newStatus: string) {
    setUpdating(reference);
    try {
      await changeStatus(reference, newStatus);
      await fetchRequests();
    } catch (err) {
      const apiErr = err as ApiError;
      alert(apiErr.detail ?? apiErr.title);
    } finally {
      setUpdating(null);
    }
  }

  return (
    <div className="space-y-5">
      <div className="flex flex-wrap gap-2">
        {STATUSES.map((s) => (
          <button
            key={s}
            onClick={() => setFilter(s)}
            className={`rounded-full px-4 py-1.5 text-sm font-medium transition ${
              filter === s
                ? "bg-blue-600 text-white"
                : "bg-gray-100 text-gray-700 hover:bg-gray-200"
            }`}
          >
            {s.replace("_", " ")}
          </button>
        ))}
      </div>

      {error && (
        <p className="rounded-lg bg-red-50 p-3 text-sm text-red-700">
          {error}
        </p>
      )}

      {loading ? (
        <p className="py-8 text-center text-sm text-gray-500">Loading...</p>
      ) : requests.length === 0 ? (
        <p className="py-8 text-center text-sm text-gray-500">
          No requests found.
        </p>
      ) : (
        <div className="space-y-3">
          {requests.map((req) => (
            <div
              key={req.reference}
              className="rounded-lg border border-gray-200 bg-white transition hover:shadow-sm"
            >
              <button
                onClick={() =>
                  setExpanded(expanded === req.reference ? null : req.reference)
                }
                className="flex w-full items-center justify-between p-4 text-left"
              >
                <div className="flex items-center gap-3">
                  <span className="font-mono text-sm font-bold">
                    {req.reference}
                  </span>
                  <StatusBadge status={req.status} />
                </div>
                <div className="flex items-center gap-3">
                  <span className="hidden text-sm text-gray-500 sm:inline">
                    {req.category.replace("_", " ")}
                  </span>
                  <svg
                    className={`h-4 w-4 text-gray-400 transition ${expanded === req.reference ? "rotate-180" : ""}`}
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M19 9l-7 7-7-7"
                    />
                  </svg>
                </div>
              </button>

              {expanded === req.reference && (
                <div className="border-t border-gray-100 px-4 pb-4 pt-3">
                  <dl className="space-y-2 text-sm">
                    <div>
                      <dt className="text-gray-500">Title</dt>
                      <dd className="font-medium">{req.title}</dd>
                    </div>
                    <div>
                      <dt className="text-gray-500">Description</dt>
                      <dd className="whitespace-pre-wrap">{req.description}</dd>
                    </div>
                    <div className="flex gap-8">
                      <div>
                        <dt className="text-gray-500">Contact</dt>
                        <dd>{req.contactEmail}</dd>
                      </div>
                      <div>
                        <dt className="text-gray-500">Submitted</dt>
                        <dd>{new Date(req.createdAt).toLocaleString()}</dd>
                      </div>
                      <div>
                        <dt className="text-gray-500">Updated</dt>
                        <dd>{new Date(req.updatedAt).toLocaleString()}</dd>
                      </div>
                    </div>
                  </dl>

                  {ALLOWED_MOVES[req.status]?.length > 0 && (
                    <div className="mt-4 flex gap-2">
                      <span className="self-center text-xs text-gray-500">
                        Move to:
                      </span>
                      {ALLOWED_MOVES[req.status].map((next) => (
                        <button
                          key={next}
                          disabled={updating === req.reference}
                          onClick={() =>
                            handleStatusChange(req.reference, next)
                          }
                          className="rounded-lg border border-gray-300 px-3 py-1.5 text-xs font-medium hover:bg-gray-50 disabled:opacity-50"
                        >
                          {next.replace("_", " ")}
                        </button>
                      ))}
                    </div>
                  )}
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
