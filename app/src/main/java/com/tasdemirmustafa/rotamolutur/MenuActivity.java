package com.tasdemirmustafa.rotamolutur;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener{



    Button harita,onerilenler,listelerim,onerilenrotalar,puanver;
    String TAG = "MenuActivityLOG: ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);



        harita = (Button) findViewById(R.id.haritaAc);
        onerilenler = (Button) findViewById(R.id.onerdigimYerler);
        listelerim = (Button) findViewById(R.id.kayitliListelerim);
        onerilenrotalar = (Button) findViewById(R.id.onerilenRotalar);
        puanver = (Button) findViewById(R.id.puanVer);



        harita.setOnClickListener(this);
        onerilenler.setOnClickListener(this);
        listelerim.setOnClickListener(this);
        onerilenrotalar.setOnClickListener(this);
        puanver.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.haritaAc:
                Log.d(TAG,"haritaAc clicked");
                Intent intent = new Intent(this,MapsActivity.class);
                startActivity(intent);
                break;

            case R.id.onerdigimYerler:
                Log.d(TAG,"onerdigimYerler clicked");
                break;


            case R.id.kayitliListelerim:
                Log.d(TAG,"kayitliListelerim clicked");
                break;

            case R.id.onerilenRotalar:
                Log.d(TAG,"onerilenRotalar clicked");
                break;

            case R.id.puanVer:
                Log.d(TAG,"puanVer clicked");
                break;
        }
    }
}
