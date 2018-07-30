package ta.nanda.pencarianruko.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
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

import ta.nanda.pencarianruko.R;
import ta.nanda.pencarianruko.RukoDetail;
import ta.nanda.pencarianruko.adapter.RukoAdapter;
import ta.nanda.pencarianruko.model.ItemRuko;
import ta.nanda.pencarianruko.util.Config;
import ta.nanda.pencarianruko.util.Request;

/**
 * Created by taufik on 21/05/18.
 */

public class Peta extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    private List<ItemRuko> items;
    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;
    private static String url = Config.HOST+"cari_ruko.php";
    private ProgressDialog pDialog;
    String keyword = "";
    Location lastLocation;
    Marker currLocationMarker;
    LatLng lokasi_asal;

    private static final String TAG = "Lokasi";

    public Peta() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.peta, container, false);

        //dapatkan  izin untuk melakukan thread policy (proses Background AsycnTask)
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        items = new ArrayList<>();

        final SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view;
    }

    private synchronized void buildGoogleApiClient() {
        Log.d(TAG, "mulai deteksi lokasi");
        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        new dapatkanData().execute();

        /*marker("0.485638", "101.402023", "http://203.153.21.11/app/lapor-sampah/images/IMG_1530604447.jpg", "Banyak sampah", "Jl, ballaasdf");
        marker("0.519283", "101.444595", "http://203.153.21.11/app/lapor-sampah/images/IMG_1530604447.jpg", "Ada sampah", "Jl, ballaasdf");
        marker("0.414229", "101.420562", "http://203.153.21.11/app/lapor-sampah/images/IMG_1530604447.jpg", "Sampah semua", "Jl, ballaasdf");*/

        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(1.4730212,102.147085))); //area pekanbaru
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String mId = marker.getId();
                String markId = mId.replaceAll("\\D+","");
                int markerId = Integer.valueOf(markId);

                for(int position = 0 ; position < items.size() ; position++) {
                    if(markerId == position){
                        Intent intent = new Intent(getActivity(), RukoDetail.class);
                        intent.putExtra("key_id_ruko", items.get(position).getId());
                        intent.putExtra("key_nama_kec", items.get(position).getKec());
                        intent.putExtra("key_judul", items.get(position).getJudul());
                        intent.putExtra("key_gambar", items.get(position).getUrl_gambar());
                        intent.putExtra("key_harga", items.get(position).getHarga());
                        intent.putExtra("key_ukuran", items.get(position).getUkuran());
                        intent.putExtra("key_l_bangunan", items.get(position).getL_bangunan());
                        intent.putExtra("key_l_tanah", items.get(position).getL_tanah());
                        intent.putExtra("key_jum_kamar", items.get(position).getJum_kamar());
                        intent.putExtra("key_kamar_mandi", items.get(position).getKamar_mandi());
                        intent.putExtra("key_daya_listrik", items.get(position).getDaya_listrik());
                        intent.putExtra("key_sertifikat", items.get(position).getSertifikat());
                        intent.putExtra("key_no_hp", items.get(position).getNo_hp());
                        intent.putExtra("key_lat", items.get(position).getLatitude());
                        intent.putExtra("key_lon", items.get(position).getLongitude());
                        startActivity(intent);
                    }

                }

            }
        });

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(getActivity());
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(getActivity(), result,
                        0).show();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected");

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000); // milliseconds
        locationRequest.setFastestInterval(1000); // the fastest rate in milliseconds at which your app can handle location updates
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(getActivity(), "Maaf, koneksi terganggu.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (currLocationMarker != null) {
            currLocationMarker.remove();
        }

        lastLocation = location;

        //ambil lokasi tengah (center map)
        //String lokasi_lat = "1.663787"; //getIntent().getStringExtra("key_lokasi_LAT_maps");
        //String lokasi_long = "101.447081"; //getIntent().getStringExtra("key_lokasi_LONG_maps");

        double lat1, lon1, lat2, lon2;
        lat1 = location.getLatitude(); //Double.valueOf(lokasi_lat);
        lon1 = location.getLongitude(); //Double.valueOf(lokasi_long);

        lat2 = location.getLatitude();
        lon2 = location.getLongitude();

        lokasi_asal = new LatLng(lat2, lon2);

        double dLon = Math.toRadians(lon2 - lon1);

        //convert to radians
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        lon1 = Math.toRadians(lon1);

        double Bx = Math.cos(lat2) * Math.cos(dLon);
        double By = Math.cos(lat2) * Math.sin(dLon);
        double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
        double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);

        LatLng lokasi_asal_tengah = new LatLng(Math.toDegrees(lat3), Math.toDegrees(lon3));


        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(lokasi_asal_tengah));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        //build_retrofit_and_get_response_nearby("restaurant");

        //stop location updates
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed");

        stopLocationUpdates();
    }


    private class dapatkanData extends AsyncTask<Void,Void,String> {

        //variabel untuk tangkap data
        private int scs = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Memuat data...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(Void... params) {
            try{
                //susun parameter
                HashMap<String,String> detail = new HashMap<>();

                try {
                    //convert this HashMap to encodedUrl to send to php file
                    String dataToSend = hashMapToUrl(detail);
                    //make a Http request and send data to php file
                    String response = Request.post(url+"?q="+keyword,dataToSend);

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

                            items.add(new ItemRuko(
                                    id,
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
                                    longitude));
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
                createMarker(items.get(i).getLatitude(), items.get(i).getLongitude(), items.get(i).getUrl_gambar(), items.get(i).getJudul(), items.get(i).getKec());

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

    protected Marker createMarker(final String lok_lat, final String lok_long, final String link_img, final String nama_objek, final String lokasi_objek) {

        double lat = Double.valueOf(lok_lat);
        double lng = Double.valueOf(lok_long);

        return mMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lng))
                .title(nama_objek)
                .snippet(lokasi_objek)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
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
