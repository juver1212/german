package com.itp.trackinn.Views.Fragments;


import static android.content.Context.CLIPBOARD_SERVICE;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.trackiinn.apptrack.R;

import org.json.JSONArray;


public class BienvenidaFragment extends Fragment {

    private Button btninicio;
    private AlertDialog alertDialog;
    String imei = "";
    Context c;
    JSONArray json;
    String url = "https://www.innovationtechnologyperu.com/trackinn/index.php/hojaruta/";
    public static final int CONNECTION_TIMEOUT=50000;
    public static final int READ_TIMEOUT=50000;
    private TextView titulo1, subtitulo1, titulo2, subtitulo2, titulo3, subtitulo3, txtconductor, txtUuid;
    String documentos_pendientes = "", inicio_ruta ="", inicio_ruta_fecha ="", documentos_atendidos ="", conductor ="",
            cod_hoja="", num_hoja="", cod_empresa="";
    private ClipboardManager myClipboard;
    private ClipData myClip;

    public BienvenidaFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_bienvenida, container, false);


        btninicio = (Button) v.findViewById(R.id.boton2);
        titulo1 = (TextView) v.findViewById(R.id.titulo1);
        subtitulo1 = (TextView) v.findViewById(R.id.subtitulo1);
        titulo2 = (TextView) v.findViewById(R.id.titulo2);
        subtitulo2 = (TextView) v.findViewById(R.id.subtitulo2);
        titulo3 = (TextView) v.findViewById(R.id.titulo3);
        subtitulo3 = (TextView) v.findViewById(R.id.subtitulo3);
        txtconductor = (TextView) v.findViewById(R.id.txtconductor);
        txtUuid = (TextView) v.findViewById(R.id.uuid);
        myClipboard = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);

        txtUuid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = txtUuid.getText().toString();
                myClip = ClipData.newPlainText("text", text);
                myClipboard.setPrimaryClip(myClip);
                Toast.makeText(getActivity(), "UUID copiado", Toast.LENGTH_SHORT).show();
            }

        });

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            cod_hoja = bundle.getString("cod_hoja");
            num_hoja = bundle.getString("num_hoja");
            cod_empresa = bundle.getString("cod_empresa");
            documentos_pendientes = bundle.getString("documentos_pendientes");
            inicio_ruta = bundle.getString("inicio_ruta");
            inicio_ruta_fecha = bundle.getString("inicio_ruta_fecha");
            documentos_atendidos = bundle.getString("documentos_entregados");
            conductor = bundle.getString("conductor");

            txtconductor.setText(conductor);

            if (inicio_ruta.equals("") || inicio_ruta.equals("null")) {
                subtitulo1.setText("Tienes "+documentos_pendientes+" entregas pendientes");
                subtitulo2.setText("No tienes ruta asignada");
                subtitulo3.setText("Tienes "+documentos_atendidos+" entregas atendidas");
            } else {
                subtitulo1.setText("Tienes "+documentos_pendientes+" entregas pendientes");
                subtitulo2.setText("La ruta inicio el "+ inicio_ruta_fecha+" a las "+inicio_ruta+"");
                subtitulo3.setText(documentos_atendidos+" entregas realizadas");
            }

        }



        btninicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment nuevoFragmento = new ListaDocumentosFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container2, nuevoFragmento);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        imei = getImei(getActivity());

        txtUuid.setText(imei);

        return v;
    }

    public static String getImei(Context c) {
        String android_id = Settings.Secure.getString(c.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return android_id;
    }
}
