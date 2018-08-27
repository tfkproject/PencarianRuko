package ta.nanda.pencarianruko.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import ta.nanda.pencarianruko.LoginUser;
import ta.nanda.pencarianruko.R;
import ta.nanda.pencarianruko.RukoDetail;
import ta.nanda.pencarianruko.RukoPemilikDetail;
import ta.nanda.pencarianruko.model.ItemRuko;
import ta.nanda.pencarianruko.model.ItemRukoPemilik;
import ta.nanda.pencarianruko.util.SessionManager;

/**
 * Created by taufik on 21/05/18.
 */

public class RukoPemilikAdapter extends RecyclerView.Adapter<RukoPemilikAdapter.ViewHolder> {

    List<ItemRukoPemilik> items;
    Context context;
    private SessionManager session;

    public RukoPemilikAdapter(Context context, List<ItemRukoPemilik> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public RukoPemilikAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ruko, parent, false);
        session = new SessionManager(context);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.itemRuko.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!session.isLoggedIn()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Anda harus login terlebih dahulu");
                    builder.setCancelable(false);

                    builder.setPositiveButton(
                            "Login",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent intent = new Intent(context, LoginUser.class);
                                    context.startActivity(intent);
                                }
                            });

                    builder.setNegativeButton(
                            "Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });

                    AlertDialog alert = builder.create();
                    alert.show();
                }else{
                    Intent intent = new Intent(context, RukoPemilikDetail.class);
                    intent.putExtra("key_id_ruko", items.get(position).getId());
                    intent.putExtra("key_id_kec", items.get(position).getId_kec());
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
                    intent.putExtra("key_sts", items.get(position).getStatus());
                    context.startActivity(intent);
                }

            }
        });
        Glide.with(context).load(items.get(position).getUrl_gambar()).into(holder.image);
        holder.txtJudul.setText(items.get(position).getJudul());
        holder.txtUkuran.setText("Ukuran: "+items.get(position).getUkuran()+" M");
        holder.txtSert.setText(items.get(position).getSertifikat());
        holder.txtHarga.setText(items.get(position).getHarga());
        String status = items.get(position).getStatus();
        if(status.equals("N")){
            holder.txtStatus.setText("Belum disewa");
            holder.txtStatus.setTextColor(Color.GREEN);
        }
        if(status.equals("Y")){
            holder.txtStatus.setText("Sudah disewakan");
            holder.txtStatus.setTextColor(Color.RED);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        CardView itemRuko;
        ImageView image;
        TextView txtJudul, txtUkuran, txtSert, txtHarga, txtStatus;

        public ViewHolder(View itemView) {
            super(itemView);

            itemRuko = (CardView) itemView.findViewById(R.id.item_ruko);

            image = (ImageView) itemView.findViewById(R.id.foto);
            txtJudul = (TextView) itemView.findViewById(R.id.judul);
            txtUkuran = (TextView) itemView.findViewById(R.id.ukuran);
            txtSert = (TextView) itemView.findViewById(R.id.sertifikat);
            txtHarga = (TextView) itemView.findViewById(R.id.harga);
            txtStatus = (TextView) itemView.findViewById(R.id.status);
        }
    }
}
