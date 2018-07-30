package ta.nanda.pencarianruko.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

import ta.nanda.pencarianruko.R;
import ta.nanda.pencarianruko.model.ItemKec;
import ta.nanda.pencarianruko.model.ItemRuko;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by taufik on 21/05/18.
 */

public class PilihAreaAdapter extends RecyclerView.Adapter<PilihAreaAdapter.ViewHolder> {

    List<ItemKec> items;
    Context context;
    private AdapterListener listener;

    public PilihAreaAdapter(Context context, List<ItemKec> items, AdapterListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @Override
    public PilihAreaAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_area, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.itemKec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onSelected(position, items.get(position).getId_kec());
            }
        });
        holder.txtNamaKec.setText(items.get(position).getNama_kec());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout itemKec;
        TextView txtNamaKec;

        public ViewHolder(View itemView) {
            super(itemView);

            itemKec = (LinearLayout) itemView.findViewById(R.id.item_kec);
            txtNamaKec = (TextView) itemView.findViewById(R.id.nama_kec);
        }
    }

    public interface AdapterListener {
        void onSelected(int position, String id_kec);
    }
}
