import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { CreditCard, Plus, Eye, EyeOff, Loader } from 'lucide-react';
import toast from 'react-hot-toast';
import { accountAPI } from '../services/api';

const Accounts = () => {
  const [accounts, setAccounts] = useState([
    { id: 1, accountNumber: '1234567890123456', type: 'SAVINGS', balance: 125430.50, currency: 'PKR', status: 'ACTIVE' },
    { id: 2, accountNumber: '9876543210987654', type: 'CURRENT', balance: 50000.00, currency: 'PKR', status: 'ACTIVE' },
  ]);
  const [showBalance, setShowBalance] = useState(true);
  const [showModal, setShowModal] = useState(false);

  const AccountCard = ({ account }) => (
    <motion.div
      initial={{ opacity: 0, scale: 0.9 }}
      animate={{ opacity: 1, scale: 1 }}
      className="bg-gradient-to-br from-primary-500 to-primary-700 rounded-2xl p-6 text-white shadow-2xl card-hover"
    >
      <div className="flex justify-between items-start mb-8">
        <div>
          <p className="text-primary-100 text-sm mb-1">{account.type} Account</p>
          <p className="text-2xl font-bold">
            {showBalance ? `PKR ${account.balance.toLocaleString()}` : '•••••••'}
          </p>
        </div>
        <CreditCard size={32} className="text-primary-100" />
      </div>
      <div className="flex justify-between items-end">
        <div>
          <p className="text-primary-100 text-xs mb-1">Account Number</p>
          <p className="font-mono text-sm">{account.accountNumber}</p>
        </div>
        <div className={`px-3 py-1 rounded-full text-xs ${
          account.status === 'ACTIVE' ? 'bg-green-500' : 'bg-red-500'
        }`}>
          {account.status}
        </div>
      </div>
    </motion.div>
  );

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold text-white mb-2">My Accounts</h1>
          <p className="text-dark-400">Manage your bank accounts</p>
        </div>
        <div className="flex space-x-4">
          <button
            onClick={() => setShowBalance(!showBalance)}
            className="p-3 bg-dark-800 rounded-lg hover:bg-dark-700 transition-colors"
          >
            {showBalance ? <EyeOff className="text-white" /> : <Eye className="text-white" />}
          </button>
          <button
            onClick={() => setShowModal(true)}
            className="flex items-center space-x-2 px-4 py-3 bg-primary-500 rounded-lg hover:bg-primary-600 transition-colors text-white font-medium"
          >
            <Plus size={20} />
            <span>New Account</span>
          </button>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {accounts.map((account) => (
          <AccountCard key={account.id} account={account} />
        ))}
      </div>
    </div>
  );
};

export default Accounts;
