package ta.nanda.pencarianruko;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import ta.nanda.pencarianruko.adapter.RukoPemilikAdapter;
import ta.nanda.pencarianruko.util.Config;
import ta.nanda.pencarianruko.util.Request;

public class RukoPemilikDetail extends AppCompatActivity {

    ImageView imgRuko;
    TextView txtJdl, txtHarga, txtUkrn, txtBgnn, txtTnh, txtJumKmr, txtKmrMndi, txtListrik, txtSrtfkt;
    Button btnSekitar, btnLihatSktr, btnHapus;

    private String url = Config.HOST+"hapus_ruko.php";
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ruko_pemilik_detail);

        final String id_ruko = getIntent().getStringExtra("key_id_ruko");
        String nama_kec = getIntent().getStringExtra("key_nama_kec");
        final String judul = getIntent().getStringExtra("key_judul");
        String gambar = getIntent().getStringExtra("key_gambar");
        String harga = getIntent().getStringExtra("key_harga");
        String ukuran = getIntent().getStringExtra("key_ukuran");
        String l_bangunan = getIntent().getStringExtra("key_l_bangunan");
        String l_tanah = getIntent().getStringExtra("key_l_tanah");
        String jum_kamar = getIntent().getStringExtra("key_jum_kamar");
        String kamar_mandi = getIntent().getStringExtra("key_kamar_mandi");
        String daya_listrik = getIntent().getStringExtra("key_daya_listrik");
        String sertifikat = getIntent().getStringExtra("key_sertifikat");
        final String no_hp = getIntent().getStringExtra("key_no_hp");
        final String latitude = getIntent().getStringExtra("key_lat");
        final String longitude = getIntent().getStringExtra("key_lon");

        getSupportActionBar().setTitle("Rincian Ruko");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imgRuko = (ImageView) findViewById(R.id.img_ruko);
        txtJdl = (TextView) findViewById(R.id.txt_jdl);
        txtHarga = (TextView) findViewById(R.id.txt_hrga);
        txtUkrn = (TextView) findViewById(R.id.txt_ukrn);
        txtBgnn = (TextView) findViewById(R.id.txt_l_bgnn);
        txtTnh = (TextView) findViewById(R.id.txt_l_tnh);
        txtJumKmr = (TextView) findViewById(R.id.txt_jum_kmr);
        txtKmrMndi = (TextView) findViewById(R.id.txt_kmr_mndi);
        txtListrik = (TextView) findViewById(R.id.txt_dy_lstrk);
        txtSrtfkt = (TextView) findViewById(R.id.txt_srtfkt);

        //set
        Glide.with(RukoPemilikDetail.this).load(gambar).into(imgRuko);
        txtJdl.setText(judul);
        txtHarga.setText(harga);
        txtUkrn.setText(ukuran+" m");
        txtBgnn.setText(l_bangunan+" m");
        txtTnh.setText(l_tanah+" m");
        txtJumKmr.setText(jum_kamar);
        txtKmrMndi.setText(kamar_mandi);
        txtListrik.setText(daya_listrik+" VA");
        txtSrtfkt.setText(sertifikat);

        btnSekitar = (Button) findViewById(R.id.btn_sekitar);
        btnLihatSktr = (Button) findViewById(R.id.btn_lht_sktr);
        btnHapus = (Button) findViewById(R.id.btn_hapus);

        btnSekitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RukoPemilikDetail.this, InputSekitaranRukoActivity.class);
                intent.putExtra("key_id_ruko", id_ruko);
                startActivity(intent);
            }
        });

        btnLihatSktr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RukoPemilikDetail.this, SekitaranActivity.class);
                intent.putExtra("key_id_ruko", id_ruko);
                intent.putExtra("key_judul_ruko", judul);
                intent.putExtra("key_lat_ruko", latitude);
                intent.putExtra("key_lon_ruko", longitude);
                startActivity(intent);
            }
        });

        btnHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RukoPemilikDetail.this);
                builder.setMessage("Yakin ingin menghapus data?");
                builder.setCancelable(false);

                builder.setPositiveButton(
                        "Ya",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                new hapusRuko(id_ruko).execute();
                            }
                        });

                builder.setNegativeButton(
                        "Tidak",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class hapusRuko extends AsyncTask<Void,Void,String> {

        //variabel untuk tangkap data
        private int scs = 0;
        private String psn, id_ruko;

        public hapusRuko(String id_ruko){
            this.id_ruko = id_ruko;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RukoPemilikDetail.this);
            pDialog.setMessage("Loading...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(Void... params) {

            try{
                //susun parameter
                HashMap<String,String> detail = new HashMap<>();
                detail.put("id_ruko", id_ruko);

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
                        psn = ob.getString("message");
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
            pDialog.dismiss();

            if(scs == 0){
                Toast.makeText(RukoPemilikDetail.this, ""+psn, Toast.LENGTH_SHORT).show();
            }else{
                //tutup activity ini
                finish();
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

}
