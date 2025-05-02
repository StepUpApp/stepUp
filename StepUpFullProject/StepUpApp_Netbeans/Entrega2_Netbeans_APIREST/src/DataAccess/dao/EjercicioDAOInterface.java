package DataAccess.dao;

import Business.modelo.Ejercicio;

import java.util.List;

public interface EjercicioDAOInterface {
    List<Ejercicio> calcularEjercicios(long tiempoSentado, double peso, double altura, int edad);
    void crearEjercicio(Ejercicio ejercicio);
    List<Ejercicio> listarEjercicios();
    void editarEjercicio(Ejercicio ejercicio);
    void borrarEjercicio(int id);
    Ejercicio obtenerEjercicioPorId(int id);
}
