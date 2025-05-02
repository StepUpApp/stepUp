package arqsoft.stepupapp.main;


import android.app.Application;

import android.content.SharedPreferences;
import android.os.StrictMode;

import DataAccess.repository.APIRESTEjercicioRepository;
import DataAccess.repository.APIRESTPartidaRepository;
import DataAccess.repository.APIRESTUbicacionRepository;
import DataAccess.repository.APIRESTUsuarioRepository;
import Business.services.RepositoryService;


public class LayerApplication extends Application {

    /*UbicacionDAO ubicacionDAO;
    EjercicioDAO ejercicioDAO;
    PartidaDAO partidaDAO;
    UsuarioDAO usuarioDAO;
    Controlador controlador;*/

    private RepositoryService servicioRepo;
    APIRESTUbicacionRepository ubicacionRepo;
    APIRESTEjercicioRepository ejercicioRepo;
    APIRESTPartidaRepository partidaRepo;
    APIRESTUsuarioRepository usuarioRepo;

    @Override
    public void onCreate() {
        super.onCreate();

        StrictMode.ThreadPolicy gfgPolicy = new StrictMode.ThreadPolicy.Builder()
                .permitAll()
                .build();
        StrictMode.setThreadPolicy(gfgPolicy);


        ubicacionRepo = new APIRESTUbicacionRepository();
        ejercicioRepo = new APIRESTEjercicioRepository();
        partidaRepo = new APIRESTPartidaRepository();
        usuarioRepo = new APIRESTUsuarioRepository();

        servicioRepo = new RepositoryService(ubicacionRepo, ejercicioRepo, partidaRepo, usuarioRepo);

    }
    public RepositoryService getControler(){
        return servicioRepo;
    }

    public String getCurrentUsername() {
        SharedPreferences prefs = getSharedPreferences("MisPreferencias", MODE_PRIVATE);
        return prefs.getString("username", null);
    }
}