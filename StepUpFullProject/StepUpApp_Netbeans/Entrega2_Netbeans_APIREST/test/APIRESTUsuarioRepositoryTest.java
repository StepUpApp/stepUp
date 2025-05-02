import java.util.List;
import Business.modelo.Usuario;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import DataAccess.repository.APIRESTUsuarioRepository;

public class APIRESTUsuarioRepositoryTest {
    
    private APIRESTUsuarioRepository repo;
    private String testObjectId;
    
    @Before
    public void setUp() {
        repo = new APIRESTUsuarioRepository();
        // Crear usuario de prueba
        Usuario usuario = new Usuario("Usuario Test", 30, 1.75, 70.0);
        testObjectId = repo.crearUsuario(usuario);
    }
    
    @After
    public void tearDown() {
        if(testObjectId != null && !testObjectId.isEmpty()) {
            repo.borrarUsuario(testObjectId);
        }
    }
    
    @Test
    public void testInsertAndGetUsuario() {
        Usuario result = repo.recuperarUsuarioPorId(testObjectId);
        
        assertNotNull("El usuario debería existir", result);
        assertEquals("El nombre debe coincidir", "Usuario Test", result.getNombre());
        assertEquals("La edad debe coincidir", 30, result.getEdad());
    }
    
    @Test
    public void testUpdateUsuario() {
        Usuario original = repo.recuperarUsuarioPorId(testObjectId);
        original.setNombre("Usuario Modificado");
        boolean success = repo.editarUsuario(original);
        
        assertTrue("La actualización debería ser exitosa", success);
        
        Usuario updated = repo.recuperarUsuarioPorId(testObjectId);
        assertEquals("El nombre debe estar actualizado", "Usuario Modificado", updated.getNombre());
    }
    
    @Test
    public void testGetAllUsuarios() {
        List<Usuario> usuarios = repo.listarUsuarios();
        assertFalse("La lista no debería estar vacía", usuarios.isEmpty());
    }
}