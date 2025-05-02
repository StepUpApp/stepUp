package Business.modelo;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Partida {
    public String objectId;
    private static int contadorId = 1; // Contador estático
    private int partidaId;
    private long tiempoInicio;
    private Long tiempoFinal;
    private long tiempoSentado;
    private boolean enCurso = true;
    private Ubicacion ubicacion;
    private String username;



    // Se recibe la Ubicacion al iniciar la partida
    public Partida(long tiempoInicio, String username, Ubicacion ubicacion) {
        this.tiempoInicio = tiempoInicio;
        this.tiempoFinal = null;
        this.username = username;
        this.tiempoSentado = 0;
        this.ubicacion = ubicacion;
        this.partidaId = contadorId++;
    }
    
    public Partida(long tiempoInicio, long tiempoFinal, Ubicacion ubicacion, int usuarioId) {
        this.tiempoInicio = tiempoInicio;
        this.tiempoFinal = tiempoFinal;
        this.tiempoSentado = tiempoFinal - tiempoInicio;
        this.ubicacion = ubicacion;
        this.partidaId = contadorId++;
        this.partidaId = usuarioId;
    }
    
    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
    
    public int getPartidaId() {
        return partidaId;
    }

    public void setPartidaId(int id) {
        this.partidaId = id;
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getTiempoInicio() {
        return tiempoInicio;
    }

    public void setTiempoInicio(long tiempoInicio) {
        this.tiempoInicio = tiempoInicio;
    }

    public long getTiempoFinal() {
        return tiempoFinal;
    }

    // Al establecer el tiempo final se calcula automáticamente el tiempo sentado
    public void setTiempoFinal(Long tiempoFinal) {
        this.tiempoFinal = tiempoFinal;
        this.tiempoSentado = tiempoFinal - tiempoInicio;
        this.enCurso = false;
    }

    public long getTiempoSentado() {
        return tiempoSentado;
    }
    
    public boolean isEnCurso() {
        return enCurso;
    }

    public void setEnCurso(boolean enCurso) {
        this.enCurso = enCurso;
    }

    public Ubicacion getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(Ubicacion ubicacion) {
        this.ubicacion = ubicacion;
    }

    private String formatearTiempo(long tiempoMs) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date(tiempoMs));
    }

    private String formatearTiempo2(long milisegundos) {
        long segundosTotales = milisegundos / 1000;
        long horas = segundosTotales / 3600;
        long minutos = (segundosTotales % 3600) / 60;
        long segundos = segundosTotales % 60;

        return String.format("%02d:%02d:%02d", horas, minutos, segundos);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Partida{")
          .append("id=").append(partidaId)
          .append(", tiempoInicio=").append(formatearTiempo(tiempoInicio))
          .append(", tiempoFinal=").append(tiempoFinal == null ? "null" : formatearTiempo(tiempoFinal))
          .append(", tiempoSentado=").append(formatearTiempo2(tiempoSentado))
          .append(", ubicacion=").append(ubicacion)
          .append('}');
        return sb.toString();
    }

}
