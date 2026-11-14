const colors: Record<string, string> = {
  SUBMITTED: "bg-blue-100 text-blue-800",
  IN_PROGRESS: "bg-yellow-100 text-yellow-800",
  RESOLVED: "bg-green-100 text-green-800",
  CLOSED: "bg-gray-100 text-gray-800",
};

export default function StatusBadge({ status }: { status: string }) {
  const label = status.replace("_", " ");
  return (
    <span
      className={`inline-block rounded-full px-3 py-1 text-xs font-semibold ${colors[status] ?? "bg-gray-100 text-gray-800"}`}
    >
      {label}
    </span>
  );
}
