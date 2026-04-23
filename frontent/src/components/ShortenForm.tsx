import { useState } from 'react';
import { Link2, Loader2, Check, ChevronDown, ChevronUp, Sparkles, Lock, Calendar } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Card, CardContent } from '@/components/ui/card';
import { api } from '@/services/api';
import type { UrlResponse, UrlRequest } from '@/types';

interface ShortenFormProps {
  onSuccess: (url: UrlResponse) => void;
}

export default function ShortenForm({ onSuccess }: ShortenFormProps) {
  const [originalUrl, setOriginalUrl] = useState('');
  const [customAlias, setCustomAlias] = useState('');
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [expiryDays, setExpiryDays] = useState('');
  const [password, setPassword] = useState('');
  const [showAdvanced, setShowAdvanced] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!originalUrl.trim()) {
      setError('Please enter a URL');
      return;
    }

    setIsLoading(true);
    setError('');
    setSuccess(false);

    try {
      const request: UrlRequest = {
        originalUrl,
        ...(customAlias && { customAlias }),
        ...(title && { title }),
        ...(description && { description }),
        ...(expiryDays && { expiryDays: parseInt(expiryDays) }),
        ...(password && { password }),
      };

      const response = await api.createShortUrl(request);
      if (response.success && response.data) {
        setSuccess(true);
        onSuccess(response.data);
        // Reset form
        setOriginalUrl('');
        setCustomAlias('');
        setTitle('');
        setDescription('');
        setExpiryDays('');
        setPassword('');
        setTimeout(() => setSuccess(false), 3000);
      } else {
        setError(response.message || 'Failed to shorten URL');
      }
    } catch (err: any) {
      setError(err.message || 'An error occurred');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="w-full max-w-3xl mx-auto">
      <div className="text-center mb-8">
        <h1 className="text-4xl font-bold text-slate-900 mb-3">
          Shorten Your Links
        </h1>
        <p className="text-slate-500 text-lg">
          Create short, memorable links with powerful analytics
        </p>
      </div>

      <Card className="shadow-lg border-slate-200">
        <CardContent className="pt-6">
          <form onSubmit={handleSubmit} className="space-y-4">
            {/* Main URL Input */}
            <div className="flex gap-3">
              <div className="flex-1 relative">
                <Link2 className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-slate-400" />
                <Input
                  type="url"
                  placeholder="Paste your long URL here..."
                  value={originalUrl}
                  onChange={(e) => setOriginalUrl(e.target.value)}
                  className="pl-11 h-14 text-lg border-slate-300 focus:border-blue-500 focus:ring-blue-500"
                />
              </div>
              <Button
                type="submit"
                disabled={isLoading}
                className="h-14 px-8 bg-blue-600 hover:bg-blue-700 text-white font-semibold text-base"
              >
                {isLoading ? (
                  <Loader2 className="w-5 h-5 animate-spin" />
                ) : success ? (
                  <Check className="w-5 h-5" />
                ) : (
                  <>
                    <Sparkles className="w-4 h-4 mr-2" />
                    Shorten
                  </>
                )}
              </Button>
            </div>

            {/* Error & Success Messages */}
            {error && (
              <div className="p-3 bg-red-50 border border-red-200 rounded-lg text-red-600 text-sm">
                {error}
              </div>
            )}
            {success && (
              <div className="p-3 bg-green-50 border border-green-200 rounded-lg text-green-600 text-sm flex items-center gap-2">
                <Check className="w-4 h-4" />
                URL shortened successfully!
              </div>
            )}

            {/* Advanced Options Toggle */}
            <button
              type="button"
              onClick={() => setShowAdvanced(!showAdvanced)}
              className="flex items-center gap-1 text-sm text-slate-500 hover:text-blue-600 transition-colors"
            >
              {showAdvanced ? <ChevronUp className="w-4 h-4" /> : <ChevronDown className="w-4 h-4" />}
              Advanced Options
            </button>

            {/* Advanced Options */}
            {showAdvanced && (
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4 pt-2 border-t border-slate-100">
                <div>
                  <Label htmlFor="customAlias" className="text-slate-700 flex items-center gap-1">
                    Custom Alias
                  </Label>
                  <Input
                    id="customAlias"
                    placeholder="my-link"
                    value={customAlias}
                    onChange={(e) => setCustomAlias(e.target.value)}
                    className="mt-1.5 border-slate-300"
                  />
                  <p className="text-xs text-slate-400 mt-1">e.g., short.url/my-link</p>
                </div>

                <div>
                  <Label htmlFor="title" className="text-slate-700 flex items-center gap-1">
                    Title
                  </Label>
                  <Input
                    id="title"
                    placeholder="My Link Title"
                    value={title}
                    onChange={(e) => setTitle(e.target.value)}
                    className="mt-1.5 border-slate-300"
                  />
                </div>

                <div>
                  <Label htmlFor="expiry" className="text-slate-700 flex items-center gap-1">
                    <Calendar className="w-3.5 h-3.5" />
                    Expiry (days)
                  </Label>
                  <Input
                    id="expiry"
                    type="number"
                    min="1"
                    max="365"
                    placeholder="30"
                    value={expiryDays}
                    onChange={(e) => setExpiryDays(e.target.value)}
                    className="mt-1.5 border-slate-300"
                  />
                </div>

                <div>
                  <Label htmlFor="password" className="text-slate-700 flex items-center gap-1">
                    <Lock className="w-3.5 h-3.5" />
                    Password Protection
                  </Label>
                  <Input
                    id="password"
                    type="password"
                    placeholder="Optional password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    className="mt-1.5 border-slate-300"
                  />
                </div>

                <div className="md:col-span-2">
                  <Label htmlFor="description" className="text-slate-700">Description</Label>
                  <Input
                    id="description"
                    placeholder="Optional description for this link"
                    value={description}
                    onChange={(e) => setDescription(e.target.value)}
                    className="mt-1.5 border-slate-300"
                  />
                </div>
              </div>
            )}
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
