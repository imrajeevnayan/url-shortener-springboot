import { useEffect, useState } from 'react';
import { Search, Loader2, AlertCircle } from 'lucide-react';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { api } from '@/services/api';
import UrlCard from './UrlCard';
import AnalyticsPanel from './AnalyticsPanel';
import type { UrlResponse } from '@/types';

interface UrlListProps {
  refresh: number;
}

export default function UrlList({ refresh }: UrlListProps) {
  const [urls, setUrls] = useState<UrlResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedAnalyticsId, setSelectedAnalyticsId] = useState<number | null>(null);

  useEffect(() => {
    loadUrls();
  }, [refresh]);

  const loadUrls = async () => {
    setLoading(true);
    setError('');
    try {
      const response = await api.getRecentUrls();
      if (response.success) {
        setUrls(response.data);
      } else {
        setError(response.message);
      }
    } catch (err: any) {
      setError(err.message || 'Failed to load URLs');
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async () => {
    if (!searchQuery.trim()) {
      loadUrls();
      return;
    }
    setLoading(true);
    try {
      const response = await api.searchUrls(searchQuery, 0, 20);
      if (response.success && response.data) {
        setUrls(response.data.content);
      }
    } catch (err: any) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm('Are you sure you want to delete this URL?')) return;
    try {
      const response = await api.deleteUrl(id);
      if (response.success) {
        setUrls(urls.filter((u) => u.id !== id));
        if (selectedAnalyticsId === id) {
          setSelectedAnalyticsId(null);
        }
      }
    } catch (err: any) {
      setError(err.message);
    }
  };

  const filteredUrls = urls.filter((url) =>
    searchQuery
      ? url.originalUrl.toLowerCase().includes(searchQuery.toLowerCase()) ||
        url.shortCode.toLowerCase().includes(searchQuery.toLowerCase()) ||
        (url.title && url.title.toLowerCase().includes(searchQuery.toLowerCase()))
      : true
  );

  if (loading && urls.length === 0) {
    return (
      <div className="flex flex-col items-center justify-center py-16">
        <Loader2 className="w-8 h-8 animate-spin text-blue-600 mb-4" />
        <p className="text-slate-500">Loading URLs...</p>
      </div>
    );
  }

  return (
    <div className="space-y-4">
      {/* Header & Search */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <h2 className="text-xl font-bold text-slate-900">Your Shortened URLs</h2>
        <div className="flex gap-2">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" />
            <Input
              placeholder="Search URLs..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
              className="pl-10 w-64 border-slate-300"
            />
          </div>
          <Button variant="outline" onClick={handleSearch} className="border-slate-300">
            Search
          </Button>
          <Button variant="outline" onClick={loadUrls} className="border-slate-300">
            Refresh
          </Button>
        </div>
      </div>

      {/* Error */}
      {error && (
        <div className="flex items-center gap-2 p-4 bg-red-50 border border-red-200 rounded-lg text-red-600">
          <AlertCircle className="w-5 h-5" />
          {error}
        </div>
      )}

      {/* Analytics Panel */}
      {selectedAnalyticsId && (
        <AnalyticsPanel
          urlId={selectedAnalyticsId}
          onClose={() => setSelectedAnalyticsId(null)}
        />
      )}

      {/* URL List */}
      <div className="space-y-3">
        {filteredUrls.length === 0 ? (
          <div className="text-center py-16 bg-slate-50 rounded-xl border border-dashed border-slate-300">
            <p className="text-slate-500 text-lg">No URLs found</p>
            <p className="text-slate-400 text-sm mt-1">
              {searchQuery ? 'Try a different search term' : 'Create your first shortened URL above'}
            </p>
          </div>
        ) : (
          filteredUrls.map((url) => (
            <UrlCard
              key={url.id}
              url={url}
              onViewAnalytics={setSelectedAnalyticsId}
              onDelete={handleDelete}
            />
          ))
        )}
      </div>
    </div>
  );
}
