import { useState, useCallback } from 'react';
import Navbar from '@/components/Navbar';
import ShortenForm from '@/components/ShortenForm';
import DashboardStats from '@/components/DashboardStats';
import UrlList from '@/components/UrlList';
import type { UrlResponse } from '@/types';

export default function Home() {
  const [refreshKey, setRefreshKey] = useState(0);

  const handleUrlCreated = useCallback((_url: UrlResponse) => {
    setRefreshKey((prev) => prev + 1);
  }, []);

  return (
    <div className="min-h-screen bg-slate-50">
      <Navbar />

      {/* Hero Section */}
      <div className="bg-white border-b border-slate-200">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
          <ShortenForm onSuccess={handleUrlCreated} />
        </div>
      </div>

      {/* Stats Section */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <DashboardStats />
      </div>

      {/* URL List Section */}
      <div id="urls" className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <UrlList refresh={refreshKey} />
      </div>

      {/* Footer */}
      <footer className="bg-white border-t border-slate-200 mt-16">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <div className="flex flex-col md:flex-row items-center justify-between gap-4">
            <p className="text-sm text-slate-500">
              URL Shortener - Built with Spring Boot and React
            </p>
            <div className="flex items-center gap-6 text-sm text-slate-500">
              <span>Features:</span>
              <span className="flex items-center gap-1">
                <span className="w-2 h-2 bg-green-400 rounded-full" />
                URL Shortening
              </span>
              <span className="flex items-center gap-1">
                <span className="w-2 h-2 bg-blue-400 rounded-full" />
                Analytics
              </span>
              <span className="flex items-center gap-1">
                <span className="w-2 h-2 bg-violet-400 rounded-full" />
                QR Codes
              </span>
            </div>
          </div>
        </div>
      </footer>
    </div>
  );
}
