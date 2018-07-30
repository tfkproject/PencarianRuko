package ta.nanda.pencarianruko.fragment;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import ta.nanda.pencarianruko.adapter.RukoFotoAdapter;
import ta.nanda.pencarianruko.model.ItemRuko;
import ta.nanda.pencarianruko.util.Config;
import ta.nanda.pencarianruko.util.Request;

/**
 * Created by taufik on 21/05/18.
 */

public class Foto extends Fragment {

    private RecyclerView rc;
    private List<ItemRuko> itemList;
    private RukoFotoAdapter adapter;
    private String url = Config.HOST+"list_ruko.php";

    private ProgressDialog pDialog;


    public Foto() {
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
        View view = inflater.inflate(R.layout.foto, container, false);

        rc = (RecyclerView) view.findViewById(R.id.recycler_view);
        itemList = new ArrayList<>();

        //isi disini
        //

        adapter = new RukoFotoAdapter(getActivity(), itemList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rc.setLayoutManager(mLayoutManager);
        rc.setAdapter(adapter);

        return view;
    }

    private class getData extends AsyncTask<Void,Void,String> {

        //variabel untuk tangkap data
        private int scs = 0;

        private String id_kec;

        public getData(String id_kec){
            this.id_kec = id_kec;
        }

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
                detail.put("kec", id_kec);

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

                            itemList.add(new ItemRuko(
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
            adapter.notifyDataSetChanged();
            pDialog.dismiss();

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
    public void onResume() {
        super.onResume();
        itemList.clear();
        //ambil data dari memori
        SharedPreferences bb = getActivity().getSharedPreferences("my_prefs", 0);
        String id_kec = bb.getString("ID_KEC", "");
        String idkec;
        if (id_kec != null){
            idkec = id_kec;
        }
        else{
            idkec = "";
        }
        new getData(idkec).execute();
    }
}
