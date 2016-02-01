package com.example.mapdemo;

import android.location.Location;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by DC0708 on 17-Nov-15.
 */
public class DraggableCircle {

    //private final Marker centerMarker;

    //private final Marker radiusMarker;

    GoogleMap googleMap;

    public final Circle circle;

    public double radius;

    public int direction;

    public int bTag;

    public static final double RADIUS_OF_EARTH_METERS = 6371009;

    private int mStrokeColor;

    private int mFillColor;

    private int mWidth;

    public DraggableCircle(LatLng center, double radius, int direction, int bTag,GoogleMap mMap) {
        this.radius = radius;
        this.direction = direction;
        this.bTag = bTag;
//            centerMarker = mMap.addMarker(new MarkerOptions()
//                    .position(center)
//                    .draggable(true));
//            radiusMarker = mMap.addMarker(new MarkerOptions()
//                    .position(toRadiusLatLng(center, radius))
//                    .draggable(true)
//                    .icon(BitmapDescriptorFactory.defaultMarker(
//                            BitmapDescriptorFactory.HUE_AZURE)));
        circle = mMap.addCircle(new CircleOptions()
                .center(center)
                .radius(radius)
                .strokeWidth(mWidth)
                .strokeColor(mStrokeColor)
                .fillColor(mFillColor));
    }

    public DraggableCircle(LatLng center, LatLng radiusLatLng,int direction, int bTag,GoogleMap mMap) {
        this.radius = toRadiusMeters(center, radiusLatLng);
        this.direction = direction;
        this.bTag = bTag;
//            centerMarker = mMap.addMarker(new MarkerOptions()
//                    .position(center)
//                    .draggable(true));
//            radiusMarker = mMap.addMarker(new MarkerOptions()
//                    .position(radiusLatLng)
//                    .draggable(true)
//                    .icon(BitmapDescriptorFactory.defaultMarker(
//                            BitmapDescriptorFactory.HUE_AZURE)));
        circle = mMap.addCircle(new CircleOptions()
                .center(center)
                .radius(radius)
                .strokeWidth(mWidth)
                .strokeColor(mStrokeColor)
                .fillColor(mFillColor));
    }

//        public boolean onMarkerMoved(Marker marker) {
//            if (marker.equals(centerMarker)) {
//                circle.setCenter(marker.getPosition());
//                radiusMarker.setPosition(toRadiusLatLng(marker.getPosition(), radius));
//                return true;
//            }
//            if (marker.equals(radiusMarker)) {
//                radius = toRadiusMeters(centerMarker.getPosition(), radiusMarker.getPosition());
//                circle.setRadius(radius);
//                return true;
//            }
//            return false;
//        }

    public void onStyleChange() {
        circle.setStrokeWidth(mWidth);
        circle.setFillColor(mFillColor);
        circle.setStrokeColor(mStrokeColor);
    }



    /** Generate LatLng of radius marker */
    private static LatLng toRadiusLatLng(LatLng center, double radius) {
        double radiusAngle = Math.toDegrees(radius / RADIUS_OF_EARTH_METERS) /
                Math.cos(Math.toRadians(center.latitude));
        return new LatLng(center.latitude, center.longitude + radiusAngle);
    }

    private static double toRadiusMeters(LatLng center, LatLng radius) {
        float[] result = new float[1];
        Location.distanceBetween(center.latitude, center.longitude,
                radius.latitude, radius.longitude, result);
        return result[0];
    }

}