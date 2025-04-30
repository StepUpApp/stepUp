package arqsoft.stepupapp.main;


import android.app.Application;

import android.os.StrictMode;

import arqsoft.stepupapp.servicio.Controlador;
import dao.EjercicioDAO;
import dao.PartidaDAO;
import dao.UbicacionDAO;
import dao.UsuarioDAO;


public class LayerApplication extends Application {

    UbicacionDAO ubicacionDAO;
    EjercicioDAO ejercicioDAO;
    PartidaDAO partidaDAO;
    UsuarioDAO usuarioDAO;
    Controlador controlador;

    @Override
    public void onCreate() {
        super.onCreate();

        StrictMode.ThreadPolicy gfgPolicy = new StrictMode.ThreadPolicy.Builder()
                .permitAll()
                .build();
        StrictMode.setThreadPolicy(gfgPolicy);

        ubicacionDAO = new UbicacionDAO();
        ejercicioDAO = new EjercicioDAO();
        partidaDAO = new PartidaDAO();
        usuarioDAO = new UsuarioDAO();
        controlador = new Controlador(ubicacionDAO, ejercicioDAO, partidaDAO, usuarioDAO);

    }
    public Controlador getControler(){
        return controlador;
    }

}