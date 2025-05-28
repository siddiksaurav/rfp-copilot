'use client';

import {useEffect, useState} from 'react';
import {AnimatePresence, motion} from 'framer-motion';
import ReactMarkdown from 'react-markdown';
import {
    ChevronLeft,
    ChevronRight,
    Download,
    Eye,
    FileText,
    Loader2,
    RefreshCw,
    Users,
    ZoomIn,
    ZoomOut
} from 'lucide-react';
import {Button} from '../../components/ui/button';
import {Card, CardContent} from '../../components/ui/card';
import {Badge} from '../../components/ui/badge';

export default function ResumeMakerClient({ torName }) {
  const [cvs, setCvs] = useState([]);
  const [loading, setLoading] = useState(false);
  const [currentPage, setCurrentPage] = useState(0);
  const [zoomLevel, setZoomLevel] = useState(100);
  const [viewMode, setViewMode] = useState('single'); // 'single' or 'all'

  const fetchCvs = async (source = 'Dummy') => {
    setLoading(true);
    try {
      const response = await fetch(`${process.env.NEXT_PUBLIC_BACKEND_URL}/generate-cv?source=${encodeURIComponent(source)}`);
      if (!response.ok) throw new Error('Failed to fetch CVs');

      const data = await response.json();
        console.log(data)
      // Split the combined CVs by the separator
      const individualCvs = data[0].split('\n\n---------------------------------------------\n\n').filter(cv => cv.trim());
      setCvs(individualCvs);
      setCurrentPage(0);
    } catch (error) {
      console.error('Error fetching CVs:', error);
      setCvs([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCvs(torName);
  }, [torName]);

  const handleZoomIn = () => {
    setZoomLevel(prev => Math.min(prev + 10, 200));
  };

  const handleZoomOut = () => {
    setZoomLevel(prev => Math.max(prev - 10, 50));
  };

  const nextPage = () => {
    setCurrentPage(prev => Math.min(prev + 1, cvs.length - 1));
  };

  const prevPage = () => {
    setCurrentPage(prev => Math.max(prev - 1, 0));
  };

  const downloadPdf = async () => {
    try {
      const response = await fetch('/api/download-cv-pdf');
      if (!response.ok) throw new Error('Failed to download PDF');

      const blob = await response.blob();
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = 'selective_employees_cv.pdf';
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
    } catch (error) {
      console.error('Error downloading PDF:', error);
    }
  };

  return (
      <div className="min-h-screen bg-gradient-to-br from-blue-50 via-indigo-50 to-purple-50 p-4">
        <div className="max-w-7xl mx-auto">
          {/* Header */}
          <motion.div
              initial={{ opacity: 0, y: -20 }}
              animate={{ opacity: 1, y: 0 }}
              className="mb-8"
          >
            <div className="flex items-center justify-between bg-white rounded-xl shadow-sm p-6 border">
              <div className="flex items-center gap-4">
                <div className="p-3 bg-blue-100 rounded-lg">
                  <FileText className="w-6 h-6 text-blue-600" />
                </div>
                <div>
                  <h1 className="text-2xl font-bold text-gray-900">CV Generator</h1>
                  <p className="text-gray-600">
                    {torName ? `Source: ${torName}` : 'Default Source'}
                  </p>
                </div>
              </div>

              <div className="flex items-center gap-2">
                <Badge variant="secondary" className="px-3 py-1">
                  <Users className="w-4 h-4 mr-1" />
                  {cvs.length} CVs Generated
                </Badge>
                <Button
                    onClick={() => fetchCvs(torName)}
                    disabled={loading}
                    variant="outline"
                    size="sm"
                >
                  <RefreshCw className={`w-4 h-4 mr-2 ${loading ? 'animate-spin' : ''}`} />
                  Refresh
                </Button>
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
                  <p className="text-gray-600">Generating CVs...</p>
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
                  <div className="flex items-center justify-between bg-white rounded-lg shadow-sm p-4 border">
                    <div className="flex items-center gap-2">
                      <Button
                          onClick={() => setViewMode(viewMode === 'single' ? 'all' : 'single')}
                          variant="outline"
                          size="sm"
                      >
                        <Eye className="w-4 h-4 mr-2" />
                        {viewMode === 'single' ? 'View All' : 'Single View'}
                      </Button>

                      {viewMode === 'single' && cvs.length > 1 && (
                          <div className="flex items-center gap-2 ml-4">
                            <Button
                                onClick={prevPage}
                                disabled={currentPage === 0}
                                variant="outline"
                                size="sm"
                            >
                              <ChevronLeft className="w-4 h-4" />
                            </Button>
                            <span className="text-sm text-gray-600 px-3">
                        {currentPage + 1} of {cvs.length}
                      </span>
                            <Button
                                onClick={nextPage}
                                disabled={currentPage === cvs.length - 1}
                                variant="outline"
                                size="sm"
                            >
                              <ChevronRight className="w-4 h-4" />
                            </Button>
                          </div>
                      )}
                    </div>

                    <div className="flex items-center gap-2">
                      <Button onClick={handleZoomOut} variant="outline" size="sm">
                        <ZoomOut className="w-4 h-4" />
                      </Button>
                      <span className="text-sm text-gray-600 px-2 min-w-[60px] text-center">
                    {zoomLevel}%
                  </span>
                      <Button onClick={handleZoomIn} variant="outline" size="sm">
                        <ZoomIn className="w-4 h-4" />
                      </Button>
                      <Button onClick={downloadPdf} className="ml-4">
                        <Download className="w-4 h-4 mr-2" />
                        Download PDF
                      </Button>
                    </div>
                  </div>
                </motion.div>

                {/* CV Display */}
                <AnimatePresence mode="wait">
                  {cvs.length > 0 ? (
                      <motion.div
                          key={viewMode}
                          initial={{ opacity: 0, scale: 0.95 }}
                          animate={{ opacity: 1, scale: 1 }}
                          exit={{ opacity: 0, scale: 0.95 }}
                          transition={{ duration: 0.3 }}
                          className="space-y-6"
                      >
                        {viewMode === 'single' ? (
                            <CVDocument
                                cv={cvs[currentPage]}
                                zoomLevel={zoomLevel}
                                pageNumber={currentPage + 1}
                            />
                        ) : (
                            cvs.map((cv, index) => (
                                <CVDocument
                                    key={index}
                                    cv={cv}
                                    zoomLevel={zoomLevel}
                                    pageNumber={index + 1}
                                />
                            ))
                        )}
                      </motion.div>
                  ) : (
                      <motion.div
                          initial={{ opacity: 0 }}
                          animate={{ opacity: 1 }}
                          className="text-center py-20"
                      >
                        <FileText className="w-16 h-16 text-gray-400 mx-auto mb-4" />
                        <h3 className="text-lg font-medium text-gray-900 mb-2">No CVs Generated</h3>
                        <p className="text-gray-600">Click refresh to generate CVs for the selected source.</p>
                      </motion.div>
                  )}
                </AnimatePresence>
              </>
          )}
        </div>
      </div>
  );
}

