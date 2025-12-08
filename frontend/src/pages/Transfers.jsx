import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { Send, ArrowRight, Loader } from 'lucide-react';
import toast from 'react-hot-toast';
import { transactionAPI } from '../services/api';

const Transfers = () => {
  const [transferType, setTransferType] = useState('internal');
  const [formData, setFormData] = useState({
    fromAccountNumber: '',
    toAccountNumber: '',
    amount: '',
    description: '',
  });
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      const response = await transactionAPI.transfer(formData);
      toast.success('Transfer successful!');
      setFormData({ fromAccountNumber: '', toAccountNumber: '', amount: '', description: '' });
    } catch (error) {
      toast.error(error.response?.data?.message || 'Transfer failed');
    }

    setLoading(false);
  };

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      <div>
        <h1 className="text-3xl font-bold text-white mb-2">Transfer Funds</h1>
        <p className="text-dark-400">Send money quickly and securely</p>
      </div>

      {/* Transfer Type Selection */}
      <div className="flex space-x-4 mb-6">
        {['internal'].map((type) => (
          <button
            key={type}
            onClick={() => setTransferType(type)}
            className={`px-6 py-3 rounded-lg font-medium transition-all ${
              transferType === type
                ? 'bg-primary-500 text-white'
                : 'bg-dark-800 text-dark-400 hover:bg-dark-700'
            }`}
          >
            {type.toUpperCase()}
          </button>
        ))}
      </div>

      {/* Transfer Form */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="bg-dark-800/50 backdrop-blur-lg border border-dark-700 rounded-2xl p-8"
      >
        <form onSubmit={handleSubmit} className="space-y-6">
          <div>
            <label className="block text-sm font-medium text-dark-300 mb-2">From Account</label>
            <input
              type="text"
              value={formData.fromAccountNumber}
              onChange={(e) => setFormData({ ...formData, fromAccountNumber: e.target.value })}
              className="w-full px-4 py-3 bg-dark-700 border border-dark-600 rounded-lg text-white focus:outline-none focus:border-primary-500"
              placeholder="Enter account number"
              required
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-dark-300 mb-2">
              {transferType === 'internal' ? 'To Account' : transferType === 'ibft' ? 'To IBAN' : 'Raast ID'}
            </label>
            <input
              type="text"
              value={formData.toAccountNumber}
              onChange={(e) => setFormData({ ...formData, toAccountNumber: e.target.value })}
              className="w-full px-4 py-3 bg-dark-700 border border-dark-600 rounded-lg text-white focus:outline-none focus:border-primary-500"
              placeholder={transferType === 'internal' ? 'Enter account number' : 'Enter IBAN'}
              required
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-dark-300 mb-2">Amount (PKR)</label>
            <input
              type="number"
              value={formData.amount}
              onChange={(e) => setFormData({ ...formData, amount: e.target.value })}
              className="w-full px-4 py-3 bg-dark-700 border border-dark-600 rounded-lg text-white focus:outline-none focus:border-primary-500"
              placeholder="0.00"
              required
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-dark-300 mb-2">Description</label>
            <textarea
              value={formData.description}
              onChange={(e) => setFormData({ ...formData, description: e.target.value })}
              className="w-full px-4 py-3 bg-dark-700 border border-dark-600 rounded-lg text-white focus:outline-none focus:border-primary-500"
              rows="3"
              placeholder="Payment description"
            />
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-gradient-to-r from-primary-500 to-primary-700 text-white py-4 rounded-lg font-medium flex items-center justify-center space-x-2 hover:from-primary-600 hover:to-primary-800 transition-all disabled:opacity-50"
          >
            {loading ? (
              <Loader className="animate-spin" size={20} />
            ) : (
              <>
                <Send size={20} />
                <span>Transfer Now</span>
                <ArrowRight size={20} />
              </>
            )}
          </button>
        </form>
      </motion.div>
    </div>
  );
};

export default Transfers;
