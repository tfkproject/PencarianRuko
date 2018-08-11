package ta.nanda.pencarianruko;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import ta.nanda.pencarianruko.util.Config;
import ta.nanda.pencarianruko.util.Request;
import ta.nanda.pencarianruko.util.SessionManager;

public class InputRukoActivity extends AppCompatActivity {

    ImageView imgRuko;
    TextView btnPilKec, edtJdl, edtHarga, edtUkrn, edtBgnn, edtTnh, edtJumKmr, edtKmrMndi, edtListrik, edtSrtfkt, txtLokasi;
    Button btnInput, btnLokasi;
    private static final int RESULT_SELECT_IMAGE = 1;
    public String timestamp, lat, lon;
    ProgressDialog pDialog;
    private static final String TAG = InputRukoActivity.class.getSimpleName();
    private static String url = Config.HOST+"tambah_ruko.php";
    private SessionManager session;
    int PLACE_PICKER_REQUEST    =   2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_ruko);

        session = new SessionManager(getApplicationContext());
        //ambil data user
        HashMap<String, String> user = session.getUserDetails();
        final String id_user = user.get(SessionManager.KEY_ID_USER);

        /*String id_ruko = getIntent().getStringExtra("key_id_ruko");
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
        final String longitude = getIntent().getStringExtra("key_lon");*/

        getSupportActionBar().setTitle("Input Ruko");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imgRuko = (ImageView) findViewById(R.id.img_ruko);

        btnPilKec = (TextView) findViewById(R.id.btn_kecamatan);
        edtJdl = (TextView) findViewById(R.id.edt_judul);
        edtHarga = (TextView) findViewById(R.id.edt_hrga);
        edtUkrn = (TextView) findViewById(R.id.edt_ukrn);
        edtBgnn = (TextView) findViewById(R.id.edt_l_bgnn);
        edtTnh = (TextView) findViewById(R.id.edt_l_tnh);
        edtJumKmr = (TextView) findViewById(R.id.edt_jum_kmr);
        edtKmrMndi = (TextView) findViewById(R.id.edt_kmr_mndi);
        edtListrik = (TextView) findViewById(R.id.edt_dy_lstrk);
        edtSrtfkt = (TextView) findViewById(R.id.edt_srtfkt);

        txtLokasi = (TextView) findViewById(R.id.txt_lokasi);


        btnPilKec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InputRukoActivity.this, PilihArea.class);
                startActivity(intent);
            }
        });

        imgRuko.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pilihGambar();
            }
        });

        btnLokasi = (Button) findViewById(R.id.btn_lokasi);
        btnLokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder   =   new PlacePicker.IntentBuilder();
                Intent intent;
                try {
                    intent  =   builder.build(InputRukoActivity.this);
                    startActivityForResult(intent,PLACE_PICKER_REQUEST );
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        btnInput = (Button) findViewById(R.id.btn_input);
        btnInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get image in bitmap format
                Bitmap image = ((GlideBitmapDrawable) imgRuko.getDrawable()).getBitmap();
                //execute the async task and upload the image to server

                if(imgRuko.getDrawable() != null){
                    SharedPreferences bb = getSharedPreferences("my_prefs", 0);
                    String id_kec = bb.getString("ID_KEC", "");

                    String judul = edtJdl.getText().toString();
                    String harga = edtHarga.getText().toString();
                    String ukuran = edtUkrn.getText().toString();
                    String l_bangunan = edtBgnn.getText().toString();
                    String l_tanah = edtTnh.getText().toString();
                    String jum_kamar = edtJumKmr.getText().toString();
                    String kamar_mandi = edtKmrMndi.getText().toString();
                    String daya_listrik = edtListrik.getText().toString();
                    String sertifikat = edtSrtfkt.getText().toString();

                    new Upload(
                            image,
                            "IMG_"+timestamp,
                            id_kec,
                            id_user,
                            judul,
                            harga,
                            ukuran,
                            l_bangunan,
                            l_tanah,
                            jum_kamar,
                            kamar_mandi,
                            daya_listrik,
                            sertifikat,
                            lat,
                            lon
                    ).execute();

                }
                else {
                    Toast.makeText(getApplicationContext(),"Foto perlu ditambahkan",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void pilihGambar() {
        //open album untuk pilih image
        Intent gallaryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallaryIntent, RESULT_SELECT_IMAGE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_SELECT_IMAGE && resultCode == RESULT_OK && data != null){
            //set the selected image to image variable
            Uri image = data.getData();
            Glide.with(getApplicationContext()).load(image).into(imgRuko);
            //imgView.setImageURI(image);

            //get the current timeStamp and strore that in the time Variable
            Long tsLong = System.currentTimeMillis() / 1000;
            timestamp = tsLong.toString();

            Toast.makeText(getApplicationContext(), timestamp, Toast.LENGTH_SHORT).show();

        }

        if( requestCode == PLACE_PICKER_REQUEST)
        {
            if(resultCode == RESULT_OK)
            {
                Place place =   PlacePicker.getPlace(data,InputRukoActivity.this);
                Double latitude = place.getLatLng().latitude;
                Double longitude = place.getLatLng().longitude;
                lat = String.valueOf(latitude);
                lon = String.valueOf(longitude);
                String address = "Lat: "+String.valueOf(latitude)+"\nLon: "+String.valueOf(longitude);
                txtLokasi.setText(address);
            }
        }
    }

    private class Upload extends AsyncTask<Void,Void,String> {
        private Bitmap image;
        private String file_name;
        private String id_kec;
        private String id_pemilik;
        private String judul;
        private String harga;
        private String ukuran;
        private String l_bangunan;
        private String l_tanah;
        private String jum_kamar;
        private String kamar_mandi;
        private String daya_listrik;
        private String sertifikat;
        private String latitude;
        private String longitude;

        private String psn = "";

        public Upload(
                Bitmap image,
                String file_name,
                String id_kec,
                String id_pemilik,
                String judul,
                String harga,
                String ukuran,
                String l_bangunan,
                String l_tanah,
                String jum_kamar,
                String kamar_mandi,
                String daya_listrik,
                String sertifikat,
                String latitude,
                String longitude
                ){
            this.image = image;
            this.file_name = file_name;
            this.id_kec = id_kec;
            this.id_pemilik = id_pemilik;
            this.judul = judul;
            this.harga = harga;
            this.ukuran = ukuran;
            this.l_bangunan = l_bangunan;
            this.l_tanah = l_tanah;
            this.jum_kamar = jum_kamar;
            this.kamar_mandi = kamar_mandi;
            this.daya_listrik = daya_listrik;
            this.sertifikat = sertifikat;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(InputRukoActivity.this);
            pDialog.setMessage("Sedang upload..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            //kompress image ke format jpg
            image.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
            /*
            * encode image ke base64 agar bisa di ambil/dibaca nanti pada file php
            * */
            final String encodeImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

            try {
                //menganbil data-data yang akan dikirim

                //generate hashMap to store encodedImage and the name
                HashMap<String,String> detail = new HashMap<>();
                detail.put("image", encodeImage);
                detail.put("gambar", file_name);
                detail.put("id_kec", id_kec);
                detail.put("id_pemilik", id_pemilik);
                detail.put("judul", judul);
                detail.put("harga", harga);
                detail.put("ukuran", ukuran);
                detail.put("l_bangunan", l_bangunan);
                detail.put("l_tanah", l_tanah);
                detail.put("jum_kamar", jum_kamar);
                detail.put("kamar_mandi", kamar_mandi);
                detail.put("daya_listrik", daya_listrik);
                detail.put("sertifikat", sertifikat);
                detail.put("lat", latitude);
                detail.put("lon", longitude);

                try{
                    //convert this HashMap to encodedUrl to send to php file
                    String dataToSend = hashMapToUrl(detail);
                    //make a Http request and send data to saveImage.php file
                    String response = Request.post(url,dataToSend);

                    //dapatkan respon
                    Log.e("Hasil upload", response);

                    JSONObject ob = new JSONObject(response);
                    psn = ob.getString("message");

                    // return response;

                }catch (JSONException e){
                    e.printStackTrace();
                    Log.e(TAG, "ERROR  " + e);
                    Toast.makeText(getApplicationContext(),"Maaf, Gagal mengupload",Toast.LENGTH_SHORT).show();
                    //return null;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }



        @Override
        protected void onPostExecute(String s) {
            pDialog.dismiss();
            Toast.makeText(getApplicationContext(), psn,Toast.LENGTH_SHORT).show();
            //terus tutup activity ini
            finish();
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

    public static final int MY_PERMISSIONS_REQUEST = 99;
    public boolean checkStoragePermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {

                        //do something
                        //pilihGambar();
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //ambil data dari memori
        SharedPreferences bb = getSharedPreferences("my_prefs", 0);
        String id_kec = bb.getString("ID_KEC", "");
        String nm_kec = bb.getString("NM_KEC", "");
        String idkec;
        if (id_kec != null){
            idkec = id_kec;
            btnPilKec.setText(nm_kec);
        }
        else{
            idkec = "";
            btnPilKec.setText("Tap untuk pilih kecamatan");
        }
    }
}
