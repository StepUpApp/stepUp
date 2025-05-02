package arqsoft.stepupapp.main;

import android.os.Bundle;
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
import arqsoft.stepupapp.adapter.UsuarioAdapter;

import Business.modelo.Usuario;

public class UsuariosActivity extends AppCompatActivity implements UsuarioAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private UsuarioAdapter adapter;
    private RepositoryService controlador;
    private Usuario usuarioSeleccionado;
    private Button btnCrear, btnEditar, btnEliminar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuarios);

        controlador = ((LayerApplication)getApplicationContext()).getControler();
        inicializarVistas();
        configurarBotones();
        cargarUsuarios();
    }

    private void inicializarVistas() {
        recyclerView = findViewById(R.id.recyclerUsuarios);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnCrear = findViewById(R.id.btnCrearUsuario);
        btnEditar = findViewById(R.id.btnEditarUsuario);
        btnEliminar = findViewById(R.id.btnEliminarUsuario);
    }

    private void configurarBotones() {
        btnCrear.setOnClickListener(v -> mostrarDialogoUsuario(null));
        btnEditar.setOnClickListener(v -> editarUsuario());
        btnEliminar.setOnClickListener(v -> eliminarUsuario());
        habilitarBotones(false);
    }

    private void cargarUsuarios() {
        List<Usuario> usuarios = controlador.listarUsuarios();
        adapter = new UsuarioAdapter(usuarios, this);
        recyclerView.setAdapter(adapter);
    }

    private void mostrarDialogoUsuario(Usuario usuarioExistente) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_usuario, null);

        EditText etNombre = dialogView.findViewById(R.id.etNombre);
        EditText etEdad = dialogView.findViewById(R.id.etEdad);
        EditText etAltura = dialogView.findViewById(R.id.etAltura);
        EditText etPeso = dialogView.findViewById(R.id.etPeso);

        if(usuarioExistente != null) {
            etNombre.setText(usuarioExistente.getNombre());
            etEdad.setText(String.valueOf(usuarioExistente.getEdad()));
            etAltura.setText(String.valueOf(usuarioExistente.getAltura()));
            etPeso.setText(String.valueOf(usuarioExistente.getPeso()));
        }

        builder.setView(dialogView)
                .setTitle(usuarioExistente == null ? "Nuevo Usuario" : "Editar Usuario")
                .setPositiveButton("Guardar", (dialog, which) -> {
                    try {
                        Usuario usuario = new Usuario(
                                etNombre.getText().toString(),
                                Integer.parseInt(etEdad.getText().toString()),
                                Double.parseDouble(etAltura.getText().toString()),
                                Double.parseDouble(etPeso.getText().toString())
                        );

                        if(usuarioExistente != null) {
                            usuario.setUsuarioId(usuarioExistente.getUsuarioId());
                            controlador.editarUsuario(usuario);
                        } else {
                            controlador.crearUsuario(usuario);
                        }

                        cargarUsuarios();
                    } catch (NumberFormatException e) {
                        mostrarError("Datos inválidos");
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void editarUsuario() {
        if (usuarioSeleccionado != null) {
            mostrarDialogoUsuario(usuarioSeleccionado);
        } else {
            mostrarError("Selecciona un usuario primero");
        }
    }

    private void eliminarUsuario() {
        if (usuarioSeleccionado != null) {
            new AlertDialog.Builder(this)
                    .setTitle("Confirmar eliminación")
                    .setMessage("¿Eliminar al usuario " + usuarioSeleccionado.getNombre() + "?")
                    .setPositiveButton("Eliminar", (dialog, which) -> {
                        controlador.borrarUsuario(usuarioSeleccionado.getObjectId());
                        usuarioSeleccionado = null;
                        cargarUsuarios();
                        habilitarBotones(false);
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        } else {
            mostrarError("Selecciona un usuario primero");
        }
    }

    @Override
    public void onItemClick(Usuario usuario) {
        usuarioSeleccionado = usuario;
        int position = controlador.listarUsuarios().indexOf(usuario);
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
        cargarUsuarios();
    }
}