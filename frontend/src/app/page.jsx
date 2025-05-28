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
    <main className="min-h-screen p-8 bg-gray-900">
      <div className="max-w-6xl mx-auto space-y-8">
        <div className="text-center space-y-4">
          <div className="space-y-2">
            <h1 className="text-4xl font-bold text-white">RFP Document Analyzer</h1>
            <div className="w-24 h-1 bg-blue-500 mx-auto rounded-full"></div>
          </div>
          <p className="text-lg text-gray-300 max-w-2xl mx-auto">
            Intelligent analysis and validation of Terms of Reference documents with automated requirement gathering
          </p>
        </div>
        
        <div className="bg-gray-800 shadow-2xl rounded-xl border border-gray-700">
          <div className="px-6 py-4 border-b border-gray-700 bg-gray-800 rounded-t-xl">
            <div className="flex items-center space-x-3">
              <div className="flex space-x-2">
                <div className="w-3 h-3 bg-red-500 rounded-full"></div>
                <div className="w-3 h-3 bg-yellow-500 rounded-full"></div>
                <div className="w-3 h-3 bg-green-500 rounded-full"></div>
              </div>
              <h2 className="text-xl font-semibold text-white">Document Processing Queue</h2>
            </div>
          </div>
          
          <div className="p-6">
            <TorProcessTable torQueue={torQueue} />
          </div>
        </div>
        
          {/* <div className="bg-gray-800 border border-gray-700 rounded-lg p-6">
            <div className="flex items-center space-x-3">
              <div className="w-10 h-10 bg-blue-500 rounded-lg flex items-center justify-center">
                <svg className="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                </svg>
              </div>
              <div>
                <h3 className="text-lg font-semibold text-white">Smart Analysis</h3>
                <p className="text-gray-400 text-sm">AI-powered document processing</p>
              </div>
            </div>
          </div>
          
          <div className="bg-gray-800 border border-gray-700 rounded-lg p-6">
            <div className="flex items-center space-x-3">
              <div className="w-10 h-10 bg-green-500 rounded-lg flex items-center justify-center">
                <svg className="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5H7a2 2 0 00-2 2v10a2 2 0 002 2h8a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4" />
                </svg>
              </div>
              <div>
                <h3 className="text-lg font-semibold text-white">Automated Validation</h3>
                <p className="text-gray-400 text-sm">Real-time requirement checking</p>
              </div>
            </div>
          </div>
          
          <div className="bg-gray-800 border border-gray-700 rounded-lg p-6">
            <div className="flex items-center space-x-3">
              <div className="w-10 h-10 bg-purple-500 rounded-lg flex items-center justify-center">
                <svg className="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 10V3L4 14h7v7l9-11h-7z" />
                </svg>
              </div>
              <div>
                <h3 className="text-lg font-semibold text-white">Fast Processing</h3>
                <p className="text-gray-400 text-sm">Quick turnaround times</p>
              </div>
            </div>
          </div> */}
      </div>
    </main>
  );
}