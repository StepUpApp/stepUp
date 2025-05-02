package DataAccess.repository;
import Business.modelo.Partida;
import java.util.ArrayList;

public interface PartidaRepositoryInterface {
    ArrayList<Partida> getAll();
    String insert(Partida partida);
    Partida getById(String objectId);
    boolean update(String objectId, Partida partida);
    boolean delete(String objectId);
    void cerrarConexion();
}