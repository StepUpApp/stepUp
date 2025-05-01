package arqsoft.stepupapp.servicio;

import android.util.Log;

import dao.*;
import modelo.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Controlador {

    private static Controlador instance;

    private UbicacionDAOInterface ubicacionDAO;
    private EjercicioDAOInterface ejercicioDAO;
    private PartidaDAOInterface partidaDAO;
    private UsuarioDAOInterface usuarioDAO;

    public Controlador(UbicacionDAOInterface ubicacionDAO,
                       EjercicioDAOInterface ejercicioDAO,
                       PartidaDAOInterface partidaDAO,
                       UsuarioDAOInterface usuarioDAO) {
        // Instanciación de los DAO
        this.ubicacionDAO = ubicacionDAO;
        this.ejercicioDAO = ejercicioDAO;
        this.partidaDAO = partidaDAO;
        this.usuarioDAO = usuarioDAO;

        Log.e("Instancia","Nueva Indstancia");
        crearDatosIniciales();
    }

    /*public static synchronized Controlador getInstance(UbicacionDAOInterface ubicacionDAO,
                                                       EjercicioDAOInterface ejercicioDAO,
                                                       PartidaDAOInterface partidaDAO,
                                                       UsuarioDAOInterface usuarioDAO) {
        if (instance == null) {

            instance = new Controlador(ubicacionDAO,ejercicioDAO,partidaDAO,usuarioDAO);
        }
        return instance;
    }*/



    private void crearDatosIniciales() {
        // Crear ubicaciones comunes
        List<Ubicacion> ubicaciones = Arrays.asList(
                new Ubicacion("Aulario", 42.8003049, -1.6367728, "aulario.jpeg"),
                // new Ubicacion("Casa", 42.7851134, -1.6175174, "casa.jpg"),
                new Ubicacion("Biblioteca", 42.7992792, -1.6354724, "biblioteca.jpg"),
                new Ubicacion("Edificio Los Pinos", 42.8000765, -1.6341928)
        );

        ubicaciones.forEach(ubicacionDAO::crearUbicacion);

        // Crear ejercicios básicos
        List<Ejercicio> ejercicios = Arrays.asList(
                new Ejercicio("Andar", 2.0, "pasos"),
                new Ejercicio("Correr", 10.0, "pasos"),
                new Ejercicio("Saltar", 1.0, "saltos"),
                new Ejercicio("Nadar", 5.0, "metros"),
                new Ejercicio("Flexiones", 2.0, "repeticiones")
        );

        ejercicios.forEach(ejercicioDAO::crearEjercicio);

        // Crear usuarios de ejemplo
        List<Usuario> usuarios = Arrays.asList(
                new Usuario("Juan", 20, 1.73, 70.0),
                new Usuario("Lucía", 23, 1.65, 70.0),
                new Usuario("n", 23, 1.65, 70.0)
        );

        usuarios.forEach(usuarioDAO::crearUsuario);
    }


    public void guardarPartida(long tiempoInicio, long tiempoFinal, Ubicacion ubicacion, int usuarioId) {
        partidaDAO.guardarPartida(tiempoInicio, tiempoFinal, ubicacion, usuarioId);
    }
    // Métodos para CRUD de UbicacionDAO
    public void crearUbicacion(Ubicacion ubicacion) {
        ubicacionDAO.crearUbicacion(ubicacion);
    }

    public List<Ubicacion> listarUbicaciones() {
        return ubicacionDAO.listarUbicaciones();
    }

    // Métodos para CRUD de EjercicioDAO
    public List<Ejercicio> calcularEjercicios(long tiempoSentado, int id) {
        Usuario usuario = usuarioDAO.obtenerUsuarioPorId(id);
        if (usuario != null) {
            double peso = usuario.getPeso();
            double altura = usuario.getAltura();
            int edad = usuario.getEdad();

            List<Ejercicio> ejerciciosCalculados = ejercicioDAO.calcularEjercicios(tiempoSentado, peso, altura, edad);
            System.out.println("Ejercicios recomendados:");
            for (Ejercicio e : ejerciciosCalculados) {
                System.out.println(e.getNombre() + ": " + e.getFactorConversion() + " " + e.getUnidadMedida());
            }
            return ejerciciosCalculados;
        } else {
            System.out.println("Usuario no encontrado.");
        }
        return null;
    }

    public void crearEjercicio(Ejercicio nuevoEjercicio) {
        ejercicioDAO.crearEjercicio(nuevoEjercicio);
    }



    public Partida startPartida(String username, Ubicacion ubicacion) {

        return partidaDAO.startPartida(username, ubicacion);
    }

    // Al detener la partida se obtiene la lista de ejercicios para calcular las repeticiones
    public void stopPartida(Partida partida) {
        partidaDAO.stopPartida(partida);
    }

    public List<Partida> listarPartidas() {
        return partidaDAO.listarPartidas();
    }

    public void mostrarTiempoSentado(int id) {
        long tiempo = partidaDAO.getTiempoSentado(id);
        System.out.println("Tiempo sentado en la partida " + id + ": " + tiempo + " ms");
    }

    // Métodos para CRUD de UsuarioDAO
    public void crearUsuario(Usuario usuario) {
        usuarioDAO.crearUsuario(usuario);
    }

    public List<Usuario> listarUsuarios() {
        return usuarioDAO.listarUsuarios();
    }

    public void editarUsuario(Usuario usuario) {
        usuarioDAO.editarUsuario(usuario);
    }

    public void borrarUsuario(int id) {
        usuarioDAO.borrarUsuario(id);
    }

    public List<Ejercicio> listarEjercicios() {
        List<Ejercicio> ejercicios = ejercicioDAO.listarEjercicios();
        if (ejercicios.isEmpty()) {
            System.out.println("No hay ejercicios disponibles.");
        } else {
            System.out.println("Lista de ejercicios:");
            for (Ejercicio e : ejercicios) {
                System.out.println(e.getNombre() + " - Factor/minuto: "
                        + e.getFactorConversion() + " " + e.getUnidadMedida());
            }
        }
        return ejercicios;
    }

    public void borrarEjercicio(int id) {
        ejercicioDAO.borrarEjercicio(id);
    }

    public void editarEjercicio(Ejercicio ejercicio) {
        ejercicioDAO.editarEjercicio(ejercicio);
    }

    public long getTiempoSentado(int id) {
        return partidaDAO.getTiempoSentado(id);
    }

    public Ubicacion obtenerUbicacion(int id) {
        return ubicacionDAO.obtenerUbicacionPorID(id);
    }

    // Métodos auxiliares adicionales
    private String formatearFechaHora(long timestamp) {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date(timestamp));
    }

    private String formatearDuracion(long milisegundos) {
        long segundos = milisegundos / 1000;
        return String.format("%02d:%02d:%02d", segundos / 3600, (segundos % 3600) / 60, segundos % 60);
    }

    public List<Partida> obtenerPartidasActivas() {
        List<Partida> partidas = partidaDAO.getPartidasActivas();
        return partidas;
    }

    public Partida obtenerPartida(int id) {
        return partidaDAO.obtenerPartidaPorId(id);
    }

    public Usuario obtenerUsuario(int id) {
        return usuarioDAO.obtenerUsuarioPorId(id);
    }

    public void actualizarUsuario(int id, String nombre, int edad, double altura, double peso) {
        Usuario usuarioActualizado = new Usuario(nombre, edad, altura, peso);
        usuarioDAO.editarUsuario(usuarioActualizado);
    }

    public Ejercicio obtenerEjercicio(int id) {
        return ejercicioDAO.obtenerEjercicioPorId(id);
    }

    public void actualizarEjercicio(String nombre, double factor, String unidad) {
        Ejercicio ejActualizado = new Ejercicio(nombre,factor, unidad);
        ejercicioDAO.editarEjercicio(ejActualizado);
    }

    public void actualizarUbicacion(Ubicacion ubicacion) {
        ubicacionDAO.editarUbicacion(ubicacion);
    }

    public void eliminarUbicacion(int id) {
        ubicacionDAO.borrarUbicacion(id);
    }

    public List<Partida> getPartidasActivas() {
        return partidaDAO.getPartidasActivas();
    }

    public void borrarPartida(int partidaId) {
        partidaDAO.borrarPartida(partidaId);
    }

    public Usuario obtenerUsuarioPorUsername(String username) {
        Usuario usuario = usuarioDAO.obtenerUsuarioPorUsername(username);
        return usuario;
    }
}
