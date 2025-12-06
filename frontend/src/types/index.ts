export type FeatureProperties = {
     totalLength: number;
     roadIds: number[];
     estimatedTime?: number; // in minutes
};

export type LineStringFeature = {
     type: "Feature";
     geometry: {
          type: "LineString";
          coordinates: [number, number][];
     };
     properties: FeatureProperties;
};

export type FloodPolygonFeature = {
     type: "Feature";
     geometry: {
          type: "Polygon";
          coordinates: [number, number][][];
     };
     properties: {
          flooded: boolean;
          pointCount?: number;
     };
};

export type FloodPointFeature = {
     type: "Feature";
     geometry: {
          type: "Point";
          coordinates: [number, number];
     };
     properties: {
          flooded: boolean;
     };
};

export type FeatureCollection = {
     type: "FeatureCollection";
     features: LineStringFeature[];
};

export type FloodFeatureCollection = {
     type: "FeatureCollection";
     features: FloodPolygonFeature[];
};

export type PointFeatureCollection = {
     type: "FeatureCollection";
     features: FloodPointFeature[];
};

export type RouteInfo = {
     distance: number; // in metres
     estimatedTime: number; // in minutes
     pointCount: number;
};

export type MapBounds = {
     southWest: [number, number];
     northEast: [number, number];
};

export const DEFAULT_BOUNDS: MapBounds = {
     southWest: [50.045, 19.900],
     northEast: [50.070, 19.960]
};

export type ThemeName = "dark" | "light" | "pink";
