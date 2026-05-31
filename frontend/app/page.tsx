import Link from "next/link";

export default function HomePage() {
  return (
    <main className="flex min-h-screen flex-col items-center justify-center gap-6">
      <h1 className="text-3xl font-bold">Beer Catalogue</h1>
      <p className="text-gray-500">Browse beers and manufacturers</p>
      <div className="flex gap-4">
        <Link
          href="/beers"
          className="px-4 py-2 bg-gray-900 text-white rounded hover:bg-gray-700"
        >
          Beers
        </Link>
        <Link
          href="/manufacturers"
          className="px-4 py-2 border border-gray-900 rounded hover:bg-gray-100"
        >
          Manufacturers
        </Link>
      </div>
    </main>
  );
}
