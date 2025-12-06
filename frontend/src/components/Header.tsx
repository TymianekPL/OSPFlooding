import React from "react";

interface HeaderProps {
     onRefreshAll: () => void;
}

export const Header: React.FC<HeaderProps> = ({ onRefreshAll }) => {
     return (
          <header className="bg-gradient-to-r from-blue-600 to-blue-800 text-white shadow-lg">
               <div className="container mx-auto px-4 py-4">
                    <div className="flex flex-col md:flex-row justify-between items-center">
                         <div className="mb-4 md:mb-0">
                              <h1 className="text-2xl font-bold">FloodSafe Navigation</h1>
                              <p className="text-blue-100 mt-1">
                                   Safe evacuation route planning during flood events
                              </p>
                         </div>

                         <div className="flex items-center space-x-4">
                              <button
                                   onClick={onRefreshAll}
                                   className="bg-white text-blue-700 hover:bg-blue-50 font-medium py-2 px-4 rounded-lg transition-colors flex items-center"
                              >
                                   <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
                                   </svg>
                                   Refresh All Data
                              </button>

                              <div className="hidden md:block">
                                   <div className="flex items-center space-x-2">
                                        <div className="h-3 w-3 bg-green-400 rounded-full animate-pulse"></div>
                                        <span className="text-sm">System Active</span>
                                   </div>
                              </div>
                         </div>
                    </div>

                    <div className="mt-4 flex flex-wrap gap-2">
                         <span className="bg-blue-500 bg-opacity-50 text-blue-100 text-xs font-medium px-3 py-1 rounded-full">
            Real-time Flood Detection
                         </span>
                         <span className="bg-blue-500 bg-opacity-50 text-blue-100 text-xs font-medium px-3 py-1 rounded-full">
            Safe Route Planning
                         </span>
                         <span className="bg-blue-500 bg-opacity-50 text-blue-100 text-xs font-medium px-3 py-1 rounded-full">
            OpenStreetMap Data
                         </span>
                         <span className="bg-blue-500 bg-opacity-50 text-blue-100 text-xs font-medium px-3 py-1 rounded-full">
            Sentinel Hub Integration
                         </span>
                    </div>
               </div>
          </header>
     );
};
