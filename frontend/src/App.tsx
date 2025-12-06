import { useState, useEffect, useCallback } from "react";
import { Header } from "./components/Header";
import { ControlPanel } from "./components/ControlPanel";
import { StatsPanel } from "./components/StatsPanel";
import { MapComponent } from "./components/MapComponent";
import {
     type FeatureCollection,
     type FloodFeatureCollection,
     type PointFeatureCollection,
     type RouteInfo,
     DEFAULT_BOUNDS,
     type MapBounds,
     type ThemeName
} from "./types";
import { configureLeafletIcons, calculateETA } from "./utilities/leafletConfig";

configureLeafletIcons();

function App() {
     const [theme, setTheme] = useState<ThemeName>(() =>
          (localStorage.getItem("theme") as ThemeName | null) || "light"
     );
     const [routeGeoJson, setRouteGeoJson] = useState<FeatureCollection>();
     const [floodPolygons, setFloodPolygons] = useState<FloodFeatureCollection>();
     const [floodPoints, setFloodPoints] = useState<PointFeatureCollection>();
     const [showFloodedAreas, setShowFloodedAreas] = useState(true);
     const [showFloodPoints, setShowFloodPoints] = useState(false);
     const [loadingFloodData, setLoadingFloodData] = useState(false);
     const [loadingRoute, setLoadingRoute] = useState(false);
     const [origin, setOrigin] = useState<[number, number] | null>(null);
     const [destination, setDestination] = useState<[number, number]>(() => {
          const [south, west] = DEFAULT_BOUNDS.southWest;
          const [north, east] = DEFAULT_BOUNDS.northEast;
          return [
               south + Math.random() * (north - south),
               west + Math.random() * (east - west)
          ];
     });
     const [routeInfo, setRouteInfo] = useState<RouteInfo>();
     const [bounds] = useState<MapBounds>(DEFAULT_BOUNDS);

     useEffect(() => {
          loadFloodData();
     }, []);

     useEffect(() => {
          localStorage.setItem("theme", theme);
          document.documentElement.setAttribute("data-theme", theme);
     }, [theme]);

     const loadFloodData = async () => {
          setLoadingFloodData(true);
          try {
               const now = new Date();
               const yesterday = new Date(now.getTime() - 24 * 60 * 60 * 1000);
               const formatDate = (date: Date) => date.toISOString().split("T")[0];
               const startDate = formatDate(yesterday);
               const endDate = formatDate(now);

               const polygonsRes = await fetch(
                    `http://localhost:8080/api/flood/polygons?south=0&west=0&north=0&east=0&startDate=${startDate}&endDate=${endDate}`
               );
               if (polygonsRes.ok) {
                    setFloodPolygons(await polygonsRes.json());
               }

               const pointsRes = await fetch(
                    `http://localhost:8080/api/flood/points?south=0&west=0&north=0&east=0&startDate=${startDate}&endDate=${endDate}`
               );
               if (pointsRes.ok) {
                    setFloodPoints(await pointsRes.json());
               }
          } catch (err) {
               console.error("Error loading flood data:", err);
          } finally {
               setLoadingFloodData(false);
          }
     };

     const requestRoute = useCallback(
          async (start: [number, number], end: [number, number]) => {
               if (!start || !end) return;
               setLoadingRoute(true);
               try {
                    const res = await fetch(
                         `http://localhost:8080/api/evac/route?start=${start[0]},${start[1]}&end=${end[0]},${end[1]}`
                    );
                    if (!res.ok) throw new Error(`HTTP error! status: ${res.status}`);

                    const data = await res.json();
                    if (data.geojson) {
                         setRouteGeoJson(data.geojson);
                         if (data.geojson.features?.[0]?.properties?.totalLength) {
                              const distance = data.geojson.features[0].properties.totalLength;
                              const estimatedTime = calculateETA(distance);
                              const pointCount = floodPoints?.features.length || 0;
                              setRouteInfo({ distance, estimatedTime, pointCount });
                         }
                    } else {
                         alert("No route found. Please try a different origin or destination.");
                    }
               } catch (err) {
                    console.error("Error fetching route:", err);
                    alert("Failed to calculate route. Please check your connection and try again.");
               } finally {
                    setLoadingRoute(false);
               }
          },
          [floodPoints]
     );

     const handleMapClick = useCallback((lat: number, lng: number) => {
          setOrigin([lat, lng]);
     }, []);

     const handleRefreshAll = async () => {
          await loadFloodData();
          if (origin) await requestRoute(origin, destination);
     };

     const floodPointCount = floodPoints?.features.length || 0;

     const appBg =
          theme === "light" ? "bg-gray-50 text-gray-900" :
               theme === "dark" ? "bg-gray-900 text-gray-50" :
                    "bg-pink-50 text-pink-900";

     const footerBg =
          theme === "light" ? "bg-gray-800 text-white" :
               theme === "dark" ? "bg-gray-900 text-gray-50" :
                    "bg-pink-700 text-white";

     const legendBg =
          theme === "light" ? "bg-white text-gray-900" :
               theme === "dark" ? "bg-gray-800 text-gray-50" :
                    "bg-pink-100 text-pink-900";

     const legendText = theme === "light" ? "text-gray-600" :
          theme === "dark" ? "text-gray-200" :
               "text-pink-900";

     return (
          <div className={`min-h-screen ${appBg}`}>
               <Header onRefreshAll={handleRefreshAll} setTheme={setTheme} theme={theme} />

               <main className="container mx-auto px-4 py-6">
                    <StatsPanel
                         routeInfo={routeInfo}
                         floodPointCount={floodPointCount}
                         isLoading={loadingFloodData || loadingRoute}
                         theme={theme}
                    />

                    <ControlPanel
                         onRouteRequest={requestRoute}
                         onRefreshFloodData={loadFloodData}
                         routeInfo={routeInfo}
                         loadingFloodData={loadingFloodData}
                         showFloodedAreas={showFloodedAreas}
                         setShowFloodedAreas={setShowFloodedAreas}
                         showFloodPoints={showFloodPoints}
                         setShowFloodPoints={setShowFloodPoints}
                         destination={destination}
                         setDestination={setDestination}
                         origin={origin}
                         setOrigin={setOrigin}
                         theme={theme}
                    />

                    <MapComponent
                         onMapClick={handleMapClick}
                         origin={origin}
                         destination={destination}
                         routeGeoJson={routeGeoJson}
                         floodPolygons={floodPolygons}
                         floodPoints={floodPoints}
                         showFloodedAreas={showFloodedAreas}
                         showFloodPoints={showFloodPoints}
                         bounds={bounds}
                         theme={theme}
                    />

                    {/* Legend */}
                    <div className={`mt-4 rounded-lg shadow p-4 ${legendBg}`}>
                         <h3 className={`text-sm font-semibold mb-2 ${legendText}`}>Map Legend</h3>
                         <div className="flex flex-wrap gap-4">
                              <div className="flex items-center">
                                   <div className="w-4 h-4 bg-green-500 rounded mr-2"></div>
                                   <span className={`text-sm ${legendText}`}>Safe Route</span>
                              </div>
                              <div className="flex items-center">
                                   <div className="w-4 h-4 bg-blue-500 rounded mr-2 opacity-30"></div>
                                   <span className={`text-sm ${legendText}`}>Flooded Area</span>
                              </div>
                              <div className="flex items-center">
                                   <div className="w-3 h-3 bg-red-500 rounded-full mr-2"></div>
                                   <span className={`text-sm ${legendText}`}>Flood Point</span>
                              </div>
                              <div className="flex items-center">
                                   <div className="w-4 h-0 border-t-2 border-blue-500 mr-2"></div>
                                   <span className={`text-sm ${legendText}`}>Area Boundary</span>
                              </div>
                         </div>
                    </div>
               </main>

               <footer className={`${footerBg} py-4 mt-8`}>
                    <div className="container mx-auto px-4 text-center text-sm">
                         <p>FloodSafe Navigation System &copy; {new Date().getFullYear()} | Using OpenStreetMap & Sentinel Hub data</p>
                         <p className="mt-1">Route planning considers real-time flood data for safe evacuation</p>
                    </div>
               </footer>
          </div>
     );
}

export default App;
