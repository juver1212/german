package com.itp.trackinn.ServiceGeolocation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Printer;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

//import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.itp.trackinn.LocationService;
import com.itp.trackinn.Utils.DBManager;
import com.itp.trackinn.Views.MainActivity;
import com.itp.trackinn.Utils.GeneralUtil;
import com.itp.trackinn.Utils.ToolsDate;
import com.itp.trackinn.data.entity.CoordenadaLocation;
import com.itp.trackinn.data.entity.DataLocation;
import com.itp.trackinn.postLocation.repository.PostLocationRepository;
import com.itp.trackinn.postLocation.repository.PostLocationRepositoryImpl;
import com.trackiinn.apptrack.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class ServiceGeolocation extends Service {
    private static final String TAG = ServiceGeolocation.class.getName();
    private TimerTask timerTask;
    private Timer timer;
    private Looper serviceLooper;
    private ServiceHandler mServiceHandler;
    //VARIABLES LOCATION
    private static final String LOCATION_KEY = "location-key";

    // Location API
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private Location mLastLocation;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationClient;

    // Códigos de petición
    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;
    private HandlerThread backgroundThread;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private int estadoBateria = 0;
    private String uuid;
    private String modelotelef;
    private String version;
    private String signal;
    private List<com.itp.trackinn.data.entity.CoordenadaLocation> listaCoordenadas = new ArrayList<>();
    private DBManager dbManager;
    private PostLocationRepository postLocationRepository = new PostLocationRepositoryImpl();
    private Context c = this;
    String imei = "";

    public ServiceGeolocation() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        initializeConfigurationForCoordinateSending();
        backgroundThread = new HandlerThread("UniqueThread", Process.THREAD_PRIORITY_BACKGROUND);
        backgroundThread.start();
        serviceLooper = backgroundThread.getLooper();
        mServiceHandler = new ServiceHandler(serviceLooper);
        estadoBateria = GeneralUtil.getBattery(this);
        uuid = GeneralUtil.getUuid(this);
        modelotelef = GeneralUtil.getModel(this);
        version = GeneralUtil.getversionName(this);
        signal = GeneralUtil.getSignal(this);
        imei = obtenerIMEI2(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        destroyAllThreads();
        addParamsToServiceHandler(intent, startId);
        String mensaje;
        if (intent != null) {
            mensaje = intent.getStringExtra("inputExtraMensaje");
        } else {
            mensaje = "Servicio Reiniciado por falta de memoria en el sistema";
        }
        startLocationUpdates();
        startForeground(100, getMyActivityNotification1(mensaje));
        return START_REDELIVER_INTENT;
    }

    public static String obtenerIMEI2(Context c) {
        String android_id = Settings.Secure.getString(c.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return android_id;
    }

    private String obtenerIMEI() {
        final TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            }
            return telephonyManager.getImei();
        } else {
            return telephonyManager.getDeviceId();
        }
    }

    private void updateNotification(String text) {
        Notification notification;
        notification = getMyActivityNotification1(text);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(100, notification);
    }

    private void destroyAllThreads() {
        if (timerTask != null) {
            timerTask.cancel();
        }
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
    }

    private void addParamsToServiceHandler(Intent intent, int startId) {
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        msg.obj = intent;
        mServiceHandler.sendMessage(msg);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyAllThreads();
        stopLocationUpdates();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void initializeConfigurationForCoordinateSending() {
        createLocationRequest();

        buildLocationSettingsRequest();

        checkLocationSettings();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    mLastLocation = location;
                }
            }
        };

    }

    private Notification getMyActivityNotification1(String text) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        String channel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            channel = createChannel();
        else {
            channel = "exampleServiceChannel";
        }

        return new NotificationCompat.Builder(this, channel)
                .setContentTitle("Sincronización de ubicación")
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setSmallIcon(R.drawable.aceros_arequipa_2)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .build();
    }

    @NonNull
    @TargetApi(26)
    private synchronized String createChannel() {
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        String name = "exampleServiceChannel";
        int importance = NotificationManager.IMPORTANCE_LOW;

        NotificationChannel mChannel = new NotificationChannel("exampleServiceChannelID", name, importance);

        mChannel.enableLights(true);
        mChannel.setLightColor(Color.BLUE);
        if (mNotificationManager != null) {
            mNotificationManager.createNotificationChannel(mChannel);
        } else {
            stopSelf();
        }
        return "exampleServiceChannelID";
    }

    private final class ServiceHandler extends Handler {

        ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            try {
                sendLocationCoordinates(Thread.currentThread().getName(), msg.arg1);
            } catch (Exception e) {
                //Crashlytics.log(1,"ServicioGeolocation", e.getMessage());
            }
        }

        private void sendLocationCoordinates(String nameThread, int startId) {
            timerTask = new TimerTask() {
                @Override
                public void run() {

                    connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    networkInfo = connectivityManager.getActiveNetworkInfo();

                    if (!isLocationPermissionGranted()) {
                        Log.i("------AVISOOOO-----", "Permisos de localización denegados");
                        updateNotification("Permisos de localización denegados");
                        return;
                    }
                    if (mLastLocation == null) {
                        Log.i("------AVISOOOO-----", "No hay informes de Localización");
                        updateNotification("Última actualización...: " + ToolsDate.getFormattedDateSimple(System.currentTimeMillis(),
                                "dd/MM/yy HH:mm:ss", null)
                                + "\n" + "No hay informes de Localización");
                        return;
                    }

                    updateNotification("Última actualización...: " + ToolsDate.getFormattedDateSimple(System.currentTimeMillis(),
                            "dd/MM/yy HH:mm:ss", null)
                            + "\n" + "Lat: "
                            + mLastLocation.getLatitude()
                            + "    Lon: "
                            + mLastLocation.getLongitude()
                    );

                    try {
                        CoordenadaLocation coordenadaLocation = new CoordenadaLocation();
                        coordenadaLocation.setImei(imei);
                        coordenadaLocation.setLatitud(mLastLocation.getLatitude());
                        coordenadaLocation.setLongitud(mLastLocation.getLongitude());
                        coordenadaLocation.setFec_reg(GeneralUtil.FechaHoraActual());
                        coordenadaLocation.setNivel_bateria(String.valueOf(estadoBateria));
                        coordenadaLocation.setVelocidad(String.valueOf(mLastLocation.getSpeed() * 3.6f));
                        coordenadaLocation.setDatos_moviles("S");
                        coordenadaLocation.setEstado_gps("S");
                        guardarDataLocal(coordenadaLocation);

                        if (networkInfo != null && networkInfo.isConnected()) {
                            if (listaCoordenadas.size() >= 6) {
                                Log.i("------AVISOOOO-----", "Comenzo envio a Webservice");
                                postLocationRepository.enviarDataLocation(extraerListaCoordenadas(), c);
                            }
                        }

                    } catch (Exception e) {
                        Log.e("Servicio transporte", e.getMessage());
                        //Crashlytics.log(1, "ServiceGeolocation|sendLocationCoordinates", e.getMessage());
                    }
                }
            };
            timer = new Timer();
            timer.scheduleAtFixedRate(timerTask, 0, 10000);
        }

    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest()
                .setInterval(1000)
                .setFastestInterval(1000 / 2)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest)
                .setAlwaysShow(true);
        mLocationSettingsRequest = builder.build();
    }

    private void checkLocationSettings() {
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(mLocationSettingsRequest);
        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                Log.d(TAG, "Los ajustes de ubicación satisfacen la configuración.");
                startLocationUpdates();
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    Log.d(TAG, "Los ajustes de ubicación no satisfacen la configuración. ");
                }
            }

        });
    }

    private void getLastLocation() {
        if (isLocationPermissionGranted()) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                mLastLocation = location;
                            }
                        }
                    });
        }
    }

    private void startLocationUpdates() {
        if (isLocationPermissionGranted()) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            fusedLocationClient.requestLocationUpdates(mLocationRequest, locationCallback, null);
        }
    }

    private void stopLocationUpdates() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private boolean isLocationPermissionGranted() {
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return permission == PackageManager.PERMISSION_GRANTED;
    }

    private void guardarDataLocal(CoordenadaLocation coordenadaLocation) {
        try {
            dbManager = new DBManager(this);

            Calendar c = Calendar.getInstance();
            //c.set(Calendar.HOUR_OF_DAY, 24);
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
            dateformat.setTimeZone(TimeZone.getTimeZone("America/Lima"));
            String fecha = dateformat.format(c.getTime())+" "+c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE)+":"+c.get(Calendar.SECOND);


            dbManager.insert(coordenadaLocation.getImei(),
                    coordenadaLocation.getLatitud(),
                    coordenadaLocation.getLongitud(),
                    fecha,
                    coordenadaLocation.getNivel_bateria(),
                    coordenadaLocation.getVelocidad(), coordenadaLocation.getDatos_moviles(), coordenadaLocation.getEstado_gps());
            listaCoordenadas.add(coordenadaLocation);
            Log.i("------AVISOOOO-----","Data correctamente guardada en Sqlite");
        }catch(Exception e){
            Log.i("------AVISOOOO-----","Error al guardar data local");
            //Crashlytics.log(1,"ServicioGeolocation", e.getMessage());
            e.getMessage();
        }
    }

    private String extraerListaCoordenadas() {
        try {
            listaCoordenadas.clear();
            dbManager = new DBManager(this);
            listaCoordenadas = new ArrayList<>(dbManager.select());
            ArrayList<JSONObject> lista = new ArrayList<>();

            for (CoordenadaLocation data : listaCoordenadas) {
                JSONObject jObject = new JSONObject();
                jObject.put("vp_imei", data.getImei());
                jObject.put("vp_latitud", String.valueOf(data.getLatitud()));
                jObject.put("vp_longitud", String.valueOf(data.getLongitud()));
                jObject.put("vp_nivel_bateria", data.getNivel_bateria());
                jObject.put("vp_velocidad", data.getVelocidad());
                jObject.put("vp_gps", data.getEstado_gps());
                jObject.put("vp_internet", data.getDatos_moviles());
                jObject.put("vp_sqlite", "S");
                jObject.put("vp_fecha", data.getFec_reg());
                lista.add(jObject);
            }
            listaCoordenadas.clear();
            return lista.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            //Crashlytics.log(1,"ServicioGeolocation", e.getMessage());
            return "";
        }
    }

}
