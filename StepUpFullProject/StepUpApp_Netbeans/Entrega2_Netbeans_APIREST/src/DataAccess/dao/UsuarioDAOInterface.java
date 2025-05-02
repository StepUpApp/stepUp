package DataAccess.dao;

import java.util.List;
import Business.modelo.Usuario;

public interface UsuarioDAOInterface {
    Usuario obtenerUsuarioPorId(int id);
    void crearUsuario(Usuario usuario);
    List<Usuario> listarUsuarios();
    void editarUsuario(Usuario usuario);
    void borrarUsuario(int id);
    Usuario obtenerUsuarioPorUsername(String username);
}
