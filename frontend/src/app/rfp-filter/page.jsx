import React from "react";
import TorClientSection from "../../components/HomeClient";

// Helper to fetch TOR queue
async function getTorQueue() {
  try {
    const res = await fetch(`${process.env.NEXT_PUBLIC_BACKEND_URL}/tor-process-queue`, {
      cache: "no-store",
      headers: { 'Content-Type': 'application/json' },
    });

    if (!res.ok) return [];
    return await res.json();
  } catch (err) {
    console.error('Error:', err);
    return [];
  }
}

export default async function RfpFilterPage() {
  const torQueue = await getTorQueue();

  return (
      <main className="min-h-screen p-8 bg-gray-900">
        <TorClientSection torQueue={torQueue} />
      </main>
  );
}
