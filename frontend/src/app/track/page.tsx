import TrackForm from "@/components/TrackForm";

export default function TrackPage() {
  return (
    <div className="mx-auto max-w-lg">
      <h1 className="mb-2 text-2xl font-bold">Track Your Request</h1>
      <p className="mb-6 text-sm text-gray-500">
        Enter the tracking code you received when you submitted your request.
      </p>
      <TrackForm />
    </div>
  );
}
