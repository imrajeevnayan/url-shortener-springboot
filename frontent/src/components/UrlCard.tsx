import { useState } from 'react';
import { Copy, Check, ExternalLink, BarChart3, QrCode, Trash2, Calendar, MousePointerClick, Lock } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog';
import type { UrlResponse } from '@/types';

interface UrlCardProps {
  url: UrlResponse;
  onViewAnalytics: (id: number) => void;
  onDelete: (id: number) => void;
}

export default function UrlCard({ url, onViewAnalytics, onDelete }: UrlCardProps) {
  const [copied, setCopied] = useState(false);
  const [showQr, setShowQr] = useState(false);

  const handleCopy = async () => {
    try {
      await navigator.clipboard.writeText(url.shortUrl);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    } catch (err) {
      // Fallback
      const textarea = document.createElement('textarea');
      textarea.value = url.shortUrl;
      document.body.appendChild(textarea);
      textarea.select();
      document.execCommand('copy');
      document.body.removeChild(textarea);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    }
  };

  const formatDate = (dateStr: string | null) => {
    if (!dateStr) return 'N/A';
    return new Date(dateStr).toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
      year: 'numeric',
    });
  };

  const truncateUrl = (url: string, maxLength = 50) => {
    if (url.length <= maxLength) return url;
    return url.substring(0, maxLength) + '...';
  };

  return (
    <div className="bg-white rounded-xl border border-slate-200 p-5 hover:shadow-md transition-shadow">
      <div className="flex items-start justify-between gap-4">
        <div className="flex-1 min-w-0">
          {/* Title & Short URL */}
          <div className="flex items-center gap-2 mb-1">
            <h3 className="font-semibold text-slate-900 truncate">
              {url.title || url.shortCode}
            </h3>
            {url.hasPassword && (
              <Lock className="w-3.5 h-3.5 text-amber-500 flex-shrink-0" />
            )}
            <span className={`text-xs px-2 py-0.5 rounded-full flex-shrink-0 ${url.active ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}`}>
              {url.active ? 'Active' : 'Inactive'}
            </span>
          </div>

          {/* Short URL */}
          <div className="flex items-center gap-2 mb-2">
            <a
              href={url.shortUrl}
              target="_blank"
              rel="noopener noreferrer"
              className="text-blue-600 hover:text-blue-700 font-medium text-sm"
            >
              {url.shortUrl}
            </a>
            <button
              onClick={handleCopy}
              className="p-1 hover:bg-slate-100 rounded transition-colors"
              title="Copy to clipboard"
            >
              {copied ? (
                <Check className="w-3.5 h-3.5 text-green-600" />
              ) : (
                <Copy className="w-3.5 h-3.5 text-slate-400" />
              )}
            </button>
          </div>

          {/* Original URL */}
          <p className="text-slate-500 text-sm truncate mb-3" title={url.originalUrl}>
            {truncateUrl(url.originalUrl)}
          </p>

          {/* Stats Row */}
          <div className="flex items-center gap-4 text-xs text-slate-400">
            <span className="flex items-center gap-1">
              <MousePointerClick className="w-3.5 h-3.5" />
              {url.clickCount?.toLocaleString() || 0} clicks
            </span>
            <span className="flex items-center gap-1">
              <Calendar className="w-3.5 h-3.5" />
              {formatDate(url.createdAt)}
            </span>
            {url.expiresAt && (
              <span className="flex items-center gap-1 text-amber-500">
                <Calendar className="w-3.5 h-3.5" />
                Expires {formatDate(url.expiresAt)}
              </span>
            )}
          </div>
        </div>

        {/* Actions */}
        <div className="flex items-center gap-1 flex-shrink-0">
          <Dialog open={showQr} onOpenChange={setShowQr}>
            <DialogTrigger asChild>
              <Button variant="ghost" size="sm" className="text-slate-500 hover:text-blue-600">
                <QrCode className="w-4 h-4" />
              </Button>
            </DialogTrigger>
            <DialogContent className="sm:max-w-md">
              <DialogHeader>
                <DialogTitle>QR Code for {url.shortCode}</DialogTitle>
              </DialogHeader>
              <div className="flex flex-col items-center gap-4 py-4">
                {url.qrCodeBase64 && (
                  <img
                    src={url.qrCodeBase64}
                    alt={`QR code for ${url.shortUrl}`}
                    className="w-64 h-64 rounded-lg border border-slate-200"
                  />
                )}
                <p className="text-sm text-slate-500">{url.shortUrl}</p>
                <a
                  href={url.qrCodeBase64 || '#'}
                  download={`qrcode-${url.shortCode}.png`}
                  className="text-blue-600 hover:text-blue-700 text-sm font-medium"
                >
                  Download QR Code
                </a>
              </div>
            </DialogContent>
          </Dialog>

          <Button
            variant="ghost"
            size="sm"
            className="text-slate-500 hover:text-blue-600"
            onClick={() => onViewAnalytics(url.id)}
          >
            <BarChart3 className="w-4 h-4" />
          </Button>

          <a
            href={url.shortUrl}
            target="_blank"
            rel="noopener noreferrer"
          >
            <Button variant="ghost" size="sm" className="text-slate-500 hover:text-blue-600">
              <ExternalLink className="w-4 h-4" />
            </Button>
          </a>

          <Button
            variant="ghost"
            size="sm"
            className="text-slate-500 hover:text-red-600"
            onClick={() => onDelete(url.id)}
          >
            <Trash2 className="w-4 h-4" />
          </Button>
        </div>
      </div>
    </div>
  );
}
