package arqsoft.stepupapp.main;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import java.util.List;
import arqsoft.stepupapp.R;
import arqsoft.stepupapp.adapter.UbicacionAdapter;
import arqsoft.stepupapp.servicio.Controlador;
import modelo.Ubicacion;

public class UbicacionesActivity extends AppCompatActivity implements UbicacionAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private UbicacionAdapter adapter;
    private Controlador controlador;
    private Ubicacion ubicacionSeleccionada;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int PERMISO_UBICACION = 1001;

    // Botones
    private Button btnCrear;
    private Button btnEditar;
    private Button btnEliminar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubicaciones);

        controlador = ((LayerApplication)getApplicationContext()).getControler();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        inicializarVistas();
        configurarBotones();
        cargarUbicaciones();
    }

    private void inicializarVistas() {
        recyclerView = findViewById(R.id.recyclerUbicaciones);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnCrear = findViewById(R.id.btnCrear);
        btnEditar = findViewById(R.id.btnEditar);
        btnEliminar = findViewById(R.id.btnEliminar);
    }

    private void configurarBotones() {
        btnCrear.setOnClickListener(v -> mostrarDialogoUbicacion(null));
        btnEditar.setOnClickListener(v -> editarUbicacion());
        btnEliminar.setOnClickListener(v -> eliminarUbicacion());
        habilitarBotones(false);
    }

    private void cargarUbicaciones() {
        List<Ubicacion> ubicaciones = controlador.listarUbicaciones();
        adapter = new UbicacionAdapter(ubicaciones, this);
        recyclerView.setAdapter(adapter);
    }

    private void mostrarDialogoUbicacion(Ubicacion ubicacionExistente) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_ubicacion, null);

        EditText etNombre = dialogView.findViewById(R.id.etNombre);
        EditText etLatitud = dialogView.findViewById(R.id.etLatitud);
        EditText etLongitud = dialogView.findViewById(R.id.etLongitud);
        Button btnObtenerUbicacion = dialogView.findViewById(R.id.btnObtenerUbicacion);

        if(ubicacionExistente != null) {
            etNombre.setText(ubicacionExistente.getNombre());
            etLatitud.setText(String.valueOf(ubicacionExistente.getLatitud()));
            etLongitud.setText(String.valueOf(ubicacionExistente.getLongitud()));
        }

        btnObtenerUbicacion.setOnClickListener(v -> obtenerUbicacionActual(etLatitud, etLongitud));

        builder.setView(dialogView)
                .setTitle(ubicacionExistente == null ? "Nueva Ubicación" : "Editar Ubicación")
                .setPositiveButton("Guardar", (dialog, which) -> {
                    try {
                        Ubicacion ubicacion = new Ubicacion(
                                etNombre.getText().toString(),
                                Double.parseDouble(etLatitud.getText().toString()),
                                Double.parseDouble(etLongitud.getText().toString())
                        );

                        if(ubicacionExistente != null) {
                            ubicacion.setUbicacionId(ubicacionExistente.getUbicacionId());
                            controlador.actualizarUbicacion(ubicacion);
                        } else {
                            controlador.crearUbicacion(ubicacion);
                        }

                        cargarUbicaciones();
                    } catch (NumberFormatException e) {
                        mostrarError("Coordenadas inválidas");
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void obtenerUbicacionActual(EditText etLatitud, EditText etLongitud) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISO_UBICACION);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                etLatitud.setText(String.valueOf(location.getLatitude()));
                etLongitud.setText(String.valueOf(location.getLongitude()));
            } else {
                mostrarError("No se pudo obtener la ubicación");
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISO_UBICACION && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Volver a intentar si el usuario concede los permisos
        }
    }

    private void editarUbicacion() {
        if (ubicacionSeleccionada != null) {
            mostrarDialogoUbicacion(ubicacionSeleccionada);
        } else {
            mostrarError("Selecciona una ubicación primero");
        }
    }

    private void eliminarUbicacion() {
        if (ubicacionSeleccionada != null) {
            controlador.eliminarUbicacion(ubicacionSeleccionada.getUbicacionId());
            ubicacionSeleccionada = null;
            cargarUbicaciones();
            habilitarBotones(false);
            mostrarMensaje("Ubicación eliminada");
        } else {
            mostrarError("Selecciona una ubicación primero");
        }
    }

    @Override
    public void onItemClick(Ubicacion ubicacion) {
        ubicacionSeleccionada = ubicacion;
        int position = controlador.listarUbicaciones().indexOf(ubicacion);
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

    private void mostrarMensaje(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarUbicaciones();
    }
}