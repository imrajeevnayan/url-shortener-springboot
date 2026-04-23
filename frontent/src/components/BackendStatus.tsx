import { useEffect, useState } from 'react';
import { Server, CheckCircle, XCircle, Settings } from 'lucide-react';

export default function BackendStatus() {
  const [isOnline, setIsOnline] = useState<boolean | null>(null);
  const [showConfig, setShowConfig] = useState(false);
  const [customUrl, setCustomUrl] = useState('');

  useEffect(() => {
    checkHealth();
    const interval = setInterval(checkHealth, 30000);
    return () => clearInterval(interval);
  }, []);

  const checkHealth = async () => {
    try {
      const response = await fetch(`${import.meta.env.VITE_API_URL || window.location.origin + '/api'}/urls/recent`, {
        method: 'GET',
        signal: AbortSignal.timeout(5000),
      });
      setIsOnline(response.ok);
    } catch {
      setIsOnline(false);
    }
  };

  if (isOnline === true) {
    return (
      <div className="flex items-center gap-2 text-sm text-green-600 bg-green-50 px-3 py-1.5 rounded-full">
        <CheckCircle className="w-4 h-4" />
        <span>Backend Connected</span>
      </div>
    );
  }

  if (isOnline === false) {
    return (
      <div className="flex items-center gap-3">
        <div className="flex items-center gap-2 text-sm text-amber-600 bg-amber-50 px-3 py-1.5 rounded-full">
          <XCircle className="w-4 h-4" />
          <span>Backend Offline</span>
        </div>
        <button
          onClick={() => setShowConfig(!showConfig)}
          className="text-sm text-blue-600 hover:text-blue-700 flex items-center gap-1"
        >
          <Settings className="w-3.5 h-3.5" />
          Setup
        </button>
        {showConfig && (
          <div className="absolute top-full right-0 mt-2 w-80 bg-white border border-slate-200 rounded-lg shadow-lg p-4 z-50">
            <h4 className="font-medium text-slate-900 mb-2">Backend Configuration</h4>
            <p className="text-xs text-slate-500 mb-3">
              The frontend needs a running Spring Boot backend. Start it locally:
            </p>
            <code className="block bg-slate-100 p-2 rounded text-xs text-slate-700 mb-3">
              java -jar url-shortener-1.0.0.jar
            </code>
            <p className="text-xs text-slate-500 mb-2">
              Or enter your backend URL:
            </p>
            <div className="flex gap-2">
              <input
                type="url"
                placeholder="http://localhost:8080/api"
                value={customUrl}
                onChange={(e) => setCustomUrl(e.target.value)}
                className="flex-1 text-xs border border-slate-300 rounded px-2 py-1.5"
              />
              <button
                onClick={() => {
                  if (customUrl) {
                    localStorage.setItem('api_url', customUrl);
                    window.location.reload();
                  }
                }}
                className="text-xs bg-blue-600 text-white px-3 py-1.5 rounded hover:bg-blue-700"
              >
                Save
              </button>
            </div>
          </div>
        )}
      </div>
    );
  }

  return (
    <div className="flex items-center gap-2 text-sm text-slate-400">
      <Server className="w-4 h-4 animate-pulse" />
      <span>Checking backend...</span>
    </div>
  );
}
