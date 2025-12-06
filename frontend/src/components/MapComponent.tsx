import React, { useCallback } from "react";
import { MapContainer, TileLayer, GeoJSON, Rectangle, Marker, Popup, useMapEvents } from "react-leaflet";
import L from "leaflet";
import type {
     FeatureCollection,
     FloodFeatureCollection,
     PointFeatureCollection,
     MapBounds, FeatureProperties, FloodPolygonFeature, ThemeName
} from "../types";

interface MapClickHandlerProps {
     onMapClick: (lat: number, lng: number) => void;
}

const MapClickHandler: React.FC<MapClickHandlerProps> = ({ onMapClick }) => {
     useMapEvents({
          click(e) {
               onMapClick(e.latlng.lat, e.latlng.lng);
          },
     });
     return null;
};

interface MapComponentProps {
     onMapClick: (lat: number, lng: number) => void;
     origin: [number, number] | null;
     destination: [number, number];
     routeGeoJson?: FeatureCollection;
     floodPolygons?: FloodFeatureCollection;
     floodPoints?: PointFeatureCollection;
     showFloodedAreas: boolean;
     showFloodPoints: boolean;
     bounds: MapBounds;
     theme: ThemeName;
}

export const MapComponent: React.FC<MapComponentProps> = ({
     onMapClick,
     origin,
     destination,
     routeGeoJson,
     floodPolygons,
     floodPoints,
     showFloodedAreas,
     showFloodPoints,
     bounds,
     theme
}) => {
     const onEachRouteFeature = useCallback((feature: { properties: FeatureProperties }, layer: L.Layer) => {
          if (feature.properties?.totalLength) {
               const distanceKm = (feature.properties.totalLength / 1000).toFixed(2);
               const timeMinutes = feature.properties.estimatedTime || Math.round(feature.properties.totalLength / 83.33); // 5 km/h walking speed
               layer.bindPopup(`
        <div class="p-2">
          <h3 class="font-semibold text-gray-800 mb-1">Route Information</h3>
          <p class="text-sm text-gray-600">Distance: <span class="font-medium">${distanceKm} km</span></p>
          <p class="text-sm text-gray-600">Estimated Walking Time: <span class="font-medium">${timeMinutes} minutes</span></p>
          <p class="text-xs text-gray-500 mt-1">Click anywhere to set new origin</p>
        </div>
      `);
          }
     }, []);

     const onEachFloodFeature = useCallback((feature: FloodPolygonFeature, layer: L.Layer) => {
          const pointCount = feature.properties.pointCount || 0;
          layer.bindPopup(`
      <div class="p-2">
        <h3 class="font-semibold text-blue-800 mb-1">Flooded Area</h3>
        <p class="text-sm text-gray-700">Detected flood points: <span class="font-medium">${pointCount}</span></p>
        <p class="text-xs text-gray-500 mt-1">Route will avoid this area</p>
      </div>
    `);
     }, []);

     return (
          <div className={`rounded-lg overflow-hidden shadow-lg border border-gray-200 ${
               theme === "light" ? "bg-white" :
                    theme === "dark" ? "bg-gray-900" :
                         "bg-pink-100"
          }`}>
               <MapContainer
                    center={[50.0575, 19.9365]}
                    zoom={17}
                    scrollWheelZoom={true}
                    className="h-[70vh] w-full"
               >
                    <TileLayer
                         url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                         attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                    />

                    {/* Map click handler - using useMapEvents hook */}
                    <MapClickHandler onMapClick={onMapClick} />

                    {/* Boundary rectangle */}
                    <Rectangle
                         bounds={[bounds.southWest, bounds.northEast]}
                         pathOptions={{ color: "#3b82f6", weight: 2, fillOpacity: 0 }}
                    />

                    {/* Flood polygons */}
                    {showFloodedAreas && floodPolygons && (
                         <GeoJSON
                              key="flood-polygons"
                              data={floodPolygons}
                              onEachFeature={onEachFloodFeature}
                              style={() => ({
                                   fillColor: "#60a5fa",
                                   color: "#3b82f6",
                                   weight: 1,
                                   opacity: 0.7,
                                   fillOpacity: 0.3
                              })}
                         />
                    )}


                    {/* Flood points */}
                    {showFloodPoints && floodPoints && (
                         <GeoJSON
                              key="flood-points"
                              data={floodPoints as never}
                              pointToLayer={(_feature, latlng) => {
                                   return L.circleMarker(latlng, {
                                        radius: 3,
                                        fillColor: "#ef4444",
                                        color: "#dc2626",
                                        weight: 1,
                                        opacity: 0.8,
                                        fillOpacity: 0.6
                                   });
                              }}
                         />
                    )}

                    {/* Route */}
                    {routeGeoJson && (
                         <GeoJSON
                              key={JSON.stringify(routeGeoJson.features.map(f => f.properties.roadIds))}
                              data={routeGeoJson as never}
                              onEachFeature={onEachRouteFeature}
                              style={{ color: "#10b981", weight: 4, opacity: 0.8 }}
                         />
                    )}

                    {/* Destination marker */}
                    <Marker position={destination}>
                         <Popup>
                              <div className="p-2">
                                   <h3 className="font-semibold text-green-700 mb-1">Evacuation Point</h3>
                                   <p className="text-sm text-gray-600">
                                        Lat: {destination[0].toFixed(6)}<br />
                                        Lon: {destination[1].toFixed(6)}
                                   </p>
                              </div>
                         </Popup>
                    </Marker>

                    {/* Origin marker */}
                    {origin && (
                         <Marker position={origin}>
                              <Popup>
                                   <div className="p-2">
                                        <h3 className="font-semibold text-blue-700 mb-1">Origin Point</h3>
                                        <p className="text-sm text-gray-600">
                                             Lat: {origin[0].toFixed(6)}<br />
                                             Lon: {origin[1].toFixed(6)}
                                        </p>
                                        <p className="text-xs text-gray-500 mt-1">Click map to change</p>
                                   </div>
                              </Popup>
                         </Marker>
                    )}
               </MapContainer>
          </div>
     );
};
