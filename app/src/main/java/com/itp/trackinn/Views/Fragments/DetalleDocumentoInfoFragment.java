package com.itp.trackinn.Views.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.trackiinn.apptrack.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetalleDocumentoInfoFragment extends Fragment {

    Button boton_gestion, boton_informacion;
    String status = "", num_guia = "", tipdoc = "", cliente = "", direccion = "", fechaCompromiso="",ruccliente="",
            contacto="",telefonocontacto="",
            horaCompromiso="", flete="", direccionReferencia="",coordinador="",telefonoCoordinador="";
    TextView txtcliente, txtfechaCompromiso, txtDireccion, txtdni,
            txthoraCompromiso, txtflete, txtdireccionReferencia,txtcoordinador,txttelefonoCoordinador,txtContacto,txttelefonoContacto;
    ImageView imgllamarcoordinador, imgllamarcontacto,imgmsncoordinador, imgmsncontacto;

    public DetalleDocumentoInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detalle_documento_info, container, false);
        boton_gestion = (Button) view.findViewById(R.id.imageButtonGestion2);
        boton_informacion = (Button) view.findViewById(R.id.imageButtonInformacion2);
        txtcliente = (TextView) view.findViewById(R.id.txtCliente);
        txtfechaCompromiso= (TextView) view.findViewById(R.id.txtFechaEntrega);
        txthoraCompromiso= (TextView) view.findViewById(R.id.txtHoraEntrega);
        txtflete= (TextView) view.findViewById(R.id.txtTipoFlete);
        txtdireccionReferencia= (TextView) view.findViewById(R.id.txtReferencia);
        txtcoordinador= (TextView) view.findViewById(R.id.txtCoordinador);
        txttelefonoCoordinador= (TextView) view.findViewById(R.id.txtCoordinadorTel);
        txtDireccion= (TextView) view.findViewById(R.id.txtDireccion);
        txtdni= (TextView) view.findViewById(R.id.txtDni);
        txttelefonoContacto= (TextView) view.findViewById(R.id.txtTelefonoContacto);
        txtContacto= (TextView) view.findViewById(R.id.txtContacto);

        imgllamarcontacto= (ImageView) view.findViewById(R.id.img_llamar_contacto);
        imgllamarcoordinador= (ImageView) view.findViewById(R.id.img_llamar_coordinador);
        imgmsncontacto= (ImageView) view.findViewById(R.id.img_msn_contacto);
        imgmsncoordinador= (ImageView) view.findViewById(R.id.img_msn_coordinador);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            num_guia = bundle.getString("documento");
            cliente = bundle.getString("cliente");
            tipdoc = bundle.getString("tipodoc");
            fechaCompromiso=bundle.getString("fechaCompromiso");
            horaCompromiso=bundle.getString("horaCompromiso");
            flete=bundle.getString("flete");
            direccionReferencia=bundle.getString("direccionReferencia");
            coordinador=bundle.getString("coordinador");
            telefonoCoordinador=bundle.getString("telefonoCoordinador");
            direccion=bundle.getString("direccion");
            ruccliente=bundle.getString("ruccliente");
            contacto=bundle.getString("contacto");
            telefonocontacto=bundle.getString("telefonocontacto");
        }

        txtcliente.setText(cliente);
        txtfechaCompromiso.setText(fechaCompromiso);
        txthoraCompromiso.setText(horaCompromiso);
        txtflete.setText(flete);
        txtdireccionReferencia.setText(direccionReferencia);
        txtcoordinador.setText(coordinador);
        txttelefonoCoordinador.setText(telefonoCoordinador);
        txtDireccion.setText(direccion);
        txtdni.setText(ruccliente);
        txtContacto.setText(contacto);
        txttelefonoContacto.setText(telefonocontacto);

        boton_gestion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
                toolbar.setTitle(tipdoc+" "+num_guia);

                Bundle args = new Bundle();
                args.putString("documento", num_guia);
                args.putString("cliente", cliente);
                args.putString("direccion", direccion);
                args.putString("tipodoc", tipdoc);
                args.putString("fechaCompromiso", fechaCompromiso);
                args.putString("horaCompromiso", horaCompromiso);
                args.putString("flete", flete);
                args.putString("direccionReferencia", direccionReferencia);
                args.putString("coordinador", coordinador);
                args.putString("telefonoCoordinador", telefonoCoordinador);
                args.putString("ruccliente", ruccliente);

                Fragment nuevoFragmento = new DetalleDocumentoFragment();
                nuevoFragmento.setArguments(args);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container2, nuevoFragmento);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        boton_informacion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });

        imgllamarcoordinador.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!telefonoCoordinador.equals("")) {
                    String dial = "tel:" + telefonoCoordinador;
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(dial)));
                }else {
                    Toast.makeText(getActivity(), "Numero de telefono invalido", Toast.LENGTH_SHORT).show();
                }
            }
        });

        imgllamarcontacto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!telefonocontacto.equals("")) {
                    String dial = "tel:" + telefonocontacto;
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(dial)));
                }else {
                    Toast.makeText(getActivity(), "Numero de telefono invalido", Toast.LENGTH_SHORT).show();
                }
            }
        });

        imgmsncoordinador.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!telefonoCoordinador.equals("")) {
                    Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + telefonoCoordinador));
                    smsIntent.putExtra("sms_body", "");
                    startActivity(smsIntent);
                }else {
                    Toast.makeText(getActivity(), "Numero de telefono invalido", Toast.LENGTH_SHORT).show();
                }
            }
        });

        imgmsncontacto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!telefonocontacto.equals("")) {
                    Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + telefonocontacto));
                    smsIntent.putExtra("sms_body", "");
                    startActivity(smsIntent);
                }else {
                    Toast.makeText(getActivity(), "Numero de telefono invalido", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}
