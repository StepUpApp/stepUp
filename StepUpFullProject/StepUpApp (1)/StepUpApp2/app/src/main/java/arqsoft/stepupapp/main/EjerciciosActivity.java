package arqsoft.stepupapp.main;

import android.os.Bundle;
import android.util.Log;
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

import Business.services.RepositoryService;
import arqsoft.stepupapp.R;
import arqsoft.stepupapp.adapter.EjercicioAdapter;
import Business.modelo.Ejercicio;

public class EjerciciosActivity extends AppCompatActivity implements EjercicioAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private EjercicioAdapter adapter;
    // private Controlador controlador;
    private RepositoryService controlador;
    private Ejercicio ejercicioSeleccionado;
    private Button btnCrear, btnEditar, btnEliminar, btnCalcular;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ejercicios);

        controlador = ((LayerApplication)getApplicationContext()).getControler();
        inicializarVistas();
        configurarBotones();
        cargarEjercicios();
    }

    private void inicializarVistas() {
        recyclerView = findViewById(R.id.recyclerEjercicios);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnCrear = findViewById(R.id.btnCrearEjercicio);
        btnEditar = findViewById(R.id.btnEditarEjercicio);
        btnEliminar = findViewById(R.id.btnEliminarEjercicio);
        btnCalcular = findViewById(R.id.btnCalcularEjercicios);
    }

    private void configurarBotones() {
        btnCrear.setOnClickListener(v -> mostrarDialogoEjercicio(null));
        btnEditar.setOnClickListener(v -> editarEjercicio());
        btnEliminar.setOnClickListener(v -> eliminarEjercicio());
        habilitarBotones(false);
    }

    private void cargarEjercicios() {
        List<Ejercicio> ejercicios = controlador.listarEjercicios();
        Log.d("EJERCICIOS", "Número de ejercicios: " + ejercicios.size());
        adapter = new EjercicioAdapter(ejercicios, this);
        recyclerView.setAdapter(adapter);
    }

    private void mostrarDialogoEjercicio(Ejercicio ejercicioExistente) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_ejercicio, null);

        EditText etNombre = view.findViewById(R.id.etNombre);
        EditText etFactor = view.findViewById(R.id.etFactor);
        EditText etUnidad = view.findViewById(R.id.etUnidad);

        if(ejercicioExistente != null) {
            etNombre.setText(ejercicioExistente.getNombre());
            etFactor.setText(String.valueOf(ejercicioExistente.getFactorConversion()));
            etUnidad.setText(ejercicioExistente.getUnidadMedida());
        }

        builder.setView(view)
                .setTitle(ejercicioExistente == null ? "Nuevo Ejercicio" : "Editar Ejercicio")
                .setPositiveButton("Guardar", (dialog, which) -> {
                    try {
                        Ejercicio ejercicio = new Ejercicio(
                                etNombre.getText().toString(),
                                Double.parseDouble(etFactor.getText().toString()),
                                etUnidad.getText().toString()
                        );

                        if(ejercicioExistente != null) {
                            ejercicio.setEjercicioId(ejercicioExistente.getEjercicioId());
                            //controlador.editarEjercicio(ejercicio);
                        } else {
                            controlador.crearEjercicio(ejercicio);
                        }
                        actualizarLista();
                    } catch (NumberFormatException e) {
                        mostrarError("Valores numéricos inválidos");
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void editarEjercicio() {
        if(ejercicioSeleccionado != null) {
            mostrarDialogoEjercicio(ejercicioSeleccionado);
        } else {
            mostrarError("Selecciona un ejercicio primero");
        }
    }

    private void eliminarEjercicio() {
        if(ejercicioSeleccionado != null) {
            new AlertDialog.Builder(this)
                    .setTitle("Confirmar eliminación")
                    .setMessage("¿Eliminar el ejercicio " + ejercicioSeleccionado.getNombre() + "?")
                    .setPositiveButton("Eliminar", (dialog, which) -> {
                        controlador.borrarEjercicio(ejercicioSeleccionado.getObjectId());
                        ejercicioSeleccionado = null;
                        actualizarLista();
                        habilitarBotones(false);
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        } else {
            mostrarError("Selecciona un ejercicio primero");
        }
    }

    private void actualizarLista() {
        adapter.actualizarLista(controlador.listarEjercicios());
    }

    @Override
    public void onItemClick(Ejercicio ejercicio) {
        ejercicioSeleccionado = ejercicio;
        int position = controlador.listarEjercicios().indexOf(ejercicio);
        adapter.setSelectedPosition(position);
        habilitarBotones(true);
    }

    private void habilitarBotones(boolean habilitar) {
        btnEditar.setEnabled(habilitar);
        btnEliminar.setEnabled(habilitar);
    }

    private void mostrarError(String mensaje) {
        Toast.makeText(this, "Error: " + mensaje, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        actualizarLista();
    }
}