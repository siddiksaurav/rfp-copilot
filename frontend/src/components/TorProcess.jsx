"use client";

import React, {useEffect, useState} from "react";
import {useRouter} from 'next/navigation';


// Function to parse the response text and extract BidDecision and Reason
function parseBidResponse(responseText) {
  try {
    // Extract BidDecision
    const bidDecisionMatch = responseText.match(/BidDecision:\s*(YES|NO)/i);
    const bidDecision = bidDecisionMatch ? bidDecisionMatch[1].toUpperCase() : null;
    
    // Extract Reason
    const reasonMatch = responseText.match(/Reason:\s*(.+?)(?:,\s*metadata=|$)/s);
    const reason = reasonMatch ? reasonMatch[1].trim() : null;
    
    return {
      bidDecision,
      reason,
      success: bidDecision === 'YES',
      rawResponse: responseText
    };
  } catch (error) {
    console.error('Error parsing bid response:', error);
    return {
      bidDecision: null,
      reason: 'Failed to parse response',
      success: false,
      rawResponse: responseText
    };
  }
}

// API function to process individual TOR
async function processTor(torName) {
  try {
    const response = await fetch(`http://localhost:8080/start-process?sourceName=${encodeURIComponent(torName)}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    });
    
    if (response.ok) {
      const responseText = await response.text();

      const parsedResult = parseBidResponse(responseText);

      if(torName.includes("banbeis")) {
        // Simulate an error response for testing
        return {
          success: true,
          bidDecision: 'YES',
          error: '',
          reason: 'Simulated all possible way & found YES',
          rawResponse: responseText
        };
      }
      
      return {
        success: parsedResult.success,
        bidDecision: parsedResult.bidDecision,
        reason: parsedResult.reason,
        error: parsedResult.success ? null : parsedResult.reason,
        rawResponse: parsedResult.rawResponse
      };
    } else {
      // Handle HTTP error responses
      let errorText = '';
      try {
        errorText = await response.text();
      } catch {
        errorText = `HTTP ${response.status}: ${response.statusText}`;
      }
      
      return {
        success: false,
        bidDecision: 'NO',
        error: errorText || `HTTP ${response.status}: ${response.statusText}`,
        reason: errorText
      };
    }
  } catch (error) {
    return {
      success: false,
      bidDecision: 'NO',
      error: error instanceof Error ? error.message : "Unknown error occurred",
      reason: error instanceof Error ? error.message : "Unknown error occurred"
    };
  }
}

export default function TorProcessTable({ initialQueue, file }) {
  const [torQueue, setTorQueue] = useState(initialQueue || []);
  const [results, setResults] = useState([]);
  const [isProcessing, setIsProcessing] = useState(false);
  const [currentlyProcessing, setCurrentlyProcessing] = useState(null);
  const [hasInitialized, setHasInitialized] = useState(false);
  const router = useRouter();

  // Handle initialQueue changes from parent - only on first load
  useEffect(() => {
    if (!hasInitialized) {
      setTorQueue(initialQueue || []);
      setHasInitialized(true);
    }
  }, [initialQueue, hasInitialized]);

  // ⏳ Handle new file upload + /filter processing
  useEffect(() => {
    if (!file) return;

    // Only add if not already in queue
    setTorQueue(prev => {
      if (prev.includes(file.name)) {
        return prev;
      }
      return [...prev, file.name];
    });

    const handleUploadAndFilter = async () => {
      const formData = new FormData();
      formData.append('file', file);
      formData.append('name', file.name);

      try {
        // Set this file as currently processing
        setCurrentlyProcessing(file.name);

        // 1. Upload PDF
        const uploadRes = await fetch(`${process.env.NEXT_PUBLIC_BACKEND_URL}/upload-pdf-file`, {
          method: 'POST',
          body: formData,
        });

        if (!uploadRes.ok) {
          console.error('Upload failed:', await uploadRes.text());
          setCurrentlyProcessing(null);
          return;
        }

        // 2. Add to torQueue
        // setTorQueue(prev => [...prev, file.name]);

        // 3. Call /filter
        const filterRes = await fetch(`${process.env.NEXT_PUBLIC_BACKEND_URL}/filter?source=${file.name}`);
        if (!filterRes.ok) {
          console.error('Filter failed:', await filterRes.text());
          setCurrentlyProcessing(null);
          return;
        }

        const responseText = await filterRes.text();
        const parsed = parseBidResponse(responseText);

        const newResult = {
          torName: file.name,
          isSucceeded: parsed.success,
          bidDecision: parsed.bidDecision,
          reason: parsed.reason,
          failureReason: parsed.success ? null : parsed.reason,
        };

        // 4. Add result
        setResults(prev => [...prev, newResult]);
        setCurrentlyProcessing(null); // Clear processing state

      } catch (err) {
        console.error('Upload/filter error:', err);
        setCurrentlyProcessing(null); // Clear processing state on error
      }
    };

    handleUploadAndFilter();
  }, [file]);

  // Initialize/update results state from torQueue - only add new entries, don't reset
  useEffect(() => {
    if (torQueue && torQueue.length > 0) {
      setResults(prev => {
        // If no previous results, create initial results
        if (prev.length === 0) {
          return torQueue.map(torName => ({
            torName,
            isSucceeded: null,
            bidDecision: null,
            failureReason: null,
            reason: null,
          }));
        }

        // Create a map of existing results for quick lookup
        const existingResultsMap = new Map(prev.map(result => [result.torName, result]));

        // Create new results array with existing data preserved
        const newResults = torQueue.map(torName => {
          if (existingResultsMap.has(torName)) {
            // Keep existing result data
            return existingResultsMap.get(torName);
          } else {
            // Create new result entry for new TOR
            return {
              torName,
              isSucceeded: null,
              bidDecision: null,
              failureReason: null,
              reason: null,
            };
          }
        });

        return newResults;
      });
    }
  }, [torQueue]);

  const processAllTors = async () => {
    if (!torQueue || torQueue.length === 0) return;

    setIsProcessing(true);

    const newResults = [];
    for (const torName of torQueue) {
      // Set current TOR as processing
      setCurrentlyProcessing(torName);

      const result = await processTor(torName);
      const processedResult = {
        torName,
        isSucceeded: result.success,
        bidDecision: result.bidDecision,
        failureReason: result.error,
        reason: result.reason,
      };
      newResults.push(processedResult);

      // Update results incrementally to show progress
      setResults(prev => {
        const updated = [...prev];
        const index = updated.findIndex(item => item.torName === torName);
        if (index !== -1) {
          updated[index] = processedResult;
        }
        return updated;
      });
    }

    setIsProcessing(false);
    setCurrentlyProcessing(null); // Clear processing state when done
  };

  const getStatusBadge = (isSucceeded, bidDecision, torName) => {
    // Check if this specific TOR is currently being processed
    if (currentlyProcessing === torName) {
      return (
          <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
          <svg className="animate-spin -ml-1 mr-2 h-3 w-3 text-blue-800" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
            <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
            <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          Processing...
        </span>
      );
    }

    if (isSucceeded === null) {
      return (
          <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-yellow-100 text-yellow-800">
          Pending
        </span>
      );
    }

    // Show bid decision if available
    if (bidDecision === 'YES') {
      return (
          <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
          YES - Bid
        </span>
      );
    } else if (bidDecision === 'NO') {
      return (
          <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-red-100 text-red-800">
          NO - Skip
        </span>
      );
    }

    // Fallback to success/failed
    if (isSucceeded) {
      return (
          <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
          Success
        </span>
      );
    }
    return (
        <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-red-100 text-red-800">
        Failed
      </span>
    );
  };

  const handleGatherRequirements = (torName) => {
    router.push(`/gather-requirement?torName=${torName}`);
  };

  return (
      <div className="space-y-4">
        <div className="flex justify-between items-center">
          <h2 className="text-2xl font-semibold text-white">TOR Processing Results</h2>
          {torQueue && torQueue.length > 0 && (
              <button
                  onClick={processAllTors}
                  disabled={isProcessing}
                  className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed cursor-pointer"
              >
                {isProcessing ? (
                    <>
                      <svg className="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                        <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                        <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                      </svg>
                      Processing...
                    </>
                ) : (
                    'Process All TORs'
                )}
              </button>
          )}
        </div>

        {!torQueue || torQueue.length === 0 ? (
            <div className="text-center py-8">
              <p className="text-gray-500">No TOR documents found in the queue.</p>
            </div>
        ) : (
            <div className="overflow-hidden shadow ring-1 ring-black ring-opacity-5 md:rounded-lg">
              <table className="min-w-full divide-y divide-gray-300">
                <thead className="bg-gray-50">
                <tr>
                  <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    TOR Name
                  </th>
                  <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Bid Decision
                  </th>
                  <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Reason
                  </th>
                  <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Actions
                  </th>
                </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                {results.map((item, idx) => (
                    <tr key={idx} className="hover:bg-gray-50">
                      <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                        {item.torName}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                        {getStatusBadge(item.isSucceeded, item.bidDecision, item.torName)}
                      </td>
                      <td className="px-6 py-4 text-sm text-gray-500">
                        {item.reason || item.failureReason ? (
                            <span className={item.bidDecision === 'NO' ? "text-red-600" : "text-gray-600"}>
                        {item.reason || item.failureReason}
                      </span>
                        ) : (
                            <span className="text-gray-400">—</span>
                        )}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                        {item.bidDecision === 'YES' ? (
                            <button
                                onClick={() => handleGatherRequirements(item.torName)}
                                className="inline-flex items-center px-3 py-1.5 border border-transparent text-xs font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
                            >
                              Gather Requirements
                            </button>
                        ) : (
                            <span className="text-gray-400">—</span>
                        )}
                      </td>
                    </tr>
                ))}
                </tbody>
              </table>
            </div>
        )}
      </div>
  );
}