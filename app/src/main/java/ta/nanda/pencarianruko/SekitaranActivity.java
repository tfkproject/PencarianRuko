package ta.nanda.pencarianruko;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ta.nanda.pencarianruko.model.ItemSekitaran;
import ta.nanda.pencarianruko.util.Config;
import ta.nanda.pencarianruko.util.Request;

public class SekitaranActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ProgressDialog pDialog;
    List<ItemSekitaran> items;
    private static String url = Config.HOST+"lihat_sekitaran.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sekitaran);

        getSupportActionBar().setTitle("Sekitaran Ruko");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String id_ruko = getIntent().getStringExtra("key_id_ruko");

        //dapatkan  izin untuk melakukan thread policy (proses Background AsycnTask)
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        items = new ArrayList<>();

        new dapatkanData(id_ruko).execute();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //new dapatkanData().execute();

        /*marker("0.485638", "101.402023", "http://203.153.21.11/app/lapor-sampah/images/IMG_1530604447.jpg", "Banyak sampah", "Jl, ballaasdf");
        marker("0.519283", "101.444595", "http://203.153.21.11/app/lapor-sampah/images/IMG_1530604447.jpg", "Ada sampah", "Jl, ballaasdf");
        marker("0.414229", "101.420562", "http://203.153.21.11/app/lapor-sampah/images/IMG_1530604447.jpg", "Sampah semua", "Jl, ballaasdf");*/

        String lat = getIntent().getStringExtra("key_lat_ruko");
        String lng = getIntent().getStringExtra("key_lon_ruko");
        String nm_ruko = getIntent().getStringExtra("key_judul_ruko");

        //mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(1.4730212,102.147085))); //area pekanbaru
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Double.valueOf(lat), Double.valueOf(lng)))); //area pekanbaru
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(Double.valueOf(lat), Double.valueOf(lng)))
                .title(nm_ruko)
                //.snippet(lokasi_objek)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))).showInfoWindow();

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String mId = marker.getId();
                String markId = mId.replaceAll("\\D+","");
                int markerId = Integer.valueOf(markId);

                /*for(int position = 0 ; position < items.size() ; position++) {
                    if(markerId == position){
                        Intent intent = new Intent(CariActivity.this, UkmDetail.class);
                        intent.putExtra("key_id", items.get(position).getId());
                        intent.putExtra("key_nama", items.get(position).getNama_ukm());
                        intent.putExtra("key_alamat", items.get(position).getAlamat());
                        intent.putExtra("key_hp", items.get(position).getNo_hp());
                        intent.putExtra("key_gambar", items.get(position).getUrl_gambar());
                        intent.putExtra("key_lat", items.get(position).getLat());
                        intent.putExtra("key_lon", items.get(position).getLon());
                        startActivity(intent);
                    }

                }*/

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private class dapatkanData extends AsyncTask<Void,Void,String> {

        //variabel untuk tangkap data
        private int scs = 0;
        private String id_ruko;

        public dapatkanData(String id_ruko){
            this.id_ruko = id_ruko;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SekitaranActivity.this);
            pDialog.setMessage("Memuat data...");
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
                    String response = Request.post(url, dataToSend);

                    //dapatkan respon
                    Log.e("Respon", response);

                    JSONObject ob = new JSONObject(response);
                    scs = ob.getInt("success");

                    if (scs == 1) {
                        JSONArray products = ob.getJSONArray("field");

                        for (int i = 0; i < products.length(); i++) {
                            JSONObject c = products.getJSONObject(i);

                            String id = c.getString("id_sekitaran");
                            String tempat = c.getString("nama_tempat");
                            String latitude = c.getString("lat");
                            String longitude = c.getString("lon");

                            items.add(new ItemSekitaran(id, tempat, latitude, longitude));
                        }
                    } else {
                        // no data found

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
            for(int i = 0 ; i < items.size() ; i++) {
                createMarker(items.get(i).getLat(), items.get(i).getLon(), items.get(i).getNama_tempat());

            }

        }

    }

    private String hashMapToUrl(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
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

    protected Marker createMarker(final String lok_lat, final String lok_long, final String nama_tempat) {

        double lat = Double.valueOf(lok_lat);
        double lng = Double.valueOf(lok_long);

        return mMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lng))
                .title(nama_tempat)
                //.snippet(lokasi_objek)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        /*MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(lat, lng));
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(final Marker marker) {
                View v = getLayoutInflater().inflate(R.layout.maps_info_window, null);

                ImageView img = v.findViewById(R.id.img);
                Glide.with(PetaActivity.this).load(link_img).asBitmap().override(250,250).listener(new RequestListener<String, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                        e.printStackTrace();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        if(!isFromMemoryCache) marker.showInfoWindow();
                        return false;
                    }
                }).into(img);

                TextView nama = v.findViewById(R.id.txt_nama);
                nama.setText(nama_objek);

                TextView jalan = v.findViewById(R.id.txt_jalan);
                jalan.setText("Lokasi: "+lokasi_objek);

                TextView lat = v.findViewById(R.id.txt_lat);
                lat.setText("Lat: "+lok_lat);

                TextView lon = v.findViewById(R.id.txt_long);
                lon.setText("Lon: "+lok_long);
                return  v;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }


        });
        Marker marker = mMap.addMarker(markerOptions);
        marker.showInfoWindow();

        return marker;*/
    }


}
