package com.pam.finalproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class SOSAdapter extends FirebaseRecyclerAdapter<
        SOS, SOSAdapter.sosViewholder> {
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public SOSAdapter(@NonNull FirebaseRecyclerOptions<SOS> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull sosViewholder holder, int position, @NonNull SOS model) {
        holder.tvName.setText(model.getName());
        holder.tvAlamat.setText(model.getAlamat());
        holder.tvNoHP.setText(model.getNoTelp());
    }

    @NonNull
    @Override
    public sosViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view
                = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bantuan, parent, false);
        return new SOSAdapter.sosViewholder(view);
    }

    public class sosViewholder extends RecyclerView.ViewHolder {
        private TextView tvName, tvAlamat, tvNoHP;
        public sosViewholder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_name);
            tvAlamat = itemView.findViewById(R.id.tv_alamat);
            tvNoHP = itemView.findViewById(R.id.tv_noHP);
        }
    }
}
