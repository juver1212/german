package com.itp.trackinn.postLocation.repository;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


import com.itp.trackinn.LocationService;
import com.itp.trackinn.ServiceGeolocation.RestApiAdapter;
import com.itp.trackinn.ServiceGeolocation.Service;
import com.itp.trackinn.Utils.DBManager;
import com.itp.trackinn.data.entity.CoordenadaLocation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostLocationRepositoryImpl implements PostLocationRepository {
    private boolean debugMode = true;

    String url="https://www.innovationtechnologyperu.com/trackinn/index.php/";
    @Override
    public void post(double latitud, double longitud) {
        Log.i("PostLocationRepository","Llego la solicitud a repository");
        CoordenadaLocation coordenadaLocation = new CoordenadaLocation();
        coordenadaLocation.setLatitud(latitud);
        coordenadaLocation.setLongitud(longitud);
        RestApiAdapter restApiAdapter = new RestApiAdapter();
        Log.i("PostLocationRepository","new restapiadapter");
        Service service = restApiAdapter.getClientServiceSinSSL(url, 15);
        Log.i("PostLocationRepository","base url: "+url);
        Call<Object> call = service.postLocation(coordenadaLocation);
        Log.i("PostLocationRepository","Lanzamos la peticion");

        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> responseRetrofit) {
                try {
                    if(!responseRetrofit.isSuccessful()){
                        if(responseRetrofit.errorBody() != null){
                            responseRetrofit.errorBody().close();
                        }
                        return;
                    }
                    okhttp3.Response responseOkHttp = responseRetrofit.raw();
                    try {

                    }catch (Exception e){
                        if(debugMode) Log.i("PostLocationRepository","body respuesta null");
                    }finally {
                        //responseOkHttp.close();
                    }
                } catch (Exception e) {
                    if(responseRetrofit.errorBody() != null){
                        responseRetrofit.errorBody().close();
                    }
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<Object> call, Throwable t) {

            }
        });
    }

    @Override
    public void post(CoordenadaLocation coordenadaLocation) {
        Log.i("PostLocationRepository","Llego la solicitud a repository");
        RestApiAdapter restApiAdapter = new RestApiAdapter();
        Log.i("PostLocationRepository","new restapiadapter");
        Service service = restApiAdapter.getClientServiceSinSSL(url, 15);
        Log.i("PostLocationRepository","base url: "+url);
        Call<Object> call = service.postLocation(coordenadaLocation);
        Log.i("PostLocationRepository","Lanzamos la peticion");

        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> responseRetrofit) {
                try {
                    if(!responseRetrofit.isSuccessful()){
                        if(responseRetrofit.errorBody() != null){
                            responseRetrofit.errorBody().close();
                        }
                        return;
                    }
                    okhttp3.Response responseOkHttp = responseRetrofit.raw();
                    try {

                    }catch (Exception e){
                        if(debugMode) Log.i("PostLocationRepository","body respuesta null");
                    }finally {
                        //responseOkHttp.close();
                    }
                } catch (Exception e) {
                    if(responseRetrofit.errorBody() != null){
                        responseRetrofit.errorBody().close();
                    }
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<Object> call, Throwable t) {
            }
        });
    }

    public void enviarDataLocation(String listaCoordenadaLocation, Context c){
        new enviarData(c).execute(listaCoordenadaLocation);
    }

    private class enviarData extends AsyncTask<String, String, String> {
        private WeakReference<Context> contextRef;

        HttpURLConnection conn;
        URL url_new = null;

        public enviarData(Context context) {
            contextRef = new WeakReference<>(context);
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                url_new = new URL("https://www.innovationtechnologyperu.com/trackinn/index.php/android/Registrar_Datos_SQlite");
                conn = (HttpURLConnection)url_new.openConnection();
                conn.setReadTimeout(50000);
                conn.setConnectTimeout(50000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder().appendQueryParameter("vp_datos", params[0]);
                String query = builder.build().getEncodedQuery();

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

                int response_code = conn.getResponseCode();

                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    return(result.toString());

                }else{
                    return("0");
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "0";
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return "0";
            } catch (ProtocolException e) {
                e.printStackTrace();
                return "0";
            } catch (IOException e) {
                e.printStackTrace();
                return "0";
            }
        }
        @Override
        protected void onPostExecute(String result) {
            Context context = contextRef.get();
            try {

                if(result.equals("1")) {
                    EliminaDatosSQLite(context);
                    Log.i("------AVISOOOO-----","Enviado correctamente al Webservice");
                } else {
                    Log.i("------AVISOOOO-----","Error al enviar a Webservice");
                }

            }  catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void EliminaDatosSQLite(Context c) {
        DBManager dbManager = new DBManager(c);
        try {
            dbManager.delete();
            Log.i("------AVISOOOO-----","Data de Sqlite borrado");
        }
        catch(Exception e){
            e.getMessage();
        }
    }
}

