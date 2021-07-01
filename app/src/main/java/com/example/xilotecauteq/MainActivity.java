package com.example.xilotecauteq;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {
    Button btninicio;
    ImageButton imgbtnfacebook;
    ImageButton imgbtngoogle;
    TextView txtemail;
    TextView txtpass;
    TextView lblregistar;

    RequestQueue requestQueue;

    String URL = "https://bioforest.uteq.edu.ec/bioforestserver/webresources/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("Logs", "STARTING THE APPLICATION");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestQueue = Volley.newRequestQueue(this);//Initialize the RequestQueue object with Google Volley

        initialize();//Method that initializes objects in declared variables
        OnClickListener();

    }

    private void initialize() {

        btninicio = findViewById(R.id.btninicio);
        imgbtnfacebook = findViewById(R.id.imgbtnfacebook);
        imgbtngoogle = findViewById(R.id.imgbtngoogle);
        txtemail = findViewById(R.id.txtemail);
        txtpass = findViewById(R.id.txtpass);
        lblregistar = findViewById(R.id.lblregistar);

        cleanData();
    }

    public void cleanData(){
        txtemail.setText("");
        txtpass.setText("");
    }

    private void OnClickListener() {
        btninicio.setOnClickListener(v -> {
            String jsonLogin = "{\n" +
                    "    \"email\": \"" + txtemail.getText().toString() + "\",\n" +
                    "    \"contrasenia\": \"" + txtpass.getText().toString() + "\"\n" +
                    "}";
            Log.i("Logs", jsonLogin);
            stringRequestVolley(jsonLogin);
        });
        lblregistar.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Opción no disponible por el momento", Toast.LENGTH_LONG).show();
        });
        imgbtnfacebook.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Opción de inicio con Facebook no disponible por el momento", Toast.LENGTH_LONG).show();
        });
        imgbtngoogle.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Opción de inicio con Google no disponible por el momento", Toast.LENGTH_LONG).show();
        });
    }

    /**
     * Method that returns a string from
     * the web services response using a Google Volley
     */
    private void stringRequestVolley(String jsondataLogin) {
        StringRequest request = new StringRequest(Request.Method.POST, URL + "personaapis/logIn", new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Logs", response);

                JsonParser jsonParser = new JsonParser();
                JsonObject jsonObject = jsonParser.parse(response).getAsJsonObject();

                if (jsonObject.size() > 0) {
                    JsonElement jsonElement = jsonObject.get("status");
                    if (jsonElement.getAsInt() == 2) {
                        JsonArray jsonArray = jsonObject.get("data").getAsJsonArray();
                        try {
                            JsonObject jsonObjectUser = jsonArray.get(0).getAsJsonObject();

                            Intent intent = new Intent(MainActivity.this, NavigationActivity.class);
                            Bundle b = new Bundle();
                            b.putString("Session", jsonObjectUser.toString());
                            intent.putExtras(b);
                            startActivity(intent);

                        } catch (Exception e) {
//                            Toast.makeText(MainActivity.this, "ERROR", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "USUARIO O CONTRASEÑA INCORRECTO", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "ERROR", Toast.LENGTH_LONG).show();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "ERROR", Toast.LENGTH_LONG).show();
            }
        }
        ) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return jsondataLogin == null ? "{}".getBytes("utf-8") : jsondataLogin.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    Toast.makeText(MainActivity.this, "ERROR", Toast.LENGTH_LONG).show();
                    return null;
                }
            }
        };
        requestQueue.add(request);
    }

}