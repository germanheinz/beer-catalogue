"use client";

import Link from "next/link";
import { useAuth } from "@/context/AuthContext";
import { useRouter } from "next/navigation";

export default function Navbar() {
  const { user, logout } = useAuth();
  const router = useRouter();

  function handleLogout() {
    logout();
    router.push("/login");
  }

  return (
    <nav className="border-b border-gray-200 bg-white px-6 py-3 flex items-center justify-between">
      <div className="flex items-center gap-6">
        <Link href="/" className="font-bold text-gray-900">
          Beer Catalogue
        </Link>
        <Link href="/beers" className="text-sm text-gray-600 hover:text-gray-900">
          Beers
        </Link>
        <Link href="/manufacturers" className="text-sm text-gray-600 hover:text-gray-900">
          Manufacturers
        </Link>
      </div>

      <div className="flex items-center gap-4">
        {user ? (
          <>
            <span className="text-sm text-gray-500">
              {user.username}{" "}
              <span className="text-xs bg-gray-100 px-2 py-0.5 rounded">{user.role}</span>
            </span>
            <button
              onClick={handleLogout}
              className="text-sm text-gray-600 hover:text-gray-900"
            >
              Logout
            </button>
          </>
        ) : (
          <Link href="/login" className="text-sm text-gray-600 hover:text-gray-900">
            Login
          </Link>
        )}
      </div>
    </nav>
  );
}
