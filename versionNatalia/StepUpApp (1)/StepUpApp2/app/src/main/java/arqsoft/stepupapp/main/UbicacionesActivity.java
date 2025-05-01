package arqsoft.stepupapp.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import arqsoft.stepupapp.R;
import arqsoft.stepupapp.adapter.UbicacionAdapter;
import arqsoft.stepupapp.servicio.ControladorRepo;
import modelo.Ubicacion;

public class UbicacionesActivity extends AppCompatActivity implements UbicacionAdapter.OnItemClickListener {

    private static final int CAMERA_REQUEST_CODE = 1002;
    private static final int GALLERY_REQUEST_CODE = 1003;
    private static final int PERMISO_UBICACION = 1001;
    private static final int PERMISO_CAMERA = 1004;
    private static final int PERMISO_ALMACENAMIENTO = 1005;

    private RecyclerView recyclerView;
    private UbicacionAdapter adapter;
    private ControladorRepo controlador;
    private Ubicacion ubicacionSeleccionada;
    private FusedLocationProviderClient fusedLocationClient;
    private AlertDialog currentDialog;

    private Button btnCrear;
    private Button btnEditar;
    private Button btnEliminar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubicaciones);

        controlador = ((LayerApplication) getApplicationContext()).getControler();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        inicializarVistas();
        configurarBotones();
        cargarUbicaciones();
        verificarPermisosIniciales();

        /*
        setResult(RESULT_OK);
        finish();*/
    }

    private void verificarPermisosIniciales() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISO_CAMERA);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISO_ALMACENAMIENTO);
        }
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, PERMISO_ALMACENAMIENTO);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISO_ALMACENAMIENTO);
        }*/
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
        ImageView ivPreview = dialogView.findViewById(R.id.ivPreview);
        Button btnSeleccionarImagen = dialogView.findViewById(R.id.btnSeleccionarImagen);

        if (ubicacionExistente != null) {
            etNombre.setText(ubicacionExistente.getNombre());
            etLatitud.setText(String.valueOf(ubicacionExistente.getLatitud()));
            etLongitud.setText(String.valueOf(ubicacionExistente.getLongitud()));
            if (ubicacionExistente.getImagen() != null) {
                cargarImagenEnVista(ivPreview, ubicacionExistente.getImagen());
            }
        }

        btnSeleccionarImagen.setOnClickListener(v -> mostrarSelectorImagen());
        btnObtenerUbicacion.setOnClickListener(v -> obtenerUbicacionActual(etLatitud, etLongitud));

        builder.setView(dialogView)
                .setTitle(ubicacionExistente == null ? "Nueva Ubicación" : "Editar Ubicación")
                .setPositiveButton("Guardar", (dialog, which) -> procesarGuardado(etNombre, etLatitud, etLongitud, ivPreview, ubicacionExistente))
                .setNegativeButton("Cancelar", null);

        currentDialog = builder.show();
    }

    private void procesarGuardado(EditText etNombre, EditText etLatitud, EditText etLongitud, ImageView ivPreview, Ubicacion ubicacionExistente) {
        try {
            String nombre = etNombre.getText().toString();
            double lat = Double.parseDouble(etLatitud.getText().toString());
            double lng = Double.parseDouble(etLongitud.getText().toString());
            String nombreImagen = guardarImagenDesdeVista(ivPreview);

            Ubicacion ubicacion = new Ubicacion(nombre, lat, lng);
            ubicacion.setImagen(nombreImagen);

            if (ubicacionExistente != null) {
                ubicacion.setObjectId(ubicacionExistente.getObjectId());
                controlador.actualizarUbicacion(ubicacionExistente.getObjectId(), ubicacion);
            } else {
                controlador.crearUbicacion(ubicacion);
            }

            cargarUbicaciones();
        } catch (NumberFormatException e) {
            mostrarError("Coordenadas inválidas");
        } catch (Exception e) {
            mostrarError("Error al guardar: " + e.getMessage());
        }
    }

    private void mostrarSelectorImagen() {
        new AlertDialog.Builder(this)
                .setTitle("Seleccionar imagen")
                .setItems(new String[]{"Cámara", "Galería"}, (dialog, which) -> {
                    if (which == 0) {
                        abrirCamara();
                    } else {
                        abrirGaleria();
                    }
                })
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK && currentDialog != null) {
            ImageView ivPreview = currentDialog.findViewById(R.id.ivPreview);

            try {
                if (requestCode == CAMERA_REQUEST_CODE && data != null) {
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    ivPreview.setImageBitmap(photo);
                } else if (requestCode == GALLERY_REQUEST_CODE && data != null) {
                    Uri selectedImage = data.getData();
                    ivPreview.setImageURI(selectedImage);
                }
            } catch (Exception e) {
                mostrarError("Error al cargar la imagen");
            }
        }
    }

    private String guardarImagenDesdeVista(ImageView imageView) {
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        if (drawable == null) return null;

        Bitmap bitmap = drawable.getBitmap();
        String nombreImagen = "ubicacion_" + System.currentTimeMillis() + ".jpg";

        File archivo = new File(getFilesDir(), nombreImagen);
        try (FileOutputStream out = new FileOutputStream(archivo)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            return nombreImagen;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*private String guardarImagenDesdeVista(ImageView imageView) {
        Bitmap bitmap = ((android.graphics.drawable.BitmapDrawable) imageView.getDrawable()).getBitmap();
        if (bitmap == null) return null;

        String nombreArchivo = "ubicacion_" + System.currentTimeMillis() + ".jpg";
        File directorio = new File(getFilesDir(), "ubicaciones_images");
        if (!directorio.exists() && !directorio.mkdirs()) return null;

        File archivo = new File(directorio, nombreArchivo);
        try (FileOutputStream out = new FileOutputStream(archivo)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            return nombreArchivo;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    } */

    /*
    private void cargarImagenEnVista(ImageView imageView, String nombreArchivo) {
        try {
            File imageFile = new File(getFilesDir(), "ubicaciones_images/" + nombreArchivo);
            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            imageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            imageView.setImageResource(R.drawable.paisaje);
        }
    }*/

    private void cargarImagenEnVista(ImageView imageView, String nombreImagen) {
        if (nombreImagen == null || nombreImagen.isEmpty()) {
            imageView.setImageResource(R.drawable.paisaje);
            return;
        }

        // 1) Intentar cargar desde almacenamiento interno
        File archivoImagen = new File(getFilesDir(), nombreImagen);
        if (archivoImagen.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(archivoImagen.getAbsolutePath());
            imageView.setImageBitmap(bitmap);
            return;
        }

        // 2) Si no existe en interno, intentar cargar desde assets
        AssetManager assetManager = getAssets();
        try (InputStream is = assetManager.open(nombreImagen)) {
            Bitmap original = BitmapFactory.decodeStream(is);
            imageView.setImageBitmap(original);
        } catch (IOException e) {
            // Ni en assets: mostrar placeholder
            e.printStackTrace();
            imageView.setImageResource(R.drawable.paisaje);
        }
    }

    private void abrirCamara() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISO_CAMERA);
            return;
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        }
    }

    private void abrirGaleria() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, PERMISO_ALMACENAMIENTO);
                return;
            }
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISO_ALMACENAMIENTO);
                return;
            }
        }
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    /*private void abrirGaleria() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISO_ALMACENAMIENTO);
            return;
        }
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    } */

    private void obtenerUbicacionActual(EditText etLatitud, EditText etLongitud) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISO_UBICACION);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length == 0) return;

        switch (requestCode) {
            case PERMISO_UBICACION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permiso de ubicación concedido", Toast.LENGTH_SHORT).show();
                } else {
                    mostrarError("Permiso de ubicación denegado");
                }
                break;

            case PERMISO_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permiso de cámara concedido", Toast.LENGTH_SHORT).show();
                    abrirCamara(); // Llama de nuevo para proceder ahora que está permitido
                } else {
                    mostrarError("Permiso de cámara denegado");
                }
                break;

            case PERMISO_ALMACENAMIENTO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permiso concedido, continuar con la operación
                    abrirGaleria();
                }
                /*else {
                    // Permiso denegado, mostrar mensaje o manejar el caso
                    Toast.makeText(this, "Permiso de almacenamiento denegado", Toast.LENGTH_SHORT).show();
                }*/
                break;
        }
    }

    // Método auxiliar para comprobar permisos por nombre
    private boolean tienePermisoConcedido(String[] permissions, int[] grantResults, String permiso) {
        for (int i = 0; i < permissions.length; i++) {
            if (permissions[i].equals(permiso)) {
                return grantResults[i] == PackageManager.PERMISSION_GRANTED;
            }
        }
        return false;
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
            controlador.eliminarUbicacion(ubicacionSeleccionada.getObjectId());
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
        adapter.setSelectedPosition(controlador.listarUbicaciones().indexOf(ubicacion));
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

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        // Opcional: puedes enviar datos si quieres
        // resultIntent.putExtra("clave", "valor");

        setResult(RESULT_OK, resultIntent);
        finish();
    }
}