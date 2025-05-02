package DataAccess.repository;

import Business.modelo.Ubicacion;
import java.util.ArrayList;

public interface UbicacionRepositoryInterface {
    ArrayList<Ubicacion> getAll();
    String insert(Ubicacion ubicacion);
    Ubicacion getById(String objectId);
    boolean update(String objectId, Ubicacion ubicacion);
    boolean delete(String objectId);
    void cerrarConexion();
}