package Presentation.vista;

import Business.services.DaoService;
import Business.modelo.Ejercicio;
import Business.modelo.Partida;
import Business.modelo.Ubicacion;
import Business.modelo.Usuario;

import java.util.List;
import java.util.Scanner;

public class Vista {
    private final DaoService daoService;
    private final Scanner scanner;

    public Vista() {
        this.daoService = new DaoService();
        this.scanner = new Scanner(System.in);
    }

    public void mostrarMenu() {
        System.out.println("\n===== MENÚ PRINCIPAL =====");
        System.out.println("1. Crear Ubicación");
        System.out.println("2. Listar Ubicaciones");
        System.out.println("3. Iniciar Partida");
        System.out.println("4. Detener Partida");
        System.out.println("5. Calcular Ejercicios");
        System.out.println("6. Crear Usuario");
        System.out.println("7. Listar Usuarios");
        System.out.println("8. Editar Usuario");
        System.out.println("9. Eliminar Usuario");
        System.out.println("10. Crear Ejercicio");
        System.out.println("11. Listar Ejercicios");
        System.out.println("12. Editar Ejercicio");
        System.out.println("13. Eliminar Ejercicio");
        System.out.println("0. Salir");
        System.out.print("Seleccione una opción: ");
    }

    public void crearUbicacion() {
        try {
            System.out.print("Ingrese el nombre de la ubicación: ");
            String nombre = scanner.nextLine();
            
            System.out.print("Ingrese la latitud: ");
            double latitud = Double.parseDouble(scanner.nextLine());
            
            System.out.print("Ingrese la longitud: ");
            double longitud = Double.parseDouble(scanner.nextLine());
            
            daoService.crearUbicacion(nombre, latitud, longitud);
        } catch (NumberFormatException e) {
            System.out.println("Error: Ingrese valores numéricos válidos");
        }
    }

    public void listarUbicaciones() {
        List<Ubicacion> ubicaciones = daoService.listarUbicaciones();
        if(ubicaciones.isEmpty()) {
            System.out.println("No hay ubicaciones registradas");
            return;
        }
        
        System.out.println("\n=== LISTADO DE UBICACIONES ===");
        System.out.printf("%-5s %-20s %-10s %-10s%n", "ID", "Nombre", "Latitud", "Longitud");
        for(Ubicacion u : ubicaciones) {
            System.out.printf("%-5d %-20s %-10.4f %-10.4f%n", 
                u.getUbicacionId(), u.getNombre(), u.getLatitud(), u.getLongitud());
        }
    }

    public void startPartida() {
        List<Ubicacion> ubicaciones = daoService.listarUbicaciones();
        if(ubicaciones.isEmpty()) {
            System.out.println("Primero debe crear una ubicación");
            return;
        }
        
        System.out.println("\nSeleccione una ubicación:");
        for(int i = 0; i < ubicaciones.size(); i++) {
            System.out.printf("%d. %s%n", i+1, ubicaciones.get(i).getNombre());
        }
        
        int seleccion = Integer.parseInt(scanner.nextLine()) - 1;
        if(seleccion >= 0 && seleccion < ubicaciones.size()) {
            daoService.startPartida("automatico", ubicaciones.get(seleccion));
        }
    }

    public void stopPartida() {
        List<Partida> activas = daoService.obtenerPartidasActivas();
        if(activas.isEmpty()) {
            System.out.println("No hay partidas activas");
            return;
        }
        
        System.out.println("\nSeleccione partida a detener:");
        for(Partida p : activas) {
            System.out.printf("ID: %d - Iniciada: %s%n", 
                p.getPartidaId(), formatDateTime(p.getTiempoInicio()));
        }
        
        System.out.print("Ingrese ID de partida: ");
        int id = Integer.parseInt(scanner.nextLine());
        Partida partidaActual = daoService.obtenerPartida(id);
        daoService.stopPartida(partidaActual);
    }

    public void calcularEjercicios() {
        List<Partida> partidas = daoService.listarPartidas();
        if(partidas.isEmpty()) {
            System.out.println("No hay partidas registradas");
            return;
        }
        
        try {
            System.out.print("Ingrese ID de partida: ");
            int id = Integer.parseInt(scanner.nextLine());
            
            Partida p = daoService.obtenerPartida(id);
            if(p != null) {
                System.out.println("\n=== EJERCICIOS CALCULADOS ===");
                daoService.calcularEjercicios(p.getTiempoSentado(), id);
            } else {
                System.out.println("Partida no encontrada");
            }
        } catch (NumberFormatException e) {
            System.out.println("ID inválido");
        }
    }

    public void crearUsuario() {
        System.out.print("Ingrese el nombre del usuario: ");
        String nombre = scanner.nextLine();

        System.out.print("Ingrese la edad: ");
        int edad = Integer.parseInt(scanner.nextLine());

        System.out.print("Ingrese la altura (m): ");
        double altura = Double.parseDouble(scanner.nextLine());

        System.out.print("Ingrese el peso (kg): ");
        double peso = Double.parseDouble(scanner.nextLine());

        daoService.crearUsuario(nombre, edad, altura, peso);
    }

