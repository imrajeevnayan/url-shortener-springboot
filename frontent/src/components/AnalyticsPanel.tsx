import { useEffect, useState } from 'react';
import { X, TrendingUp, Users, Globe, Monitor, Smartphone, Tablet, MousePointerClick } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { api } from '@/services/api';
import type { AnalyticsDto, StatItem } from '@/types';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell,
  Legend,
} from 'recharts';

interface AnalyticsPanelProps {
  urlId: number;
  onClose: () => void;
}

const COLORS = ['#2563eb', '#7c3aed', '#059669', '#d97706', '#dc2626', '#0891b2', '#be185d', '#4338ca'];

export default function AnalyticsPanel({ urlId, onClose }: AnalyticsPanelProps) {
  const [analytics, setAnalytics] = useState<AnalyticsDto | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    loadAnalytics();
  }, [urlId]);

  const loadAnalytics = async () => {
    setLoading(true);
    setError('');
    try {
      const response = await api.getAnalytics(urlId);
      if (response.success) {
        setAnalytics(response.data);
      } else {
        setError(response.message);
      }
    } catch (err: any) {
      setError(err.message || 'Failed to load analytics');
    } finally {
      setLoading(false);
    }
  };

  const StatRow = ({ label, count, percentage, icon: Icon }: { label: string; count: number; percentage: number; icon?: React.ElementType }) => (
    <div className="flex items-center justify-between py-2 border-b border-slate-100 last:border-0">
      <div className="flex items-center gap-2">
        {Icon && <Icon className="w-4 h-4 text-slate-400" />}
        <span className="text-sm text-slate-700">{label}</span>
      </div>
      <div className="flex items-center gap-3">
        <div className="w-24 h-2 bg-slate-100 rounded-full overflow-hidden">
          <div
            className="h-full bg-blue-500 rounded-full transition-all"
            style={{ width: `${Math.max(percentage, 2)}%` }}
          />
        </div>
        <span className="text-sm font-medium text-slate-900 w-10 text-right">{count}</span>
      </div>
    </div>
  );

  if (loading) {
    return (
      <div className="bg-white rounded-xl border border-slate-200 p-6 animate-pulse">
        <div className="h-8 bg-slate-200 rounded w-48 mb-4" />
        <div className="space-y-3">
          {[1, 2, 3, 4, 5].map((i) => (
            <div key={i} className="h-4 bg-slate-200 rounded" />
          ))}
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-red-50 border border-red-200 rounded-xl p-6">
        <p className="text-red-600">{error}</p>
        <Button variant="outline" onClick={loadAnalytics} className="mt-3">
          Retry
        </Button>
      </div>
    );
  }

  if (!analytics) return null;

  return (
    <div className="bg-white rounded-xl border border-slate-200 shadow-lg overflow-hidden">
      {/* Header */}
      <div className="flex items-center justify-between p-6 border-b border-slate-100">
        <div>
          <h3 className="text-lg font-semibold text-slate-900 flex items-center gap-2">
            <TrendingUp className="w-5 h-5 text-blue-600" />
            Analytics: /{analytics.shortCode}
          </h3>
          <p className="text-sm text-slate-500 mt-1">{analytics.originalUrl}</p>
        </div>
        <Button variant="ghost" size="sm" onClick={onClose} className="text-slate-400 hover:text-slate-600">
          <X className="w-5 h-5" />
        </Button>
      </div>

      {/* Overview Stats */}
      <div className="grid grid-cols-2 gap-4 p-6 border-b border-slate-100">
        <div className="bg-blue-50 rounded-lg p-4">
          <div className="flex items-center gap-2 mb-1">
            <MousePointerClick className="w-4 h-4 text-blue-600" />
            <span className="text-sm text-blue-600 font-medium">Total Clicks</span>
          </div>
          <p className="text-3xl font-bold text-blue-900">{analytics.totalClicks.toLocaleString()}</p>
        </div>
        <div className="bg-violet-50 rounded-lg p-4">
          <div className="flex items-center gap-2 mb-1">
            <Users className="w-4 h-4 text-violet-600" />
            <span className="text-sm text-violet-600 font-medium">Unique Visitors</span>
          </div>
          <p className="text-3xl font-bold text-violet-900">{analytics.uniqueVisitors.toLocaleString()}</p>
        </div>
      </div>

      {/* Clicks Over Time Chart */}
      {analytics.clicksOverTime.length > 0 && (
        <div className="p-6 border-b border-slate-100">
          <h4 className="text-sm font-semibold text-slate-700 mb-4">Clicks Over Time</h4>
          <div className="h-48">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={analytics.clicksOverTime}>
                <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" />
                <XAxis
                  dataKey="date"
                  tickFormatter={(value) => new Date(value).toLocaleDateString('en-US', { month: 'short', day: 'numeric' })}
                  stroke="#94a3b8"
                  fontSize={12}
                />
                <YAxis stroke="#94a3b8" fontSize={12} />
                <Tooltip
                  contentStyle={{
                    backgroundColor: '#fff',
                    border: '1px solid #e2e8f0',
                    borderRadius: '8px',
                    fontSize: '12px',
                  }}
                  formatter={(value: number) => [value, 'Clicks']}
                />
                <Bar dataKey="count" fill="#2563eb" radius={[4, 4, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>
      )}

      {/* Detailed Stats */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 p-6">
        {/* Browsers */}
        {analytics.browsers.length > 0 && (
          <div>
            <h4 className="text-sm font-semibold text-slate-700 mb-3 flex items-center gap-2">
              <Globe className="w-4 h-4" />
              Browsers
            </h4>
            <div className="bg-slate-50 rounded-lg p-4">
              {analytics.browsers.slice(0, 5).map((browser: StatItem) => (
                <StatRow key={browser.name} label={browser.name} count={browser.count} percentage={browser.percentage} />
              ))}
              <div className="mt-3 h-32">
                <ResponsiveContainer width="100%" height="100%">
                  <PieChart>
                    <Pie
                      data={analytics.browsers.slice(0, 5)}
                      cx="50%"
                      cy="50%"
                      innerRadius={30}
                      outerRadius={55}
                      dataKey="count"
                      nameKey="name"
                    >
                      {analytics.browsers.slice(0, 5).map((_, index) => (
                        <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                      ))}
                    </Pie>
                    <Tooltip />
                    <Legend fontSize={11} />
                  </PieChart>
                </ResponsiveContainer>
              </div>
            </div>
          </div>
        )}

        {/* Devices */}
        {analytics.devices.length > 0 && (
          <div>
            <h4 className="text-sm font-semibold text-slate-700 mb-3 flex items-center gap-2">
              <Monitor className="w-4 h-4" />
              Devices
            </h4>
            <div className="bg-slate-50 rounded-lg p-4">
              {analytics.devices.slice(0, 5).map((device: StatItem) => (
                <StatRow
                  key={device.name}
                  label={device.name}
                  count={device.count}
                  percentage={device.percentage}
                  icon={device.name === 'Mobile' ? Smartphone : device.name === 'Tablet' ? Tablet : Monitor}
                />
              ))}
              <div className="mt-3 h-32">
                <ResponsiveContainer width="100%" height="100%">
                  <PieChart>
                    <Pie
                      data={analytics.devices.slice(0, 5)}
                      cx="50%"
                      cy="50%"
                      innerRadius={30}
                      outerRadius={55}
                      dataKey="count"
                      nameKey="name"
                    >
                      {analytics.devices.slice(0, 5).map((_, index) => (
                        <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                      ))}
                    </Pie>
                    <Tooltip />
                    <Legend fontSize={11} />
                  </PieChart>
                </ResponsiveContainer>
              </div>
            </div>
          </div>
        )}

        {/* Operating Systems */}
        {analytics.operatingSystems.length > 0 && (
          <div>
            <h4 className="text-sm font-semibold text-slate-700 mb-3">Operating Systems</h4>
            <div className="bg-slate-50 rounded-lg p-4">
              {analytics.operatingSystems.slice(0, 5).map((os: StatItem) => (
                <StatRow key={os.name} label={os.name} count={os.count} percentage={os.percentage} />
              ))}
            </div>
          </div>
        )}

        {/* Referrers */}
        {analytics.referrers.length > 0 && (
          <div>
            <h4 className="text-sm font-semibold text-slate-700 mb-3">Top Referrers</h4>
            <div className="bg-slate-50 rounded-lg p-4">
              {analytics.referrers.slice(0, 5).map((ref: StatItem) => (
                <StatRow key={ref.name} label={ref.name} count={ref.count} percentage={ref.percentage} />
              ))}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
