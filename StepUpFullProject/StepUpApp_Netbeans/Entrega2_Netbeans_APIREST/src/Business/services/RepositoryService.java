package Business.services;

import Business.modelo.Ejercicio;
import Business.modelo.Partida;
import Business.modelo.Ubicacion;
import Business.modelo.Usuario;
import DataAccess.repository.APIRESTEjercicioRepository;
import DataAccess.repository.APIRESTPartidaRepository;
import DataAccess.repository.APIRESTUbicacionRepository;
import DataAccess.repository.APIRESTUsuarioRepository;

import java.util.ArrayList;
import java.util.List;

public class RepositoryService {

    private final APIRESTUbicacionRepository ubicacionRepo;
    private final APIRESTEjercicioRepository ejercicioRepo;
    private final APIRESTPartidaRepository partidaRepo;
    private final APIRESTUsuarioRepository usuarioRepo;

    public RepositoryService(APIRESTUbicacionRepository ubicacionRepo,
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
        return ubicacionRepo.crearUbicacion(ubicacion);
    }

    public List<Ubicacion> listarUbicaciones() {

        return ubicacionRepo.listarUbicaciones();
    }

    public Ubicacion obtenerUbicacion(String objectId) {
        return ubicacionRepo.obtenerUbicacionPorID(objectId);
    }

    public boolean actualizarUbicacion(String objectId, Ubicacion ubicacion) {
        return ubicacionRepo.editarUbicacion(ubicacion);
    }

    public boolean eliminarUbicacion(String objectId) {

        return ubicacionRepo.borrarUbicacion(objectId);
    }

    // Métodos para Ejercicios
    public List<Ejercicio> listarEjercicios() {

        return ejercicioRepo.listarEjercicios();
    }

    public List<Ejercicio> calcularEjercicios(long tiempoSentado, Usuario usuario) {
        List<Ejercicio> ejercicios = ejercicioRepo.listarEjercicios();
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
        String objectId = partidaRepo.guardarPartida(nuevaPartida);
        return partidaRepo.obtenerPartidaPorId(objectId);
    }

    public void stopPartida(Partida partida) {
        partida.setTiempoFinal(System.currentTimeMillis());
        partidaRepo.editarPartida(partida);
    }

    public List<Partida> listarPartidas() {

        return partidaRepo.listarPartidas();
    }

    public Partida obtenerPartida(String objectId) {

        return partidaRepo.obtenerPartidaPorId(objectId);
    }

    // Métodos para Usuarios
    public String crearUsuario(Usuario usuario) {

        return usuarioRepo.crearUsuario(usuario);
    }

    public Usuario obtenerUsuario(String objectId) {

        return usuarioRepo.recuperarUsuarioPorId(objectId);
    }

    public Usuario obtenerUsuarioPorUsername(String username) {
        return usuarioRepo.getByUsername(username);
    }

    public boolean actualizarUsuario(String objectId, Usuario usuario) {
        return usuarioRepo.editarUsuario( usuario);
    }

    public boolean eliminarUsuario(String objectId) {

        return usuarioRepo.borrarUsuario(objectId);
    }

    // Métodos auxiliares
    public void cerrarConexiones() {
        ubicacionRepo.cerrarConexion();
        ejercicioRepo.cerrarConexion();
        partidaRepo.cerrarConexion();
        usuarioRepo.cerrarConexion();
    }

    public void borrarEjercicio(String objectId) {
        ejercicioRepo.borrarEjercicio(objectId);
    }

    public void crearEjercicio(Ejercicio ejercicio) {

        ejercicioRepo.crearEjercicio(ejercicio);
    }

    public Ejercicio obtenerEjercicio(String objectId) {

        return ejercicioRepo.obtenerEjercicioPorId(objectId);
    }

    public boolean actualizarEjercicio(String objectId, Ejercicio ejercicio) {
        return ejercicioRepo.editarEjercicio(ejercicio);
    }

    public boolean eliminarEjercicio(String objectId) {

        return ejercicioRepo.borrarEjercicio(objectId);
    }

    /* ------------------------- *
     *  CRUD PARA PARTIDAS       *
     * ------------------------- */
    public Partida crearPartida(Partida partida) {
        String objectId = partidaRepo.guardarPartida(partida);
        return partidaRepo.obtenerPartidaPorId(objectId);
    }


    public boolean actualizarPartida(String objectId, Partida partida) {
        return partidaRepo.editarPartida(partida);
    }

    public boolean eliminarPartida(String objectId) {

        return partidaRepo.borrarPartida(objectId);
    }

    public List<Usuario> listarUsuarios() {

        return usuarioRepo.listarUsuarios();
    }

    public void borrarPartida(String objectId) {

        partidaRepo.borrarPartida(objectId);
    }

    public void editarUsuario(Usuario usuario) {

        usuarioRepo.editarUsuario(usuario);
    }

    public void borrarUsuario(String objectId) {

        usuarioRepo.borrarUsuario(objectId);
    }

    public List<Partida> listarPartidasPorUsuario(String username) {
        List<Partida> todasPartidas = partidaRepo.listarPartidas();
        List<Partida> partidasFiltradas = new ArrayList<>();

        for (Partida partida : todasPartidas) {
            if (partida.getUsername() != null && partida.getUsername().equals(username)) {
                partidasFiltradas.add(partida);
            }
        }
        return partidasFiltradas;
    }
}
