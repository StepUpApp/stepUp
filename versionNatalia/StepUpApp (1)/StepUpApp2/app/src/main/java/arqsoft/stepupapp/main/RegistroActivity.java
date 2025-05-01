package arqsoft.stepupapp.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import arqsoft.stepupapp.R;
import modelo.Usuario;
import repository.APIRESTUsuarioRepository;

public class RegistroActivity extends AppCompatActivity {

    private APIRESTUsuarioRepository usuarioRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        usuarioRepo = new APIRESTUsuarioRepository();

        EditText etNombreUsuario = findViewById(R.id.etNombreUsuario);
        EditText etContrasena = findViewById(R.id.etContrasena);
        EditText etNombre = findViewById(R.id.etNombre);
        EditText etEdad = findViewById(R.id.etEdad);
        EditText etAltura = findViewById(R.id.etAltura);
        EditText etPeso = findViewById(R.id.etPeso);
        Button btnRegistro = findViewById(R.id.btnRegistro);

        btnRegistro.setOnClickListener(v -> {
            // Validar campos
            if (etNombreUsuario.getText().toString().isEmpty() ||
                    etContrasena.getText().toString().isEmpty()) {
                Toast.makeText(this, "Usuario y contraseña obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            // Crear objeto Usuario
            Usuario nuevoUsuario = new Usuario(
                    etNombreUsuario.getText().toString().trim(),
                    etContrasena.getText().toString().trim(),
                    etNombre.getText().toString(),
                    Integer.parseInt(etEdad.getText().toString()),
                    Double.parseDouble(etAltura.getText().toString()),
                    Double.parseDouble(etPeso.getText().toString())
            );

            String result = usuarioRepo.insert(nuevoUsuario);
            if (result != null && !result.isEmpty()) {
                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                guardarSesion(nuevoUsuario);
                startActivity(new Intent(this, MainActivity.class));
            } else {
                Toast.makeText(this, "Error en el registro", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void guardarSesion(Usuario usuario) {
        SharedPreferences prefs = getSharedPreferences("MisPreferencias", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("username", usuario.getNombreUsuario());
        editor.putString("objectId", usuario.getObjectId());
        editor.apply();
    }
}