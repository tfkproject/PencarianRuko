package ta.nanda.pencarianruko;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.widget.ExpandableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ta.nanda.pencarianruko.adapter.PilihAreaAdapter;
import ta.nanda.pencarianruko.model.ItemKec;
import ta.nanda.pencarianruko.model.ItemRuko;
import ta.nanda.pencarianruko.util.Config;
import ta.nanda.pencarianruko.util.Request;

public class PilihArea extends AppCompatActivity {

    private RecyclerView rc;
    private List<ItemKec> itemList;
    private PilihAreaAdapter adapter;
    private String url = Config.HOST+"list_area.php";
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pilih_area);

        getSupportActionBar().setTitle("Pilih Area");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rc = (RecyclerView) findViewById(R.id.recycler_view);
        itemList = new ArrayList<>();

        //isi disini
        new getData().execute();

        adapter = new PilihAreaAdapter(PilihArea.this, itemList, new PilihAreaAdapter.AdapterListener() {
            @Override
            public void onSelected(int position, String id_kec) {
                //simpan id_ortu ke memori
                SharedPreferences prefs = getSharedPreferences("my_prefs", MODE_PRIVATE);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("ID_KEC", id_kec );
                edit.apply();
                finish();
            }
        });
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(PilihArea.this);
        rc.setLayoutManager(mLayoutManager);
        rc.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pilih, menu);
        return true;
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
        if (id == R.id.act_no_pilih){
            //simpan id_ortu ke memori
            SharedPreferences prefs = getSharedPreferences("my_prefs", MODE_PRIVATE);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString("ID_KEC", "" );
            edit.apply();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private class getData extends AsyncTask<Void,Void,String> {

        //variabel untuk tangkap data
        private int scs = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(PilihArea.this);
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
                            String id = c.getString("id_kec");
                            String nama_kec = c.getString("nama_kec");

                            itemList.add(new ItemKec(id, nama_kec));

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

}
