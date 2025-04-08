package arqsoft.stepupapp.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import arqsoft.stepupapp.R;
import modelo.Ejercicio;

public class EjercicioAdapter extends RecyclerView.Adapter<EjercicioAdapter.ViewHolder> {

    private List<Ejercicio> ejercicios;
    private OnItemClickListener listener;
    private int selectedPosition = -1;

    public interface OnItemClickListener {
        void onItemClick(Ejercicio ejercicio);
    }

    public EjercicioAdapter(List<Ejercicio> ejercicios, OnItemClickListener listener) {
        this.ejercicios = ejercicios;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ejercicio, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ejercicio ejercicio = ejercicios.get(position);
        holder.itemView.setTag(this); // <--- AÑADE ESTA LÍNEA
        holder.bind(ejercicio, position);
    }

    @Override
    public int getItemCount() {
        return ejercicios.size();
    }

    public void actualizarLista(List<Ejercicio> nuevaLista) {
        ejercicios = nuevaLista;
        notifyDataSetChanged();
    }

    public void setSelectedPosition(int position) {
        int previousPosition = selectedPosition;
        selectedPosition = position;
        if(previousPosition != -1) notifyItemChanged(previousPosition);
        notifyItemChanged(selectedPosition);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, tvNombre, tvDetalle;
        View itemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            tvId = itemView.findViewById(R.id.tvIdEjercicio);
            tvNombre = itemView.findViewById(R.id.tvNombreEjercicio);
            tvDetalle = itemView.findViewById(R.id.tvDetallesEjercicio);
        }

        public void bind(Ejercicio ejercicio, int position) {
            EjercicioAdapter adapter = (EjercicioAdapter) itemView.getTag(); // Obtener el adapter del tag

            tvId.setText(String.valueOf(ejercicio.getEjercicioId()));
            tvNombre.setText(ejercicio.getNombre());
            tvDetalle.setText(String.format("%.2f %s",
                    ejercicio.getFactorConversion(),
                    ejercicio.getUnidadMedida()));

            itemView.setBackgroundResource(
                    position == adapter.selectedPosition
                            ? R.color.selected_item_background
                            : android.R.color.transparent
            );

            itemView.setOnClickListener(v -> {
                adapter.setSelectedPosition(position);
                adapter.listener.onItemClick(ejercicio);
            });
        }
    }
}