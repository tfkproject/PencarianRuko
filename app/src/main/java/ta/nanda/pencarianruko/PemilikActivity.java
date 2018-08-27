package ta.nanda.pencarianruko;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ta.nanda.pencarianruko.adapter.RukoAdapter;
import ta.nanda.pencarianruko.adapter.RukoPemilikAdapter;
import ta.nanda.pencarianruko.fragment.Foto;
import ta.nanda.pencarianruko.fragment.Peta;
import ta.nanda.pencarianruko.fragment.Ruko;
import ta.nanda.pencarianruko.model.ItemRuko;
import ta.nanda.pencarianruko.model.ItemRukoPemilik;
import ta.nanda.pencarianruko.util.Config;
import ta.nanda.pencarianruko.util.Request;
import ta.nanda.pencarianruko.util.SessionManager;

public class PemilikActivity extends AppCompatActivity {

    private RecyclerView rc;
    private List<ItemRukoPemilik> itemList;
    private RukoPemilikAdapter adapter;
    private String url = Config.HOST+"list_ruko_pemilik.php";

    private SessionManager session;

    private ProgressDialog pDialog;

    private boolean log_in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pemilik);

        getSupportActionBar().setTitle("Daftar Ruko Anda");

        session = new SessionManager(getApplicationContext());
        session.checkLogin();

        //kalau belum login
        if(!session.isLoggedIn()){
            log_in = false;
            finish();
        }
        //kalau sudah login
        else{
            log_in = true;
            //ambil data user
            HashMap<String, String> user = session.getUserDetails();
            String id_user = user.get(SessionManager.KEY_ID_USER);
            String nm_user = user.get(SessionManager.KEY_NM_USER);
            String jns_user = user.get(SessionManager.KEY_JNS_USER);


            rc = (RecyclerView) findViewById(R.id.recycler_view);
            itemList = new ArrayList<>();

            //new getData(id_user).execute();

            adapter = new RukoPemilikAdapter(PemilikActivity.this, itemList);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(PemilikActivity.this);
            rc.setLayoutManager(mLayoutManager);
            rc.setAdapter(adapter);

            FloatingActionButton fab = findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(PemilikActivity.this, InputRukoActivity.class);
                    startActivity(intent);
                }
            });
        }

    }

    private class getData extends AsyncTask<Void,Void,String> {

        //variabel untuk tangkap data
        private int scs = 0;

        private String id_pemilik, psn;

        public getData(String id_pemilik){
            this.id_pemilik = id_pemilik;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(PemilikActivity.this);
            pDialog.setMessage("Memuat data...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(Void... params) {

            try{
                //susun parameter
                HashMap<String,String> detail = new HashMap<>();
                detail.put("id_pemilik", id_pemilik);

                try {
                    //convert this HashMap to encodedUrl to send to php file
                    String dataToSend = hashMapToUrl(detail);
                    //make a Http request and send data to php file
                    String response = Request.post(url,dataToSend);

                    //dapatkan respon
                    Log.e("Respon", response);

                    JSONObject ob = new JSONObject(response);
                    scs = ob.getInt("success");

                    if (scs == 1) {
                        JSONArray products = ob.getJSONArray("field");

                        for (int i = 0; i < products.length(); i++) {
                            JSONObject c = products.getJSONObject(i);

                            // Storing each json item in variable
                            String id = c.getString("id_ruko");
                            String id_kec = c.getString("id_kec");
                            String nama_kec = c.getString("nama_kec");
                            String url_gambar = c.getString("gambar");
                            String judul = c.getString("judul");
                            String ukuran = c.getString("ukuran");
                            String sertifikat = c.getString("sertifikat");
                            String harga = c.getString("harga");
                            String l_bangunan = c.getString("l_bangunan");
                            String l_tanah = c.getString("l_tanah");
                            String jum_kamar = c.getString("jum_kamar");
                            String kamar_mandi = c.getString("kamar_mandi");
                            String daya_listrik = c.getString("daya_listrik");
                            String no_hp = c.getString("no_hp");
                            String latitude = c.getString("latitude");
                            String longitude = c.getString("longitude");
                            String status = c.getString("status_sewa");

                            itemList.add(new ItemRukoPemilik(
                                    id,
                                    id_kec,
                                    nama_kec,
                                    url_gambar,
                                    judul,
                                    ukuran,
                                    sertifikat,
                                    harga,
                                    l_bangunan,
                                    l_tanah,
                                    jum_kamar,
                                    kamar_mandi,
                                    daya_listrik,
                                    no_hp,
                                    latitude,
                                    longitude,
                                    status));

                        }
                    } else {
                        // no data found
                        psn = ob.getString("message");
                    }

                } catch (JSONException e){
                    e.printStackTrace();
                }

            } catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            adapter.notifyDataSetChanged();
            pDialog.dismiss();

            if(scs == 0){
                Toast.makeText(PemilikActivity.this, psn, Toast.LENGTH_SHORT).show();
            }
        }

    }

    private String hashMapToUrl(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pemilik, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.act_logout){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PemilikActivity.this);
            alertDialogBuilder.setMessage("Yakin ingin logout?");
            alertDialogBuilder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    session.logoutUser();
                    Intent intent = new Intent(PemilikActivity.this, LoginUser.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);

                    //terus tutup activity ini
                    finish();
                }
            });

            alertDialogBuilder.setNegativeButton("Tidak",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //..
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        itemList.clear();
        HashMap<String, String> user = session.getUserDetails();
        String id_user = user.get(SessionManager.KEY_ID_USER);
        new getData(id_user).execute();
    }
}
