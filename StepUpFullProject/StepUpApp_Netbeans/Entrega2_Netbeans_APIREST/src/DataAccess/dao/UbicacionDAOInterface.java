package DataAccess.dao;

import java.util.List;
import Business.modelo.Ubicacion;

public interface UbicacionDAOInterface {
    void crearUbicacion(Ubicacion ubicacion);
    List<Ubicacion> listarUbicaciones();
    void editarUbicacion(Ubicacion ubicacion);
    void borrarUbicacion(int id);
    Ubicacion findByCoordinates(double latitude, double longitude);
    Ubicacion obtenerUbicacionPorID(int id); 
    void setImagen(int id, String url);
}
