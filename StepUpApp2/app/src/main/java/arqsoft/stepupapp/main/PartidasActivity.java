package arqsoft.stepupapp.main;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import arqsoft.stepupapp.R;
import arqsoft.stepupapp.adapter.PartidaAdapter;
import arqsoft.stepupapp.servicio.Controlador;
import modelo.Ejercicio;
import modelo.Partida;
import modelo.Ubicacion;
import modelo.Usuario;

public class PartidasActivity extends AppCompatActivity implements PartidaAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private PartidaAdapter adapter;
    private Controlador controlador;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partidas);

        // Inicializar el controlador antes de usarlo
        controlador = ((LayerApplication)getApplicationContext()).getControler();

        inicializarVistas();
        configurarBotones();
        iniciarActualizacionAutomatica();
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Partida> partidasActualizadas = controlador.listarPartidas();
        adapter.actualizarLista(partidasActualizadas);
    }

    private void inicializarVistas() {
        recyclerView = findViewById(R.id.recyclerPartidas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listaPartidas = controlador.listarPartidas();
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
        btnCalcular.setOnClickListener(v -> mostrarDialogoCalculo(partidaSeleccionada));

        habilitarBotones(false);
    }

    private void actualizarListaPartidas() {
        // Actualiza la lista de partidas desde el controlador
        List<Partida> partidasActualizadas = controlador.listarPartidas();
        adapter.actualizarLista(partidasActualizadas);
    }

    private void crearNuevaPartida() {
        // Para crear una nueva partida, por ejemplo se pasa una ubicación predeterminada
        Ubicacion ubicacion = new Ubicacion("Ubicación Predeterminada", 0.0, 0.0);
        controlador.startPartida(ubicacion);
        actualizarListaPartidas();
        Toast.makeText(this, "Partida iniciada", Toast.LENGTH_SHORT).show();
    }

    private void eliminarPartida() {
        if (partidaSeleccionada != null) {
            controlador.borrarPartida(partidaSeleccionada.getPartidaId());
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
            partidaSeleccionada.setTiempoFinal(System.currentTimeMillis());
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
        List<Partida> partidas = controlador.listarPartidas();
        // Actualiza visualmente cada partida que esté en curso
        for (int i = 0; i < partidas.size(); i++) {
            Partida p = partidas.get(i);
            if (p.isEnCurso()) {
                adapter.notifyItemChanged(i);
            }
        }
    }

    private void mostrarDialogoCalculo(Partida partidaSeleccionada) {
        if (partidaSeleccionada == null) {
            mostrarMensaje("Selecciona una partida para calcular ejercicios");
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_calculo, null);
        EditText etUsuarioId = view.findViewById(R.id.etUsuarioId);

        builder.setView(view)
                .setTitle("Calcular Ejercicios")
                .setPositiveButton("Calcular", (dialog, which) -> {
                    try {
                        long tiempo = partidaSeleccionada.getTiempoSentado();
                        int usuarioId = Integer.parseInt(etUsuarioId.getText().toString());
                        Usuario usuario = controlador.obtenerUsuario(usuarioId);
                        if (usuario != null) {
                            List<Ejercicio> resultados = controlador.calcularEjercicios(tiempo, usuarioId);
                            mostrarResultados(resultados);
                        } else {
                            mostrarError("Usuario no encontrado");
                        }
                    } catch (NumberFormatException e) {
                        mostrarError("Datos inválidos");
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarResultados(List<Ejercicio> resultados) {
        StringBuilder sb = new StringBuilder("Ejercicios recomendados:\n\n");
        for (Ejercicio e : resultados) {
            sb.append(e.getNombre())
                    .append(": ")
                    .append(String.format("%.2f %s", e.getFactorConversion(), e.getUnidadMedida()))
                    .append("\n");
        }
        new AlertDialog.Builder(this)
                .setTitle("Resultados")
                .setMessage(sb.toString())
                .setPositiveButton("OK", null)
                .show();
    }

    private void mostrarError(String mensaje) {
        Toast.makeText(this, "Error: " + mensaje, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(Partida partida) {
        partidaSeleccionada = partida;
        // Se actualiza la posición seleccionada en el adaptador
        int position = controlador.listarPartidas().indexOf(partida);
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
