import React from "react";
import type { RouteInfo, ThemeName } from "../types";

interface StatsPanelProps {
     routeInfo?: RouteInfo;
     floodPointCount: number;
     isLoading: boolean;
     theme: ThemeName;
}

export const StatsPanel: React.FC<StatsPanelProps> = ({
     routeInfo,
     floodPointCount,
     isLoading,
     theme
}) => {
     const textColor = theme === "light" ? "text-gray-900" : theme === "dark" ? "text-gray-50" : "text-pink-900";
     const headingColor = theme === "light" ? "text-gray-800" : theme === "dark" ? "text-gray-200" : "text-pink-700";
     const bgColor = theme === "light" ? "bg-white" : theme === "dark" ? "bg-gray-800" : "bg-pink-100";

     const panelBg = (base: string) =>
          theme === "light" ? `bg-${base}-50 border-${base}-100 text-${base}-900` :
               theme === "dark" ? `bg-${base}-900 border-${base}-800 text-${base}-200` :
                    `bg-${base}-200 border-${base}-300 text-${base}-900`;

     const panelText = (base: string) =>
          theme === "light" ? `text-${base}-700` :
               theme === "dark" ? `text-${base}-200` :
                    `text-${base}-700`;

     return (
          <div className={`rounded-lg shadow-lg p-4 mb-4 ${bgColor} ${textColor}`}>
               <h2 className={`text-lg font-semibold mb-3 ${headingColor}`}>Statistics</h2>

               <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                    {/* Flood Data Stats */}
                    <div className={`rounded p-3 border ${panelBg("blue")}`}>
                         <h3 className={`text-sm font-semibold mb-2 ${panelText("blue")}`}>Flood Data</h3>
                         <div className="space-y-1">
                              <div className="flex justify-between">
                                   <span className={`text-sm ${panelText("blue")}`}>Flood Points Detected:</span>
                                   <span className={`text-sm font-medium ${panelText("blue")}`}>
                                        {floodPointCount.toLocaleString()}
                                   </span>
                              </div>
                              <div className="flex justify-between">
                                   <span className={`text-sm ${panelText("blue")}`}>Last Updated:</span>
                                   <span className={`text-sm font-medium ${panelText("blue")}`}>Just now</span>
                              </div>
                         </div>
                    </div>

                    {/* Route Stats */}
                    <div className={`rounded p-3 border ${panelBg("green")}`}>
                         <h3 className={`text-sm font-semibold mb-2 ${panelText("green")}`}>Current Route</h3>
                         {routeInfo ? (
                              <div className="space-y-1">
                                   <div className="flex justify-between">
                                        <span className={`text-sm ${panelText("green")}`}>Distance:</span>
                                        <span className={`text-sm font-medium ${panelText("green")}`}>
                                             {(routeInfo.distance / 1000).toFixed(2)} km
                                        </span>
                                   </div>
                                   <div className="flex justify-between">
                                        <span className={`text-sm ${panelText("green")}`}>Walking Time:</span>
                                        <span className={`text-sm font-medium ${panelText("green")}`}>
                                             {routeInfo.estimatedTime} minutes
                                        </span>
                                   </div>
                                   <div className="flex justify-between">
                                        <span className={`text-sm ${panelText("green")}`}>Avg Speed:</span>
                                        <span className={`text-sm font-medium ${panelText("green")}`}>5.0 km/h</span>
                                   </div>
                              </div>
                         ) : (
                              <p className={`text-sm ${panelText("green")}`}>No route calculated yet</p>
                         )}
                    </div>

                    {/* System Status */}
                    <div className={`rounded p-3 border ${panelBg("gray")}`}>
                         <h3 className={`text-sm font-semibold mb-2 ${panelText("gray")}`}>System Status</h3>
                         <div className="space-y-1">
                              <div className="flex justify-between items-center">
                                   <span className={`text-sm ${panelText("gray")}`}>API Connection:</span>
                                   <span className="flex items-center">
                                        <span className={`h-2 w-2 rounded-full mr-2 ${
                                             isLoading ? "bg-yellow-500 animate-pulse" :
                                                  theme === "dark" ? "bg-green-400" : "bg-green-500"
                                        }`}></span>
                                        <span className={`text-sm font-medium ${panelText("gray")}`}>
                                             {isLoading ? "Processing..." : "Connected"}
                                        </span>
                                   </span>
                              </div>
                              <div className="flex justify-between">
                                   <span className={`text-sm ${panelText("gray")}`}>Flood Detection:</span>
                                   <span className={`text-sm font-medium ${panelText("gray")}`}>Active</span>
                              </div>
                              <div className="flex justify-between">
                                   <span className={`text-sm ${panelText("gray")}`}>Route Planning:</span>
                                   <span className={`text-sm font-medium ${panelText("gray")}`}>Optimal</span>
                              </div>
                         </div>
                    </div>
               </div>
          </div>
     );
};
