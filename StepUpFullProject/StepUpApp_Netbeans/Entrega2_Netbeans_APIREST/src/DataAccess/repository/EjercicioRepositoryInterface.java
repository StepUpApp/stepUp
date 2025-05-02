package DataAccess.repository;

import Business.modelo.Ejercicio;
import java.util.ArrayList;

public interface EjercicioRepositoryInterface {
    ArrayList<Ejercicio> getAll();
    String insert(Ejercicio ejercicio);
    Ejercicio getById(String objectId);
    boolean update(String objectId, Ejercicio ejercicio);
    boolean delete(String objectId);
    void cerrarConexion();
}