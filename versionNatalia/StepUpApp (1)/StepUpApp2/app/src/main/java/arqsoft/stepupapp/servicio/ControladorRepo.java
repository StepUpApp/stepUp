package arqsoft.stepupapp.servicio;

import android.util.Log;
import repository.*;
import modelo.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ControladorRepo {


    private final APIRESTUbicacionRepository ubicacionRepo;
    private final APIRESTEjercicioRepository ejercicioRepo;
    private final APIRESTPartidaRepository partidaRepo;
    private final APIRESTUsuarioRepository usuarioRepo;

    public ControladorRepo(APIRESTUbicacionRepository ubicacionRepo,
                           APIRESTEjercicioRepository ejercicioRepo,
                           APIRESTPartidaRepository partidaRepo,
                           APIRESTUsuarioRepository usuarioRepo) {
        this.ubicacionRepo = ubicacionRepo;
        this.ejercicioRepo = ejercicioRepo;
        this.partidaRepo = partidaRepo;
        this.usuarioRepo = usuarioRepo;

    }


    // Métodos para Ubicaciones
    public String crearUbicacion(Ubicacion ubicacion) {
        return ubicacionRepo.insert(ubicacion);
    }

    public List<Ubicacion> listarUbicaciones() {
        return ubicacionRepo.getAll();
    }

    public Ubicacion obtenerUbicacion(String objectId) {
        return ubicacionRepo.getById(objectId);
    }

    public boolean actualizarUbicacion(String objectId, Ubicacion ubicacion) {
        return ubicacionRepo.update(objectId, ubicacion);
    }

    public boolean eliminarUbicacion(String objectId) {
        return ubicacionRepo.delete(objectId);
    }

    // Métodos para Ejercicios
    public List<Ejercicio> listarEjercicios() {
        return ejercicioRepo.getAll();
    }

    public List<Ejercicio> calcularEjercicios(long tiempoSentado, Usuario usuario) {
        List<Ejercicio> ejercicios = ejercicioRepo.getAll();
        List<Ejercicio> resultados = new ArrayList<>();

        // Lógica de cálculo local (adaptada de tu DAO original)
        for (Ejercicio e : ejercicios) {
            double factor = calcularFactorPersonalizado(
                    e.getFactorConversion(),
                    usuario.getPeso(),
                    usuario.getAltura(),
                    usuario.getEdad(),
                    tiempoSentado
            );

            int factorRedondeado = (int) Math.ceil(factor);

            resultados.add(new Ejercicio(
                    e.getNombre(),
                    factorRedondeado,
                    e.getUnidadMedida(),
                    e.getImagen()
            ));
        }
        return resultados;
    }

    private double calcularFactorPersonalizado(double base, double peso, double altura, int edad, long tiempo) {
        // Implementa tu lógica de cálculo aquí
        return base * (peso / 70) * (tiempo / 3600000.0); // Ejemplo simplificado
    }

    // Métodos para Partidas
    public Partida startPartida(String username, Ubicacion ubicacion) {
        Partida nuevaPartida = new Partida(System.currentTimeMillis(), username, ubicacion);
        nuevaPartida.setUbicacion(ubicacion);
        String objectId = partidaRepo.insert(nuevaPartida);
        return partidaRepo.getById(objectId);
    }

    public void stopPartida(Partida partida) {
        partida.setTiempoFinal(System.currentTimeMillis());
        partidaRepo.update(partida.getObjectId(), partida);
    }

    public List<Partida> listarPartidas() {
        return partidaRepo.getAll();
    }

    public Partida obtenerPartida(String objectId) {
        return partidaRepo.getById(objectId);
    }

    // Métodos para Usuarios
    public String crearUsuario(Usuario usuario) {
        return usuarioRepo.insert(usuario);
    }

    public Usuario obtenerUsuario(String objectId) {
        return usuarioRepo.getById(objectId);
    }

    public Usuario obtenerUsuarioPorUsername(String username) {
        return usuarioRepo.getByUsername(username);
    }

    public boolean actualizarUsuario(String objectId, Usuario usuario) {
        return usuarioRepo.update(objectId, usuario);
    }

    public boolean eliminarUsuario(String objectId) {
        return usuarioRepo.delete(objectId);
    }

    // Métodos auxiliares
    public void cerrarConexiones() {
        ubicacionRepo.cerrarConexion();
        ejercicioRepo.cerrarConexion();
        partidaRepo.cerrarConexion();
        usuarioRepo.cerrarConexion();
    }

    public void borrarEjercicio(String objectId) {
        ejercicioRepo.delete(objectId);
    }

    public void crearEjercicio(Ejercicio ejercicio) {
        ejercicioRepo.insert(ejercicio);
    }

    public Ejercicio obtenerEjercicio(String objectId) {
        return ejercicioRepo.getById(objectId);
    }

    public boolean actualizarEjercicio(String objectId, Ejercicio ejercicio) {
        return ejercicioRepo.update(objectId, ejercicio);
    }

    public boolean eliminarEjercicio(String objectId) {
        return ejercicioRepo.delete(objectId);
    }

    /* ------------------------- *
     *  CRUD PARA PARTIDAS       *
     * ------------------------- */
    public Partida crearPartida(Partida partida) {
        String objectId = partidaRepo.insert(partida);
        return partidaRepo.getById(objectId);
    }


    public boolean actualizarPartida(String objectId, Partida partida) {
        return partidaRepo.update(objectId, partida);
    }

    public boolean eliminarPartida(String objectId) {
        return partidaRepo.delete(objectId);
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepo.getAll();
    }

    public void borrarPartida(String objectId) {
        partidaRepo.delete(objectId);
    }

    public void editarUsuario(Usuario usuario) {
        usuarioRepo.update(usuario.objectId, usuario);
    }

    public void borrarUsuario(String objectId) {
        usuarioRepo.delete(objectId);
    }

    public List<Partida> listarPartidasPorUsuario(String username) {
        List<Partida> todasPartidas = partidaRepo.getAll();
        List<Partida> partidasFiltradas = new ArrayList<>();

        for (Partida partida : todasPartidas) {
            if (partida.getUsername() != null && partida.getUsername().equals(username)) {
                partidasFiltradas.add(partida);
            }
        }
        return partidasFiltradas;
    }
}