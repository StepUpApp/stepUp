//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package Business.services;

import DataAccess.dao.EjercicioDAO;
import DataAccess.dao.PartidaDAO;
import DataAccess.dao.UbicacionDAO;
import DataAccess.dao.UsuarioDAO;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import Business.modelo.Ejercicio;
import Business.modelo.Partida;
import Business.modelo.Ubicacion;
import Business.modelo.Usuario;

public class DaoService {
    private UbicacionDAO ubicacionDAO = new UbicacionDAO();
    private EjercicioDAO ejercicioDAO = new EjercicioDAO();
    private PartidaDAO partidaDAO = new PartidaDAO();
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    public DaoService() {
        this.crearDatosIniciales();
    }

    private void crearDatosIniciales() {
        List<Ejercicio> ejercicios = Arrays.asList(new Ejercicio("Andar", 2.0, "pasos/minuto"), new Ejercicio("Correr", 10.0, "pasos/minuto"), new Ejercicio("Saltar", 15.0, "saltos/minuto"), new Ejercicio("Nadar", 5.0, "metros/minuto"), new Ejercicio("Flexiones", 20.0, "repeticiones/minuto"));
        EjercicioDAO var10001 = this.ejercicioDAO;
        ejercicios.forEach(var10001::crearEjercicio);
        List<Ubicacion> ubicaciones = Arrays.asList(new Ubicacion("Aulario", 36.714946, -4.478879), new Ubicacion("Casa", 36.721234, -4.419876), new Ubicacion("Biblioteca", 36.718765, -4.428912));
        UbicacionDAO var4 = this.ubicacionDAO;
        ubicaciones.forEach(var4::crearUbicacion);
        List<Usuario> usuarios = Arrays.asList(new Usuario("Juan", 20, 1.73, 70.0), new Usuario("Lucía", 23, 1.65, 70.0));
        UsuarioDAO var5 = this.usuarioDAO;
        usuarios.forEach(var5::crearUsuario);
    }

    public static void main(String[] args) throws InterruptedException {
        DaoService daoService = new DaoService();
        Partida partidaActual = null;
        System.out.println("Creando usuarios...");
        daoService.crearUsuario("Juan Pérez", 30, 75.5, 1.8);
        daoService.crearUsuario("Ana López", 25, 65.0, 1.7);
        System.out.println("Lista de usuarios:");
        List<Usuario> usuarios = daoService.listarUsuarios();
        Iterator var4 = usuarios.iterator();

        while(var4.hasNext()) {
            Usuario usuario = (Usuario)var4.next();
            System.out.println(usuario);
        }

        System.out.println("Creando ubicaciones...");
        daoService.crearUbicacion("Parque Central", 40.7128, -74.006);
        daoService.crearUbicacion("Plaza Mayor", 41.6528, -4.7247);
        System.out.println("Lista de ubicaciones:");
        List<Ubicacion> ubicaciones = daoService.listarUbicaciones();
        Iterator var10 = ubicaciones.iterator();

        while(var10.hasNext()) {
            Ubicacion u = (Ubicacion)var10.next();
            System.out.println(u);
        }

        if (!ubicaciones.isEmpty()) {
            Ubicacion ubicacionAleatoria = (Ubicacion)ubicaciones.get((new Random()).nextInt(ubicaciones.size()));
            System.out.println("Ubicación seleccionada: " + ubicacionAleatoria);
            System.out.println("Iniciando partida...");
            String username = "automático";
            partidaActual = daoService.startPartida(username, ubicacionAleatoria);
        } else {
            System.out.println("No hay ubicaciones disponibles.");
        }

        System.out.println("Lista de partidas:");
        List<Partida> partidas = daoService.listarPartidas();
        Iterator var13 = partidas.iterator();

        while(var13.hasNext()) {
            Partida partida = (Partida)var13.next();
            System.out.println(partida);
        }

        System.out.println("Creando ejercicios...");
        daoService.crearEjercicio("Sentadillas", 1.0, "repeticiones");
        daoService.crearEjercicio("Saltos", 1.5, "repeticiones");
        Thread.sleep(10000L);
        if (!partidas.isEmpty()) {
            int partidaId = ((Partida)partidas.get(0)).getPartidaId();
            System.out.println("Deteniendo partida " + partidaId);
            daoService.stopPartida(partidaActual);
        }

        int idUsuario = 1;
        long tiempoSentado = ((Partida)partidas.get(0)).getTiempoSentado();
        daoService.calcularEjercicios(tiempoSentado, idUsuario);
    }

    public void crearUbicacion(String nombre, double latitud, double longitud) {
        Ubicacion ubicacion = new Ubicacion(nombre, latitud, longitud);
        this.ubicacionDAO.crearUbicacion(ubicacion);
    }

    public List<Ubicacion> listarUbicaciones() {
        return this.ubicacionDAO.listarUbicaciones();
    }

