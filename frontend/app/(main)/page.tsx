import Link from "next/link";

export default function HomePage() {
  return (
    <div className="flex flex-col items-center justify-center gap-6 py-20">
      <h1 className="text-3xl font-bold">Beer Catalogue</h1>
      <p className="text-gray-500">Browse beers and manufacturers</p>
      <div className="flex gap-4">
        <Link
          href="/beers"
          className="px-4 py-2 bg-gray-900 text-white rounded hover:bg-gray-700 text-sm"
        >
          Beers
        </Link>
        <Link
          href="/manufacturers"
          className="px-4 py-2 border border-gray-900 rounded hover:bg-gray-100 text-sm"
        >
          Manufacturers
        </Link>
      </div>
    </div>
  );
}
