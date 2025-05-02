package Business.modelo;

public class Usuario {
    public String objectId;
    private static int contadorId = 1; 
    private int usuarioId;
    private String username;
    private String password;
    private String nombre;
    private int edad;
    private double altura;
    private double peso;

    public Usuario(String nombre, int edad, double altura, double peso) {
        this.nombre = nombre;
        this.edad = edad;
        this.altura = altura;
        this.peso = peso;
        this.usuarioId = contadorId++;
    }
    
    public Usuario(String nombreUsuario, String contraseña, String nombre, int edad, double altura, double peso) {
        this.username = nombreUsuario;
        this.password = contraseña;
        this.nombre = nombre;
        this.edad = edad;
        this.altura = altura;
        this.peso = peso;
        this.usuarioId = contadorId++;
    }
    
    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int id) {
        this.usuarioId = usuarioId;
    }

    
    public String getNombreUsuario() {
        return username;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.username = nombreUsuario;
    }

    public String getContraseña() {
        return password;
    }

    public void setContraseña(String contraseña) {
        this.password = contraseña;
    }
    
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public double getAltura() {
        return altura;
    }

    public void setAltura(double altura) {
        this.altura = altura;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + usuarioId +
                ", nombre='" + nombre + '\'' +
                ", edad=" + edad +
                ", altura=" + altura +
                ", peso=" + peso +
                '}';
    }
}
