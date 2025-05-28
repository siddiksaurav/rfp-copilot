import React from "react";
import TorProcessTable from "../components/TorProcess";

// Helper to fetch TOR queue from the server
async function getTorQueue() {
  try {
    const queueRes = await fetch(`${process.env.NEXT_PUBLIC_BACKEND_URL}/tor-process-queue`, { 
      cache: "no-store",
      headers: {
        'Content-Type': 'application/json',
      }
    });
    
    if (!queueRes.ok) {
      console.error('Failed to fetch TOR queue:', queueRes.statusText);
      return [];
    }
    
    const torNames = await queueRes.json();
    return torNames;
  } catch (error) {
    console.error('Error in getTorQueue:', error);
    return [];
  }
}

export default async function Home() {
  const torQueue = await getTorQueue();

  return (
    <main className="min-h-screen p-8 bg-gray-50">
      <div className="max-w-6xl mx-auto space-y-8">
        <div className="text-center space-y-2">
          <h1 className="text-4xl font-bold text-gray-900">RFP Document Analyzer</h1>
          {/* <p className="text-lg text-gray-600">
            Upload your Terms of Reference (TOR) document for automatic validation and analysis
          </p> */}
        </div>
        
        <TorProcessTable torQueue={torQueue} />
      </div>
    </main>
  );
}