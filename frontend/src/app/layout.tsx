import type { Metadata } from "next";
import { Geist, Geist_Mono } from "next/font/google";
import Link from "next/link";
import "./globals.css";

const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

export const metadata: Metadata = {
  title: "Request Portal",
  description: "Submit and track service requests",
};

export default function RootLayout({ children }: LayoutProps<"/">) {
  return (
    <html
      lang="en"
      className={`${geistSans.variable} ${geistMono.variable} h-full antialiased`}
    >
      <body className="flex min-h-full flex-col">
        <header className="border-b border-gray-200 bg-white">
          <div className="mx-auto flex max-w-4xl items-center justify-between px-4 py-4">
            <Link href="/" className="text-lg font-bold">
              Request Portal
            </Link>
            <nav className="flex gap-6 text-sm font-medium">
              <Link
                href="/"
                className="text-gray-600 hover:text-gray-900"
              >
                Submit
              </Link>
              <Link
                href="/track"
                className="text-gray-600 hover:text-gray-900"
              >
                Track
              </Link>
              <Link
                href="/staff"
                className="text-gray-600 hover:text-gray-900"
              >
                Staff
              </Link>
            </nav>
          </div>
        </header>

        <main className="mx-auto w-full max-w-4xl flex-1 px-4 py-8">
          {children}
        </main>

        <footer className="border-t border-gray-200 py-4 text-center text-xs text-gray-400">
          Request Portal &middot; Stage 3
        </footer>
      </body>
    </html>
  );
}