function CVDocument({ cv, zoomLevel, pageNumber }) {
  return (
      <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.4 }}
          className="flex justify-center"
      >
        <Card className="bg-white shadow-lg border max-w-4xl w-full overflow-hidden">
          <div className="bg-gray-100 px-4 py-2 border-b flex items-center justify-between">
            <div className="flex items-center gap-2">
              <div className="w-3 h-3 bg-red-400 rounded-full"></div>
              <div className="w-3 h-3 bg-yellow-400 rounded-full"></div>
              <div className="w-3 h-3 bg-green-400 rounded-full"></div>
            </div>
            <span className="text-sm text-gray-600 font-mono">CV #{pageNumber}</span>
          </div>

          <CardContent className="p-0">
            <div
                className="p-8 overflow-auto transition-all duration-300"
                style={{
                  transform: `scale(${zoomLevel / 100})`,
                  transformOrigin: 'top left',
                  width: `${10000 / zoomLevel}%`
                }}
            >
              <div className="prose prose-gray max-w-none">
                <ReactMarkdown
                    components={{
                      h1: ({ children }) => (
                          <h1 className="text-2xl font-bold text-gray-900 mb-4 pb-2 border-b border-gray-200">
                            {children}
                          </h1>
                      ),
                      h2: ({ children }) => (
                          <h2 className="text-xl font-semibold text-gray-800 mt-6 mb-3">
                            {children}
                          </h2>
                      ),
                      h3: ({ children }) => (
                          <h3 className="text-lg font-medium text-gray-700 mt-4 mb-2">
                            {children}
                          </h3>
                      ),
                      p: ({ children }) => (
                          <p className="text-gray-700 mb-3 leading-relaxed">
                            {children}
                          </p>
                      ),
                      ul: ({ children }) => (
                          <ul className="list-disc list-inside text-gray-700 mb-4 space-y-1">
                            {children}
                          </ul>
                      ),
                      strong: ({ children }) => (
                          <strong className="font-semibold text-gray-900">
                            {children}
                          </strong>
                      )
                    }}
                >
                  {cv}
                </ReactMarkdown>
              </div>
            </div>
          </CardContent>
        </Card>
      </motion.div>
  );
}