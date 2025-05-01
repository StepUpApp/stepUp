package arqsoft.stepupapp.main;


import android.app.Application;

import android.content.SharedPreferences;
import android.os.StrictMode;

import arqsoft.stepupapp.servicio.ControladorRepo;
import repository.APIRESTEjercicioRepository;
import repository.APIRESTPartidaRepository;
import repository.APIRESTUbicacionRepository;
import repository.APIRESTUsuarioRepository;


public class LayerApplication extends Application {

    /*UbicacionDAO ubicacionDAO;
    EjercicioDAO ejercicioDAO;
    PartidaDAO partidaDAO;
    UsuarioDAO usuarioDAO;
    Controlador controlador;*/

    private ControladorRepo controladorRepo;
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

        /*ubicacionDAO = new UbicacionDAO();
        ejercicioDAO = new EjercicioDAO();
        partidaDAO = new PartidaDAO();
        usuarioDAO = new UsuarioDAO();
        controlador = new Controlador(ubicacionDAO, ejercicioDAO, partidaDAO, usuarioDAO);*/

        ubicacionRepo = new APIRESTUbicacionRepository();
        ejercicioRepo = new APIRESTEjercicioRepository();
        partidaRepo = new APIRESTPartidaRepository();
        usuarioRepo = new APIRESTUsuarioRepository();

        controladorRepo = new ControladorRepo(ubicacionRepo, ejercicioRepo, partidaRepo, usuarioRepo);

    }
    public ControladorRepo getControler(){
        return controladorRepo;
    }

    public String getCurrentUsername() {
        SharedPreferences prefs = getSharedPreferences("MisPreferencias", MODE_PRIVATE);
        return prefs.getString("username", null);
    }
}