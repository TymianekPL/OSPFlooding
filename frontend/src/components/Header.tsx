import React from "react";
import type {ThemeName} from "../types";

interface HeaderProps {
     onRefreshAll: () => void;
     theme: ThemeName;
     setTheme: (themeName: ThemeName) => void;
}

export const Header: React.FC<HeaderProps> = ({onRefreshAll, theme, setTheme}) => {
     const handleThemeChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
          setTheme(e.target.value as ThemeName);
          document.documentElement.setAttribute("data-theme", e.target.value);
     };
     const headerClasses = {
          light: "bg-gradient-to-r from-blue-600 to-blue-800 text-white",
          dark: "bg-gradient-to-r from-gray-800 to-gray-900 text-white",
          pink: "bg-gradient-to-r from-pink-500 to-pink-700 text-white"
     };

     const tagClasses = {
          light: "bg-blue-500 bg-opacity-50 text-blue-100",
          dark: "bg-gray-700 bg-opacity-50 text-gray-300",
          pink: "bg-pink-500 bg-opacity-50 text-pink-100"
     };

     return (
          <header className={`${headerClasses[theme]} shadow-lg`}>
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
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                                             d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"/>
                                   </svg>
                                   Refresh All Data
                              </button>

                              <select
                                   value={theme}
                                   onChange={handleThemeChange}
                                   className="bg-white text-blue-700 font-medium py-2 px-3 rounded-lg"
                              >
                                   <option value="light">Light</option>
                                   <option value="dark">Dark</option>
                                   <option value="pink">Pink</option>
                              </select>

                              <div className="hidden md:block">
                                   <div className="flex items-center space-x-2">
                                        <div className="h-3 w-3 bg-green-400 rounded-full animate-pulse"></div>
                                        <span className="text-sm">System Active</span>
                                   </div>
                              </div>
                         </div>
                    </div>

                    <div className="mt-4 flex flex-wrap gap-2">
                         {["Real-time Flood Detection", "Safe Route Planning", "OpenStreetMap Data", "Sentinel Hub Integration"].map(tag => (
                              <span
                                   key={tag}
                                   className={`${tagClasses[theme]} text-xs font-medium px-3 py-1 rounded-full`}
                              >{tag}</span>
                         ))}
                    </div>
               </div>
          </header>
     );
};
