package arqsoft.stepupapp.main;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import arqsoft.stepupapp.R;
import modelo.Ubicacion;
import android.widget.EditText;
import android.widget.Button;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class GuardarUbicacionActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int GALLERY_REQUEST_CODE = 2;
    private ImageView imageViewUbicacion;
    private AppCompatImageButton btnCamera;
    private EditText etLatitud, etLongitud;
    private Button btnGuardarUbicacion;
    private EditText editTextNombreUbicacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guardar_ubicacion);

        if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 100);
        }

        // Inicializa las vistas
        imageViewUbicacion = findViewById(R.id.imageViewUbicacion);
        btnCamera = findViewById(R.id.btnCamera);
        etLatitud = findViewById(R.id.etLatitud);
        etLongitud = findViewById(R.id.etLongitud);
        btnGuardarUbicacion = findViewById(R.id.btnGuardarUbicacion);
        editTextNombreUbicacion = findViewById(R.id.editTextNombreUbicacion);
        setSupportActionBar(findViewById(R.id.myToolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }


        // Recibimos los datos del Intent
        Intent intent = getIntent();
        double latitud = intent.getDoubleExtra("LATITUD", 0.0); // Si no existe, 0.0 será el valor predeterminado
        double longitud = intent.getDoubleExtra("LONGITUD", 0.0); // Si no existe, 0.0 será el valor predeterminado

        // Ponemos las coordenadas en los EditText
        etLatitud.setText(String.valueOf(latitud));
        etLongitud.setText(String.valueOf(longitud));

        btnCamera.setOnClickListener(v -> {
            // Abre la cámara o la galería
            showImageSourceDialog();
        });

        btnGuardarUbicacion.setOnClickListener(v -> {

            String nombre = editTextNombreUbicacion.getText().toString().trim();
            if (nombre.isEmpty()) {
                Toast.makeText(this, "Ingresa un nombre para la ubicación", Toast.LENGTH_SHORT).show();
                return;
            }

            double lat = Double.parseDouble(etLatitud.getText().toString());
            double lon = Double.parseDouble(etLongitud.getText().toString());

            // Guardar la imagen
            String imageFileName = nombre.replaceAll("\\s+", "_").toLowerCase() + ".jpg";
            guardarImagenEnAssets(imageFileName);

            // Crear la ubicación
            Ubicacion nuevaUbicacion = new Ubicacion(nombre, lat, lon, imageFileName);
            ((LayerApplication)getApplicationContext()).getControler().crearUbicacion(nuevaUbicacion);

            Toast.makeText(this, "Ubicación guardada con éxito", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();

        });
    }

    private void showImageSourceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(new CharSequence[]{"Tomar foto", "Seleccionar de galería"}, (dialog, which) -> {
            if (which == 0) {
                // Abrir cámara
                openCamera();
            } else {
                // Abrir galería
                openGallery();
            }
        });
        builder.show();
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    private void guardarImagenEnAssets(String nombreArchivo) {
        BitmapDrawable drawable = (BitmapDrawable) imageViewUbicacion.getDrawable();
        if (drawable == null) return;

        Bitmap bitmap = drawable.getBitmap();

        File assetsDir = new File(getFilesDir(), "../assets");
        if (!assetsDir.exists()) {
            assetsDir.mkdirs();
        }

        File archivoDestino = new File(assetsDir, nombreArchivo);
        try (FileOutputStream out = new FileOutputStream(archivoDestino)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al guardar la imagen", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imageViewUbicacion.setImageBitmap(photo);
            } else if (requestCode == GALLERY_REQUEST_CODE) {
                Uri selectedImage = data.getData();
                imageViewUbicacion.setImageURI(selectedImage);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
