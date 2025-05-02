package arqsoft.stepupapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import arqsoft.stepupapp.R;
import Business.modelo.Partida;

public class PartidaAdapter extends RecyclerView.Adapter<PartidaAdapter.PartidaViewHolder> {

    private List<Partida> partidas;
    private OnItemClickListener listener;
    private int selectedPosition = -1;

    public interface OnItemClickListener {
        void onItemClick(Partida partida);
    }

    public PartidaAdapter(List<Partida> partidas, OnItemClickListener listener) {
        this.partidas = partidas;
        this.listener = listener;
    }

    public void actualizarLista(List<Partida> nuevasPartidas) {
        this.partidas = nuevasPartidas;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PartidaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_partida, parent, false);
        return new PartidaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PartidaViewHolder holder, int position) {
        Partida partida = partidas.get(position);
        holder.bind(partida, position);
    }

    @Override
    public int getItemCount() {
        return partidas.size();
    }

    public void setSelectedPosition(int position) {
        this.selectedPosition = position;
        notifyDataSetChanged();
    }

    class PartidaViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTiempoInicio;
        private TextView tvTiempoFinal;
        private TextView tvDuracion;
        private View itemView;

        public PartidaViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            tvTiempoInicio = itemView.findViewById(R.id.tvTiempoInicio);
            tvTiempoFinal = itemView.findViewById(R.id.tvTiempoFinal);
            tvDuracion = itemView.findViewById(R.id.tvDuracion);
        }

        public void bind(Partida partida, int position) {
            String inicio = formatearFechaHora(partida.getTiempoInicio());
            String fin;
            String duracion;

            if (partida.isEnCurso()) {
                long tiempoTranscurrido = System.currentTimeMillis() - partida.getTiempoInicio();
                duracion = convertirMsAHorasMinutosSegundos(tiempoTranscurrido);
                fin = "En progreso";
            } else {
                long tiempoTotal = partida.getTiempoFinal() - partida.getTiempoInicio();
                duracion = convertirMsAHorasMinutosSegundos(tiempoTotal);
                fin = formatearFechaHora(partida.getTiempoFinal());
            }

            tvTiempoInicio.setText("Inicio: " + inicio);
            tvTiempoFinal.setText("Fin: " + fin);
            tvDuracion.setText("Duración: " + duracion);

            itemView.setSelected(position == selectedPosition);
            itemView.setBackgroundResource(position == selectedPosition ?
                    R.color.selected_item_background : android.R.color.transparent);

            itemView.setOnClickListener(v -> {
                if (selectedPosition != position) {
                    notifyItemChanged(selectedPosition);
                    selectedPosition = position;
                    notifyItemChanged(selectedPosition);
                }
                listener.onItemClick(partida);
            });
        }

        private String formatearFechaHora(long timestamp) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            return sdf.format(new Date(timestamp));
        }

        private String convertirMsAHorasMinutosSegundos(long milisegundos) {
            long segundosTotales = milisegundos / 1000;
            long horas = segundosTotales / 3600;
            long minutos = (segundosTotales % 3600) / 60;
            long segundos = segundosTotales % 60;
            return String.format(Locale.getDefault(), "%02d:%02d:%02d", horas, minutos, segundos);
        }
    }
}
