import StaffDashboard from "@/components/StaffDashboard";

export default function StaffPage() {
  return (
    <div>
      <h1 className="mb-2 text-2xl font-bold">Staff Dashboard</h1>
      <p className="mb-6 text-sm text-gray-500">
        View all requests and update their status.
      </p>
      <StaffDashboard />
    </div>
  );
}
