package Business.interfaces;

import Business.modelo.Ejercicio;

import java.util.ArrayList;
import java.util.List;

public interface EjercicioInterface<T> {

    ArrayList<Ejercicio> listarEjercicios();
    String crearEjercicio(Ejercicio ejercicio);
    boolean editarEjercicio(Ejercicio ejercicio);
    boolean borrarEjercicio(T id);
    Ejercicio obtenerEjercicioPorId(T id);
    List<Ejercicio> calcularEjercicios(long tiempoSentado, double peso, double altura, int edad);

}
