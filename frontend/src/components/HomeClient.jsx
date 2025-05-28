'use client';

import {motion} from 'framer-motion';
import {Button} from '../../components/ui/button';
import {UploadCloud} from 'lucide-react';
import TorProcessTable from './TorProcess';

export default function TorClientSection({ torQueue }) {
    return (
        <div className="max-w-6xl mx-auto space-y-8">
            <div className="text-center space-y-4">
                <motion.div
                    initial={{ opacity: 0, y: -20 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ duration: 0.5 }}
                    className="space-y-2"
                >
                    <h1 className="text-4xl font-bold text-white">RFP Document Analyzer</h1>
                    <div className="w-24 h-1 bg-blue-500 mx-auto rounded-full" />
                </motion.div>
                <p className="text-lg text-gray-300 max-w-2xl mx-auto">
                    Upload and analyze Terms of Reference (ToR) documents intelligently with automated requirement extraction and validation.
                </p>
            </div>

            <motion.div
                initial={{ opacity: 0, scale: 0.97 }}
                animate={{ opacity: 1, scale: 1 }}
                transition={{ duration: 0.4 }}
                className="bg-gray-800 shadow-2xl rounded-xl border border-gray-700"
            >
                <div className="px-6 py-4 border-b border-gray-700 bg-gray-800 rounded-t-xl flex items-center justify-between">
                    <div className="flex items-center space-x-3">
                        <div className="flex space-x-2">
                            <div className="w-3 h-3 bg-red-500 rounded-full" />
                            <div className="w-3 h-3 bg-yellow-500 rounded-full" />
                            <div className="w-3 h-3 bg-green-500 rounded-full" />
                        </div>
                        <h2 className="text-xl font-semibold text-white">Document Processing Queue</h2>
                    </div>

                    <Button variant="outline" className="text-gray-800 border-gray-600 hover:bg-gray-400 cursor-pointer">
                        <UploadCloud className="w-4 h-4 mr-2" />
                        Upload TOR
                    </Button>
                </div>

                <div className="p-6">
                    <TorProcessTable torQueue={torQueue} />
                </div>
            </motion.div>
        </div>
    );
}
