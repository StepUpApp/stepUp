package Business.interfaces;

import Business.modelo.Ubicacion;

import java.util.ArrayList;
import java.util.List;

public interface UbicacionInterface<T> {

    String crearUbicacion(Ubicacion ubicacion);
    List<Ubicacion> listarUbicaciones(String usuarioId);
    Boolean editarUbicacion(Ubicacion ubicacion);
    Boolean borrarUbicacion(T id);
    Ubicacion obtenerUbicacionPorID(T id);
    Ubicacion findByCoordinates(double latitude, double longitude);
    void setImagen(int id, String url);

}
