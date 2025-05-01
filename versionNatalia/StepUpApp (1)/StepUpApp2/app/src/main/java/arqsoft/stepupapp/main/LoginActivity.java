package arqsoft.stepupapp.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import arqsoft.stepupapp.R;
import modelo.Usuario;
import repository.APIRESTUsuarioRepository;

public class LoginActivity extends AppCompatActivity {

    private APIRESTUsuarioRepository usuarioRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("MisPreferencias", MODE_PRIVATE);
        String nombreUsuario = prefs.getString("username", null);
        String objectId = prefs.getString("objectId", null);

        if (nombreUsuario != null && objectId != null) {
            // Si hay sesión guardada, ir directamente al MainActivity
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return; // Importante para no cargar el layout de login
        }
        setContentView(R.layout.activity_login);

        usuarioRepo = new APIRESTUsuarioRepository();

        EditText etNombreUsuario = findViewById(R.id.etNombreUsuario);
        EditText etContrasena = findViewById(R.id.etContrasena);
        Button btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {
            String username = etNombreUsuario.getText().toString();
            String contrasena = etContrasena.getText().toString();

            if (username.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            Usuario usuario = usuarioRepo.getByUsername(username);
            if (usuario != null && usuario.getContraseña().equals(contrasena)) {
                guardarSesion(usuario);
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
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

    private void cerrarSesion() {
        SharedPreferences prefs = getSharedPreferences("MisPreferencias", MODE_PRIVATE);
        prefs.edit().clear().apply();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    public void irARegistro(View view) {
        Intent intent = new Intent(this, RegistroActivity.class);
        startActivity(intent);
    }
}