    public void listarUsuarios() {
        List<Usuario> usuarios = daoService.listarUsuarios();
        if(usuarios.isEmpty()) {
            System.out.println("No hay usuarios registrados");
            return;
        }
        
        System.out.println("\n=== LISTADO DE USUARIOS ===");
        System.out.printf("%-5s %-20s %-5s %-8s %-8s%n", 
            "ID", "Nombre", "Edad", "Altura", "Peso");
        for(Usuario u : usuarios) {
            System.out.printf("%-5d %-20s %-5d %-8.2f %-8.2f%n", 
                u.getUsuarioId(), u.getNombre(), u.getEdad(), u.getAltura(), u.getPeso());
        }
    }

    public void editarUsuario() {
        System.out.print("Ingrese ID de usuario a editar: ");
        int id = Integer.parseInt(scanner.nextLine());

        Usuario actual = daoService.obtenerUsuario(id);
        if(actual == null) {
            System.out.println("Usuario no encontrado");
            return;
        }

        System.out.print("Nuevo nombre (" + actual.getNombre() + "): ");
        String nombre = scanner.nextLine();

        System.out.print("Nueva edad (" + actual.getEdad() + "): ");
        int edad = Integer.parseInt(scanner.nextLine());

        System.out.print("Nueva altura (" + actual.getAltura() + "): ");
        double altura = Double.parseDouble(scanner.nextLine());

        System.out.print("Nuevo peso (" + actual.getPeso() + "): ");
        double peso = Double.parseDouble(scanner.nextLine());

        daoService.actualizarUsuario(id, nombre, edad, altura, peso);
    }

    public void eliminarUsuario() {
        System.out.print("Ingrese ID de usuario a eliminar: ");
        int id = Integer.parseInt(scanner.nextLine());

        daoService.borrarUsuario(id);
    }

    public void crearEjercicio() {
        System.out.print("Nombre del ejercicio: ");
        String nombre = scanner.nextLine();

        System.out.print("Factor de conversión: ");
        double factor = Double.parseDouble(scanner.nextLine());

        System.out.print("Unidad de medida: ");
        String unidad = scanner.nextLine();

        daoService.crearEjercicio(nombre, factor, unidad);
    }

    public void listarEjercicios() {
        List<Ejercicio> ejercicios = daoService.listarEjercicios();
        if(ejercicios.isEmpty()) {
            System.out.println("No hay ejercicios registrados");
            return;
        }
        
        System.out.println("\n=== LISTADO DE EJERCICIOS ===");
        System.out.printf("%-20s %-10s %-15s%n", 
             "Nombre", "Factor", "Unidad");
        for(Ejercicio e : ejercicios) {
            System.out.printf("%-20s %-10.2f %-15s%n", 
                e.getNombre(), e.getFactorConversion(), e.getUnidadMedida());
        }
    }

    public void editarEjercicio() {
        System.out.print("ID de ejercicio a editar: ");
        int id = Integer.parseInt(scanner.nextLine());

        Ejercicio actual = daoService.obtenerEjercicio(id);
        if(actual == null) {
            System.out.println("Ejercicio no encontrado");
            return;
        }

        System.out.print("Nuevo nombre (" + actual.getNombre() + "): ");
        String nombre = scanner.nextLine();

        System.out.print("Nuevo factor (" + actual.getFactorConversion() + "): ");
        double factor = Double.parseDouble(scanner.nextLine());

        System.out.print("Nueva unidad (" + actual.getUnidadMedida() + "): ");
        String unidad = scanner.nextLine();

        daoService.actualizarEjercicio(nombre, factor, unidad);
    }

    public void eliminarEjercicio() {
        System.out.print("ID de ejercicio a eliminar: ");
        int id = Integer.parseInt(scanner.nextLine());

        daoService.borrarUsuario(id);
    }

    private String formatDateTime(long timestamp) {
        return new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
            .format(new java.util.Date(timestamp));
    }

    public void ejecutar() {
        boolean salir = false;
        
        while(!salir) {
            try {
                mostrarMenu();
                int opcion = Integer.parseInt(scanner.nextLine());
                
                switch(opcion) {
                    case 1: crearUbicacion(); break;
                    case 2: listarUbicaciones(); break;
                    case 3: startPartida(); break;
                    case 4: stopPartida(); break;
                    case 5: calcularEjercicios(); break;
                    case 6: crearUsuario(); break;
                    case 7: listarUsuarios(); break;
                    case 8: editarUsuario(); break;
                    case 9: eliminarUsuario(); break;
                    case 10: crearEjercicio(); break;
                    case 11: listarEjercicios(); break;
                    case 12: editarEjercicio(); break;
                    case 13: eliminarEjercicio(); break;
                    case 0: salir = true; break;
                    default: System.out.println("Opción inválida");
                }
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número válido");
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        scanner.close();
        System.out.println("Sistema cerrado");
    }
}