package com.itp.trackinn.postLocation.repository;

import android.content.Context;

import com.itp.trackinn.data.entity.CoordenadaLocation;

import java.util.List;

public interface PostLocationRepository {

    void post(double latitud, double longitud);

    void post(CoordenadaLocation coordenadaLocation);

    void enviarDataLocation(String coordenadaLocation, Context c);

}
