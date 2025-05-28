'use client';

import {useEffect, useState} from 'react';
import {AnimatePresence, motion} from 'framer-motion';
import ReactMarkdown from 'react-markdown';
import {
  Calendar,
  CalendarDays,
  Clock,
  Download,
  FileText,
  Loader2,
  Printer,
  RefreshCw,
  Share2,
  ZoomIn,
  ZoomOut
} from 'lucide-react';
import {parseRequirementResponse} from "../lib/utils";

export default function WorkScheduleClient({ torName }) {
  const [schedule, setSchedule] = useState('');
  const [loading, setLoading] = useState(false);
  const [zoomLevel, setZoomLevel] = useState(100);
  const [lastUpdated, setLastUpdated] = useState(null);

  const fetchSchedule = async (source = '') => {
    setLoading(true);
    try {
      const url = `${process.env.NEXT_PUBLIC_BACKEND_URL}/work-schedule/generate${source ? `?source=${encodeURIComponent(source)}` : ''}`;
      const response = await fetch(url);
      if (!response.ok) throw new Error('Failed to fetch work schedule');

      const data = await response.text();
      const parsedData = parseRequirementResponse(data);
      setSchedule(parsedData?.content || parsedData?.rawResponse);
      setLastUpdated(new Date());
    } catch (error) {
      console.error('Error fetching work schedule:', error);
      setSchedule('');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchSchedule(torName);
  }, [torName]);

  const handleZoomIn = () => {
    setZoomLevel(prev => Math.min(prev + 10, 200));
  };

  const handleZoomOut = () => {
    setZoomLevel(prev => Math.max(prev - 10, 50));
  };

  const handlePrint = () => {
    window.print();
  };

  const handleShare = async () => {
    if (navigator.share) {
      try {
        await navigator.share({
          title: 'Work Schedule',
          text: 'Check out this work schedule',
          url: window.location.href
        });
      } catch (error) {
        console.log('Error sharing:', error);
      }
    } else {
      // Fallback - copy to clipboard
      await navigator.clipboard.writeText(window.location.href);
      alert('Link copied to clipboard!');
    }
  };

  const downloadAsText = () => {
    const blob = new Blob([schedule], { type: 'text/plain' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `work-schedule-${torName || 'default'}-${new Date().toISOString().split('T')[0]}.txt`;
    document.body.appendChild(a);
    a.click();
    window.URL.revokeObjectURL(url);
    document.body.removeChild(a);
  };

  return (
      <div className="min-h-screen bg-gradient-to-br from-blue-50 via-indigo-50 to-purple-50 p-4">
        <div className="max-w-6xl mx-auto">
          {/* Header */}
          <motion.div
              initial={{ opacity: 0, y: -20 }}
              animate={{ opacity: 1, y: 0 }}
              className="mb-8"
          >
            <div className="flex items-center justify-between bg-white rounded-xl shadow-sm p-6 border border-gray-200">
              <div className="flex items-center gap-4">
                <div className="p-3 bg-gradient-to-br from-blue-500 to-indigo-600 rounded-lg">
                  <Calendar className="w-6 h-6 text-white" />
                </div>
                <div>
                  <h1 className="text-2xl font-bold text-gray-900">Work Schedule Generator</h1>
                  <div className="flex items-center gap-4 mt-1">
                    <p className="text-gray-600">
                      {torName ? `Source: ${torName}` : 'Default Source'}
                    </p>
                    {lastUpdated && (
                        <span className="text-sm text-gray-500">
                      Updated: {lastUpdated.toLocaleTimeString()}
                    </span>
                    )}
                  </div>
                </div>
              </div>

              <div className="flex items-center gap-2">
              <span className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-green-100 text-green-800">
                <Clock className="w-4 h-4 mr-1" />
                {schedule ? 'Generated' : 'Pending'}
              </span>
                <button
                    onClick={() => fetchSchedule(torName)}
                    disabled={loading}
                    className="inline-flex items-center px-3 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  <RefreshCw className={`w-4 h-4 mr-2 ${loading ? 'animate-spin' : ''}`} />
                  Regenerate
                </button>
              </div>
            </div>
          </motion.div>

          {loading ? (
              <motion.div
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                  className="flex items-center justify-center py-20"
              >
                <div className="text-center">
                  <Loader2 className="w-8 h-8 animate-spin text-blue-600 mx-auto mb-4" />
                  <p className="text-gray-600">Generating work schedule...</p>
                  <p className="text-sm text-gray-500 mt-2">This may take a few moments</p>
                </div>
              </motion.div>
          ) : (
              <>
                {/* Controls */}
                <motion.div
                    initial={{ opacity: 0, y: -10 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ delay: 0.1 }}
                    className="mb-6"
                >
                  <div className="flex items-center justify-between bg-white rounded-lg shadow-sm p-4 border border-gray-200">
                    <div className="flex items-center gap-2">
                      <div className="flex items-center gap-2 px-3 py-1 bg-gray-100 rounded-md">
                        <CalendarDays className="w-4 h-4 text-gray-600" />
                        <span className="text-sm font-medium text-gray-700">Schedule Document</span>
                      </div>
                    </div>

                    <div className="flex items-center gap-2">
                      <button
                          onClick={handleZoomOut}
                          className="inline-flex items-center px-2 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
                      >
                        <ZoomOut className="w-4 h-4" />
                      </button>
                      <span className="text-sm text-gray-600 px-2 min-w-[60px] text-center">
                    {zoomLevel}%
                  </span>
                      <button
                          onClick={handleZoomIn}
                          className="inline-flex items-center px-2 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
                      >
                        <ZoomIn className="w-4 h-4" />
                      </button>

                      <div className="w-px h-6 bg-gray-300 mx-2"></div>

                      <button
                          onClick={handlePrint}
                          className="inline-flex items-center px-3 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
                      >
                        <Printer className="w-4 h-4 mr-2" />
                        Print
                      </button>

                      <button
                          onClick={handleShare}
                          className="inline-flex items-center px-3 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
                      >
                        <Share2 className="w-4 h-4 mr-2" />
                        Share
                      </button>

                      <button
                          onClick={downloadAsText}
                          className="inline-flex items-center px-4 py-2 text-sm font-medium text-white bg-blue-600 border border-transparent rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
                      >
                        <Download className="w-4 h-4 mr-2" />
                        Download
                      </button>
                    </div>
                  </div>
                </motion.div>

                {/* Schedule Display */}
                <AnimatePresence mode="wait">
                  {schedule ? (
                      <motion.div
                          key="schedule"
                          initial={{ opacity: 0, scale: 0.95 }}
                          animate={{ opacity: 1, scale: 1 }}
                          exit={{ opacity: 0, scale: 0.95 }}
                          transition={{ duration: 0.3 }}
                          className="flex justify-center"
                      >
                        <ScheduleDocument
                            schedule={schedule}
                            zoomLevel={zoomLevel}
                            source={torName}
                        />
                      </motion.div>
                  ) : (
                      <motion.div
                          initial={{ opacity: 0 }}
                          animate={{ opacity: 1 }}
                          className="text-center py-20"
                      >
                        <Calendar className="w-16 h-16 text-gray-400 mx-auto mb-4" />
                        <h3 className="text-lg font-medium text-gray-900 mb-2">No Schedule Generated</h3>
                        <p className="text-gray-600 mb-4">Click "Regenerate" to create a work schedule for the selected source.</p>
                        <button
                            onClick={() => fetchSchedule(torName)}
                            className="inline-flex items-center px-4 py-2 text-sm font-medium text-white bg-blue-600 border border-transparent rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
                        >
                          <Calendar className="w-4 h-4 mr-2" />
                          Generate Schedule
                        </button>
                      </motion.div>
                  )}
                </AnimatePresence>
              </>
          )}
        </div>
      </div>
  );
}

function ScheduleDocument({ schedule, zoomLevel, source }) {
  return (
      <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.4 }}
          className="bg-white rounded-lg shadow-xl border border-gray-200 max-w-5xl w-full overflow-hidden"
      >
        {/* Document Header */}
        <div className="bg-gradient-to-r from-blue-500 to-indigo-600 px-6 py-4 text-white">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <Calendar className="w-6 h-6" />
              <div>
                <h2 className="text-lg font-semibold">Work Schedule</h2>
                <p className="text-blue-100 text-sm">
                  {source ? `Generated for: ${source}` : 'Default Schedule'}
                </p>
              </div>
            </div>
            <div className="text-right">
              <p className="text-sm text-blue-100">Generated on</p>
              <p className="font-medium">{new Date().toLocaleDateString()}</p>
            </div>
          </div>
        </div>

        {/* Document Content */}
        <div className="p-0">
          <div
              className="p-8 overflow-auto transition-all duration-300 bg-white"
              style={{
                transform: `scale(${zoomLevel / 100})`,
                transformOrigin: 'top left',
                width: `${10000 / zoomLevel}%`,
                minHeight: '600px'
              }}
          >
            <div className="prose prose-gray max-w-none">
              <ReactMarkdown
                  components={{
                    h1: ({ children }) => (
                        <h1 className="text-2xl font-bold text-gray-900 mb-6 pb-3 border-b-2 border-blue-200">
                          {children}
                        </h1>
                    ),
                    h2: ({ children }) => (
                        <h2 className="text-xl font-semibold text-gray-800 mt-8 mb-4 pb-2 border-b border-gray-200">
                          {children}
                        </h2>
                    ),
                    h3: ({ children }) => (
                        <h3 className="text-lg font-medium text-gray-700 mt-6 mb-3">
                          {children}
                        </h3>
                    ),
                    p: ({ children }) => (
                        <p className="text-gray-700 mb-4 leading-relaxed">
                          {children}
                        </p>
                    ),
                    ul: ({ children }) => (
                        <ul className="list-disc list-inside text-gray-700 mb-6 space-y-2">
                          {children}
                        </ul>
                    ),
                    ol: ({ children }) => (
                        <ol className="list-decimal list-inside text-gray-700 mb-6 space-y-2">
                          {children}
                        </ol>
                    ),
                    li: ({ children }) => (
                        <li className="text-gray-700 leading-relaxed">
                          {children}
                        </li>
                    ),
                    strong: ({ children }) => (
                        <strong className="font-semibold text-gray-900">
                          {children}
                        </strong>
                    ),
                    table: ({ children }) => (
                        <div className="overflow-x-auto mb-6">
                          <table className="min-w-full divide-y divide-gray-200 border border-gray-300 rounded-lg overflow-hidden">
                            {children}
                          </table>
                        </div>
                    ),
                    thead: ({ children }) => (
                        <thead className="bg-gray-50">
                        {children}
                        </thead>
                    ),
                    tbody: ({ children }) => (
                        <tbody className="bg-white divide-y divide-gray-200">
                        {children}
                        </tbody>
                    ),
                    tr: ({ children }) => (
                        <tr className="hover:bg-gray-50">
                          {children}
                        </tr>
                    ),
                    th: ({ children }) => (
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                          {children}
                        </th>
                    ),
                    td: ({ children }) => (
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                          {children}
                        </td>
                    ),
                    blockquote: ({ children }) => (
                        <blockquote className="border-l-4 border-blue-500 pl-4 py-2 mb-6 bg-blue-50 rounded-r-lg">
                          <div className="text-gray-700 italic">
                            {children}
                          </div>
                        </blockquote>
                    ),
                    code: ({ children }) => (
                        <code className="bg-gray-100 px-2 py-1 rounded text-sm font-mono text-gray-800">
                          {children}
                        </code>
                    ),
                    pre: ({ children }) => (
                        <pre className="bg-gray-100 p-4 rounded-lg overflow-x-auto mb-6 text-sm">
                    {children}
                  </pre>
                    )
                  }}
              >
                {schedule}
              </ReactMarkdown>
            </div>
          </div>
        </div>

        {/* Document Footer */}
        <div className="bg-gray-50 px-6 py-3 border-t border-gray-200">
          <div className="flex items-center justify-between text-sm text-gray-600">
            <div className="flex items-center gap-2">
              <FileText className="w-4 h-4" />
              <span>Work Schedule Document</span>
            </div>
            <div className="flex items-center gap-4">
              <span>Generated: {new Date().toLocaleString()}</span>
              {source && <span>Source: {source}</span>}
            </div>
          </div>
        </div>
      </motion.div>
  );
}