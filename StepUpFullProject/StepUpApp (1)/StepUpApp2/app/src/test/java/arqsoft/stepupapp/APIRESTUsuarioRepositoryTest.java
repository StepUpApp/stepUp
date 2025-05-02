package arqsoft.stepupapp;

import modelo.Usuario;
import repository.APIRESTUsuarioRepository;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class APIRESTUsuarioRepositoryTest {

    private APIRESTUsuarioRepository usuarioRepo;
    private Usuario testUsuario;

    @Before
    public void setUp() {
        usuarioRepo = new APIRESTUsuarioRepository();
        // Configurar un usuario de prueba
        testUsuario = new Usuario(
                "testuser_" + System.currentTimeMillis(), // Usuario único
                "testpassword",
                "Test User",
                30,
                1.75,
                70.5
        );
    }

    @Test
    public void testInsertUsuario() {
        // Ejecutar el método a probar
        String objectId = usuarioRepo.insert(testUsuario);

        // Verificaciones
        assertNotNull("El objectId no debe ser nulo", objectId);
        assertFalse("El objectId no debe estar vacío", objectId.isEmpty());

        System.out.println("Usuario insertado con objectId: " + objectId);
    }
}
