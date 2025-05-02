import DataAccess.repository.APIRESTEjercicioRepository;
import Business.modelo.Ejercicio;
import java.util.ArrayList;
import java.util.List;

public class TestEjercicioRepository {
    public static void main(String[] args) {
        // Instanciamos el repositorio de Ejercicio
        APIRESTEjercicioRepository ejercicioRepo = new APIRESTEjercicioRepository();

        // 1. Insertar un nuevo Ejercicio
        Ejercicio nuevoEjercicio = new Ejercicio("Flexiones", 1.2, "repeticiones");
        String objectId = ejercicioRepo.crearEjercicio(nuevoEjercicio);
        System.out.println("Ejercicio insertado con objectId: " + objectId);
        // Asignamos el objectId obtenido al objeto
        nuevoEjercicio.setObjectId(objectId);

        // 2. Obtener el Ejercicio por objectId
        Ejercicio ejercicioRecuperado = ejercicioRepo.obtenerEjercicioPorId(objectId);
        if (ejercicioRecuperado != null) {
            System.out.println("Ejercicio recuperado: " + ejercicioRecuperado);
        } else {
            System.out.println("No se pudo recuperar el ejercicio.");
        }

        // 3. Actualizar el Ejercicio (por ejemplo, modificar el factorConversion)
        nuevoEjercicio.setFactorConversion(1.5);
        boolean actualizado = ejercicioRepo.editarEjercicio(nuevoEjercicio);
        if (actualizado) {
            System.out.println("Ejercicio actualizado correctamente.");
            // Se vuelve a obtener para confirmar la actualización
            Ejercicio actualizadoEjercicio = ejercicioRepo.obtenerEjercicioPorId(objectId);
            System.out.println("Ejercicio tras actualización: " + actualizadoEjercicio);
        } else {
            System.out.println("Error al actualizar el ejercicio.");
        }

        // 4. Listar todos los Ejercicios
        ArrayList<Ejercicio> listaEjercicios = ejercicioRepo.listarEjercicios();
        System.out.println("Lista de ejercicios:");
        for (Ejercicio e : listaEjercicios) {
            System.out.println(e);
        }

        // 5. Eliminar el Ejercicio insertado
        boolean eliminado = ejercicioRepo.borrarEjercicio(objectId);
        if (eliminado) {
            System.out.println("Ejercicio eliminado correctamente.");
        } else {
            System.out.println("Error al eliminar el ejercicio.");
        }

        // Verificar que ya no se encuentre en el listado
        listaEjercicios = ejercicioRepo.listarEjercicios();
        System.out.println("Lista de ejercicios tras eliminación:");
        for (Ejercicio e : listaEjercicios) {
            System.out.println(e);
        }
    }
}
