package arqsoft.stepupapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

import arqsoft.stepupapp.R;
import modelo.Ubicacion;

public class UbicacionAdapter extends RecyclerView.Adapter<UbicacionAdapter.UbicacionViewHolder> {

    private List<Ubicacion> ubicaciones;
    private OnItemClickListener listener;
    private int selectedPosition = -1;

    public interface OnItemClickListener {
        void onItemClick(Ubicacion ubicacion);
    }

    public UbicacionAdapter(List<Ubicacion> ubicaciones, OnItemClickListener listener) {
        this.ubicaciones = ubicaciones;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UbicacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ubicacion, parent, false);
        return new UbicacionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UbicacionViewHolder holder, int position) {
        Ubicacion ubicacion = ubicaciones.get(position);
        holder.bind(ubicacion, position);
    }

    @Override
    public int getItemCount() {
        return ubicaciones.size();
    }

    public void setSelectedPosition(int position) {
        this.selectedPosition = position;
        notifyItemChanged(position);
    }

    class UbicacionViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNombre;
        private TextView tvCoordenadas;
        private ImageView ivImagen;
        private View itemView;

        public UbicacionViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvCoordenadas = itemView.findViewById(R.id.tvCoordenadas);
            ivImagen = itemView.findViewById(R.id.ivPreviaImagen);
        }

        public void bind(Ubicacion ubicacion, int position) {
            tvNombre.setText(ubicacion.getNombre());
            tvCoordenadas.setText(String.format("Lat: %.6f, Lng: %.6f",
                    ubicacion.getLatitud(),
                    ubicacion.getLongitud()));

            itemView.setSelected(position == selectedPosition);
            itemView.setBackgroundResource(
                    position == selectedPosition ?
                            R.color.selected_item_background :
                            android.R.color.transparent
            );

            itemView.setOnClickListener(v -> {
                if (selectedPosition != position) {
                    notifyItemChanged(selectedPosition);
                    selectedPosition = position;
                    notifyItemChanged(selectedPosition);
                }
                listener.onItemClick(ubicacion);
            });
        }
    }
}