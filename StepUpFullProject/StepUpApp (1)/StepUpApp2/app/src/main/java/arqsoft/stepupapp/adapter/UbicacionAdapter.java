package arqsoft.stepupapp.adapter;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import arqsoft.stepupapp.R;
import Business.modelo.Ubicacion;

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
        private CardView cardView;

        private Context context;
        public UbicacionViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            this.itemView = itemView;
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvCoordenadas = itemView.findViewById(R.id.tvCoordenadas);
            ivImagen = itemView.findViewById(R.id.ivImagen);
            cardView = itemView.findViewById(R.id.cardView);

        }

        public void bind(Ubicacion ubicacion, int position) {
            tvNombre.setText(ubicacion.getNombre());
            tvCoordenadas.setText(String.format("Lat: %.6f, Lng: %.6f",
                    ubicacion.getLatitud(),
                    ubicacion.getLongitud()));
            cargarImagenMixta(ubicacion.getImagen());
            //cargarImagenDesdeAssets(ubicacion.getImagen());

           // itemView.setSelected(position == selectedPosition);
            /*itemView.setBackgroundResource(
                    position == selectedPosition ?
                            R.color.selected_item_background :
                            android.R.color.transparent
            );*/

            this.cardView.setCardBackgroundColor(
                    position == selectedPosition ?
                            context.getResources().getColor(R.color.teal_700) :
                            context.getResources().getColor(android.R.color.white)
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

        private void cargarImagenDesdeAssets(String nombreImagen) {
            if (nombreImagen == null || nombreImagen.isEmpty()) {
                ivImagen.setImageResource(R.drawable.paisaje);
                return;
            }

            try {
                InputStream is = context.getAssets().open(nombreImagen);
                Bitmap originalBitmap = BitmapFactory.decodeStream(is);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(
                        originalBitmap,
                        200,
                        200,
                        true
                );
                ivImagen.setImageBitmap(scaledBitmap);
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
                ivImagen.setImageResource(R.drawable.paisaje);
            }
        }


        private void cargarImagenMixta(String nombreImagen) {
            if (nombreImagen == null || nombreImagen.isEmpty()) {
                ivImagen.setImageResource(R.drawable.paisaje);
                return;
            }

            // 1) Intentar cargar desde almacenamiento interno
            File archivoImagen = new File(context.getFilesDir(), nombreImagen);
            if (archivoImagen.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(archivoImagen.getAbsolutePath());
                ivImagen.setImageBitmap(bitmap); // No escalamos aquí
                return; // Si lo encontramos en interno, no necesitamos seguir con el proceso
            }

            // 2) Si no está en almacenamiento interno, intentar cargar desde assets
            try (InputStream is = context.getAssets().open(nombreImagen)) {
                Bitmap originalBitmap = BitmapFactory.decodeStream(is);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(
                        originalBitmap,
                        200,  // tamaño deseado
                        200,  // tamaño deseado
                        true
                );
                ivImagen.setImageBitmap(scaledBitmap);
            } catch (IOException e) {
                e.printStackTrace();
                ivImagen.setImageResource(R.drawable.paisaje); // Placeholder en caso de error
            }
        }



    }
}