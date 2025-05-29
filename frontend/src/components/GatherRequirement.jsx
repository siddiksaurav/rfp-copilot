'use client';

import {useEffect, useState} from 'react';
import ReactMarkdown from 'react-markdown';
import FloatingChat from "./chat/FloatingChat";
import {AnimatePresence, motion} from 'framer-motion';
import {useRouter} from "next/navigation";

export default function GatherRequirementClient({ torName }) {
  const [data, setData] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [showActionButtons, setShowActionButtons] = useState(false);
  const router = useRouter();

  // Function to parse the requirement response
  const parseRequirementResponse = (responseText) => {
    try {
      // Check if it's the AssistantMessage format
      if (responseText.includes('AssistantMessage [')) {
        // Extract textContent using regex
        const textContentMatch = responseText.match(/textContent=([^,]+(?:,[^,]*)*?)(?:,\s*metadata=|$)/s);
        if (textContentMatch) {
          let textContent = textContentMatch[1];

          // Remove leading/trailing quotes if present
          if (textContent.startsWith('"') && textContent.endsWith('"')) {
            textContent = textContent.slice(1, -1);
          }

          // Handle escaped characters
          textContent = textContent
              .replace(/\\n/g, '\n')
              .replace(/\\t/g, '\t')
              .replace(/\\"/g, '"')
              .replace(/\\\\/g, '\\');

          return {
            success: true,
            content: textContent,
            messageType: 'ASSISTANT',
            rawResponse: responseText
          };
        }
      }

      // Try to parse as JSON
      const parsed = JSON.parse(responseText);
      if (parsed.textContent) {
        return {
          success: true,
          content: parsed.textContent,
          messageType: parsed.messageType || 'ASSISTANT',
          rawResponse: responseText
        };
      }

      return {
        success: true,
        content: responseText,
        messageType: 'PLAIN_TEXT',
        rawResponse: responseText
      };
    } catch {
      // If not JSON, treat as plain text
      return {
        success: true,
        content: responseText,
        messageType: 'PLAIN_TEXT',
        rawResponse: responseText
      };
    }
  };

  const fetchRequirementData = async () => {
    if (!torName) return;

    setLoading(true);
    setError(null);
    setData('');
    setShowActionButtons(false);

    try {
      const params = new URLSearchParams();
      if (torName) {
        params.append('source', torName);
      }

      const response = await fetch(`${process.env.NEXT_PUBLIC_BACKEND_URL}/gather-requirement?${params.toString()}`);

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const responseText = await response.text();
      console.log(`Response text for ${torName}:`, responseText);

      const parsedResult = parseRequirementResponse(responseText);

      if (parsedResult.success) {
        setData(parsedResult.content);
        // Show action buttons after data is loaded
        setTimeout(() => setShowActionButtons(true), 500);
      } else {
        setError('Failed to parse response');
      }

    } catch (err) {
      setError(err instanceof Error ? err.message : 'An error occurred');
      console.error('Error fetching requirement data:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleCVMaker = () => {
    console.log('Navigate to CV Maker');
    router.push(`/resume?torName=${torName}`);
  };

  const handleWorkScheduleGenerator = () => {
    console.log('Navigate to Work Schedule Generator');
    router.push(`/work-schedule?torName=${torName}`);
  };

  useEffect(() => {
    if (torName) {
      fetchRequirementData();
    }
  }, [torName]);

  // Animation variants
  const containerVariants = {
    hidden: { opacity: 0, y: 20 },
    visible: {
      opacity: 1,
      y: 0,
      transition: {
        duration: 0.6,
        staggerChildren: 0.1
      }
    }
  };

  const itemVariants = {
    hidden: { opacity: 0, y: 10 },
    visible: { opacity: 1, y: 0 }
  };

  const buttonVariants = {
    hidden: { opacity: 0, scale: 0.8 },
    visible: {
      opacity: 1,
      scale: 1,
      transition: {
        type: "spring",
        stiffness: 100,
        damping: 10
      }
    },
    hover: {
      scale: 1.05,
      transition: { duration: 0.2 }
    },
    tap: { scale: 0.95 }
  };

  return (
      <motion.div
          className="min-h-screen p-8 bg-gray-900"
          initial="hidden"
          animate="visible"
          variants={containerVariants}
      >
        <FloatingChat/>
        <div className="max-w-4xl mx-auto p-6">
          <motion.div
              className="bg-white shadow-lg rounded-lg overflow-hidden"
              variants={itemVariants}
              whileHover={{ y: -2, transition: { duration: 0.2 } }}
          >
            <motion.div
                className="px-6 py-4 border-b border-gray-200"
                variants={itemVariants}
            >
              <h1 className="text-2xl font-bold text-gray-900">
                Gather Requirement Analysis
              </h1>
              {torName && (
                  <p className="text-sm text-gray-600 mt-1">
                    Source: <span className="font-medium">{torName}</span>
                  </p>
              )}
            </motion.div>

            <motion.div className="p-6" variants={itemVariants}>
              {!torName ? (
                  <motion.div
                      className="text-center py-8"
                      initial={{ opacity: 0 }}
                      animate={{ opacity: 1 }}
                      transition={{ delay: 0.3 }}
                  >
                    <div className="text-gray-500">
                      <motion.svg
                          className="mx-auto h-12 w-12 text-gray-400"
                          fill="none"
                          viewBox="0 0 24 24"
                          stroke="currentColor"
                          animate={{ rotate: [0, 5, -5, 0] }}
                          transition={{ duration: 2, repeat: Infinity, repeatDelay: 3 }}
                      >
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                      </motion.svg>
                      <h3 className="mt-2 text-sm font-medium text-gray-900">No source specified</h3>
                      <p className="mt-1 text-sm text-gray-500">
                        Please provide a source parameter to begin requirement analysis.
                      </p>
                    </div>
                  </motion.div>
              ) : (
                  <>
                    <motion.div
                        className="flex items-center justify-between mb-4"
                        variants={itemVariants}
                    >
                      <motion.button
                          onClick={fetchRequirementData}
                          disabled={loading}
                          className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed"
                          variants={buttonVariants}
                          whileHover="hover"
                          whileTap="tap"
                      >
                        {loading ? (
                            <>
                              <motion.svg
                                  className="animate-spin -ml-1 mr-3 h-5 w-5 text-white"
                                  xmlns="http://www.w3.org/2000/svg"
                                  fill="none"
                                  viewBox="0 0 24 24"
                                  animate={{ rotate: 360 }}
                                  transition={{ duration: 1, repeat: Infinity, ease: "linear" }}
                              >
                                <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                                <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                              </motion.svg>
                              Analyzing...
                            </>
                        ) : (
                            'Refresh Analysis'
                        )}
                      </motion.button>
                    </motion.div>

                    <AnimatePresence>
                      {error && (
                          <motion.div
                              className="rounded-md bg-red-50 p-4 mb-4"
                              initial={{ opacity: 0, height: 0 }}
                              animate={{ opacity: 1, height: 'auto' }}
                              exit={{ opacity: 0, height: 0 }}
                              transition={{ duration: 0.3 }}
                          >
                            <div className="flex">
                              <div className="flex-shrink-0">
                                <svg className="h-5 w-5 text-red-400" viewBox="0 0 20 20" fill="currentColor">
                                  <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd" />
                                </svg>
                              </div>
                              <div className="ml-3">
                                <h3 className="text-sm font-medium text-red-800">Error occurred</h3>
                                <p className="text-sm text-red-700 mt-1">{error}</p>
                              </div>
                            </div>
                          </motion.div>
                      )}
                    </AnimatePresence>

                    <motion.div
                        className="border rounded-lg"
                        variants={itemVariants}
                        layout
                    >
                      <div className="px-4 py-3 bg-gray-50 border-b">
                        <h3 className="text-lg font-medium text-gray-900">Analysis Results</h3>
                      </div>
                      <div className="p-4">
                        <AnimatePresence mode="wait">
                          {loading && !data && (
                              <motion.div
                                  className="flex items-center justify-center py-8"
                                  initial={{ opacity: 0 }}
                                  animate={{ opacity: 1 }}
                                  exit={{ opacity: 0 }}
                                  key="loading"
                              >
                                <div className="animate-pulse flex space-x-4 w-full">
                                  <div className="flex-1 space-y-2">
                                    <motion.div
                                        className="h-4 bg-gray-200 rounded w-3/4"
                                        animate={{ opacity: [0.5, 1, 0.5] }}
                                        transition={{ duration: 1.5, repeat: Infinity }}
                                    />
                                    <motion.div
                                        className="h-4 bg-gray-200 rounded w-1/2"
                                        animate={{ opacity: [0.5, 1, 0.5] }}
                                        transition={{ duration: 1.5, repeat: Infinity, delay: 0.2 }}
                                    />
                                    <motion.div
                                        className="h-4 bg-gray-200 rounded w-5/6"
                                        animate={{ opacity: [0.5, 1, 0.5] }}
                                        transition={{ duration: 1.5, repeat: Infinity, delay: 0.4 }}
                                    />
                                  </div>
                                </div>
                              </motion.div>
                          )}

                          {data ? (
                              <motion.div
                                  className="prose prose-slate max-w-none"
                                  initial={{ opacity: 0, y: 20 }}
                                  animate={{ opacity: 1, y: 0 }}
                                  transition={{ duration: 0.5 }}
                                  key="content"
                              >
                                <ReactMarkdown
                                    components={{
                                      h1: ({children}) => <h1 className="text-2xl font-bold text-gray-900 mb-4 pb-2 border-b border-gray-200">{children}</h1>,
                                      h2: ({children}) => <h2 className="text-xl font-semibold text-gray-800 mb-3 mt-6">{children}</h2>,
                                      h3: ({children}) => <h3 className="text-lg font-medium text-gray-700 mb-2 mt-4">{children}</h3>,
                                      ul: ({children}) => <ul className="list-disc ml-6 mb-4 space-y-1">{children}</ul>,
                                      ol: ({children}) => <ol className="list-decimal ml-6 mb-4 space-y-1">{children}</ol>,
                                      li: ({children}) => <li className="text-gray-700 leading-relaxed">{children}</li>,
                                      p: ({children}) => <p className="text-gray-700 mb-3 leading-relaxed">{children}</p>,
                                      strong: ({children}) => <strong className="font-semibold text-gray-900">{children}</strong>,
                                      em: ({children}) => <em className="italic text-gray-800">{children}</em>,
                                      code: ({children}) => <code className="bg-gray-100 px-2 py-1 rounded text-sm font-mono text-red-600">{children}</code>,
                                      pre: ({children}) => <pre className="bg-gray-50 p-4 rounded-lg overflow-x-auto text-sm border">{children}</pre>,
                                      blockquote: ({children}) => <blockquote className="border-l-4 border-blue-500 pl-4 italic text-gray-600 my-4">{children}</blockquote>,
                                      hr: () => <hr className="my-6 border-t border-gray-300" />,
                                      table: ({children}) => <table className="min-w-full divide-y divide-gray-200 my-4">{children}</table>,
                                      thead: ({children}) => <thead className="bg-gray-50">{children}</thead>,
                                      tbody: ({children}) => <tbody className="bg-white divide-y divide-gray-200">{children}</tbody>,
                                      tr: ({children}) => <tr>{children}</tr>,
                                      th: ({children}) => <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">{children}</th>,
                                      td: ({children}) => <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{children}</td>,
                                    }}
                                >
                                  {data}
                                </ReactMarkdown>
                              </motion.div>
                          ) : !loading && (
                              <motion.p
                                  className="text-gray-500 italic text-center py-8"
                                  initial={{ opacity: 0 }}
                                  animate={{ opacity: 1 }}
                                  key="placeholder"
                              >
                                Click "Refresh Analysis" to start the requirement gathering process.
                              </motion.p>
                          )}
                        </AnimatePresence>
                      </div>
                    </motion.div>

                    {/* Action Buttons - Show after analysis is complete */}
                    <AnimatePresence>
                      {showActionButtons && data && (
                          <motion.div
                              className="mt-6 flex flex-col sm:flex-row gap-6 justify-center"
                              initial={{ opacity: 0, y: 30 }}
                              animate={{ opacity: 1, y: 0 }}
                              exit={{ opacity: 0, y: -30 }}
                              transition={{ duration: 0.5, delay: 0.2 }}
                          >
                            <motion.button
                                onClick={handleCVMaker}
                                className="inline-flex items-center px-6 py-2 border border-transparent text-base font-medium rounded-md text-white bg-purple-600 hover:from-purple-700 hover:to-purple-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-purple-500 shadow-lg cursor-pointer"
                                variants={buttonVariants}
                                initial="hidden"
                                animate="visible"
                                whileHover="hover"
                                whileTap="tap"
                            >
                              <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                              </svg>
                              CV Maker
                            </motion.button>

                            <motion.button
                                onClick={handleWorkScheduleGenerator}
                                className="inline-flex items-center px-6 py-2 border border-transparent text-base font-medium rounded-md text-white bg-teal-500 hover:from-teal-700 hover:to-teal-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500 shadow-lg cursor-pointer"
                                variants={buttonVariants}
                                initial="hidden"
                                animate="visible"
                                whileHover="hover"
                                whileTap="tap"
                                transition={{ delay: 0.1 }}
                            >
                              <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3a4 4 0 118 0v4m-4 6v3m5-1V7a1 1 0 00-1-1H7a1 1 0 00-1 1v10a1 1 0 001 1h10a1 1 0 001-1V7a1 1 0 00-1-1z" />
                              </svg>
                              Work Schedule Generator
                            </motion.button>
                          </motion.div>
                      )}
                    </AnimatePresence>
                  </>
              )}
            </motion.div>
          </motion.div>
        </div>
      </motion.div>
  );
}