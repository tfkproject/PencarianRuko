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

public class InputSekitaranRukoActivity extends AppCompatActivity {

    ImageView imgRuko;
    TextView edtTempat, txtLokasi;
    Button btnInput, btnLokasi;
    private static final int RESULT_SELECT_IMAGE = 1;
    public String timestamp, lat, lon;
    ProgressDialog pDialog;
    private static final String TAG = InputSekitaranRukoActivity.class.getSimpleName();
    private static String url = Config.HOST+"input_sekitaran.php";
    private SessionManager session;
    int PLACE_PICKER_REQUEST    =   2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_ruko_sekitaran);

        session = new SessionManager(getApplicationContext());
        //ambil data user
        HashMap<String, String> user = session.getUserDetails();
        final String id_user = user.get(SessionManager.KEY_ID_USER);

        getSupportActionBar().setTitle("Input Lokasi Sekitar Ruko");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edtTempat = (TextView) findViewById(R.id.edt_tempat);

        txtLokasi = (TextView) findViewById(R.id.txt_lokasi);

        btnLokasi = (Button) findViewById(R.id.btn_lokasi);
        btnLokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder   =   new PlacePicker.IntentBuilder();
                Intent intent;
                try {
                    intent  =   builder.build(InputSekitaranRukoActivity.this);
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

                if(edtTempat.getText().toString().length() > 0){
                    String id_ruko = getIntent().getStringExtra("key_id_ruko");

                    String tempat = edtTempat.getText().toString();
                    new Upload(id_ruko, tempat, lat, lon).execute();

                }
                else {
                    Toast.makeText(getApplicationContext(),"Field tidak boleh kosong!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( requestCode == PLACE_PICKER_REQUEST)
        {
            if(resultCode == RESULT_OK)
            {
                Place place =   PlacePicker.getPlace(data,InputSekitaranRukoActivity.this);
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
        private String id_ruko;
        private String tempat;
        private String latitude;
        private String longitude;

        private int scs = 0;
        private String psn = "";

        public Upload(
                String id_ruko,
                String tempat,
                String latitude,
                String longitude
                ){
            this.id_ruko = id_ruko;
            this.tempat = tempat;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(InputSekitaranRukoActivity.this);
            pDialog.setMessage("Sedang menambahkan..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                //menganbil data-data yang akan dikirim

                //generate hashMap to store encodedImage and the name
                HashMap<String,String> detail = new HashMap<>();
                detail.put("id_ruko", id_ruko);
                detail.put("nama_tempat", tempat);
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
                    scs = ob.getInt("success");

                    if (scs == 1) {
                        psn = ob.getString("message");
                    } else {
                        // no data found
                        psn = ob.getString("message");

                    }

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
            if(scs == 0){
                Toast.makeText(InputSekitaranRukoActivity.this, ""+psn, Toast.LENGTH_SHORT).show();
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

}
