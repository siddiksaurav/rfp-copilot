import React from 'react';
import LandingPage from "../components/landing-page";

export default async function Home() {
  return (
      <main className="min-h-screen p-8 bg-gray-900">
        <LandingPage />
      </main>
  );
}