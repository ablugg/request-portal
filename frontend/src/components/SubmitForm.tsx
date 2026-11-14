"use client";

import { useState } from "react";
import { submitRequest, type PublicRequestView, type ApiError } from "@/lib/api";
import StatusBadge from "./StatusBadge";

const CATEGORIES = [
  { value: "BILLING", label: "Billing" },
  { value: "ACCOUNT", label: "Account" },
  { value: "TECHNICAL_ISSUE", label: "Technical Issue" },
  { value: "ACCESSIBILITY", label: "Accessibility" },
  { value: "GENERAL", label: "General" },
];

export default function SubmitForm() {
  const [category, setCategory] = useState("");
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [contactEmail, setContactEmail] = useState("");
  const [result, setResult] = useState<PublicRequestView | null>(null);
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [submitting, setSubmitting] = useState(false);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setErrors({});
    setResult(null);
    setSubmitting(true);

    try {
      const view = await submitRequest({
        category,
        title,
        description,
        contactEmail,
      });
      setResult(view);
      setCategory("");
      setTitle("");
      setDescription("");
      setContactEmail("");
    } catch (err) {
      const apiErr = err as ApiError;
      if (apiErr.errors) {
        setErrors(apiErr.errors);
      } else {
        setErrors({ _general: apiErr.detail ?? apiErr.title });
      }
    } finally {
      setSubmitting(false);
    }
  }

  if (result) {
    return (
      <div className="rounded-lg border border-green-200 bg-green-50 p-6">
        <h3 className="mb-2 text-lg font-semibold text-green-900">
          Request Submitted
        </h3>
        <p className="mb-4 text-green-800">
          Your tracking code is:
        </p>
        <p className="mb-4 font-mono text-2xl font-bold text-green-900">
          {result.reference}
        </p>
        <div className="mb-4 flex items-center gap-2 text-sm text-green-700">
          <span>Status:</span>
          <StatusBadge status={result.status} />
        </div>
        <p className="mb-4 text-sm text-green-700">
          Save this code to check the status of your request.
        </p>
        <button
          onClick={() => setResult(null)}
          className="rounded-lg bg-green-700 px-4 py-2 text-sm font-medium text-white hover:bg-green-800"
        >
          Submit Another
        </button>
      </div>
    );
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-5">
      {errors._general && (
        <p className="rounded-lg bg-red-50 p-3 text-sm text-red-700">
          {errors._general}
        </p>
      )}

      <div>
        <label htmlFor="category" className="mb-1 block text-sm font-medium">
          Category
        </label>
        <select
          id="category"
          value={category}
          onChange={(e) => setCategory(e.target.value)}
          className="w-full rounded-lg border border-gray-300 bg-white px-3 py-2 text-sm focus:border-blue-500 focus:ring-1 focus:ring-blue-500"
        >
          <option value="">Select a category</option>
          {CATEGORIES.map((c) => (
            <option key={c.value} value={c.value}>
              {c.label}
            </option>
          ))}
        </select>
        {errors.category && (
          <p className="mt-1 text-sm text-red-600">{errors.category}</p>
        )}
      </div>

      <div>
        <label htmlFor="title" className="mb-1 block text-sm font-medium">
          Title
        </label>
        <input
          id="title"
          type="text"
          maxLength={120}
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          placeholder="Short description of your issue"
          className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:ring-1 focus:ring-blue-500"
        />
        {errors.title && (
          <p className="mt-1 text-sm text-red-600">{errors.title}</p>
        )}
      </div>

      <div>
        <label htmlFor="description" className="mb-1 block text-sm font-medium">
          Description
        </label>
        <textarea
          id="description"
          maxLength={2000}
          rows={4}
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          placeholder="Describe your request in detail"
          className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:ring-1 focus:ring-blue-500"
        />
        {errors.description && (
          <p className="mt-1 text-sm text-red-600">{errors.description}</p>
        )}
      </div>

      <div>
        <label htmlFor="contactEmail" className="mb-1 block text-sm font-medium">
          Contact Email
        </label>
        <input
          id="contactEmail"
          type="email"
          maxLength={254}
          value={contactEmail}
          onChange={(e) => setContactEmail(e.target.value)}
          placeholder="you@example.com"
          className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:ring-1 focus:ring-blue-500"
        />
        {errors.contactEmail && (
          <p className="mt-1 text-sm text-red-600">{errors.contactEmail}</p>
        )}
      </div>

      <button
        type="submit"
        disabled={submitting}
        className="w-full rounded-lg bg-blue-600 px-4 py-2.5 text-sm font-medium text-white hover:bg-blue-700 disabled:opacity-50"
      >
        {submitting ? "Submitting..." : "Submit Request"}
      </button>
    </form>
  );
}
