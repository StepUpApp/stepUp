package arqsoft.stepupapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

import arqsoft.stepupapp.R;
import modelo.Usuario;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder> {

    private List<Usuario> usuarios;
    private OnItemClickListener listener;
    private int selectedPosition = -1;

    public interface OnItemClickListener {
        void onItemClick(Usuario usuario);
    }

    public UsuarioAdapter(List<Usuario> usuarios, OnItemClickListener listener) {
        this.usuarios = usuarios;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_usuario, parent, false);
        return new UsuarioViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {
        Usuario usuario = usuarios.get(position);
        holder.bind(usuario, position);
    }

    @Override
    public int getItemCount() {
        return usuarios.size();
    }

    public void setSelectedPosition(int position) {
        this.selectedPosition = position;
        notifyItemChanged(position);
    }

    class UsuarioViewHolder extends RecyclerView.ViewHolder {
        private TextView tvId;
        private TextView tvNombre;
        private TextView tvDetalles;
        private View itemView;

        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            tvId = itemView.findViewById(R.id.tvId);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvDetalles = itemView.findViewById(R.id.tvDetalles);
        }

        public void bind(Usuario usuario, int position) {
            tvId.setText(String.valueOf(usuario.getUsuarioId()));
            tvNombre.setText(usuario.getNombre());
            String detalles = String.format("%d años | %.2f m | %.2f kg",
                    usuario.getEdad(),
                    usuario.getAltura(),
                    usuario.getPeso());
            tvDetalles.setText(detalles);

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
                listener.onItemClick(usuario);
            });
        }
    }
}