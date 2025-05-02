package DataAccess.dao;

import java.util.ArrayList;
import java.util.List;
import Business.modelo.Usuario;
import Business.interfaces.UsuarioInterface;

public class UsuarioDAO implements UsuarioInterface<Integer> {
    private List<Usuario> usuarios = new ArrayList<>();
    private int contadorId = 1; // Para simular un id único para cada usuario

    @Override
    public Usuario recuperarUsuarioPorId(Integer id) {
        for (Usuario u : usuarios) { 
            if (u.getUsuarioId() == id) {
                return u;
            }
        }
        return null; // Si no encuentra el usuario
    }
    
    @Override
    public Usuario obtenerUsuarioPorUsername(String username) {
        for (Usuario u : usuarios) { 
            if (u.getNombreUsuario() != null && 
                u.getNombreUsuario().equalsIgnoreCase(username)) {
                return u;
            }
        }
        return null;
    }
    @Override
    public String crearUsuario(Usuario usuario) {
        usuario.setUsuarioId(contadorId++);
        usuarios.add(usuario);
        System.out.println("Usuario creado: " + usuario);
        return null;
    }

    @Override
    public List<Usuario> listarUsuarios() {
        return usuarios;
    }

    @Override
    public boolean editarUsuario(Usuario usuario) {
        for (Usuario u : usuarios) {
            if (u.getUsuarioId() == usuario.getUsuarioId()) {
                System.out.println("Editando usuarioo: " + usuario);
                u.setNombre(usuario.getNombre());
                u.setEdad(usuario.getEdad());
                u.setAltura(usuario.getAltura());
                u.setPeso(usuario.getPeso());
                System.out.println("Usuario editado: " + u);
                return true;

            }
        }
        return false;
    }

    @Override
    public boolean borrarUsuario(Integer id) {
        usuarios.removeIf(u -> u.getUsuarioId() == id);
        System.out.println("Usuario con id " + id + " eliminado.");
        return true;
    }
   
}
