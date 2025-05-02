package Business.interfaces;

import Business.modelo.Partida;
import Business.modelo.Ubicacion;

import java.util.ArrayList;
import java.util.List;

public interface PartidaInterface<T> {

    List<Partida> listarPartidas();
    boolean editarPartida(Partida partida);
    Partida obtenerPartidaPorId(T id);
    String guardarPartida(Partida partida);
    boolean borrarPartida(T id);

    Partida startPartida(String username, Ubicacion ubicacion);
    void stopPartida(Partida partida);
    long getTiempoSentado(int id);
    List<Partida> getPartidasActivas();


}
