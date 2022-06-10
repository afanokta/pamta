package com.pam.finalproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class PengaduanAdapter extends FirebaseRecyclerAdapter<
        Pengaduan, PengaduanAdapter.pengaduanViewholder> {
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public PengaduanAdapter(@NonNull FirebaseRecyclerOptions<Pengaduan> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull pengaduanViewholder holder, int position, @NonNull Pengaduan model) {
        holder.tvKategoriKejahatan.setText(model.getKategoriKejahatan());
        holder.tvKronologiKejadian.setText(model.getKronologiKejadian());
        holder.tvTanggalKejadian.setText(model.getTanggal());
        holder.tvJamKejadian.setText(model.getJam());
    }

    @NonNull
    @Override
    public pengaduanViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view
                = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pengaduan, parent, false);
        return new PengaduanAdapter.pengaduanViewholder(view);
    }

    public class pengaduanViewholder extends RecyclerView.ViewHolder {
        private TextView tvKategoriKejahatan, tvKronologiKejadian, tvTanggalKejadian, tvJamKejadian;
        public pengaduanViewholder(@NonNull View itemView) {
            super(itemView);

            tvKategoriKejahatan = itemView.findViewById(R.id.tv_kategori_kejahatan);
            tvKronologiKejadian = itemView.findViewById(R.id.tv_kronologi_kejadian);
            tvTanggalKejadian = itemView.findViewById(R.id.tv_tanggal_kejadian);
            tvJamKejadian = itemView.findViewById(R.id.tv_jam_kejadian);
        }
    }
}
