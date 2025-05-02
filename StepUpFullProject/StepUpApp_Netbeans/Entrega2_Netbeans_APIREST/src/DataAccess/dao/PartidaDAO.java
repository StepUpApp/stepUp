package DataAccess.dao;

import Business.modelo.Partida;
import Business.modelo.Ubicacion;
import java.util.ArrayList;
import java.util.List;
import Business.interfaces.PartidaInterface;

public class PartidaDAO implements PartidaInterface<Integer> {
    private List<Partida> partidas = new ArrayList<>();
    private int contadorId = 1; // Para simular un id único para cada partida

    @Override
    public Partida startPartida(String username, Ubicacion ubicacion) {
        // Inicia la partida guardando el tiempo actual y una ubicación por defecto.
        // Se crea una Ubicacion "Default" con latitud y longitud en 0.0.
        Partida nuevaPartida = new Partida(System.currentTimeMillis(), username, ubicacion);
        nuevaPartida.setPartidaId(contadorId++);
        partidas.add(nuevaPartida);
        return nuevaPartida;
    }
    
    public String guardarPartida(Partida partida) {
        partidas.add(partida);
        return null;
 
    }

    @Override
    public void stopPartida(Partida partida) {
        // Detiene la partida, calcula el tiempo transcurrido y los ejercicios a realizar.
        long tiempoFinal = System.currentTimeMillis();
        partida.setTiempoFinal(tiempoFinal);

        System.out.println("Partida detenida: " + partida);
    }
    
    @Override
    public long getTiempoSentado(int id) {
        // Devuelve el tiempo sentado de la partida especificada por id.
        for (Partida partida : partidas) {
            if (partida.getPartidaId() == id) {
                return partida.getTiempoSentado();
            }
        }
        return -1; // Si no se encuentra la partida.
    }


    @Override
    public List<Partida> listarPartidas() {
        // Retorna todas las partidas almacenadas.
        return partidas;
    }

    @Override
    public boolean editarPartida(Partida partida) {
        // Busca la partida y actualiza sus atributos relevantes (por ejemplo, la ubicación y el tiempo final).
        // Se omite actualizar el tiempo sentado, ya que se calcula automáticamente al establecer el tiempo final.
        for (Partida p : partidas) {
            if (p.getPartidaId() == partida.getPartidaId()) {
                p.setTiempoFinal(partida.getTiempoFinal());
                p.setUbicacion(partida.getUbicacion());
                // Se pueden agregar otras actualizaciones si se requieren
                System.out.println("Partida editada: " + p);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean borrarPartida(Integer id) {
        // Elimina la partida con el id especificado.
        partidas.removeIf(p -> p.getPartidaId() == id);
        System.out.println("Partida con id " + id + " eliminada.");
        return true;
    }
    
    @Override
    public Partida obtenerPartidaPorId(Integer id) {
        // Buscar ubicación por ID
        for (Partida p : partidas) {
            if (p.getPartidaId() == id) {
                return p;
            }
        }
        return null; 
    }

    @Override
    public List<Partida> getPartidasActivas() {
        List<Partida> activas = new ArrayList<>();
        for (Partida partida : partidas) {
            if (partida.isEnCurso()) {
                activas.add(partida);
            }
        }
        return activas;
    }
    
}
