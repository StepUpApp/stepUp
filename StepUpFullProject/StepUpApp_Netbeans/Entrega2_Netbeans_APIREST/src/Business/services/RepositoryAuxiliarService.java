//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package Business.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import Business.modelo.Ejercicio;
import Business.modelo.Partida;
import Business.modelo.Ubicacion;
import Business.modelo.Usuario;
import DataAccess.repository.APIRESTEjercicioRepository;
import DataAccess.repository.APIRESTPartidaRepository;
import DataAccess.repository.APIRESTUbicacionRepository;
import DataAccess.repository.APIRESTUsuarioRepository;

public class RepositoryAuxiliarService {
    private final APIRESTEjercicioRepository ejercicioRepo = new APIRESTEjercicioRepository();
    private final APIRESTPartidaRepository partidaRepo = new APIRESTPartidaRepository();
    private final APIRESTUbicacionRepository ubicacionRepo = new APIRESTUbicacionRepository();
    private final APIRESTUsuarioRepository usuarioRepo = new APIRESTUsuarioRepository();

    public RepositoryAuxiliarService() {
    }

    public String crearUsuario(String nombre, int edad, double peso, double altura) {
        Usuario usuario = new Usuario(nombre, edad, altura, peso);
        return this.usuarioRepo.crearUsuario(usuario);
    }

    public List<Usuario> listarUsuarios() {
        return this.usuarioRepo.listarUsuarios();
    }

    public Usuario obtenerUsuario(String objectId) {
        return this.usuarioRepo.recuperarUsuarioPorId(objectId);
    }

    public boolean actualizarUsuario(String objectId, String nombre, int edad, double altura, double peso) {
        Usuario usuario = new Usuario(nombre, edad, altura, peso);
        usuario.setObjectId(objectId);
        return this.usuarioRepo.editarUsuario(usuario);
    }

    public String crearEjercicio(String nombre, double factor, String unidad) {
        Ejercicio ejercicio = new Ejercicio(nombre, factor, unidad);
        return this.ejercicioRepo.crearEjercicio(ejercicio);
    }

    public List<Ejercicio> listarEjercicios() {
        return this.ejercicioRepo.listarEjercicios();
    }

    public boolean actualizarEjercicio(String objectId, String nombre, double factor, String unidad) {
        Ejercicio ejercicio = new Ejercicio(nombre, factor, unidad);
        ejercicio.setObjectId(objectId);
        return this.ejercicioRepo.editarEjercicio(ejercicio);
    }

    public String crearUbicacion(String nombre, double lat, double lon) {
        Ubicacion ubicacion = new Ubicacion(nombre, lat, lon);
        return this.ubicacionRepo.crearUbicacion(ubicacion);
    }

    public Ubicacion obtenerUbicacion(String objectId) {
        return this.ubicacionRepo.obtenerUbicacionPorID(objectId);
    }

    public String iniciarPartida(String username, Ubicacion ubicacion) {
        Partida partida = new Partida(System.currentTimeMillis(), username, ubicacion);
        partida.setEnCurso(true);
        return this.partidaRepo.guardarPartida(partida);
    }

    public void finalizarPartida(String objectId) {
        Partida partida = this.partidaRepo.obtenerPartidaPorId(objectId);
        if (partida != null) {
            partida.setTiempoFinal(System.currentTimeMillis());
            this.partidaRepo.editarPartida(partida);
            partida.setEnCurso(false);
        }

    }

    public String formatearTiempo(long milisegundos) {
        long segundos = milisegundos / 1000L;
        return String.format("%02d:%02d:%02d", segundos / 3600L, segundos % 3600L / 60L, segundos % 60L);
    }

