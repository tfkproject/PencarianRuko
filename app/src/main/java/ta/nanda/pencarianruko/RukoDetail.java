package ta.nanda.pencarianruko;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class RukoDetail extends AppCompatActivity {

    ImageView imgRuko;
    TextView txtJdl, txtHarga, txtUkrn, txtBgnn, txtTnh, txtJumKmr, txtKmrMndi, txtListrik, txtSrtfkt, txtSts;
    Button btnTelp, btnSekitar, btnDirek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ruko_detail);

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
        final String status = getIntent().getStringExtra("key_sts");

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
        txtSts = (TextView) findViewById(R.id.txt_sts);

        //set
        Glide.with(RukoDetail.this).load(gambar).into(imgRuko);
        txtJdl.setText(judul);
        txtHarga.setText(harga);
        txtUkrn.setText(ukuran+" m");
        txtBgnn.setText(l_bangunan+" m");
        txtTnh.setText(l_tanah+" m");
        txtJumKmr.setText(jum_kamar);
        txtKmrMndi.setText(kamar_mandi);
        txtListrik.setText(daya_listrik+" VA");
        txtSrtfkt.setText(sertifikat);

        if(status.equals("N")){
            txtSts.setText("Belum disewa");
            txtSts.setTextColor(Color.GREEN);
        }
        if(status.equals("Y")){
            txtSts.setText("Sudah disewakan");
            txtSts.setTextColor(Color.RED);
        }

        btnTelp = (Button) findViewById(R.id.btn_edit);
        btnSekitar = (Button) findViewById(R.id.btn_sekitar);
        btnDirek = (Button) findViewById(R.id.btn_hapus);

        btnTelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:"+no_hp));
                startActivity(intent);
            }
        });

        btnSekitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RukoDetail.this, SekitaranActivity.class);
                intent.putExtra("key_id_ruko", id_ruko);
                intent.putExtra("key_judul_ruko", judul);
                intent.putExtra("key_lat_ruko", latitude);
                intent.putExtra("key_lon_ruko", longitude);
                startActivity(intent);
            }
        });

        btnDirek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RukoDetail.this, DirectionMap.class);
                intent.putExtra("key_nama_tujuan", judul);
                intent.putExtra("key_lat_tujuan", latitude);
                intent.putExtra("key_long_tujuan", longitude);
                startActivity(intent);
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

}
