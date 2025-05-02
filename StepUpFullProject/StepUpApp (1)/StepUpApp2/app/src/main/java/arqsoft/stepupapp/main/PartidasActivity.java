package arqsoft.stepupapp.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import Business.services.RepositoryService;
import arqsoft.stepupapp.R;
import arqsoft.stepupapp.adapter.PartidaAdapter;
import Business.modelo.*;


public class PartidasActivity extends AppCompatActivity implements PartidaAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private PartidaAdapter adapter;
    private RepositoryService controlador;

    private List<Partida> listaPartidas;
    private Partida partidaSeleccionada;
    private Handler handler = new Handler();
    private Runnable updateRunnable;

    // Botones
    private Button btnNueva;
    private Button btnEditar;    // (si lo implementas)
    private Button btnEliminar;
    private Button btnDetener;
    private Button btnCalcular;

    private AlertDialog ejerciciosDialog;
    private int currentEjercicioIndex = 0;
    private List<Ejercicio> ejerciciosList;

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partidas);

        // Inicializar el controlador antes de usarlo
        controlador = ((LayerApplication)getApplicationContext()).getControler();

        username = ((LayerApplication) getApplication()).getCurrentUsername();

        if (username == null) {
            // Redirigir al login si no hay sesión
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        inicializarVistas();
        configurarBotones();
        iniciarActualizacionAutomatica();
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Partida> partidasActualizadas = controlador.listarPartidasPorUsuario(username);
        adapter.actualizarLista(partidasActualizadas);
    }

    private void inicializarVistas() {
        recyclerView = findViewById(R.id.recyclerPartidas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listaPartidas = controlador.listarPartidasPorUsuario(username);
        // Instanciar el adaptador con la lista actual y asignar el listener para los clicks
        adapter = new PartidaAdapter(listaPartidas, this);
        recyclerView.setAdapter(adapter);

        btnNueva = findViewById(R.id.btnNuevaPartida);
        btnEliminar = findViewById(R.id.btnEliminarPartida);
        btnDetener = findViewById(R.id.btnDetenerPartida);
        btnCalcular = findViewById(R.id.btnCalcularEjercicios);
    }

    private void configurarBotones() {
        btnNueva.setOnClickListener(v -> crearNuevaPartida());
        btnEliminar.setOnClickListener(v -> eliminarPartida());
        btnDetener.setOnClickListener(v -> detenerPartida());
        btnCalcular.setOnClickListener(v -> calcularEjercicios(partidaSeleccionada));

        habilitarBotones(false);
    }

    private void actualizarListaPartidas() {
        // Actualiza la lista de partidas desde el controlador
        List<Partida> partidasActualizadas = controlador.listarPartidasPorUsuario(username);
        adapter.actualizarLista(partidasActualizadas);
    }

    private void crearNuevaPartida() {
        // Para crear una nueva partida, por ejemplo se pasa una ubicación predeterminada
        Ubicacion ubicacion = new Ubicacion("Ubicación Predeterminada", 0.0, 0.0);
        controlador.startPartida(username, ubicacion);
        actualizarListaPartidas();
        Toast.makeText(this, "Partida iniciada", Toast.LENGTH_SHORT).show();
    }

    private void eliminarPartida() {
        if (partidaSeleccionada != null) {
            controlador.borrarPartida(partidaSeleccionada.getObjectId());
            actualizarListaPartidas();
            habilitarBotones(false);
            mostrarMensaje("Partida eliminada");
        } else {
            mostrarMensaje("Selecciona una partida primero");
        }
    }

    private void detenerPartida() {
        if (partidaSeleccionada != null && partidaSeleccionada.isEnCurso()) {
            // Se actualiza el tiempo final de la partida en curso
            controlador.stopPartida(partidaSeleccionada);
            // Aquí podrías llamar a un método en el controlador para actualizar la partida, si fuese necesario.
            actualizarListaPartidas();
            mostrarMensaje("Partida detenida");
        } else {
            mostrarMensaje("Selecciona una partida en curso");
        }
    }

    private void iniciarActualizacionAutomatica() {
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                actualizarPartidasEnCurso();
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(updateRunnable);
    }

    private void actualizarPartidasEnCurso() {
        List<Partida> partidas = controlador.listarPartidasPorUsuario(username);
        // Actualiza visualmente cada partida que esté en curso
        for (int i = 0; i < partidas.size(); i++) {
            Partida p = partidas.get(i);
            if (p.isEnCurso()) {
                adapter.notifyItemChanged(i);
            }
        }
    }

    private void calcularEjercicios(Partida partidaSeleccionada) {

        if (partidaSeleccionada == null) {
            mostrarMensaje("Selecciona una partida para calcular ejercicios");
            return;
        }

        String username = ((LayerApplication)getApplication()).getCurrentUsername();
        if (username == null) {
            mostrarError("Usuario no autenticado");
            return;
        }

        try {
            long tiempo = partidaSeleccionada.getTiempoSentado();
            Usuario usuario = controlador.obtenerUsuarioPorUsername(username);

            if (usuario != null) {
                ejerciciosList = controlador.calcularEjercicios(tiempo, usuario);
                if (!ejerciciosList.isEmpty()) {
                    currentEjercicioIndex = 0;
                    mostrarEjercicioDialog();
                } else {
                    mostrarMensaje("No hay ejercicios recomendados");
                }
            } else {
                mostrarError("Usuario no encontrado");
            }
        } catch (NumberFormatException e) {
            mostrarError("Datos inválidos");
        }
    }

    private void mostrarEjercicioDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.ejercicios, null);
        builder.setView(view);

        ImageView imgEjercicio = view.findViewById(R.id.imgEjercicio);
        TextView tvNombre = view.findViewById(R.id.tvNombreEjercicio);
        TextView tvDetalle = view.findViewById(R.id.tvDetalleEjercicio);
        Button btnAnterior = view.findViewById(R.id.btnAnterior);
        Button btnSiguiente = view.findViewById(R.id.btnSiguiente);

        actualizarVistasEjercicio(imgEjercicio, tvNombre, tvDetalle);

        btnAnterior.setOnClickListener(v -> {
            if (currentEjercicioIndex > 0) {
                currentEjercicioIndex--;
                actualizarVistasEjercicio(imgEjercicio, tvNombre, tvDetalle);
            }
        });

        btnSiguiente.setOnClickListener(v -> {
            if (currentEjercicioIndex < ejerciciosList.size() - 1) {
                currentEjercicioIndex++;
                actualizarVistasEjercicio(imgEjercicio, tvNombre, tvDetalle);
            }
        });

        ejerciciosDialog = builder.create();
        ejerciciosDialog.show();
    }

    private void actualizarVistasEjercicio(ImageView imageView, TextView nombre, TextView detalle) {
        Ejercicio ejercicioActual = ejerciciosList.get(currentEjercicioIndex);

        // Depuración
        Log.d("IMAGEN", "Intentando cargar: " + ejercicioActual.getImagen());

        Bitmap bitmap = cargarImagenDesdeAssets(ejercicioActual.getImagen());
        imageView.setImageBitmap(bitmap);

        nombre.setText(ejercicioActual.getNombre());
        detalle.setText(String.format("%.0f %s",
                ejercicioActual.getFactorConversion(),
                ejercicioActual.getUnidadMedida()));
    }

    private Bitmap cargarImagenDesdeAssets(String nombreArchivo) {
        if (nombreArchivo == null || nombreArchivo.isEmpty()) {
            Log.e("Assets", "Nombre de archivo es nulo o vacío");
            return BitmapFactory.decodeResource(getResources(), R.drawable.paisaje);
        }

        try {
            InputStream is = getAssets().open(nombreArchivo);
            return BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            Log.e("Assets", "Error cargando imagen: " + nombreArchivo, e);
            return BitmapFactory.decodeResource(getResources(), R.drawable.paisaje);
        }
    }

    private void mostrarError(String mensaje) {
        Toast.makeText(this, "Error: " + mensaje, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(Partida partida) {
        partidaSeleccionada = partida;
        // Se actualiza la posición seleccionada en el adaptador
        int position = controlador.listarPartidasPorUsuario(username).indexOf(partida);
        adapter.setSelectedPosition(position);
        habilitarBotones(true);
    }

    private void habilitarBotones(boolean habilitar) {
        btnEliminar.setEnabled(habilitar);
        // Solo se puede detener si la partida seleccionada está en curso
        btnDetener.setEnabled(habilitar && partidaSeleccionada != null && partidaSeleccionada.isEnCurso());
        btnCalcular.setEnabled(habilitar);
    }

    private void mostrarMensaje(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateRunnable);
    }
}
