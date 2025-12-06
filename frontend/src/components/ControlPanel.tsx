import React, {useState} from "react";
import type {RouteInfo, ThemeName} from "../types";

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
     theme: ThemeName;
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
     setOrigin,
     theme
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
          <div className={`rounded-lg shadow-lg p-4 mb-4 ${
               theme === "light" ? "bg-white text-gray-900" :
                    theme === "dark" ? "bg-gray-800 text-gray-50" :
                         "bg-pink-100 text-pink-900"
          }`}>
               <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">

                    {/* Origin Section */}
                    <div className="space-y-2">
                         <h3 className={`text-sm font-semibold ${
                              theme === "light" ? "text-gray-700" :
                                   theme === "dark" ? "text-gray-200" :
                                        "text-pink-700"
                         }`}>Origin</h3>
                         <div className="flex gap-2">
                              <button
                                   onClick={handleSetCurrentLocation}
                                   className={`flex-1 py-2 px-3 rounded text-sm font-medium transition-colors ${
                                        theme === "light" ? "bg-blue-50 hover:bg-blue-100 text-blue-700" :
                                             theme === "dark" ? "bg-blue-900 hover:bg-blue-800 text-blue-200" :
                                                  "bg-pink-200 hover:bg-pink-300 text-pink-800"
                                   }`}
                              >Use Current Location</button>
                              <button
                                   onClick={() => setOrigin([0, 0])}
                                   className={`py-2 px-3 rounded text-sm font-medium transition-colors ${
                                        theme === "light" ? "bg-gray-50 hover:bg-gray-100 text-gray-700" :
                                             theme === "dark" ? "bg-gray-700 hover:bg-gray-600 text-gray-200" :
                                                  "bg-pink-200 hover:bg-pink-300 text-pink-800"
                                   }`}
                              >Clear</button>
                         </div>
                         {origin && (
                              <div className={`text-xs ${
                                   theme === "light" ? "text-gray-600" :
                                        theme === "dark" ? "text-gray-300" :
                                             "text-pink-800"
                              }`}>
                                   <p>Lat: {origin[0].toFixed(6)}</p>
                                   <p>Lon: {origin[1].toFixed(6)}</p>
                              </div>
                         )}
                         <p className={`text-xs ${
                              theme === "light" ? "text-gray-500" :
                                   theme === "dark" ? "text-gray-400" :
                                        "text-pink-700"
                         }`}>
                              {origin ? "Click map to change origin" : "Click map to set origin"}
                         </p>
                    </div>

                    {/* Destination Section */}
                    <div className="space-y-2">
                         <div className="flex justify-between items-center">
                              <h3 className={`text-sm font-semibold ${
                                   theme === "light" ? "text-gray-700" :
                                        theme === "dark" ? "text-gray-200" :
                                             "text-pink-700"
                              }`}>Destination</h3>
                              <button
                                   onClick={handleRandomDestination}
                                   className={`text-xs font-medium ${
                                        theme === "light" ? "text-blue-600 hover:text-blue-800" :
                                             theme === "dark" ? "text-blue-300 hover:text-blue-200" :
                                                  "text-pink-700 hover:text-pink-900"
                                   }`}
                              >Random</button>
                         </div>
                         <div className="space-y-2">
                              <div>
                                   <label className={`block text-xs mb-1 ${
                                        theme === "light" ? "text-gray-600" :
                                             theme === "dark" ? "text-gray-300" :
                                                  "text-pink-700"
                                   }`}>Latitude</label>
                                   <input
                                        type="number"
                                        step="0.000001"
                                        value={destination[0]}
                                        onChange={(e) => setDestination([parseFloat(e.target.value) || 0, destination[1]])}
                                        className={`w-full px-3 py-2 border rounded text-sm focus:outline-none focus:ring-2 focus:border-transparent ${
                                             theme === "light" ? "border-gray-300 focus:ring-blue-500" :
                                                  theme === "dark" ? "border-gray-600 focus:ring-blue-300 bg-gray-700 text-gray-200" :
                                                       "border-pink-300 focus:ring-pink-500 bg-pink-200 text-pink-900"
                                        }`}
                                   />
                              </div>
                              <div>
                                   <label className={`block text-xs mb-1 ${
                                        theme === "light" ? "text-gray-600" :
                                             theme === "dark" ? "text-gray-300" :
                                                  "text-pink-700"
                                   }`}>Longitude</label>
                                   <input
                                        type="number"
                                        step="0.000001"
                                        value={destination[1]}
                                        onChange={(e) => setDestination([destination[0], parseFloat(e.target.value) || 0])}
                                        className={`w-full px-3 py-2 border rounded text-sm focus:outline-none focus:ring-2 focus:border-transparent ${
                                             theme === "light" ? "border-gray-300 focus:ring-blue-500" :
                                                  theme === "dark" ? "border-gray-600 focus:ring-blue-300 bg-gray-700 text-gray-200" :
                                                       "border-pink-300 focus:ring-pink-500 bg-pink-200 text-pink-900"
                                        }`}
                                   />
                              </div>
                         </div>
                    </div>

                    {/* Flood Controls */}
                    <div className="space-y-3">
                         <h3 className={`text-sm font-semibold ${
                              theme === "light" ? "text-gray-700" :
                                   theme === "dark" ? "text-gray-200" :
                                        "text-pink-700"
                         }`}>Flood Data</h3>
                         <div className="space-y-2">
                              <label className="flex items-center space-x-2 cursor-pointer">
                                   <input
                                        type="checkbox"
                                        checked={showFloodedAreas}
                                        onChange={(e) => setShowFloodedAreas(e.target.checked)}
                                        className={`rounded ${
                                             theme === "light" ? "text-blue-600 focus:ring-blue-500" :
                                                  theme === "dark" ? "text-blue-300 focus:ring-blue-300" :
                                                       "text-pink-700 focus:ring-pink-500"
                                        }`}
                                   />
                                   <span className={`text-sm ${
                                        theme === "light" ? "text-gray-700" :
                                             theme === "dark" ? "text-gray-200" :
                                                  "text-pink-700"
                                   }`}>Show Flooded Areas</span>
                              </label>
                              <label className="flex items-center space-x-2 cursor-pointer">
                                   <input
                                        type="checkbox"
                                        checked={showFloodPoints}
                                        onChange={(e) => setShowFloodPoints(e.target.checked)}
                                        className={`rounded ${
                                             theme === "light" ? "text-blue-600 focus:ring-blue-500" :
                                                  theme === "dark" ? "text-blue-300 focus:ring-blue-300" :
                                                       "text-pink-700 focus:ring-pink-500"
                                        }`}
                                   />
                                   <span className={`text-sm ${
                                        theme === "light" ? "text-gray-700" :
                                             theme === "dark" ? "text-gray-200" :
                                                  "text-pink-700"
                                   }`}>Show Flood Points</span>
                              </label>
                         </div>
                         <button
                              onClick={onRefreshFloodData}
                              disabled={loadingFloodData}
                              className={`w-full py-2 px-3 rounded text-sm font-medium transition-colors ${
                                   loadingFloodData
                                        ? "bg-gray-100 text-gray-400 cursor-not-allowed"
                                        : theme === "light"
                                             ? "bg-blue-600 hover:bg-blue-700 text-white"
                                             : theme === "dark"
                                                  ? "bg-blue-800 hover:bg-blue-700 text-gray-50"
                                                  : "bg-pink-600 hover:bg-pink-700 text-white"
                              }`}
                         >
                              {loadingFloodData ? "Loading..." : "Refresh Flood Data"}
                         </button>
                    </div>

                    {/* Route Controls */}
                    <div className="space-y-3">
                         <h3 className={`text-sm font-semibold ${
                              theme === "light" ? "text-gray-700" :
                                   theme === "dark" ? "text-gray-200" :
                                        "text-pink-700"
                         }`}>Route</h3>
                         <button
                              onClick={handleRequestRoute}
                              disabled={!origin || isLoading}
                              className={`w-full py-2 px-3 rounded text-sm font-medium transition-colors ${
                                   !origin || isLoading
                                        ? "bg-gray-100 text-gray-400 cursor-not-allowed"
                                        : theme === "light"
                                             ? "bg-green-600 hover:bg-green-700 text-white"
                                             : theme === "dark"
                                                  ? "bg-green-700 hover:bg-green-600 text-gray-50"
                                                  : "bg-pink-500 hover:bg-pink-600 text-white"
                              }`}
                         >
                              {isLoading ? "Calculating..." : "Calculate Route"}
                         </button>
                         {routeInfo && (
                              <div className={`rounded p-3 ${
                                   theme === "light" ? "bg-blue-50 border-blue-100 text-blue-900" :
                                        theme === "dark" ? "bg-blue-900 border-blue-800 text-blue-200" :
                                             "bg-pink-200 border-pink-300 text-pink-900"
                              }`}>
                                   <h4 className="text-xs font-semibold mb-2">Route Information</h4>
                                   <div className="space-y-1">
                                        <div className="flex justify-between">
                                             <span className="text-xs">Distance:</span>
                                             <span className="text-xs font-medium">{(routeInfo.distance / 1000).toFixed(2)} km</span>
                                        </div>
                                        <div className="flex justify-between">
                                             <span className="text-xs">Walking Time:</span>
                                             <span className="text-xs font-medium">{routeInfo.estimatedTime} min</span>
                                        </div>
                                   </div>
                              </div>
                         )}
                    </div>
               </div>
          </div>
     );
};
