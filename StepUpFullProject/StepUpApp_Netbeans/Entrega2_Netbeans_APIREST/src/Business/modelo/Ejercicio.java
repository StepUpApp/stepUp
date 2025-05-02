package Business.modelo;

public class Ejercicio {
    public String objectId;
    private static int contadorId = 1; 
    private int ejercicioId;
    private String nombre;
    private double factorConversion;  
    private String unidadMedida;
    private String imagen;

    
    public Ejercicio(String nombre, double factorConversion, String unidadMedida) {
        this.nombre = nombre;
        this.factorConversion = factorConversion;
        this.unidadMedida = unidadMedida;
        this.ejercicioId = contadorId++;
    }
    
    public Ejercicio(String nombre, double factorConversion, String unidadMedida, String imagen) {
        this.nombre = nombre;
        this.factorConversion = factorConversion;
        this.unidadMedida = unidadMedida;
        this.ejercicioId = contadorId++;
        this.imagen = imagen;
    }
    
    
    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public int getEjercicioId() { return ejercicioId; }
    public void setEjercicioId(int id) { this.ejercicioId = ejercicioId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public double getFactorConversion() { return factorConversion; }
    public void setFactorConversion(double factorConversion) { this.factorConversion = factorConversion; }
    
    public String getUnidadMedida() { return unidadMedida; }
    public void setUnidadMedida(String unidadMedida) { this.unidadMedida = unidadMedida; }
    
    
    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    @Override
    public String toString() {
        return "id=" + ejercicioId +
                ", nombre='" + nombre + '\'' +
                ", duracion=" + factorConversion + " " + unidadMedida +
                '}';
    }
}
