package Business.interfaces;

import Business.modelo.Usuario;

import java.util.ArrayList;
import java.util.List;

public interface UsuarioInterface <T>{
    Usuario recuperarUsuarioPorId(T id);
    String crearUsuario(Usuario usuario);
    List<Usuario> listarUsuarios();
    boolean borrarUsuario(T id);
    Usuario obtenerUsuarioPorUsername(String username);
    boolean editarUsuario(Usuario usuario);
}
