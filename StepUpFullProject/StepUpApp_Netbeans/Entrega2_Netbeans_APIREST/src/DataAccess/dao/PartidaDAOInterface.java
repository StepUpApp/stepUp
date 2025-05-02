package DataAccess.dao;

import java.util.List;

import Business.modelo.Partida;
import Business.modelo.Ubicacion;

public interface PartidaDAOInterface {
    List<Partida> listarPartidas();
    void guardarPartida(long tiempoInicio, long tiempoFinal, Ubicacion ubicacion, int usuarioId);
    void editarPartida(Partida partida);
    void borrarPartida(int id);
    Partida startPartida(String username, Ubicacion ubicacion);
    void stopPartida(Partida partida);
    long getTiempoSentado(int id);
    Partida obtenerPartidaPorId(int id);
    List<Partida> getPartidasActivas();
}
