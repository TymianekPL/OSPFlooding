import L from "leaflet";
import "leaflet/dist/leaflet.css";

// Fix for default icons in React/TypeScript
export const configureLeafletIcons = () => {
     delete (L.Icon.Default.prototype as any)._getIconUrl;
     L.Icon.Default.mergeOptions({
          iconRetinaUrl: "https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-icon-2x.png",
          iconUrl: "https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-icon.png",
          shadowUrl: "https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-shadow.png",
     });
};

// Calculate ETA based on walking speed (5 km/h)
export const calculateETA = (distanceMeters: number): number => {
     const walkingSpeedKph = 5; // Average walking speed
     const walkingSpeedMps = walkingSpeedKph * 1000 / 3600;
     const timeSeconds = distanceMeters / walkingSpeedMps;
     return Math.round(timeSeconds / 60); // Convert to minutes
};