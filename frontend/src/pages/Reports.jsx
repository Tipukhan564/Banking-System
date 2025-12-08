import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { Download, FileText, FileSpreadsheet, Calendar } from 'lucide-react';
import toast from 'react-hot-toast';
import { reportAPI } from '../services/api';

const Reports = () => {
  const [reportConfig, setReportConfig] = useState({
    accountId: '',
    startDate: '',
    endDate: '',
    format: 'pdf',
  });

  const handleDownload = async () => {
    try {
      const response = reportConfig.format === 'pdf'
        ? await reportAPI.generatePDF(reportConfig.accountId, reportConfig.startDate, reportConfig.endDate)
        : await reportAPI.generateExcel(reportConfig.accountId, reportConfig.startDate, reportConfig.endDate);

      const blob = new Blob([response.data], {
        type: reportConfig.format === 'pdf' ? 'application/pdf' : 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
      });
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `report.${reportConfig.format}`;
      a.click();

      toast.success('Report downloaded successfully!');
    } catch (error) {
      toast.error('Failed to generate report');
    }
  };

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      <div>
        <h1 className="text-3xl font-bold text-white mb-2">Reports</h1>
        <p className="text-dark-400">Generate and download transaction reports</p>
      </div>

      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="bg-dark-800/50 backdrop-blur-lg border border-dark-700 rounded-2xl p-8"
      >
        <div className="space-y-6">
          <div>
            <label className="block text-sm font-medium text-dark-300 mb-2">Account ID</label>
            <input
              type="number"
              value={reportConfig.accountId}
              onChange={(e) => setReportConfig({ ...reportConfig, accountId: e.target.value })}
              className="w-full px-4 py-3 bg-dark-700 border border-dark-600 rounded-lg text-white focus:outline-none focus:border-primary-500"
              placeholder="Enter account ID"
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-dark-300 mb-2">Start Date</label>
              <input
                type="datetime-local"
                value={reportConfig.startDate}
                onChange={(e) => setReportConfig({ ...reportConfig, startDate: e.target.value })}
                className="w-full px-4 py-3 bg-dark-700 border border-dark-600 rounded-lg text-white focus:outline-none focus:border-primary-500"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-dark-300 mb-2">End Date</label>
              <input
                type="datetime-local"
                value={reportConfig.endDate}
                onChange={(e) => setReportConfig({ ...reportConfig, endDate: e.target.value })}
                className="w-full px-4 py-3 bg-dark-700 border border-dark-600 rounded-lg text-white focus:outline-none focus:border-primary-500"
              />
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-dark-300 mb-2">Format</label>
            <div className="grid grid-cols-2 gap-4">
              <button
                onClick={() => setReportConfig({ ...reportConfig, format: 'pdf' })}
                className={`flex items-center justify-center space-x-2 px-6 py-4 rounded-lg transition-all ${
                  reportConfig.format === 'pdf'
                    ? 'bg-primary-500 text-white'
                    : 'bg-dark-700 text-dark-400 hover:bg-dark-600'
                }`}
              >
                <FileText size={20} />
                <span>PDF</span>
              </button>
              <button
                onClick={() => setReportConfig({ ...reportConfig, format: 'excel' })}
                className={`flex items-center justify-center space-x-2 px-6 py-4 rounded-lg transition-all ${
                  reportConfig.format === 'excel'
                    ? 'bg-primary-500 text-white'
                    : 'bg-dark-700 text-dark-400 hover:bg-dark-600'
                }`}
              >
                <FileSpreadsheet size={20} />
                <span>Excel</span>
              </button>
            </div>
          </div>

          <button
            onClick={handleDownload}
            className="w-full bg-gradient-to-r from-primary-500 to-primary-700 text-white py-4 rounded-lg font-medium flex items-center justify-center space-x-2 hover:from-primary-600 hover:to-primary-800 transition-all"
          >
            <Download size={20} />
            <span>Generate Report</span>
          </button>
        </div>
      </motion.div>
    </div>
  );
};

export default Reports;
