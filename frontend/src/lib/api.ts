export interface PublicRequestView {
  reference: string;
  category: string;
  title: string;
  status: string;
  createdAt: string;
  updatedAt: string;
}

export interface StaffRequestView extends PublicRequestView {
  description: string;
  contactEmail: string;
}

export interface CreateRequestInput {
  category: string;
  title: string;
  description: string;
  contactEmail: string;
}

export interface FieldError {
  field: string;
  message: string;
}

export interface ApiError {
  title: string;
  detail?: string;
  status: number;
  errors?: Record<string, string>;
}

const BASE = "/api";

async function handleResponse<T>(res: Response): Promise<T> {
  if (!res.ok) {
    const body = await res.json().catch(() => null);
    const error: ApiError = {
      title: body?.title ?? "Something went wrong",
      detail: body?.detail,
      status: res.status,
      errors: body?.errors,
    };
    throw error;
  }
  return res.json();
}

export async function submitRequest(
  input: CreateRequestInput
): Promise<PublicRequestView> {
  const res = await fetch(`${BASE}/requests`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(input),
  });
  return handleResponse<PublicRequestView>(res);
}

export async function trackRequest(
  reference: string
): Promise<PublicRequestView> {
  const res = await fetch(`${BASE}/requests/${encodeURIComponent(reference)}`);
  return handleResponse<PublicRequestView>(res);
}

export async function listRequests(
  status?: string
): Promise<StaffRequestView[]> {
  const url = status
    ? `${BASE}/staff/requests?status=${encodeURIComponent(status)}`
    : `${BASE}/staff/requests`;
  const res = await fetch(url);
  return handleResponse<StaffRequestView[]>(res);
}

export async function changeStatus(
  reference: string,
  status: string
): Promise<StaffRequestView> {
  const res = await fetch(
    `${BASE}/staff/requests/${encodeURIComponent(reference)}/status`,
    {
      method: "PATCH",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ status }),
    }
  );
  return handleResponse<StaffRequestView>(res);
}
