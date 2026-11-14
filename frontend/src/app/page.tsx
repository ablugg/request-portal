import SubmitForm from "@/components/SubmitForm";

export default function HomePage() {
  return (
    <div className="mx-auto max-w-lg">
      <h1 className="mb-2 text-2xl font-bold">Submit a Request</h1>
      <p className="mb-6 text-sm text-gray-500">
        Fill out the form below and you will receive a tracking code to check
        the status of your request.
      </p>
      <SubmitForm />
    </div>
  );
}
