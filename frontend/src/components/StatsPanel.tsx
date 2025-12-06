import React from "react";
import type {RouteInfo} from "../types";

interface StatsPanelProps {
     routeInfo?: RouteInfo;
     floodPointCount: number;
     isLoading: boolean;
}

export const StatsPanel: React.FC<StatsPanelProps> = ({
     routeInfo,
     floodPointCount,
     isLoading
}) => {
     return (
          <div className="bg-white rounded-lg shadow p-4 mb-4">
               <h2 className="text-lg font-semibold text-gray-800 mb-3">Statistics</h2>

               <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                    {/* Flood Data Stats */}
                    <div className="bg-blue-50 border border-blue-100 rounded p-3">
                         <h3 className="text-sm font-semibold text-blue-800 mb-2">Flood Data</h3>
                         <div className="space-y-1">
                              <div className="flex justify-between">
                                   <span className="text-sm text-blue-700">Flood Points Detected:</span>
                                   <span className="text-sm font-medium text-blue-900">
                                        {floodPointCount.toLocaleString()}
                                   </span>
                              </div>
                              <div className="flex justify-between">
                                   <span className="text-sm text-blue-700">Last Updated:</span>
                                   <span className="text-sm font-medium text-blue-900">
                Just now
                                   </span>
                              </div>
                         </div>
                    </div>

                    {/* Route Stats */}
                    <div className="bg-green-50 border border-green-100 rounded p-3">
                         <h3 className="text-sm font-semibold text-green-800 mb-2">Current Route</h3>
                         {routeInfo ? (
                              <div className="space-y-1">
                                   <div className="flex justify-between">
                                        <span className="text-sm text-green-700">Distance:</span>
                                        <span className="text-sm font-medium text-green-900">
                                             {(routeInfo.distance / 1000).toFixed(2)} km
                                        </span>
                                   </div>
                                   <div className="flex justify-between">
                                        <span className="text-sm text-green-700">Walking Time:</span>
                                        <span className="text-sm font-medium text-green-900">
                                             {routeInfo.estimatedTime} minutes
                                        </span>
                                   </div>
                                   <div className="flex justify-between">
                                        <span className="text-sm text-green-700">Avg Speed:</span>
                                        <span className="text-sm font-medium text-green-900">
                  5.0 km/h
                                        </span>
                                   </div>
                              </div>
                         ) : (
                              <p className="text-sm text-green-700">No route calculated yet</p>
                         )}
                    </div>

                    {/* System Status */}
                    <div className="bg-gray-50 border border-gray-100 rounded p-3">
                         <h3 className="text-sm font-semibold text-gray-800 mb-2">System Status</h3>
                         <div className="space-y-1">
                              <div className="flex justify-between items-center">
                                   <span className="text-sm text-gray-700">API Connection:</span>
                                   <span className="flex items-center">
                                        <span className={`h-2 w-2 rounded-full mr-2 ${
                                             isLoading ? "bg-yellow-500 animate-pulse" : "bg-green-500"
                                        }`}></span>
                                        <span className="text-sm font-medium text-gray-900">
                                             {isLoading ? "Processing..." : "Connected"}
                                        </span>
                                   </span>
                              </div>
                              <div className="flex justify-between">
                                   <span className="text-sm text-gray-700">Flood Detection:</span>
                                   <span className="text-sm font-medium text-gray-900">
                Active
                                   </span>
                              </div>
                              <div className="flex justify-between">
                                   <span className="text-sm text-gray-700">Route Planning:</span>
                                   <span className="text-sm font-medium text-gray-900">
                Optimal
                                        {/* since this is a prototype, these will remain static/unchanged. seems like a bad idea but whatever, as of this commit, this application is not used in any serious environment... */}
                                   </span>
                              </div>
                         </div>
                    </div>
               </div>
          </div>
     );
};
