import { Link2, MousePointerClick, Activity, Globe } from 'lucide-react';
import { useEffect, useState } from 'react';
import { api } from '@/services/api';
import type { DashboardStats as DashboardStatsType } from '@/types';

export default function DashboardStats() {
  const [stats, setStats] = useState<DashboardStatsType | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadStats();
  }, []);

  const loadStats = async () => {
    try {
      const response = await api.getDashboardStats();
      if (response.success) {
        setStats(response.data);
      }
    } catch (err) {
      console.error('Failed to load stats:', err);
    } finally {
      setLoading(false);
    }
  };

  const statCards = [
    {
      label: 'Total URLs',
      value: stats?.totalUrls || 0,
      icon: Link2,
      color: 'bg-blue-50 text-blue-600',
      borderColor: 'border-blue-200',
    },
    {
      label: 'Active URLs',
      value: stats?.activeUrls || 0,
      icon: Activity,
      color: 'bg-green-50 text-green-600',
      borderColor: 'border-green-200',
    },
    {
      label: 'Total Clicks',
      value: stats?.totalClicks?.toLocaleString() || 0,
      icon: MousePointerClick,
      color: 'bg-violet-50 text-violet-600',
      borderColor: 'border-violet-200',
    },
    {
      label: 'URLs Created Today',
      value: stats?.recentUrls?.filter(u => {
        const today = new Date().toDateString();
        return new Date(u.createdAt).toDateString() === today;
      }).length || 0,
      icon: Globe,
      color: 'bg-amber-50 text-amber-600',
      borderColor: 'border-amber-200',
    },
  ];

  if (loading) {
    return (
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        {[1, 2, 3, 4].map((i) => (
          <div key={i} className="bg-white rounded-xl border border-slate-200 p-6 animate-pulse">
            <div className="h-10 w-10 bg-slate-200 rounded-lg mb-4" />
            <div className="h-6 bg-slate-200 rounded w-20 mb-2" />
            <div className="h-4 bg-slate-200 rounded w-16" />
          </div>
        ))}
      </div>
    );
  }

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
      {statCards.map((stat) => (
        <div
          key={stat.label}
          className={`bg-white rounded-xl border ${stat.borderColor} p-6 hover:shadow-md transition-shadow`}
        >
          <div className={`w-10 h-10 rounded-lg ${stat.color} flex items-center justify-center mb-4`}>
            <stat.icon className="w-5 h-5" />
          </div>
          <p className="text-2xl font-bold text-slate-900">{stat.value}</p>
          <p className="text-sm text-slate-500 mt-1">{stat.label}</p>
        </div>
      ))}
    </div>
  );
}