    public void calcularEjercicios(long tiempoSentado, int id) {
        Usuario usuario = this.usuarioDAO.recuperarUsuarioPorId(id);
        if (usuario != null) {
            double peso = usuario.getPeso();
            double altura = usuario.getAltura();
            int edad = usuario.getEdad();
            List<Ejercicio> ejerciciosCalculados = this.ejercicioDAO.calcularEjercicios(tiempoSentado, peso, altura, edad);
            System.out.println("Ejercicios recomendados:");
            Iterator var11 = ejerciciosCalculados.iterator();

            while(var11.hasNext()) {
                Ejercicio e = (Ejercicio)var11.next();
                System.out.println(e.getNombre() + ": " + e.getFactorConversion() + " " + e.getUnidadMedida());
            }
        } else {
            System.out.println("Usuario no encontrado.");
        }

    }

    public void crearEjercicio(String nombre, double factorConversion, String unidadMedida) {
        Ejercicio nuevoEjercicio = new Ejercicio(nombre, factorConversion, unidadMedida);
        this.ejercicioDAO.crearEjercicio(nuevoEjercicio);
    }

    public Partida startPartida(String username, Ubicacion ubicacion) {
        return this.partidaDAO.startPartida(username, ubicacion);
    }

    public void stopPartida(Partida partida) {
        List<Ejercicio> ejercicios = this.ejercicioDAO.listarEjercicios();
        this.partidaDAO.stopPartida(partida);
    }

    public List<Partida> listarPartidas() {
        return this.partidaDAO.listarPartidas();
    }

    public void mostrarTiempoSentado(int id) {
        long tiempo = this.partidaDAO.getTiempoSentado(id);
        System.out.println("Tiempo sentado en la partida " + id + ": " + tiempo + " ms");
    }

    public void crearUsuario(String nombre, int edad, double peso, double altura) {
        Usuario usuario = new Usuario(nombre, edad, peso, altura);
        this.usuarioDAO.crearUsuario(usuario);
    }

    public List<Usuario> listarUsuarios() {
        return this.usuarioDAO.listarUsuarios();
    }

    public void editarUsuario(int id, String nuevoNombre, int edad, double altura, double peso) {
        Usuario usuario = new Usuario(nuevoNombre, edad, altura, peso);
        this.usuarioDAO.editarUsuario(usuario);
    }

    public void borrarUsuario(int id) {
        this.usuarioDAO.borrarUsuario(id);
    }

    public List<Ejercicio> listarEjercicios() {
        List<Ejercicio> ejercicios = this.ejercicioDAO.listarEjercicios();
        if (ejercicios.isEmpty()) {
            System.out.println("No hay ejercicios disponibles.");
        } else {
            System.out.println("Lista de ejercicios:");
            Iterator var2 = ejercicios.iterator();

            while(var2.hasNext()) {
                Ejercicio e = (Ejercicio)var2.next();
                System.out.println(e.getNombre() + " - Factor/minuto: " + e.getFactorConversion() + " " + e.getUnidadMedida());
            }
        }

        return ejercicios;
    }

    public void borrarEjercicio(int id) {
        this.ejercicioDAO.borrarEjercicio(id);
    }

    public void editarEjercicio(int id, String nuevoNombre, int nuevoFactor, String nuevaUnidad) {
        Ejercicio ejercicio = new Ejercicio(nuevoNombre, (double)nuevoFactor, nuevaUnidad);
        this.ejercicioDAO.editarEjercicio(ejercicio);
    }

    public long getTiempoSentado(int id) {
        return this.partidaDAO.getTiempoSentado(id);
    }

    public Ubicacion obtenerUbicacion(int id) {
        return this.ubicacionDAO.obtenerUbicacionPorID(id);
    }

    private String formatearFechaHora(long timestamp) {
        return (new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")).format(new Date(timestamp));
    }

    private String formatearDuracion(long milisegundos) {
        long segundos = milisegundos / 1000L;
        return String.format("%02d:%02d:%02d", segundos / 3600L, segundos % 3600L / 60L, segundos % 60L);
    }

    public List<Partida> obtenerPartidasActivas() {
        List<Partida> partidas = this.partidaDAO.getPartidasActivas();
        return partidas;
    }

    public Partida obtenerPartida(int id) {
        return this.partidaDAO.obtenerPartidaPorId(id);
    }

    public Usuario obtenerUsuario(int id) {
        return this.usuarioDAO.recuperarUsuarioPorId(id);
    }

    public void actualizarUsuario(int id, String nombre, int edad, double altura, double peso) {
        Usuario usuarioActualizado = new Usuario(nombre, edad, altura, peso);
        this.usuarioDAO.editarUsuario(usuarioActualizado);
    }

    public Ejercicio obtenerEjercicio(int id) {
        return this.ejercicioDAO.obtenerEjercicioPorId(id);
    }

    public void actualizarEjercicio(String nombre, double factor, String unidad) {
        Ejercicio ejActualizado = new Ejercicio(nombre, factor, unidad);
        this.ejercicioDAO.editarEjercicio(ejActualizado);
    }

    public void actualizarUbicacion(String nombre, double lat, double lon) {
        Ubicacion ubiActualizada = new Ubicacion(nombre, lat, lon);
        this.ubicacionDAO.editarUbicacion(ubiActualizada);
    }

    public void eliminarUbicacion(int id) {
        this.ubicacionDAO.borrarUbicacion(id);
    }

    public List<Partida> getPartidasActivas() {
        return this.partidaDAO.getPartidasActivas();
    }
}
