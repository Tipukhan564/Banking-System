import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { Wallet, TrendingUp, ArrowUpRight, ArrowDownLeft, DollarSign } from 'lucide-react';
import { AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';

const Dashboard = () => {
  const [stats, setStats] = useState({
    totalBalance: 125430.50,
    monthlyIncome: 45230.00,
    monthlyExpenses: 23150.00,
    savings: 102280.50,
  });

  const chartData = [
    { name: 'Jan', balance: 95000 },
    { name: 'Feb', balance: 98000 },
    { name: 'Mar', balance: 105000 },
    { name: 'Apr', balance: 110000 },
    { name: 'May', balance: 118000 },
    { name: 'Jun', balance: 125430 },
  ];

  const recentTransactions = [
    { id: 1, type: 'credit', amount: 5000, description: 'Salary Credit', date: '2025-10-25' },
    { id: 2, type: 'debit', amount: 1200, description: 'Electricity Bill', date: '2025-10-24' },
    { id: 3, type: 'credit', amount: 3000, description: 'Freelance Payment', date: '2025-10-23' },
    { id: 4, type: 'debit', amount: 500, description: 'Grocery Shopping', date: '2025-10-22' },
  ];

  const StatCard = ({ title, value, icon: Icon, gradient, change }) => (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.5 }}
      className="bg-dark-800/50 backdrop-blur-lg border border-dark-700 rounded-2xl p-6 hover:shadow-2xl hover:shadow-primary-500/10 transition-all card-hover"
    >
      <div className="flex items-center justify-between mb-4">
        <div className={`p-3 rounded-xl bg-gradient-to-br ${gradient}`}>
          <Icon className="text-white" size={24} />
        </div>
        {change && (
          <span className={`text-sm font-medium ${change > 0 ? 'text-green-400' : 'text-red-400'}`}>
            {change > 0 ? '+' : ''}{change}%
          </span>
        )}
      </div>
      <h3 className="text-dark-400 text-sm mb-1">{title}</h3>
      <p className="text-white text-2xl font-bold">PKR {value.toLocaleString()}</p>
    </motion.div>
  );

  return (
    <div className="space-y-6">
      {/* Welcome Message */}
      <motion.div
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        className="mb-8"
      >
        <h1 className="text-3xl font-bold text-white mb-2">Welcome Back!</h1>
        <p className="text-dark-400">Here's your financial overview</p>
      </motion.div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <StatCard
          title="Total Balance"
          value={stats.totalBalance}
          icon={Wallet}
          gradient="from-primary-500 to-primary-700"
          change={8.5}
        />
        <StatCard
          title="Monthly Income"
          value={stats.monthlyIncome}
          icon={TrendingUp}
          gradient="from-green-500 to-green-700"
          change={12.3}
        />
        <StatCard
          title="Monthly Expenses"
          value={stats.monthlyExpenses}
          icon={DollarSign}
          gradient="from-red-500 to-red-700"
          change={-5.2}
        />
        <StatCard
          title="Savings"
          value={stats.savings}
          icon={ArrowUpRight}
          gradient="from-purple-500 to-purple-700"
          change={15.8}
        />
      </div>

      {/* Chart Section */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5, delay: 0.2 }}
        className="bg-dark-800/50 backdrop-blur-lg border border-dark-700 rounded-2xl p-6"
      >
        <h2 className="text-xl font-bold text-white mb-6">Balance Overview</h2>
        <ResponsiveContainer width="100%" height={300}>
          <AreaChart data={chartData}>
            <defs>
              <linearGradient id="colorBalance" x1="0" y1="0" x2="0" y2="1">
                <stop offset="5%" stopColor="#0ea5e9" stopOpacity={0.8} />
                <stop offset="95%" stopColor="#0ea5e9" stopOpacity={0} />
              </linearGradient>
            </defs>
            <CartesianGrid strokeDasharray="3 3" stroke="#334155" />
            <XAxis dataKey="name" stroke="#64748b" />
            <YAxis stroke="#64748b" />
            <Tooltip
              contentStyle={{
                backgroundColor: '#1e293b',
                border: '1px solid #334155',
                borderRadius: '8px',
                color: '#fff',
              }}
            />
            <Area
              type="monotone"
              dataKey="balance"
              stroke="#0ea5e9"
              fillOpacity={1}
              fill="url(#colorBalance)"
            />
          </AreaChart>
        </ResponsiveContainer>
      </motion.div>

      {/* Recent Transactions */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5, delay: 0.3 }}
        className="bg-dark-800/50 backdrop-blur-lg border border-dark-700 rounded-2xl p-6"
      >
        <h2 className="text-xl font-bold text-white mb-6">Recent Transactions</h2>
        <div className="space-y-4">
          {recentTransactions.map((txn, index) => (
            <motion.div
              key={txn.id}
              initial={{ opacity: 0, x: -20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ duration: 0.3, delay: index * 0.1 }}
              className="flex items-center justify-between p-4 bg-dark-700/50 rounded-lg hover:bg-dark-700 transition-colors"
            >
              <div className="flex items-center space-x-4">
                <div
                  className={`p-2 rounded-lg ${
                    txn.type === 'credit' ? 'bg-green-500/20' : 'bg-red-500/20'
                  }`}
                >
                  {txn.type === 'credit' ? (
                    <ArrowDownLeft className="text-green-400" size={20} />
                  ) : (
                    <ArrowUpRight className="text-red-400" size={20} />
                  )}
                </div>
                <div>
                  <p className="text-white font-medium">{txn.description}</p>
                  <p className="text-dark-400 text-sm">{txn.date}</p>
                </div>
              </div>
              <span
                className={`font-bold ${
                  txn.type === 'credit' ? 'text-green-400' : 'text-red-400'
                }`}
              >
                {txn.type === 'credit' ? '+' : '-'}PKR {txn.amount.toLocaleString()}
              </span>
            </motion.div>
          ))}
        </div>
      </motion.div>
    </div>
  );
};

export default Dashboard;
