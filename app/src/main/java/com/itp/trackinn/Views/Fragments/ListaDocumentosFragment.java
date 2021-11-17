package com.itp.trackinn.Views.Fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.itp.trackinn.Utils.ListaGuiaCampos;
import com.itp.trackinn.Views.MenuInferiorActivity;
import com.trackiinn.apptrack.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import dmax.dialog.SpotsDialog;

public class ListaDocumentosFragment extends Fragment {

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    String nombre_chofer;
    private final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private ListView lv1;
    ListaGuiaAdapterNew adapter;
    //String url = "http://www.innovationtechnologyperu.com/trackinn/index.php/hojaruta/";
    String url = "https://www.innovationtechnologyperu.com/trackinn/index.php/hojaruta/";
    //extraer_imei
    ArrayList<ListaGuiaCampos> datos=new ArrayList<ListaGuiaCampos>();
    private AlertDialog alertDialog;
    String imei = "";
    Context c;
    JSONArray json;
    String guias[];

    public ListaDocumentosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container2,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_lista_documentos, container2, false);

        lv1 = (ListView) view.findViewById(R.id.lista);
        imei = getImei(getActivity());
        boolean flg_activate=false;

        adapter = new ListaGuiaAdapterNew(getActivity(),R.layout.listview_item_row, datos);
        new asyniniciosession().execute(imei);
        return view;
    }

    public static String getImei(Context c) {
        String android_id = Settings.Secure.getString(c.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return android_id;
    }

    private class asyniniciosession extends AsyncTask<String, Integer, String> {
        HttpURLConnection conn;
        URL url_new = null;
        int progress = 0;
        Notification notification;
        NotificationManager notificationManager;
        int id = 10;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            alertDialog = new SpotsDialog.Builder().setContext(getActivity()).setTheme(R.style.DialogWaiting).build();
            alertDialog.setTitle("Un momento");
            alertDialog.setMessage("Cargando lista de documentos");
            alertDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                url_new = new URL(url + "/extraer_imei");
                Log.e("url a consultar ", url+ "/extraer_imei");
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "exception";
            }
            try {
                //Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection) url_new.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("vp_imei", params[0]);
                Log.e("el imei es ", params[0]);
                //.appendQueryParameter("password", params[1]);
                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception";
            }

            try {

                int response_code = conn.getResponseCode();

                if (response_code == HttpURLConnection.HTTP_OK) {

                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line, linea = "";

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                        linea = linea + " " + line;
                    }

                    json = new JSONArray(linea);
                    String s = "";
                    if (json.length() != 0) {
                        for (int i = 0; i < json.length(); i++) {
                            s = json.get(i).toString();
                            JSONObject last = new JSONObject(s);
                            last = json.getJSONObject(i);
                            ListaGuiaCampos objguia = new ListaGuiaCampos();
                            objguia.guia = last.getString("Serie_Ref") + "-" + last.getString("Num_Ref");
                            objguia.destino = last.getString("direccion");
                            objguia.tipo_documento = last.getString("Doc_Ref");
                            objguia.latitud = last.getString("latitud");
                            objguia.longitud = last.getString("longitud");
                            objguia.cliente = last.getString("Nom_Destino");
                            objguia.contacto = last.getString("contacto_cliente");
                            objguia.numero_contacto = last.getString("telefono_contacto_cliente");
                            objguia.fechaCompromiso = last.getString("fecha_pactada");
                            objguia.horaCompromiso = last.getString("hora_pactada");
                            objguia.flete = last.getString("tipoflete");
                            objguia.direccionReferencia = last.getString("direccion_referencia");
                            objguia.coordinador = last.getString("coordinador");
                            objguia.telefonoCoordinador = last.getString("telcoordinador");
                            objguia.ruccliente = last.getString("RUC_Cli");
                            datos.add(objguia);
                        }
                        //Log.e("objeto json ",s);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                conn.disconnect();
            }
            return ("unexception");
        }


        @Override
        protected void onPostExecute(String result) {

            alertDialog.dismiss();

            if (result.equals("exception")) {
                Toast.makeText(getActivity(), "Problemas al cargar documentos", Toast.LENGTH_LONG).show();
            }

            if (datos.size() > 0) {
                adapter.UpdateData(datos);
                lv1.setAdapter(adapter);
            } else {
                Toast.makeText(getActivity(), "No existen documentos pendientes", Toast.LENGTH_LONG).show();
                Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(600);
            }

        }
    }

    public static boolean GPSActivado(Context context) {
        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void showAlert() {
        final androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
        dialog.setTitle("gps desactivado")
                .setMessage("Su ubicación esta desactivada.\npor favor active su ubicación " +
                        "para seguir usando esta app")
                .setPositiveButton("Configuración de ubicación", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }

    public class ListaGuiaAdapterNew extends ArrayAdapter<ListaGuiaCampos> {
        Context context;
        int layoutResourceId;
        ArrayList<ListaGuiaCampos> data=null;

        public ListaGuiaAdapterNew(Context context, int layoutResourceId, ArrayList<ListaGuiaCampos> data) {
            super(context, layoutResourceId, data);
            this.layoutResourceId = layoutResourceId;
            this.context = context;
            if(data.size()>0) {
                this.data = data;
            }
        }
        public void UpdateData(ArrayList<ListaGuiaCampos> data) {
            if(data.size()>0) {
                this.data = data;
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            WeatherHolder holder = null;

            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new WeatherHolder();
            holder.imgIcon = (ImageView)row.findViewById(R.id.imgIcon);
            holder.imgIcon_mensaje = (ImageView)row.findViewById(R.id.imgIcon_mensaje);
            holder.imgDireccionValidada = (ImageView)row.findViewById(R.id.direccionvalidada);
            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);
            holder.txtDestino = (TextView)row.findViewById(R.id.txtDestino);
            holder.txtTipoDocumento = (TextView)row.findViewById(R.id.txtTipoDoc);


            /*if(row == null)
            {
                LayoutInflater inflater = ((Activity)context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);
                holder = new WeatherHolder();
                holder.imgIcon = (ImageView)row.findViewById(R.id.imgIcon);
                holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);
                holder.txtDestino = (TextView)row.findViewById(R.id.txtDestino);
                //row.setTag(holder);
            }
            else
            {
                holder = (WeatherHolder)row.getTag();
            }*/

            final ListaGuiaCampos weather = data.get(position);
            holder.txtTitle.setText(weather.guia);
            holder.txtDestino.setText(weather.destino);
            holder.txtTipoDocumento.setText(weather.tipo_documento);
            holder.txtFechaCompromiso = weather.fechaCompromiso;
            holder.txtHoraCompromiso = weather.horaCompromiso;
            holder.txtFlete = weather.flete;
            holder.txtDireccionReferencia = weather.direccionReferencia;
            holder.txtCoordinador = weather.coordinador;
            holder.txtTelefonoCoordinador = weather.telefonoCoordinador;
            holder.txtruccliente = weather.ruccliente;
            holder.txtcontacto = weather.contacto;
            holder.txttelefonocontacto = weather.numero_contacto;


            if((!weather.latitud.equals("") && !weather.longitud.equals("")) && (!weather.latitud.equals("0") && !weather.longitud.equals("0"))) {
                holder.imgDireccionValidada.setVisibility(View.VISIBLE);
            } else{
                holder.imgDireccionValidada.setVisibility(View.INVISIBLE);
            }

            holder.imgIcon.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if((!weather.latitud.equals("") && !weather.longitud.equals("")) && (!weather.latitud.equals("0") && !weather.longitud.equals("0"))){
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse("waze://?ll=" + weather.latitud + ", " + weather.longitud + "&navigate=yes"));
                        startActivity(i);
                    }
                    else{
                        String DireccionDestino = weather.destino.replace(" ","%20");
                        String url = "https://waze.com/ul?q="+DireccionDestino;
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    }
                }
            });

            holder.imgIcon_mensaje.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    String phoneNo = weather.numero_contacto;
                    if(!TextUtils.isEmpty(phoneNo)) {
                        String dial = "tel:" + phoneNo;
                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(dial)));
                    }else {
                        Toast.makeText(getActivity(), "Telefono no registrado", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            holder.txtTitle.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (GPSActivado(getActivity())) {
                        Context c = getContext();
                        Bundle args = new Bundle();
                        args.putString("documento", weather.guia);
                        args.putString("cliente", weather.cliente);
                        args.putString("direccion", weather.destino);
                        args.putString("tipodoc", weather.tipo_documento);
                        args.putString("fechaCompromiso", weather.fechaCompromiso);
                        args.putString("horaCompromiso", weather.horaCompromiso);
                        args.putString("flete", weather.flete);
                        args.putString("direccionReferencia", weather.direccionReferencia);
                        args.putString("coordinador", weather.coordinador);
                        args.putString("telefonoCoordinador", weather.telefonoCoordinador);
                        args.putString("ruccliente", weather.ruccliente);
                        args.putString("contacto", weather.contacto);
                        args.putString("telefonocontacto", weather.numero_contacto);


                        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
                        toolbar.setTitle(weather.tipo_documento+" "+weather.guia);

                        Fragment nuevoFragmento = new DetalleDocumentoFragment();
                        nuevoFragmento.setArguments(args);
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.container2, nuevoFragmento);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                    else
                    {
                        showAlert();
                    }
                }
            });

            holder.txtDestino.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (GPSActivado(getActivity())) {
                        Context c = getContext();
                        Bundle args = new Bundle();
                        args.putString("documento", weather.guia);
                        args.putString("cliente", weather.cliente);
                        args.putString("direccion", weather.destino);
                        args.putString("tipodoc", weather.tipo_documento);
                        args.putString("fechaCompromiso", weather.fechaCompromiso);
                        args.putString("horaCompromiso", weather.horaCompromiso);
                        args.putString("flete", weather.flete);
                        args.putString("direccionReferencia", weather.direccionReferencia);
                        args.putString("coordinador", weather.coordinador);
                        args.putString("telefonoCoordinador", weather.telefonoCoordinador);
                        args.putString("ruccliente", weather.ruccliente);
                        args.putString("contacto", weather.contacto);
                        args.putString("telefonocontacto", weather.numero_contacto);

                        Fragment nuevoFragmento = new DetalleDocumentoFragment();
                        nuevoFragmento.setArguments(args);
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.container2, nuevoFragmento);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                    else
                    {
                        showAlert();
                    }
                }
            });

            return row;
        }

        public class WeatherHolder
        {
            ImageView imgIcon;
            ImageView imgIcon_mensaje;
            ImageView imgDireccionValidada;
            TextView txtTitle;
            TextView txtDestino;
            TextView txtTipoDocumento;
            String txtFechaCompromiso;
            String txtHoraCompromiso;
            String txtFlete;
            String txtDireccionReferencia;
            String txtCoordinador;
            String txtTelefonoCoordinador;
            String txtruccliente;
            String txtcontacto;
            String txttelefonocontacto;
        }
    }

}
