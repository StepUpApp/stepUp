package Business.modelo;

public class Ubicacion {
    public String objectId;
    private static int contadorId = 1; // Contador estático

    private int ubicacionId;
    private String nombre;
    private double latitud;
    private double longitud;
    private String imagen;
    private String usuarioId = null;

    public Ubicacion(String nombre, double latitud, double longitud) {
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
        this.ubicacionId = contadorId++;
    }
    
    public Ubicacion(String nombre, double latitud, double longitud, String imagen, String usuarioId) {
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
        this.ubicacionId = contadorId++;
        this.imagen = imagen;
        this.usuarioId = usuarioId;
    }


    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }
    
    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public int getUbicacionId() {
        return ubicacionId;
    }

    public void setUbicacionId(int id) {
        this.ubicacionId = ubicacionId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }
    
    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    @Override
    public String toString() {
        return "Ubicacion{" +
                "id=" + ubicacionId +
                ", nombre='" + nombre + '\'' +
                ", latitud=" + latitud +
                ", longitud=" + longitud +
                '}';
    }
}
