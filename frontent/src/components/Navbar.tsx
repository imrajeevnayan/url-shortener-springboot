import { Link2, BarChart3, Home, Github } from 'lucide-react';
import { useState } from 'react';
import BackendStatus from './BackendStatus';

export default function Navbar() {
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);

  return (
    <nav className="bg-white border-b border-slate-200 sticky top-0 z-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between h-16">
          <div className="flex items-center">
            <div className="flex-shrink-0 flex items-center gap-2">
              <div className="w-9 h-9 bg-blue-600 rounded-lg flex items-center justify-center">
                <Link2 className="w-5 h-5 text-white" />
              </div>
              <span className="text-xl font-bold text-slate-900">ShortURL</span>
            </div>
          </div>

          <div className="hidden md:flex items-center space-x-8">
            <a href="#" className="text-slate-600 hover:text-blue-600 font-medium transition-colors flex items-center gap-1.5">
              <Home className="w-4 h-4" />
              Home
            </a>
            <a href="#urls" className="text-slate-600 hover:text-blue-600 font-medium transition-colors flex items-center gap-1.5">
              <BarChart3 className="w-4 h-4" />
              My URLs
            </a>
            <a href="https://github.com" target="_blank" rel="noopener noreferrer" className="text-slate-600 hover:text-blue-600 font-medium transition-colors flex items-center gap-1.5">
              <Github className="w-4 h-4" />
              GitHub
            </a>
            <BackendStatus />
          </div>

          <div className="md:hidden flex items-center">
            <button
              onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
              className="text-slate-600 hover:text-slate-900 p-2"
            >
              <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                {isMobileMenuOpen ? (
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                ) : (
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />
                )}
              </svg>
            </button>
          </div>
        </div>
      </div>

      {isMobileMenuOpen && (
        <div className="md:hidden bg-white border-t border-slate-100">
          <div className="px-4 pt-2 pb-3 space-y-1">
            <a href="#" className="block px-3 py-2 text-slate-600 hover:text-blue-600 font-medium">Home</a>
            <a href="#analytics" className="block px-3 py-2 text-slate-600 hover:text-blue-600 font-medium">Analytics</a>
          </div>
        </div>
      )}
    </nav>
  );
}
