package com.example.winteq;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.winteq.api.ApiClient;
import com.example.winteq.api.Api_Interface;
import com.example.winteq.model.asset.AssetData;
import com.example.winteq.model.wms.WmsData;
import com.example.winteq.model.wms.WmsResponseData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AssetManagementReplace extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    SharedPreferences sp;
    FloatingActionButton fab_send;
    Api_Interface apiInterface;
    TextView tv_asset_part, tv_line, tv_station, tv_machine_qty, tv_machine, tv_replace,
            tv_regis, tv_update, tv_machine_lifetime, tv_machine_category, enterc_id;
    TextView tv_item, tv_copro, tv_type, tv_category, tv_qty, tv_date, tv_tag, tv_lifetime, tv_desc;
    EditText et_enterc;
    ImageView itempiczg;
    AssetData assetData;

    private String xId, xCategory, xPart, xLine, xStation, xQty, xMachine, xLifetime, xRegister, xReplace, xUpdate;
    private List<WmsData> listGetWms;

    private static final String SHARE_PREF_NAME = "mypref";
    private static final String FULLNAME = "fullname";
    private static final String IMAGE = "image";
    private static final String LINE = "asset_line";
    private static final String STATION = "asset_station";
    private static final String MACHINE = "machine_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_asset_management_replace);

        sp = getSharedPreferences(SHARE_PREF_NAME, MODE_PRIVATE);

        apiInterface = ApiClient.getClient().create(Api_Interface.class);
        drawerLayout = findViewById(R.id.amreplace);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        et_enterc = findViewById(R.id.et_enterc);
        enterc_id = findViewById(R.id.enterc_id);

        Intent sendAP = getIntent();
        xId = sendAP.getStringExtra("xId");
        xCategory = sendAP.getStringExtra("xCategory");
        xPart = sendAP.getStringExtra("xPart");
        xLine = sendAP.getStringExtra("xLine");
        xStation = sendAP.getStringExtra("xStation");
        xQty = sendAP.getStringExtra("xQty");
        xMachine = sendAP.getStringExtra("xMachine");
        xLifetime = sendAP.getStringExtra("xLifetime");
        xRegister = sendAP.getStringExtra("xRegister");
        xReplace = sendAP.getStringExtra("xReplace");
        xUpdate = sendAP.getStringExtra("xUpdate");

        //machine part data id
        tv_asset_part = findViewById(R.id.itemnamewzh);
        tv_line = findViewById(R.id.tv7zh);
        tv_station = findViewById(R.id.stationright);
        tv_machine_qty = findViewById(R.id.itemqtyzh);
        tv_machine = findViewById(R.id.itemcoprozh);
        tv_replace = findViewById(R.id.datereplaceto);
        tv_regis = findViewById(R.id.dateregisterto);
        tv_machine_lifetime = findViewById(R.id.itemlifetimeh);
        tv_machine_category = findViewById(R.id.itemcatzh);
        tv_update = findViewById(R.id.lastdatereplace);

        //set machine part data
        enterc_id.setText(xId);
        tv_asset_part.setText(xPart);
        tv_line.setText(xLine);
        tv_station.setText(xStation);
        tv_machine_qty.setText(xQty);
        tv_machine.setText(xMachine);
        tv_replace.setText(xReplace);
        tv_regis.setText(xRegister);
        tv_machine_lifetime.setText(xLifetime);
        tv_machine_category.setText(xCategory);
        if(xUpdate != null) {
            tv_update.setText(xUpdate);
        }else{
            tv_update.setText("None");
        }

        viewData();
        Toast.makeText(AssetManagementReplace.this, xMachine, Toast.LENGTH_SHORT).show();

        //warehouse part data id
        tv_item = findViewById(R.id.itemnamewzg);
        tv_copro = findViewById(R.id.itemcoprozg);
        tv_type = findViewById(R.id.itemtypeg);
        tv_category = findViewById(R.id.itemcatzg);
        tv_qty = findViewById(R.id.itemqtyzg);
        tv_date = findViewById(R.id.itemdatezg);
        tv_tag = findViewById(R.id.itemtagzg);
        tv_lifetime = findViewById(R.id.itemlifetimeg);
        tv_desc = findViewById(R.id.itemdesczg);
        itempiczg = findViewById(R.id.itempiczg);


        fab_send = findViewById(R.id.fab_send);
        fab_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AssetManagementUpdate();
            }
        });

        View header = navigationView.getHeaderView(0);

        TextView nama = (TextView) header.findViewById(R.id.fname);

        navigationView.bringToFront();
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        nama.setText(sp.getString(FULLNAME, null));

        ImageView pfph = (ImageView) header.findViewById(R.id.pfph);

        String profileS = sp.getString(IMAGE, null);


        if(!(profileS.isEmpty())) {
            String imageUri = profileS;
            ImageView Image2 = pfph;
            Picasso.get().load(imageUri).into(Image2);
        }

        byte[] bytes = Base64.decode(profileS,Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        if (bitmap != null) {
            pfph.setImageBitmap(bitmap);
        }

    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(AssetManagementReplace.this, AssetManagementNotifPart.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.nav_home:
                Intent intent1 = new Intent(AssetManagementReplace.this, Dashboard.class);
                startActivity(intent1);
                break;

            case R.id.nav_profile:
                Intent intent2 = new Intent(AssetManagementReplace.this, Profile.class);
                startActivity(intent2);
                break;

            case R.id.nav_logout:
                sp.edit().putBoolean(SHARE_PREF_NAME, false).apply();
                SharedPreferences.Editor editor = sp.edit();
                editor.clear();
                editor.commit();
                Toast.makeText(AssetManagementReplace.this, "Log out successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AssetManagementReplace.this, Login.class);
                startActivity(intent);
                break;

            case R.id.nav_grafik:
                Intent intent3 = new Intent(AssetManagementReplace.this, Graph.class);
                startActivity(intent3);
                break;

            case R.id.nav_contact:
                Intent intent4 = new Intent(AssetManagementReplace.this, Contact.class);
                startActivity(intent4);
                break;

            case R.id.nav_help:
                Intent intent5 = new Intent(AssetManagementReplace.this, Help.class);
                startActivity(intent5);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void viewData(){
        Api_Interface aiData = ApiClient.getClient().create(Api_Interface.class);
        Call<WmsResponseData> getData = aiData.aiAssetWarehouseData(xPart);

        getData.enqueue(new Callback<WmsResponseData>() {
            @Override
            public void onResponse(Call<WmsResponseData> call, Response<WmsResponseData> response) {

                if(response.body() != null && response.body().isStatus()) {
                    boolean status = response.body().isStatus();
                    String message = response.body().getMessage();
                    listGetWms = response.body().getData();

                    tv_item.setText(listGetWms.get(0).getItem_name());
                    tv_copro.setText(listGetWms.get(0).getCopro());
                    tv_type.setText(listGetWms.get(0).getType());
                    tv_category.setText(listGetWms.get(0).getCategory());
                    tv_qty.setText(listGetWms.get(0).getQty());
                    tv_date.setText(listGetWms.get(0).getDate());
                    tv_tag.setText(listGetWms.get(0).getNo_tag());
                    tv_lifetime.setText(listGetWms.get(0).getLifetime_wms());
                    if(listGetWms.get(0).getDescription() != null) {
                        tv_desc.setText(listGetWms.get(0).getDescription());
                    }
                    if(listGetWms.get(0).getImage() != null) {
                        String imageUri = listGetWms.get(0).getImage();
                        ImageView Image2 = itempiczg;
                        Picasso.get().load(imageUri).into(Image2);
                    }

                }
            }
            @Override
            public void onFailure(Call<WmsResponseData> call, Throwable t) {
                Toast.makeText(AssetManagementReplace.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void AssetManagementUpdate() {
        apiInterface = ApiClient.getClient().create(Api_Interface.class);
        String asset_part = tv_asset_part.getText().toString();
        String asset_id = enterc_id.getText().toString();
        String copro = tv_copro.getText().toString();
        String asset_qty = tv_machine_qty.getText().toString();
        ProgressDialog pd = new ProgressDialog(AssetManagementReplace.this);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();

        Call<AssetData> amAddCall = apiInterface.aiAssetCoproData(asset_id, asset_part, copro, asset_qty);
        amAddCall.enqueue(new Callback<AssetData>() {
            @Override
            public void onResponse(Call<AssetData> call, Response<AssetData> response) {
                //sr untuk menampung array message dalam bentuk string
                //loop isi data dari array message lalu di append ke dalam string sr
                //if else untuk mencegah mengambil value awal dari string sr ("")
                String sr = "";
                for(int i=0 ; i<response.body().getMessage().length ; i++){
                    if(sr.length() == 0){
                        sr = response.body().getMessage()[i];
                    }else{
                        sr = sr + "\n" + response.body().getMessage()[i];
                    }
                }
                if(response.body() != null && response.body().isStatus()){
                    assetData = response.body();
                    pd.dismiss();

                    Toast.makeText(AssetManagementReplace.this, sr, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AssetManagementReplace.this, AssetManagementView.class);
                    startActivity(intent);
                    finish();
                } else {
                    pd.dismiss();
                    Toast.makeText(AssetManagementReplace.this, sr, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AssetData> call, Throwable t) {
                pd.dismiss();
                Toast.makeText(AssetManagementReplace.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}