
import Business.interfaces.UbicacionInterface;
import Business.interfaces.UsuarioInterface;
import Business.services.AndroidService;
import DataAccess.dao.*;
import Presentation.gui.MainFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        UbicacionInterface<Integer> ubicacionDAO = new UbicacionDAO();
        EjercicioDAO ejercicioDAO = new EjercicioDAO();
        PartidaDAO partidaDAO = new PartidaDAO();
        UsuarioInterface<Integer> usuarioDAO = new UsuarioDAO();
        AndroidService controlador = new AndroidService( ubicacionDAO, ejercicioDAO, partidaDAO, usuarioDAO);
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(controlador);
            frame.setVisible(true);
        });
    }
}