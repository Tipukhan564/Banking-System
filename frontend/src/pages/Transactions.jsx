import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { Search, Download, Filter, ArrowUpRight, ArrowDownLeft } from 'lucide-react';

const Transactions = () => {
  const [transactions] = useState([
    { id: 1, ref: 'TXN001', type: 'TRANSFER', amount: 5000, status: 'COMPLETED', date: '2025-10-25 14:30' },
    { id: 2, ref: 'TXN002', type: 'DEPOSIT', amount: 10000, status: 'COMPLETED', date: '2025-10-24 10:15' },
    { id: 3, ref: 'TXN003', type: 'WITHDRAWAL', amount: 2000, status: 'COMPLETED', date: '2025-10-23 16:45' },
    { id: 4, ref: 'TXN004', type: 'IBFT', amount: 15000, status: 'PENDING', date: '2025-10-22 09:20' },
  ]);

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold text-white mb-2">Transactions</h1>
          <p className="text-dark-400">View all your transactions</p>
        </div>
        <button className="flex items-center space-x-2 px-4 py-3 bg-primary-500 rounded-lg hover:bg-primary-600 transition-colors text-white">
          <Download size={20} />
          <span>Export</span>
        </button>
      </div>

      {/* Search and Filter */}
      <div className="flex space-x-4">
        <div className="flex-1 relative">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-dark-400" size={20} />
          <input
            type="text"
            placeholder="Search transactions..."
            className="w-full pl-12 pr-4 py-3 bg-dark-800 border border-dark-700 rounded-lg text-white focus:outline-none focus:border-primary-500"
          />
        </div>
        <button className="flex items-center space-x-2 px-6 py-3 bg-dark-800 rounded-lg hover:bg-dark-700 text-white">
          <Filter size={20} />
          <span>Filter</span>
        </button>
      </div>

      {/* Transactions Table */}
      <motion.div
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        className="bg-dark-800/50 backdrop-blur-lg border border-dark-700 rounded-2xl overflow-hidden"
      >
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-dark-700">
              <tr>
                <th className="px-6 py-4 text-left text-sm font-medium text-dark-300">Reference</th>
                <th className="px-6 py-4 text-left text-sm font-medium text-dark-300">Type</th>
                <th className="px-6 py-4 text-left text-sm font-medium text-dark-300">Amount</th>
                <th className="px-6 py-4 text-left text-sm font-medium text-dark-300">Status</th>
                <th className="px-6 py-4 text-left text-sm font-medium text-dark-300">Date</th>
              </tr>
            </thead>
            <tbody>
              {transactions.map((txn) => (
                <motion.tr
                  key={txn.id}
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                  className="border-t border-dark-700 hover:bg-dark-700/50 transition-colors"
                >
                  <td className="px-6 py-4 text-white font-mono text-sm">{txn.ref}</td>
                  <td className="px-6 py-4">
                    <span className="px-3 py-1 bg-primary-500/20 text-primary-400 rounded-full text-xs">
                      {txn.type}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-white font-bold">PKR {txn.amount.toLocaleString()}</td>
                  <td className="px-6 py-4">
                    <span className={`px-3 py-1 rounded-full text-xs ${
                      txn.status === 'COMPLETED' ? 'bg-green-500/20 text-green-400' : 'bg-yellow-500/20 text-yellow-400'
                    }`}>
                      {txn.status}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-dark-400 text-sm">{txn.date}</td>
                </motion.tr>
              ))}
            </tbody>
          </table>
        </div>
      </motion.div>
    </div>
  );
};

export default Transactions;
