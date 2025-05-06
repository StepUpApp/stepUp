package arqsoft.stepupapp.main;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import Business.services.RepositoryService;
import arqsoft.stepupapp.R;

import Business.modelo.*;

public class MainActivity extends AppCompatActivity {

    private String usuarioLogeadoId;
    private static final int REQUEST_LOCATION_PERMISSION = 100;
    private static final int REQUEST_CODE_UBICACIONES = 42;
    private TextView tvUbicacionActual;
    private ImageView ivUbicacion;
    private Button btnGuardarUbicacion, btnDetenerPartida, btnIniciarPartida;
    private FusedLocationProviderClient fusedLocationClient;
    private RepositoryService controlador;
    private Chronometer chronometerTiempo;
    private Ubicacion ubicacionActiva;
    private Partida partidaActiva;
    private List<Partida> listaPartidas;
    double lat;
    double lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Verificar si hay sesión activa
        SharedPreferences prefs = getSharedPreferences("MisPreferencias", MODE_PRIVATE);
        usuarioLogeadoId = prefs.getString("objectId", null);
        String username = prefs.getString("username", null);

        if (username == null) {
            // Redirigir al Login si no hay sesión
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);
        ImageView btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> cerrarSesion());
        setSupportActionBar(findViewById(R.id.myToolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        controlador = ((LayerApplication)getApplicationContext()).getControler();

        // Inicia el cronómetro
        chronometerTiempo = findViewById(R.id.chronometerTiempo);
        chronometerTiempo.setBase(SystemClock.elapsedRealtime());

        // Inicializa las vistas
        tvUbicacionActual = findViewById(R.id.tvUbicacionActual);
        ivUbicacion = findViewById(R.id.ivUbicacion);
        btnGuardarUbicacion = findViewById(R.id.btnGuardarUbicacion);
        btnDetenerPartida = findViewById(R.id.btnDetenerPartida);
        btnIniciarPartida = findViewById(R.id.btnIniciarPartida); // Nuevo botón para iniciar partida
        Button btnHistorialEstudio = findViewById(R.id.btnHistorialEstudio);
        Button btnUbicacionesRegistradas = findViewById(R.id.btnUbicacionesRegistradas);

        // Inicializa el FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Deshabilita los botones inicialmente
        btnGuardarUbicacion.setEnabled(false);
        btnDetenerPartida.setEnabled(false);
        //btnIniciarPartida.setEnabled(true); // Se habilitará si la ubicación es registrada

        btnIniciarPartida.setClickable(false);
        // Configura el listener del botón Guardar Ubicación (si lo necesitas)
        btnGuardarUbicacion.setOnClickListener(view -> {
            if (lat != 0 && lng != 0) {
                Intent intent = new Intent(MainActivity.this, GuardarUbicacionActivity.class);

                intent.putExtra("LATITUD", lat);
                intent.putExtra("LONGITUD", lng);

                startActivityForResult(intent, 1);

            } else {
                Toast.makeText(this, "Las coordenadas no están disponibles", Toast.LENGTH_SHORT).show();
            }

        });

        // Listener para iniciar partida manualmente
        btnIniciarPartida.setOnClickListener(view -> {

            Log.d("MainActivity", "Ubicación activa: " + (ubicacionActiva != null ? ubicacionActiva.getNombre() : "null"));


            if (ubicacionActiva == null) {
                Toast.makeText(this, "Debes registrar tu ubicación actual", Toast.LENGTH_SHORT).show();
                return;
            }

            if (partidaActiva == null) {
                partidaActiva = controlador.startPartida(username, ubicacionActiva);
                chronometerTiempo.setBase(SystemClock.elapsedRealtime());
                chronometerTiempo.start();
                btnDetenerPartida.setEnabled(true);
                btnIniciarPartida.setEnabled(false);
                Toast.makeText(this, "Partida iniciada", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Ya hay una partida activa", Toast.LENGTH_SHORT).show();
            }

            /*if (ubicacionActiva != null && partidaActiva == null) {
                partidaActiva = controlador.startPartida(ubicacionActiva);
                listaPartidas = controlador.listarPartidas();
                chronometerTiempo.setBase(SystemClock.elapsedRealtime());
                chronometerTiempo.start();
                btnDetenerPartida.setEnabled(true);
                btnIniciarPartida.setEnabled(false);
                Toast.makeText(this, "Partida iniciada", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No se puede iniciar partida", Toast.LENGTH_SHORT).show();
            }

             */

        });

        // Listener para detener partida
        btnDetenerPartida.setOnClickListener(view -> {
            if (partidaActiva != null) {
                chronometerTiempo.stop();
                controlador.stopPartida(partidaActiva);
                Toast.makeText(this, "Partida detenida y guardada", Toast.LENGTH_SHORT).show();
                // Resetear variables
                partidaActiva = null;
                btnDetenerPartida.setEnabled(false);
                btnIniciarPartida.setEnabled(true);
            }
        });

        btnHistorialEstudio.setOnClickListener(view -> {
            // Navegar a PartidasActivity (historial de estudio)
            Intent intentHistorial = new Intent(MainActivity.this, PartidasActivity.class);
            startActivity(intentHistorial);
        });

        btnUbicacionesRegistradas.setOnClickListener(view -> {
            // Navegar a UbicacionesActivity (ubicaciones registradas)
            for (Ubicacion u : controlador.listarUbicaciones(usuarioLogeadoId)) {
                Log.d("UbicacionRegistrada", "Nombre: " + u.getNombre() + ", lat: " + u.getLatitud() + ", lon: " + u.getLongitud());
            }
            Intent intentUbicaciones = new Intent(MainActivity.this, UbicacionesActivity.class);
            startActivityForResult(intentUbicaciones,  REQUEST_CODE_UBICACIONES);

        });

        // Solicita los permisos de ubicación al abrir la app
        requestLocationPermission();
    }

    private void requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Verifica si ya está concedido el permiso
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION_PERMISSION);
            } else {
                obtenerUbicacion();
            }
        } else {
            obtenerUbicacion();
        }
    }

    private void cerrarSesion() {
        new AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Estás seguro de salir?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    SharedPreferences prefs = getSharedPreferences("MisPreferencias", MODE_PRIVATE);
                    prefs.edit().clear().apply();

                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                obtenerUbicacion();
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void obtenerUbicacion() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        procesarUbicacion(location);
                    } else {
                        tvUbicacionActual.setText("No se pudo obtener la ubicación");
                    }
                })
                .addOnFailureListener(e -> tvUbicacionActual.setText("Error: " + e.getMessage()));
    }

    /**
     * Procesa la ubicación obtenida comparándola con la lista de ubicaciones registradas.
     * Si coincide (dentro de un umbral) se muestra el nombre y se carga su imagen.
     * Si no, se muestran las coordenadas y se habilita el botón de guardar.
     */
    private void procesarUbicacion(Location location) {
        lat = location.getLatitude();
        lng = location.getLongitude();
        Ubicacion ubicacionEncontrada = buscarUbicacionRegistrada(lat, lng);

        if (ubicacionEncontrada != null) {
            // Si la ubicación está registrada, se habilita el botón para iniciar partida

            tvUbicacionActual.setText("Estás en: " + ubicacionEncontrada.getNombre());
            btnGuardarUbicacion.setEnabled(false);
            btnIniciarPartida.setEnabled(true);   // Se habilita iniciar partida
            btnDetenerPartida.setEnabled(false);

            ubicacionActiva = ubicacionEncontrada;
            // Nota: No iniciamos la partida automáticamente; se espera que el usuario
            // presione el botón de iniciar. Así se puede controlar la coexistencia
            // de partidas.

            String nombreImagen = ubicacionEncontrada.getImagen();
            if (nombreImagen == null || nombreImagen.isEmpty()) {
                Toast.makeText(this, "Imagen no registrada para esta ubicación", Toast.LENGTH_SHORT).show();
                //ivUbicacion.setImageResource(android.R.color.transparent);
                //ivUbicacion.setImageResource(R.drawable.paisaje);
                cargarImagen("paisaje.png");
            } else {
                cargarImagen(nombreImagen);
            }
        } else {

            cargarImagen("paisaje.png");
            String ubicacionDesconocida = "Estás en: Desconocido";
            String coordenadas = String.format("(Lat: %.5f, Lng: %.5f)", lat, lng);

            SpannableString textoFinal = new SpannableString(ubicacionDesconocida + "\n" + coordenadas);
            textoFinal.setSpan(new AbsoluteSizeSpan(24, true), 0, ubicacionDesconocida.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // Tamaño grande para la ubicación
            textoFinal.setSpan(new AbsoluteSizeSpan(12, true), ubicacionDesconocida.length(), textoFinal.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // Tamaño más pequeño para las coordenadas

            tvUbicacionActual.setText(textoFinal);

            btnGuardarUbicacion.setEnabled(true);
            btnIniciarPartida.setEnabled(false);
            chronometerTiempo.stop();
            chronometerTiempo.setBase(SystemClock.elapsedRealtime());
            //ivUbicacion.setImageResource(android.R.color.transparent);
            ubicacionActiva = null;
        }
    }

    private Ubicacion buscarUbicacionRegistrada(double lat, double lng) {
        final double tolerancia = 0.001;
        for (Ubicacion u : controlador.listarUbicaciones(usuarioLogeadoId)) {
            if (Math.abs(u.getLatitud() - lat) < tolerancia && Math.abs(u.getLongitud() - lng) < tolerancia) {
                return u;
            }
        }
        return null;
    }

    // Ahora devuelve boolean
    private boolean cargarImagenFromAssets(String nombreImagen) {
        AssetManager assetManager = getAssets();
        try (InputStream is = assetManager.open(nombreImagen)) {
            Bitmap originalBitmap = BitmapFactory.decodeStream(is);

            // Escalamos la imagen
            int targetWidth = 200, targetHeight = 200;
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, true);

            ivUbicacion.setImageBitmap(scaledBitmap);
            return true;  // Éxito
        } catch (IOException e) {
            // No existe en assets
            return false;
        }
    }

    private void cargarImagenDesdeInterno(String nombreImagen) {
        File archivoImagen = new File(getFilesDir(), nombreImagen);
        if (archivoImagen.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(archivoImagen.getAbsolutePath());
            ivUbicacion.setImageBitmap(bitmap); // No escalamos aquí
        } else {
            Toast.makeText(this, "Imagen no encontrada en almacenamiento interno", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarImagen(String nombreImagen) {
        if (nombreImagen == null || nombreImagen.isEmpty()) {
            Toast.makeText(this, "Esta ubicación no tiene imagen registrada", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ahora sí podemos usar el resultado en el if
        if (!cargarImagenFromAssets(nombreImagen)) {
            cargarImagenDesdeInterno(nombreImagen);
        }
    }




    /*private void cargarImagenFromAssets(String nombreImagen) {
        if (nombreImagen == null || nombreImagen.isEmpty()) {
            Toast.makeText(this, "Esta ubicación no tiene imagen registrada", Toast.LENGTH_SHORT).show();
            return;
        }

        AssetManager assetManager = getAssets();
        try (InputStream is = assetManager.open(nombreImagen)) {
            Bitmap originalBitmap = BitmapFactory.decodeStream(is);

            // Escalamos la imagen a un tamaño más pequeño
            int targetWidth = 200;
            int targetHeight = 200;
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, true);

            // Solo seteamos el Bitmap, NO modificamos layoutParams
            ivUbicacion.setImageBitmap(scaledBitmap);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error: La imagen no existe en assets", Toast.LENGTH_SHORT).show();
        }
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Aqui","Dentro de activity result");

        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Vuelves de UbicacionesActivity, refrescas la ubicación
            Log.d("Aqui","Dentro de sii");
            actualizarUbicacion();
        }

        if (requestCode == REQUEST_CODE_UBICACIONES && resultCode == RESULT_OK) {
            actualizarUbicacion();
        }
    }

    private void actualizarUbicacion() {
        // Supongamos que solo quieres mostrar la última ubicación
        Ubicacion ultima = controlador.listarUbicaciones(usuarioLogeadoId).get(controlador.listarUbicaciones(usuarioLogeadoId).size() - 1);
        Log.d("Aqui","Dentro de actualizar");
        Log.d("Aqui",ultima.getNombre());

        tvUbicacionActual.setText("Estás en: " + ultima.getNombre());
        btnGuardarUbicacion.setEnabled(false);
        btnIniciarPartida.setEnabled(true);   // Se habilita iniciar partida
        btnDetenerPartida.setEnabled(false);

        ubicacionActiva = ultima;
        // Nota: No iniciamos la partida automáticamente; se espera que el usuario
        // presione el botón de iniciar. Así se puede controlar la coexistencia
        // de partidas.

        String nombreImagen = ultima.getImagen();
        if (nombreImagen == null || nombreImagen.isEmpty()) {
            Toast.makeText(this, "Imagen no registrada para esta ubicación", Toast.LENGTH_SHORT).show();
            //ivUbicacion.setImageResource(android.R.color.transparent);
            //ivUbicacion.setImageResource(R.drawable.paisaje);
            cargarImagen("paisaje.png");
        } else {
            cargarImagen(nombreImagen);
        }
    }

}
