package DataAccess.repository;

import java.util.ArrayList;
import java.util.List;

import Business.modelo.Usuario;

public interface UsuarioRepositoryInterface {
    ArrayList<Usuario> getAll();
    String insert(Usuario usuario);
    Usuario getById(String objectId);
    boolean update(String objectId, Usuario usuario);
    boolean delete(String objectId);
    Usuario obtenerUsuarioPorId(int id);
    void cerrarConexion();

}