    public void calcularEjercicios(long tiempoSentado, String idUsuario) {
        Usuario usuario = this.usuarioRepo.recuperarUsuarioPorId(idUsuario);
        if (usuario == null) {
            System.out.println("Usuario no encontrado.");
        } else {
            List<Ejercicio> ejercicios = this.ejercicioRepo.listarEjercicios();
            if (ejercicios.isEmpty()) {
                System.out.println("No hay ejercicios disponibles.");
            } else {
                double minutosEjercicio = (double)tiempoSentado / 60000.0;
                double factorAjuste = this.calcularFactorAjuste(usuario);
                System.out.println("Ejercicios recomendados para " + usuario.getNombre() + ":");
                Iterator var10 = ejercicios.iterator();

                while(var10.hasNext()) {
                    Ejercicio ejercicio = (Ejercicio)var10.next();
                    double cantidad = minutosEjercicio * ejercicio.getFactorConversion() * factorAjuste;
                    cantidad = (double)Math.round(cantidad * 100.0) / 100.0;
                    System.out.printf("- %s: %.2f %s\n", ejercicio.getNombre(), cantidad, ejercicio.getUnidadMedida());
                }

            }
        }
    }

    private double calcularFactorAjuste(Usuario usuario) {
        return 1.0 + (usuario.getPeso() - 70.0) / 100.0 + (usuario.getAltura() - 170.0) / 200.0 - (double)(usuario.getEdad() - 30) / 200.0;
    }

    public static void main(String[] args) throws Exception {
        RepositoryAuxiliarService controlador = new RepositoryAuxiliarService();
        List<String> objectIdsAEliminar = new ArrayList();
        System.out.println("=== INICIALIZACIÓN DE DATOS ===");
        System.out.println("\nCreando ejercicios...");
        String[] ejercicioIds = new String[]{controlador.crearEjercicio("Andar", 2.0, "pasos"), controlador.crearEjercicio("Correr", 0.2, "km"), controlador.crearEjercicio("Saltar", 3.0, "saltos"), controlador.crearEjercicio("Nadar", 1.0, "largos"), controlador.crearEjercicio("Flexiones", 2.0, "repeticiones")};
        Iterator var4 = controlador.listarEjercicios().iterator();

        while(var4.hasNext()) {
            Ejercicio e = (Ejercicio)var4.next();
            System.out.println(e);
        }

        Collections.addAll(objectIdsAEliminar, ejercicioIds);
        System.out.println("\nCreando ubicaciones...");
        String[] ubicacionIds = new String[]{controlador.crearUbicacion("Aulario", 36.714946, -4.478879), controlador.crearUbicacion("Casa", 36.721234, -4.419876), controlador.crearUbicacion("Biblioteca", 36.718765, -4.428912)};
        Collections.addAll(objectIdsAEliminar, ubicacionIds);
        System.out.println("\nCreando usuarios...");
        String[] usuarioIds = new String[]{controlador.crearUsuario("Juan", 20, 1.73, 70.0), controlador.crearUsuario("Lucia", 23, 1.65, 60.0)};
        Collections.addAll(objectIdsAEliminar, usuarioIds);
        System.out.println("\n=== INICIO DE PRUEBA ===");
        Ubicacion aulario = controlador.obtenerUbicacion(ubicacionIds[0]);
        String partidaId = controlador.iniciarPartida("auto", aulario);
        objectIdsAEliminar.add(partidaId);
        System.out.println("Partida iniciada en: " + aulario.getNombre());
        System.out.println("\nSimulando 10 segundos de juego...");
        Thread.sleep(10000L);
        controlador.finalizarPartida(partidaId);
        Partida partida = controlador.partidaRepo.obtenerPartidaPorId(partidaId);
        System.out.println("Tiempo sentado: " + controlador.formatearTiempo(partida.getTiempoSentado()));
        System.out.println("\nCalculando ejercicios para Juan:");
        String testingUserId = controlador.crearUsuario("Juan", 20, 1.73, 70.0);
        controlador.calcularEjercicios(partida.getTiempoSentado(), testingUserId);
        System.out.println("\n=== LIMPIEZA ===");
        objectIdsAEliminar.forEach((id) -> {
            System.out.println("Eliminando: " + id);
            controlador.partidaRepo.borrarPartida(id);
            controlador.ejercicioRepo.borrarEjercicio(id);
            controlador.ubicacionRepo.borrarUbicacion(id);
            controlador.usuarioRepo.borrarUsuario(id);
        });
        System.out.println("\nEstado final:");
        System.out.println("Ejercicios: " + controlador.listarEjercicios().size());
        System.out.println("Ubicaciones: " + controlador.ubicacionRepo.listarUbicaciones(null).size());
        System.out.println("Usuarios: " + controlador.listarUsuarios().size());
    }
}
