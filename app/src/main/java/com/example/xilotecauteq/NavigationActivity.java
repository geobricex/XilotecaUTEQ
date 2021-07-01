package com.example.xilotecauteq;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.hdodenhof.circleimageview.CircleImageView;

public class NavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerlayout;
    NavigationView navigationview;
    Toolbar toolbar;
    ActionBarDrawerToggle actionbardrawertoggle;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    CircleImageView imgUser;
    TextView txtUsuario;
    TextView lblcerrar;
    View view;
    MenuItem menuItem;

    JsonElement nombre;
    JsonElement apellido;
    JsonElement email;
    JsonElement imagen;
    JsonElement rol;

    String jsonDataUser = "{}";
    String URLStorage = "https://bioforest.uteq.edu.ec//bioforestStorage/bio_users/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("Logs", "START NAVIGATION");

        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_navigation);

        Bundle bundle = this.getIntent().getExtras();
        jsonDataUser = (bundle.getString("Session"));
        if (!jsonDataUser.equals("")) {
            Log.i("Logs", jsonDataUser);

            initdata();

            initialize();

            loaddata();

            OnClickListener();
        } else {
            Toast.makeText(NavigationActivity.this, "NO HA INICIADO SESIÓN", Toast.LENGTH_LONG).show();
            killSession();
        }


    }

    public void initdata() {
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(jsonDataUser).getAsJsonObject();
        if (jsonObject.size() > 0) {
            nombre = jsonObject.get("nombre");
            apellido = jsonObject.get("apellido");
            email = jsonObject.get("email");
            imagen = jsonObject.get("imagen");
            rol = jsonObject.get("rol");

        } else {
            Toast.makeText(NavigationActivity.this, "ERROR", Toast.LENGTH_LONG).show();

        }
    }

    public void initialize() {
        drawerlayout = findViewById(R.id.drawerlayout);
        navigationview = findViewById(R.id.navigationview);
        toolbar = findViewById(R.id.toolbar);
        navigationview.setNavigationItemSelectedListener(this);

        view = navigationview.getHeaderView(0);
        txtUsuario = view.findViewById(R.id.txtUsuario);
        imgUser = view.findViewById(R.id.imgUser);
        lblcerrar = view.findViewById(R.id.lblcerrar);

        navigationview.setItemIconTintList(null);
    }

    public void loaddata() {
        txtUsuario.setText(nombre.getAsString() + " " + apellido.getAsString());
        Glide.with(this).load(URLStorage + imagen.getAsString()).centerCrop().into(imgUser);

        actionbardrawertoggle = new ActionBarDrawerToggle(this, drawerlayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawerlayout.addDrawerListener(actionbardrawertoggle);
        actionbardrawertoggle.setDrawerIndicatorEnabled(true);
        actionbardrawertoggle.syncState();

        if (!rol.getAsString().equals("R")) {
            Menu menu = navigationview.getMenu();
            MenuItem item = menu.getItem(1);
            item.setVisible(false);
        }

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.framelayout, new MainFragment());
        fragmentTransaction.commit();

    }

    private void OnClickListener() {
        lblcerrar.setOnClickListener(v -> {
            killSession();
        });
    }

    public void killSession() {
        Intent intent = new Intent(NavigationActivity.this, MainActivity.class);
        Bundle b = new Bundle();
        b.putString("Session", "");
        intent.putExtras(b);
        startActivity(intent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        drawerlayout.closeDrawer(GravityCompat.START);
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        if (menuItem.getItemId() == R.id.insertar) {
            fragmentTransaction.replace(R.id.framelayout, new InsertFragment());
            fragmentTransaction.commit();
        } else if (menuItem.getItemId() == R.id.buscador) {
            fragmentTransaction.replace(R.id.framelayout, new BuscarFragment());
            fragmentTransaction.commit();
        }else{
            fragmentTransaction.replace(R.id.framelayout, new MainFragment());
            fragmentTransaction.commit();
        }

        return true;
    }
}