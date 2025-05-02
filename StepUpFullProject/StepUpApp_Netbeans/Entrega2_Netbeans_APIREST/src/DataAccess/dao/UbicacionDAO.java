package DataAccess.dao;

import Business.modelo.Ubicacion;
import java.util.ArrayList;
import java.util.List;
import Business.interfaces.UbicacionInterface;

public class UbicacionDAO implements UbicacionInterface<Integer> {
    private List<Ubicacion> ubicaciones = new ArrayList<>();
    private int contadorId = 1;
    private static final double TOLERANCIA = 0.0001; // Tolerancia para comparación de coordenadas

    @Override
    public Ubicacion findByCoordinates(double latitude, double longitude) {
        // Buscar ubicación por coordenadas usando tolerancia
        for (Ubicacion ubicacion : ubicaciones) {
            if (Math.abs(ubicacion.getLatitud() - latitude) < TOLERANCIA &&
                Math.abs(ubicacion.getLongitud() - longitude) < TOLERANCIA) {
                return ubicacion;
            }
        }
        return null;
    }

    @Override
    public String crearUbicacion(Ubicacion ubicacion) {
        ubicacion.setUbicacionId(contadorId++);
        ubicaciones.add(ubicacion);
        System.out.println("Ubicación creada: " + ubicacion);
        return null;
    }

    @Override
    public List<Ubicacion> listarUbicaciones() {
        return ubicaciones;
    }

    @Override
    public Boolean editarUbicacion(Ubicacion ubicacion) {
        for (Ubicacion u : ubicaciones) {
            if (u.getUbicacionId() == ubicacion.getUbicacionId()) {
                System.out.println("editando ubicacion: " + u);
                u.setNombre(ubicacion.getNombre());

                u.setLatitud(ubicacion.getLatitud());
                u.setLongitud(ubicacion.getLongitud());
                System.out.println("Lista actual:");
                for (Ubicacion ubic : ubicaciones) {
                    System.out.println(ubic);
                }
                return true;
            }
        }



        return false;
    }

    @Override
    public Boolean borrarUbicacion(Integer id) {
        ubicaciones.removeIf(u -> u.getUbicacionId() == id);
        System.out.println("Ubicación con id " + id + " eliminada.");
        return true;
    }

    @Override
    public Ubicacion obtenerUbicacionPorID(Integer id) {
        // Buscar ubicación por ID
        for (Ubicacion u : ubicaciones) {
            if (u.getUbicacionId() == id) {
                return u;
            }
        }
        return null; // Si no se encuentra la ubicación
    }
    
    @Override 
    public void setImagen(int id, String url) {
        Ubicacion u = obtenerUbicacionPorID(id);
        u.setImagen(url);
    }
}
