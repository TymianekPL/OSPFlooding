import React, {useState} from "react";
import type {RouteInfo} from "../types";

interface ControlPanelProps {
     onRouteRequest: (start: [number, number], end: [number, number]) => void;
     onRefreshFloodData: () => Promise<void>;
     routeInfo?: RouteInfo;
     loadingFloodData: boolean;
     showFloodedAreas: boolean;
     setShowFloodedAreas: (show: boolean) => void;
     showFloodPoints: boolean;
     setShowFloodPoints: (show: boolean) => void;
     destination: [number, number];
     setDestination: (dest: [number, number]) => void;
     origin: [number, number] | null;
     setOrigin: (origin: [number, number]) => void;
}

export const ControlPanel: React.FC<ControlPanelProps> = ({
     onRouteRequest,
     onRefreshFloodData,
     routeInfo,
     loadingFloodData,
     showFloodedAreas,
     setShowFloodedAreas,
     showFloodPoints,
     setShowFloodPoints,
     destination,
     setDestination,
     origin,
     setOrigin
}) => {
     const [isLoading, setIsLoading] = useState(false);

     const handleRequestRoute = async () => {
          if (!origin) {
               alert("Please click on the map to select an origin point first.");
               return;
          }

          setIsLoading(true);
          try {
               onRouteRequest(origin, destination);
          } finally {
               setIsLoading(false);
          }
     };

     const handleRandomDestination = () => {
          const [south, west] = [50.045, 19.900];
          const [north, east] = [50.070, 19.960];
          const newLat = south + Math.random() * (north - south);
          const newLon = west + Math.random() * (east - west);
          setDestination([newLat, newLon]);
     };

     const handleSetCurrentLocation = () => {
          if (navigator.geolocation) {
               navigator.geolocation.getCurrentPosition(
                    (position) => {
                         setOrigin([position.coords.latitude, position.coords.longitude]);
                    },
                    (error) => {
                         alert(`Unable to get your location: ${error.message}`);
                    }
               );
          } else {
               alert("Geolocation is not supported by your browser.");
          }
     };

     return (
          <div className="bg-white rounded-lg shadow-lg p-4 mb-4">
               <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
                    {/* Origin Section */}
                    <div className="space-y-2">
                         <h3 className="text-sm font-semibold text-gray-700">Origin</h3>
                         <div className="flex gap-2">
                              <button
                                   onClick={handleSetCurrentLocation}
                                   className="flex-1 bg-blue-50 hover:bg-blue-100 text-blue-700 text-sm font-medium py-2 px-3 rounded transition-colors"
                              >
                                   Use Current Location
                              </button>
                              <button
                                   onClick={() => setOrigin([0, 0])}
                                   className="bg-gray-50 hover:bg-gray-100 text-gray-700 text-sm font-medium py-2 px-3 rounded transition-colors"
                              >
                                   Clear
                              </button>
                         </div>
                         {origin && (
                              <div className="text-xs text-gray-600">
                                   <p>Lat: {origin[0].toFixed(6)}</p>
                                   <p>Lon: {origin[1].toFixed(6)}</p>
                              </div>
                         )}
                         <p className="text-xs text-gray-500">
                              {origin ? "Click map to change origin" : "Click map to set origin"}
                         </p>
                    </div>

                    {/* Destination Section */}
                    <div className="space-y-2">
                         <div className="flex justify-between items-center">
                              <h3 className="text-sm font-semibold text-gray-700">Destination</h3>
                              <button
                                   onClick={handleRandomDestination}
                                   className="text-xs text-blue-600 hover:text-blue-800"
                              >
                                   Random
                              </button>
                         </div>
                         <div className="space-y-2">
                              <div>
                                   <label className="block text-xs text-gray-600 mb-1">Latitude</label>
                                   <input
                                        type="number"
                                        step="0.000001"
                                        value={destination[0]}
                                        onChange={(e) => setDestination([parseFloat(e.target.value) || 0, destination[1]])}
                                        className="w-full px-3 py-2 border border-gray-300 rounded text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                   />
                              </div>
                              <div>
                                   <label className="block text-xs text-gray-600 mb-1">Longitude</label>
                                   <input
                                        type="number"
                                        step="0.000001"
                                        value={destination[1]}
                                        onChange={(e) => setDestination([destination[0], parseFloat(e.target.value) || 0])}
                                        className="w-full px-3 py-2 border border-gray-300 rounded text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                   />
                              </div>
                         </div>
                    </div>

                    {/* Flood Controls */}
                    <div className="space-y-3">
                         <h3 className="text-sm font-semibold text-gray-700">Flood Data</h3>
                         <div className="space-y-2">
                              <label className="flex items-center space-x-2 cursor-pointer">
                                   <input
                                        type="checkbox"
                                        checked={showFloodedAreas}
                                        onChange={(e) => setShowFloodedAreas(e.target.checked)}
                                        className="rounded text-blue-600 focus:ring-blue-500"
                                   />
                                   <span className="text-sm text-gray-700">Show Flooded Areas</span>
                              </label>
                              <label className="flex items-center space-x-2 cursor-pointer">
                                   <input
                                        type="checkbox"
                                        checked={showFloodPoints}
                                        onChange={(e) => setShowFloodPoints(e.target.checked)}
                                        className="rounded text-blue-600 focus:ring-blue-500"
                                   />
                                   <span className="text-sm text-gray-700">Show Flood Points</span>
                              </label>
                         </div>
                         <button
                              onClick={onRefreshFloodData}
                              disabled={loadingFloodData}
                              className={`w-full py-2 px-3 rounded text-sm font-medium transition-colors ${
                                   loadingFloodData
                                        ? "bg-gray-100 text-gray-400 cursor-not-allowed"
                                        : "bg-blue-600 hover:bg-blue-700 text-white"
                              }`}
                         >
                              {loadingFloodData ? (
                                   <span className="flex items-center justify-center">
                                        <svg className="animate-spin h-4 w-4 mr-2 text-white" viewBox="0 0 24 24">
                                             <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor"
                                                  strokeWidth="4"
                                                  fill="none"/>
                                             <path className="opacity-75" fill="currentColor"
                                                  d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"/>
                                        </svg>
                Loading...
                                   </span>
                              ) : (
                                   "Refresh Flood Data"
                              )}
                         </button>
                    </div>

                    {/* Route Controls & Info */}
                    <div className="space-y-3">
                         <h3 className="text-sm font-semibold text-gray-700">Route</h3>
                         <button
                              onClick={handleRequestRoute}
                              disabled={!origin || isLoading}
                              className={`w-full py-2 px-3 rounded text-sm font-medium transition-colors ${
                                   !origin || isLoading
                                        ? "bg-gray-100 text-gray-400 cursor-not-allowed"
                                        : "bg-green-600 hover:bg-green-700 text-white"
                              }`}
                         >
                              {isLoading ? (
                                   <span className="flex items-center justify-center">
                                        <svg className="animate-spin h-4 w-4 mr-2 text-white" viewBox="0 0 24 24">
                                             <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor"
                                                  strokeWidth="4"
                                                  fill="none"/>
                                             <path className="opacity-75" fill="currentColor"
                                                  d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"/>
                                        </svg>
                Calculating...
                                   </span>
                              ) : (
                                   "Calculate Route"
                              )}
                         </button>

                         {routeInfo && (
                              <div className="bg-blue-50 border border-blue-100 rounded p-3">
                                   <h4 className="text-xs font-semibold text-blue-800 mb-2">Route Information</h4>
                                   <div className="space-y-1">
                                        <div className="flex justify-between">
                                             <span className="text-xs text-blue-700">Distance:</span>
                                             <span className="text-xs font-medium text-blue-900">
                                                  {(routeInfo.distance / 1000).toFixed(2)} km
                                             </span>
                                        </div>
                                        <div className="flex justify-between">
                                             <span className="text-xs text-blue-700">Walking Time:</span>
                                             <span className="text-xs font-medium text-blue-900">
                                                  {routeInfo.estimatedTime} minutes
                                             </span>
                                        </div>
                                   </div>
                              </div>
                         )}
                    </div>
               </div>
          </div>
     );
};
