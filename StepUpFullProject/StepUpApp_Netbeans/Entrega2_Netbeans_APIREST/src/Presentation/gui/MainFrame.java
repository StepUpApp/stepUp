package Presentation.gui;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import Business.services.AndroidService;
import Business.modelo.Usuario;
import Business.modelo.Ubicacion;
import Business.modelo.Partida;
import Business.modelo.Ejercicio;
import java.util.List;

public class MainFrame extends JFrame {
    private AndroidService controlador;

    public MainFrame(AndroidService controlador) {
        this.controlador = controlador;
        setTitle("Sistema de Gestión");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrar la ventana
        
        UIManager.put("Button.font", new Font("Arial", Font.PLAIN, 30)); // Botones
        UIManager.put("Label.font", new Font("Arial", Font.PLAIN, 30));  // Etiquetas
        UIManager.put("TextField.font", new Font("Arial", Font.PLAIN, 30)); // Campos de texto
        UIManager.put("OptionPane.messageFont", new Font("Arial", Font.PLAIN, 30)); // Fuente Arial, tamaño 16
        UIManager.put("JPanel.font", new Font("Arial", Font.PLAIN, 30)); // Fuente Arial, tamaño 16

        initComponents();
    }

    private void initComponents() {
        // Panel principal con BorderLayout
        JPanel panel = new JPanel(new BorderLayout());

        // Panel para los botones
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 10, 10)); // 4 filas, 1 columna, con espacio entre botones

        // Botones para cada funcionalidad
        JButton ubicacionButton = new JButton("Gestión de Ubicaciones");
        JButton usuarioButton = new JButton("Gestión de Usuarios");
        JButton partidaButton = new JButton("Gestión de Partidas");
        JButton ejercicioButton = new JButton("Gestión de Ejercicios");

        // Asignar acciones a los botones
        ubicacionButton.addActionListener(e -> gestionarUbicaciones());
        usuarioButton.addActionListener(e -> gestionarUsuarios());
        partidaButton.addActionListener(e -> gestionarPartidas());
        ejercicioButton.addActionListener(e -> gestionarEjercicios());

        // Agregar botones al panel
        buttonPanel.add(ubicacionButton);
        buttonPanel.add(usuarioButton);
        buttonPanel.add(partidaButton);
        buttonPanel.add(ejercicioButton);

        // Agregar el panel de botones al centro del panel principal
        panel.add(buttonPanel, BorderLayout.CENTER);

        // Agregar el panel principal a la ventana
        add(panel);
    }

    // ==================== Lógica para Ubicaciones ====================
    private void gestionarUbicaciones() {
        String[] opciones = {"Crear Ubicación", "Listar Ubicaciones", "Editar Ubicación", "Eliminar Ubicación"};
        int opcion = JOptionPane.showOptionDialog(this, "Seleccione una opción", "Gestión de Ubicaciones",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, opciones, opciones[0]);

        switch (opcion) {
            case 0:
                crearUbicacion();
                break;
            case 1:
                listarUbicaciones();
                break;
            case 2:
                editarUbicacion();
                break;
            case 3:
                eliminarUbicacion();
                break;
        }
    }

    private void crearUbicacion() {
        JTextField nombreField = new JTextField();
        JTextField latitudField = new JTextField();
        JTextField longitudField = new JTextField();

        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        inputPanel.add(new JLabel("Nombre:"));
        inputPanel.add(nombreField);
        inputPanel.add(new JLabel("Latitud:"));
        inputPanel.add(latitudField);
        inputPanel.add(new JLabel("Longitud:"));
        inputPanel.add(longitudField);

        int result = JOptionPane.showConfirmDialog(this, inputPanel, "Crear Ubicación", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String nombre = nombreField.getText();
                double latitud = Double.parseDouble(latitudField.getText());
                double longitud = Double.parseDouble(longitudField.getText());

                Ubicacion newUbicacion = new Ubicacion(nombre, latitud, longitud);
                controlador.crearUbicacion(newUbicacion);
                JOptionPane.showMessageDialog(this, "Ubicación creada exitosamente.");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Error: Ingresa valores numéricos válidos para latitud y longitud.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void listarUbicaciones() {
        List<Ubicacion> ubicaciones = controlador.listarUbicaciones(null);
        String[] columnNames = {"ID", "Nombre", "Latitud", "Longitud"};
        Object[][] data = new Object[ubicaciones.size()][4];

        for (int i = 0; i < ubicaciones.size(); i++) {
            Ubicacion u = ubicaciones.get(i);
            data[i][0] = u.getUbicacionId();
            data[i][1] = u.getNombre();
            data[i][2] = u.getLatitud();
            data[i][3] = u.getLongitud();
        }

        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        JOptionPane.showMessageDialog(this, scrollPane, "Lista de Ubicaciones", JOptionPane.PLAIN_MESSAGE);
    }

    private void editarUbicacion() {
        List<Ubicacion> ubicaciones = controlador.listarUbicaciones(null);
        if(ubicaciones.isEmpty()){
            JOptionPane.showMessageDialog(this, "No hay ubicaciones para editar");
            return;
        }

        Ubicacion[] ubicacionArray = ubicaciones.toArray(new Ubicacion[0]);
        Ubicacion seleccionada = (Ubicacion) JOptionPane.showInputDialog(
            this,
            "Seleccione ubicación a editar:",
            "Editar Ubicación",
            JOptionPane.PLAIN_MESSAGE,
            null,
            ubicacionArray,
            ubicacionArray[0]
        );

        if(seleccionada != null){
            JTextField nombreField = new JTextField(seleccionada.getNombre());
            JTextField latField = new JTextField(String.valueOf(seleccionada.getLatitud()));
            JTextField lonField = new JTextField(String.valueOf(seleccionada.getLongitud()));

            JPanel panel = new JPanel(new GridLayout(3, 2));
            panel.add(new JLabel("Nombre:"));
            panel.add(nombreField);
            panel.add(new JLabel("Latitud:"));
            panel.add(latField);
            panel.add(new JLabel("Longitud:"));
            panel.add(lonField);

            int result = JOptionPane.showConfirmDialog(
                this, panel, "Editar Ubicación", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
            );

            if(result == JOptionPane.OK_OPTION){
                try {

                    controlador.actualizarUbicacion(seleccionada);

                    JOptionPane.showMessageDialog(this, "Ubicación actualizada!");
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Error en formato numérico", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void eliminarUbicacion() {
        List<Ubicacion> ubicaciones = controlador.listarUbicaciones(null);
        if(ubicaciones.isEmpty()){
            JOptionPane.showMessageDialog(this, "No hay ubicaciones para eliminar");
            return;
        }

        Ubicacion[] ubicacionArray = ubicaciones.toArray(new Ubicacion[0]);
        Ubicacion seleccionada = (Ubicacion) JOptionPane.showInputDialog(
            this,
            "Seleccione ubicación a eliminar:",
            "Eliminar Ubicación",
            JOptionPane.PLAIN_MESSAGE,
            null,
            ubicacionArray,
            ubicacionArray[0]
        );

        if(seleccionada != null){
            int confirm = JOptionPane.showConfirmDialog(
                this, 
                "¿Eliminar ubicación: " + seleccionada.getNombre() + "?", 
                "Confirmar", 
                JOptionPane.YES_NO_OPTION
            );

            if(confirm == JOptionPane.YES_OPTION){
                controlador.eliminarUbicacion(seleccionada.getUbicacionId());
                JOptionPane.showMessageDialog(this, "Ubicación eliminada!");
            }
        }
    }
    // ==================== Lógica para Usuarios ====================
    private void gestionarUsuarios() {
        String[] opciones = {"Crear Usuario", "Listar Usuarios", "Editar Usuario", "Eliminar Usuario"};
        int opcion = JOptionPane.showOptionDialog(this, "Seleccione una opción", "Gestión de Usuarios",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, opciones, opciones[0]);

        switch (opcion) {
            case 0:
                crearUsuario();
                break;
            case 1:
                listarUsuarios();
                break;
            case 2:
                editarUsuario();
                break;
            case 3:
                eliminarUsuario();
                break;
        }
    }

    private void crearUsuario() {
        JTextField nombreField = new JTextField();
        JTextField edadField = new JTextField();
        JTextField alturaField = new JTextField();
        JTextField pesoField = new JTextField();

        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        inputPanel.add(new JLabel("Nombre:"));
        inputPanel.add(nombreField);
        inputPanel.add(new JLabel("Edad:"));
        inputPanel.add(edadField);
        inputPanel.add(new JLabel("Altura (en metros):"));
        inputPanel.add(alturaField);
        inputPanel.add(new JLabel("Peso (en kg):"));
        inputPanel.add(pesoField);

        int result = JOptionPane.showConfirmDialog(this, inputPanel, "Crear Usuario", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String nombre = nombreField.getText();
                int edad = Integer.parseInt(edadField.getText());

                // Reemplazar comas por puntos en altura y peso
                String alturaText = alturaField.getText().replace(",", ".");
                String pesoText = pesoField.getText().replace(",", ".");

                double altura = Double.parseDouble(alturaText);
                double peso = Double.parseDouble(pesoText);

                Usuario newUsuario = new Usuario(nombre, edad, altura, peso);
                controlador.crearUsuario(newUsuario);
                JOptionPane.showMessageDialog(this, "Usuario creado exitosamente.");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Error: Ingresa valores numéricos válidos para edad, altura y peso.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void listarUsuarios() {
        List<Usuario> usuarios = controlador.listarUsuarios();
        String[] columnNames = {"ID", "Nombre", "Edad", "Altura", "Peso"};
        Object[][] data = new Object[usuarios.size()][5];

        for (int i = 0; i < usuarios.size(); i++) {
            Usuario u = usuarios.get(i);
            data[i][0] = u.getUsuarioId();
            data[i][1] = u.getNombre();
            data[i][2] = u.getEdad();
            data[i][3] = u.getAltura();
            data[i][4] = u.getPeso();
        }

        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        JOptionPane.showMessageDialog(this, scrollPane, "Lista de Usuarios", JOptionPane.PLAIN_MESSAGE);
    }

    private void editarUsuario() {
        List<Usuario> usuarios = controlador.listarUsuarios();
        if(usuarios.isEmpty()){
            JOptionPane.showMessageDialog(this, "No hay usuarios para editar");
            return;
        }

        Usuario[] usuarioArray = usuarios.toArray(new Usuario[0]);
        Usuario seleccionado = (Usuario) JOptionPane.showInputDialog(
            this,
            "Seleccione usuario a editar:",
            "Editar Usuario",
            JOptionPane.PLAIN_MESSAGE,
            null,
            usuarioArray,
            usuarioArray[0]
        );

        if(seleccionado != null){
            JTextField nombreField = new JTextField(seleccionado.getNombre());
            JTextField edadField = new JTextField(String.valueOf(seleccionado.getEdad()));
            JTextField alturaField = new JTextField(String.valueOf(seleccionado.getAltura()));
            JTextField pesoField = new JTextField(String.valueOf(seleccionado.getPeso()));

            JPanel panel = new JPanel(new GridLayout(4, 2));
            panel.add(new JLabel("Nombre:"));
            panel.add(nombreField);
            panel.add(new JLabel("Edad:"));
            panel.add(edadField);
            panel.add(new JLabel("Altura:"));
            panel.add(alturaField);
            panel.add(new JLabel("Peso:"));
            panel.add(pesoField);

            int result = JOptionPane.showConfirmDialog(
                this, panel, "Editar Usuario", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
            );

            if(result == JOptionPane.OK_OPTION){
                try {
                    controlador.actualizarUsuario(
                        seleccionado.getUsuarioId(),
                        nombreField.getText(),
                        Integer.parseInt(edadField.getText()),
                        Double.parseDouble(alturaField.getText()),
                        Double.parseDouble(pesoField.getText())
                    );
                    JOptionPane.showMessageDialog(this, "Usuario actualizado!");
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Error en formato numérico", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void eliminarUsuario() {
        List<Usuario> usuarios = controlador.listarUsuarios();
        if(usuarios.isEmpty()){
            JOptionPane.showMessageDialog(this, "No hay usuarios para eliminar");
            return;
        }

        Usuario[] usuarioArray = usuarios.toArray(new Usuario[0]);
        Usuario seleccionado = (Usuario) JOptionPane.showInputDialog(
            this,
            "Seleccione usuario a eliminar:",
            "Eliminar Usuario",
            JOptionPane.PLAIN_MESSAGE,
            null,
            usuarioArray,
            usuarioArray[0]
        );

        if(seleccionado != null){
            int confirm = JOptionPane.showConfirmDialog(
                this, 
                "¿Eliminar usuario: " + seleccionado.getNombre() + "?", 
                "Confirmar", 
                JOptionPane.YES_NO_OPTION
            );

            if(confirm == JOptionPane.YES_OPTION){
                controlador.borrarUsuario(seleccionado.getUsuarioId());
                JOptionPane.showMessageDialog(this, "Usuario eliminado!");
            }
        }
    }

    // ==================== Lógica para Partidas ====================
    private void gestionarPartidas() {
        String[] opciones = {"Iniciar Partida", "Detener Partida", "Mostrar Tiempo Sentado", "Mostrar historial partidas"};
        int opcion = JOptionPane.showOptionDialog(this, "Seleccione una opción", "Gestión de Partidas",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, opciones, opciones[0]);

        switch (opcion) {
            case 0:
                iniciarPartida();
                break;
            case 1:
                detenerPartida();
                break;
            case 2:
                mostrarTiempoSentado(); // Llamar al método para mostrar el tiempo sentado
                break;
            case 3:
                mostrarHistorialPartidas();
        }
    }
    
    private void mostrarHistorialPartidas() {
        // Obtener la lista de partidas desde el controlador
        List<Partida> partidas = controlador.listarPartidas();

        // Definir los nombres de las columnas
        String[] columnNames = {"ID", "Tiempo Inicio", "Tiempo Final", "Tiempo Sentado"};

        // Crear una matriz de datos para la tabla
        Object[][] data = new Object[partidas.size()][4];
        for (int i = 0; i < partidas.size(); i++) {
            Partida p = partidas.get(i);
            data[i][0] = p.getPartidaId();
            data[i][1] = formatearTiempo(p.getTiempoInicio());
            data[i][2] = p.getTiempoFinal() == 0 ? "En curso" : formatearTiempo(p.getTiempoFinal());
            data[i][3] = formatearTiempo2(p.getTiempoSentado());
        }

        // Crear la tabla y mostrarla en un JOptionPane
        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        JOptionPane.showMessageDialog(this, scrollPane, "Historial de Partidas", JOptionPane.PLAIN_MESSAGE);
    }

    private void mostrarTiempoSentado() {
        // Crear un campo de texto para que el usuario ingrese el ID de la partida
        JTextField idField = new JTextField();

        // Crear un panel para organizar los componentes
        JPanel panel = new JPanel(new GridLayout(1, 2));
        panel.add(new JLabel("ID de la Partida:"));
        panel.add(idField);

        // Mostrar el diálogo y obtener la respuesta del usuario
        int result = JOptionPane.showConfirmDialog(this, panel, "Ingrese el ID de la Partida",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        // Si el usuario presiona "OK", procesar el ID
        if (result == JOptionPane.OK_OPTION) {
            try {
                // Obtener el ID ingresado por el usuario
                int id = Integer.parseInt(idField.getText());

                // Obtener el tiempo sentado de la partida usando el controlador
                long tiempoSentado = controlador.getTiempoSentado(id);

                // Mostrar el tiempo sentado en un mensaje
                JOptionPane.showMessageDialog(this, "Tiempo sentado en la partida " + id + ": " +
                        formatearTiempo2(tiempoSentado), "Tiempo Sentado", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException e) {
                // Si el usuario ingresa un valor no numérico, mostrar un mensaje de error
                JOptionPane.showMessageDialog(this, "Error: Ingresa un ID válido (número entero).",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private String formatearTiempo(long tiempoMs) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date(tiempoMs));
    }
    
    private String formatearTiempo2(long milisegundos) {
        long segundosTotales = milisegundos / 1000; // Convertir milisegundos a segundos
        long horas = segundosTotales / 3600; // Calcular horas
        long minutos = (segundosTotales % 3600) / 60; // Calcular minutos
        long segundos = segundosTotales % 60; // Calcular segundos

        return String.format("%02d:%02d:%02d", horas, minutos, segundos); // Formatear a HH:mm:ss
    }

    private void iniciarPartida() {
        List<Ubicacion> ubicaciones = controlador.listarUbicaciones(null);
        if(ubicaciones.isEmpty()){
            JOptionPane.showMessageDialog(this, "Primero crea una ubicación");
            return;
        }

        Ubicacion[] ubicacionArray = ubicaciones.toArray(new Ubicacion[0]);
        Ubicacion seleccionada = (Ubicacion) JOptionPane.showInputDialog(
            this,
            "Seleccione ubicación:",
            "Iniciar Partida",
            JOptionPane.PLAIN_MESSAGE,
            null,
            ubicacionArray,
            ubicacionArray[0]
        );

        if(seleccionada != null){
            String username = "automatico";
            controlador.startPartida(username, seleccionada);
            JOptionPane.showMessageDialog(this, "Partida iniciada en: " + seleccionada.getNombre());
        }
    }

    private void detenerPartida() {
        List<Partida> partidasActivas = controlador.getPartidasActivas();
        if(partidasActivas.isEmpty()){
            JOptionPane.showMessageDialog(this, "No hay partidas activas");
            return;
        }

        Partida[] partidaArray = partidasActivas.toArray(new Partida[0]);
        Partida seleccionada = (Partida) JOptionPane.showInputDialog(
            this,
            "Seleccione partida a detener:",
            "Detener Partida",
            JOptionPane.PLAIN_MESSAGE,
            null,
            partidaArray,
            partidaArray[0]
        );

        if(seleccionada != null){
            controlador.stopPartida(seleccionada);
            JOptionPane.showMessageDialog(this, "Partida finalizada!\nTiempo total: " + 
                formatearTiempo2(seleccionada.getTiempoSentado()));
        }
    }


    // ==================== Lógica para Ejercicios ====================
    private void gestionarEjercicios() {
        String[] opciones = {"Crear Ejercicio", "Listar Ejercicios", "Editar Ejercicio", "Eliminar Ejercicio"};
        int opcion = JOptionPane.showOptionDialog(this, "Seleccione una opción", "Gestión de Ejercicios",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, opciones, opciones[0]);

        switch (opcion) {
            case 0:
                crearEjercicio();
                break;
            case 1:
                listarEjercicios();
                break;
            case 2:
                editarEjercicio();
                break;
            case 3:
                eliminarEjercicio();
                break;
        }
    }

    private void crearEjercicio() {
        JTextField nombreField = new JTextField();
        JTextField factorField = new JTextField();
        JTextField unidadField = new JTextField();

        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        inputPanel.add(new JLabel("Nombre:"));
        inputPanel.add(nombreField);
        inputPanel.add(new JLabel("Factor de Conversión:"));
        inputPanel.add(factorField);
        inputPanel.add(new JLabel("Unidad de Medida:"));
        inputPanel.add(unidadField);

        int result = JOptionPane.showConfirmDialog(this, inputPanel, "Crear Ejercicio", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String nombre = nombreField.getText();
                int factor = (int) Double.parseDouble(factorField.getText());
                String unidad = unidadField.getText();

                Ejercicio newEjercicio = new Ejercicio(nombre, factor, unidad);
                controlador.crearEjercicio(newEjercicio);
                JOptionPane.showMessageDialog(this, "Ejercicio creado exitosamente.");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Error: Ingresa un valor numérico válido para el factor de conversión.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void listarEjercicios() {
        List<Ejercicio> ejercicios = controlador.listarEjercicios();
        String[] columnNames = {"Nombre", "Factor de Conversión", "Unidad de Medida"};
        Object[][] data = new Object[ejercicios.size()][3];

        for (int i = 0; i < ejercicios.size(); i++) {
            Ejercicio e = ejercicios.get(i);
            data[i][0] = e.getNombre();
            data[i][1] = e.getFactorConversion();
            data[i][2] = e.getUnidadMedida();
        }

        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        JOptionPane.showMessageDialog(this, scrollPane, "Lista de Ejercicios", JOptionPane.PLAIN_MESSAGE);
    }

    private void editarEjercicio() {
        List<Ejercicio> ejercicios = controlador.listarEjercicios();
        if(ejercicios.isEmpty()){
            JOptionPane.showMessageDialog(this, "No hay ejercicios para editar");
            return;
        }

        Ejercicio[] ejercicioArray = ejercicios.toArray(new Ejercicio[0]);
        Ejercicio seleccionado = (Ejercicio) JOptionPane.showInputDialog(
            this,
            "Seleccione ejercicio a editar:",
            "Editar Ejercicio",
            JOptionPane.PLAIN_MESSAGE,
            null,
            ejercicioArray,
            ejercicioArray[0]
        );

        if(seleccionado != null){
            JTextField nombreField = new JTextField(seleccionado.getNombre());
            JTextField factorField = new JTextField(String.valueOf(seleccionado.getFactorConversion()));
            JTextField unidadField = new JTextField(seleccionado.getUnidadMedida());

            JPanel panel = new JPanel(new GridLayout(3, 2));
            panel.add(new JLabel("Nombre:"));
            panel.add(nombreField);
            panel.add(new JLabel("Factor:"));
            panel.add(factorField);
            panel.add(new JLabel("Unidad:"));
            panel.add(unidadField);

            int result = JOptionPane.showConfirmDialog(
                this, panel, "Editar Ejercicio", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
            );

            if(result == JOptionPane.OK_OPTION){
                try {
                    controlador.actualizarEjercicio(
                        nombreField.getText(),
                        Double.parseDouble(factorField.getText()),
                        unidadField.getText()
                    );
                    JOptionPane.showMessageDialog(this, "Ejercicio actualizado!");
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Error en formato numérico", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void eliminarEjercicio() {
        List<Ejercicio> ejercicios = controlador.listarEjercicios();
        if(ejercicios.isEmpty()){
            JOptionPane.showMessageDialog(this, "No hay ejercicios para eliminar");
            return;
        }

        Ejercicio[] ejercicioArray = ejercicios.toArray(new Ejercicio[0]);
        Ejercicio seleccionado = (Ejercicio) JOptionPane.showInputDialog(
            this,
            "Seleccione ejercicio a eliminar:",
            "Eliminar Ejercicio",
            JOptionPane.PLAIN_MESSAGE,
            null,
            ejercicioArray,
            ejercicioArray[0]
        );

        if(seleccionado != null){
            int confirm = JOptionPane.showConfirmDialog(
                this, 
                "¿Eliminar ejercicio: " + seleccionado.getNombre() + "?", 
                "Confirmar", 
                JOptionPane.YES_NO_OPTION
            );

            if(confirm == JOptionPane.YES_OPTION){
                controlador.borrarEjercicio(seleccionado.getEjercicioId());
                JOptionPane.showMessageDialog(this, "Ejercicio eliminado!");
            }
        }
    }
}