package com.itp.trackinn.Views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.itp.trackinn.Views.Fragments.DetalleDocumentoFragment;
import com.itp.trackinn.Views.Fragments.DetalleDocumentoInfoFragment;
import com.itp.trackinn.Views.Fragments.NuevoDocumentoFragment;
import com.itp.trackinn.Views.Fragments.ListaDocumentosFragment;
import com.trackiinn.apptrack.R;

public class MenuInferiorActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private ImageButton image;
    String documento = "";
    String cliente = "";
    String tipodoc = "";
    String numdoc = "";

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            FragmentManager manager = getSupportFragmentManager();

            Bundle args = new Bundle();
            args.putString("documento", documento);
            args.putString("cliente", cliente);
            args.putString("tipodoc", tipodoc);
            args.putString("numdoc", numdoc);

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    DetalleDocumentoFragment nuevoFragmento = new DetalleDocumentoFragment();
                    nuevoFragmento.setArguments(args);
                    manager.beginTransaction().replace(R.id.container2, nuevoFragmento).commit();
                    return true;
                case R.id.navigation_dashboard:
                    DetalleDocumentoInfoFragment nuevoFragmento1 = new DetalleDocumentoInfoFragment();
                    nuevoFragmento1.setArguments(args);
                    manager.beginTransaction().replace(R.id.container2, nuevoFragmento1).commit();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_inferior);

        mTextMessage = (TextView) findViewById(R.id.textView10);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        String documento = getIntent().getStringExtra("documento");
        String cliente = getIntent().getStringExtra("cliente");
        String tipodoc = getIntent().getStringExtra("tipodoc");
        String numdoc = getIntent().getStringExtra("numdoc");

        //mTextMessage.setText("holalaaa");

    }



}
