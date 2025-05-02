package DataAccess.dao;

import Business.modelo.Ejercicio;
import java.util.ArrayList;
import java.util.List;
import Business.interfaces.EjercicioInterface;

public class EjercicioDAO implements EjercicioInterface<Integer> {
    private List<Ejercicio> ejercicios = new ArrayList<>();
    private int contadorId = 1;
    private static final double MS_A_MINUTOS = 60000.0;

    public EjercicioDAO() {

    }

    @Override
    public List<Ejercicio> calcularEjercicios(long tiempoSentado, double peso, double altura, int edad) {
        List<Ejercicio> resultado = new ArrayList<>();
        // Convertir tiempo de milisegundos a minutos
        double minutosEjercicio = tiempoSentado / MS_A_MINUTOS;

        // Factor de ajuste según características del usuario
        double factorAjuste = 1 + (peso - 70) / 100.0 + (altura - 170) / 200.0 - (edad - 30) / 200.0;

        for (Ejercicio e : ejercicios) {
            // Aplicar el factor de conversión y ajuste
            double cantidad = minutosEjercicio * e.getFactorConversion() * factorAjuste;
            cantidad = Math.round(cantidad * 100.0) / 100.0; // Redondear a 2 decimales
            // Se crea un nuevo objeto Ejercicio con la cantidad calculada en factorConversion
            resultado.add(new Ejercicio(e.getNombre(), cantidad, e.getUnidadMedida()));
        }

        return resultado;
    }

    @Override
    public String crearEjercicio(Ejercicio ejercicio) {
        ejercicio.setEjercicioId(contadorId++);
        ejercicios.add(ejercicio);
        return null;
    }

    @Override
    public ArrayList<Ejercicio> listarEjercicios() {
        return (ArrayList<Ejercicio>) ejercicios;
    }
    
    @Override
    public boolean editarEjercicio(Ejercicio ejercicio) {
        for (Ejercicio e : ejercicios) {
            if (e.getEjercicioId() == ejercicio.getEjercicioId()) {
                e.setNombre(ejercicio.getNombre());
                e.setFactorConversion(ejercicio.getFactorConversion());
                e.setUnidadMedida(ejercicio.getUnidadMedida());
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean borrarEjercicio(Integer id) {
        ejercicios.removeIf(e -> e.getEjercicioId() == id);
        return true;
    }
    
    @Override
    public Ejercicio obtenerEjercicioPorId(Integer id) {
        // Buscar ubicación por ID
        for (Ejercicio e : ejercicios) {
            if (e.getEjercicioId() == id) {
                return e;
            }
        }
        return null; 
    }
}
