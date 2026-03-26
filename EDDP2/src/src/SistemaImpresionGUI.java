
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap; 
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Clase principal que gestiona la Interfaz Gráfica de Usuario (GUI) del Simulador de Cola de Impresión.
 * Coordina la interacción visual con el usuario y administra las estructuras de datos subyacentes
 * (Montículo Binario, Tabla de Dispersión y Listas Enlazadas).
 * * @author Juan Blanco
 */
public class SistemaImpresionGUI extends JFrame {

    /** * Área de texto utilizada como consola virtual para mostrar los registros (logs) 
     * y las acciones que ocurren dentro del simulador. 
     */
    private JTextArea consolaSalida;

    // VARIABLES GLOBALES (Estructuras de Datos)
    
    /** Montículo Binario (Min-Heap) utilizado para gestionar la cola de prioridad de la impresora. */
    private MonticuloBinario monticulo;
    
    /** Tabla de Dispersión (Hash) utilizada para el acceso directo (O(1)) y cancelación de documentos en cola. */
    private TablaDispersion tablaHash;
    
    /** Contador global de tiempo simulado, base para el cálculo de las etiquetas de prioridad. */
    private int relojGlobal = 0;
    
    /** Mapa que almacena todos los usuarios registrados en el sistema, indexados por su nombre de usuario. */
    private HashMap<String, Usuario> mapaUsuarios; 

    /**
     * Constructor principal de la interfaz gráfica.
     * Inicializa las estructuras de datos (Montículo, Tabla Hash y HashMap de usuarios),
     * configura las listas desplegables y enlaza los componentes visuales autogenerados.
     */
    public SistemaImpresionGUI() {
        // INICIALIZACIÓN DE ESTRUCTURAS
        monticulo = new MonticuloBinario(100);
        tablaHash = new TablaDispersion(50);
        mapaUsuarios = new HashMap<>(); // Inicializamos el mapa vacío
        
        initComponents(); // Inicialización de los componentes visuales de NetBeans
        
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "prioridad_alta", "prioridad_media", "prioridad_baja" }));
        
        consolaSalida = jTextArea1;
        setLocationRelativeTo(null);
    }

    /**
     * Configura las propiedades básicas de la ventana principal como el título,
     * tamaño, comportamiento de cierre predeterminado y el layout principal.
     */
    private void configurarVentana() {
        setTitle("Simulador de Cola de Impresión SO");
        setSize(950, 600); // Lo agrandé un poco más para que quepa el nuevo panel
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        setLayout(new BorderLayout());
    }

    /**
     * Actualiza y re-dibuja el panel visual del árbol utilizando GraphStream.
     * Recrea el grafo a partir del estado actual del Montículo Binario para reflejar 
     * en tiempo real las inserciones y eliminaciones en la estructura de datos.
     */
    private void actualizarGrafo() {
        if (panelGrafo == null) {
            System.out.println("Error: panelGrafo no ha sido inicializado en el diseño.");
            return;
        }

        panelGrafo.removeAll();

        // Generar el componente del grafo
        Component vistaArbol = VisorArbol.crearPanelGrafo(monticulo);

        panelGrafo.setLayout(new java.awt.BorderLayout());
        panelGrafo.add(vistaArbol, java.awt.BorderLayout.CENTER);

        // ESTO ES LO MÁS IMPORTANTE: refresca visualmente el componente
        panelGrafo.revalidate();
        panelGrafo.repaint();
    }

    /**
     * Abre un cuadro de diálogo para seleccionar un archivo CSV desde el sistema local.
     * Lee el archivo línea por línea, extrae los datos (nombre, prioridad) y registra 
     * a los nuevos usuarios dentro del {@code mapaUsuarios} global.
     */
    private void cargarArchivoCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccione el archivo de usuarios CSV");
        
        int seleccion = fileChooser.showOpenDialog(this);
        
        if (seleccion == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            consolaSalida.append("Intentando cargar archivo: " + archivo.getName() + "\n");
            
            // Limpiamos el mapa de usuarios anterior
            mapaUsuarios.clear();

            try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
                String linea;
                br.readLine(); 
                
                int usuariosCargados = 0;
                while ((linea = br.readLine()) != null) {
                    String[] datos = linea.split(",");
                    if (datos.length == 2) {
                        String username = datos[0].trim();
                        String tipo = datos[1].trim();
                        
                        // Creamos el usuario y lo guardamos en el mapa global
                        Usuario nuevoUser = new Usuario(username, tipo);
                        mapaUsuarios.put(username, nuevoUser);
                        
                        consolaSalida.append("  - Usuario cargado: " + username + " (" + tipo + ")\n");
                        usuariosCargados++;
                    }
                }
                consolaSalida.append(">> Se cargaron " + usuariosCargados + " usuarios en el mapa del sistema.\n\n");
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al leer el archivo: " + ex.getMessage(), 
                                              "Error de Lectura", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            consolaSalida.append("Carga de archivo cancelada por el usuario.\n");
        }
    }

    /**
     * Intercepta la petición de cancelar una impresión y ejecuta la lógica cruzada
     * entre la Tabla Hash y el Montículo Binario para eliminar el documento correcto.
     * * @param nombreUsuario El nombre del usuario propietario que desea cancelar su documento encolado.
     */
    public void cancelarImpresionUsuario(String nombreUsuario) {
        // 1. Buscamos el documento (con la nueva lógica, traerá el de mayor prioridad)
        RegistroImpresion registroAEliminar = tablaHash.buscar(nombreUsuario);
        
        if (registroAEliminar != null) {
            // 2. Lógica del montículo para sacarlo de la cola
            int indiceEnMonticulo = registroAEliminar.posicionEnHeap;
            registroAEliminar.etiquetaTiempo = -999999; 
            monticulo.flotar(indiceEnMonticulo);
            monticulo.eliminar_min();
            
            // 3. Le pasamos "registroAEliminar" a la tabla para borrarlo
            tablaHash.eliminar(nombreUsuario, registroAEliminar);
            
            consolaSalida.append("Documento de " + nombreUsuario + " eliminado correctamente de la cola.\n");
            
            // Refrescar el árbol visual
            actualizarGrafo(); 
        } else {
            consolaSalida.append("El usuario " + nombreUsuario + " no tiene documentos en cola.\n");
        }
    }

    /**
     * Transfiere un documento desde la lista de "pendientes" del usuario hacia la
     * cola de impresión del sistema (el Montículo Binario). Modifica la etiqueta de
     * tiempo base dependiendo del nivel de prioridad del usuario.
     * * @param usuario El objeto del usuario que envía el documento.
     * @param nombreDocumento El nombre exacto del documento a extraer e imprimir.
     */
    public void enviarDocumentoACola(Usuario usuario, String nombreDocumento) {
        // Obtenemos el documento de la lista del usuario
        Documento docAImprimir = usuario.getDocumentosPendientes().eliminarPorNombre(nombreDocumento);
    
        if (docAImprimir == null) {
            consolaSalida.append(">> Error: No se encontró el documento '" + nombreDocumento + "'\n");
            return;
        }

        // Lógica de Prioridad (Reloj Global)
        relojGlobal += 10; 
        int etiquetaFinal = relojGlobal; 
        String tipo = usuario.getTipo().toLowerCase();

        // Ajustamos el tiempo según la prioridad del usuario
        if (tipo.contains("alta")) {
            etiquetaFinal = relojGlobal / 3;
        } else if (tipo.contains("media")) {
            etiquetaFinal = relojGlobal / 2;
        } 

        // 1. Crear el Registro de Impresión con el nombre de usuario
        RegistroImpresion nuevoRegistro = new RegistroImpresion(docAImprimir, etiquetaFinal, usuario.getUsername());    
        
        // 2. Insertar en el Montículo (O(log n))
        monticulo.insertar(nuevoRegistro);

        // 3. Insertar en la Tabla Hash (O(1)) para poder cancelarlo después
        tablaHash.insertar(usuario.getUsername(), nuevoRegistro);

        // 4. Mostrar en consola
        consolaSalida.append(">> ENCOLADO: " + docAImprimir.getNombre() + " (Usuario: " + usuario.getUsername() + ")\n");
        consolaSalida.append("   Prioridad calculada: " + etiquetaFinal + "\n\n");
        consolaSalida.setCaretPosition(consolaSalida.getDocument().getLength());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButton2 = new javax.swing.JButton();
        CargarArchivo = new javax.swing.JButton();
        enviardocacola = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        txtUsuarioEnviar = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtDocumentoEnviar = new javax.swing.JTextField();
        panelGrafo = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        Nuevousuario = new javax.swing.JTextField();
        ElimUsuario = new javax.swing.JButton();
        CrearUsuario = new javax.swing.JButton();
        creardoc = new javax.swing.JButton();
        elimdocpen = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(102, 255, 255));
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton1.setBackground(new java.awt.Color(204, 255, 255));
        jButton1.setText("Liberar Impresora");
        jButton1.addActionListener(this::jButton1ActionPerformed);
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 220, -1, -1));

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 310, 380, 240));

        jButton2.setBackground(new java.awt.Color(204, 255, 204));
        jButton2.setText("Cancelar Impresión");
        jButton2.addActionListener(this::jButton2ActionPerformed);
        getContentPane().add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 220, -1, -1));

        CargarArchivo.setBackground(new java.awt.Color(255, 51, 51));
        CargarArchivo.setText("Cargar Archivo");
        CargarArchivo.addActionListener(this::CargarArchivoActionPerformed);
        getContentPane().add(CargarArchivo, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 40, -1, -1));

        enviardocacola.setBackground(new java.awt.Color(204, 204, 255));
        enviardocacola.setText(" Enviar Documento a la cola");
        enviardocacola.addActionListener(this::enviardocacolaActionPerformed);
        getContentPane().add(enviardocacola, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 180, -1, -1));

        jLabel1.setText("Usuario");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 50, -1, 20));

        txtUsuarioEnviar.setText(" ");
        txtUsuarioEnviar.addActionListener(this::txtUsuarioEnviarActionPerformed);
        getContentPane().add(txtUsuarioEnviar, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 90, 150, -1));

        jLabel2.setText("Documento");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 60, -1, -1));
        getContentPane().add(txtDocumentoEnviar, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 90, 140, -1));

        panelGrafo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout panelGrafoLayout = new javax.swing.GroupLayout(panelGrafo);
        panelGrafo.setLayout(panelGrafoLayout);
        panelGrafoLayout.setHorizontalGroup(
            panelGrafoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 638, Short.MAX_VALUE)
        );
        panelGrafoLayout.setVerticalGroup(
            panelGrafoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 428, Short.MAX_VALUE)
        );

        getContentPane().add(panelGrafo, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 190, 640, 430));

        jLabel3.setText("Nuevo Usuario");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 60, -1, -1));

        jComboBox1.setBackground(new java.awt.Color(255, 153, 0));
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox1.addActionListener(this::jComboBox1ActionPerformed);
        getContentPane().add(jComboBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(840, 90, -1, -1));

        Nuevousuario.setText("  ");
        Nuevousuario.addActionListener(this::NuevousuarioActionPerformed);
        getContentPane().add(Nuevousuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 90, 130, -1));

        ElimUsuario.setBackground(new java.awt.Color(153, 153, 255));
        ElimUsuario.setText("Eliminar Usuario");
        ElimUsuario.addActionListener(this::ElimUsuarioActionPerformed);
        getContentPane().add(ElimUsuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 120, -1, -1));

        CrearUsuario.setBackground(new java.awt.Color(102, 153, 255));
        CrearUsuario.setText("Crear Usuario");
        CrearUsuario.addActionListener(this::CrearUsuarioActionPerformed);
        getContentPane().add(CrearUsuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 120, -1, -1));

        creardoc.setBackground(new java.awt.Color(204, 255, 204));
        creardoc.setForeground(new java.awt.Color(1, 0, 0));
        creardoc.setText("Crear Documento");
        creardoc.addActionListener(this::creardocActionPerformed);
        getContentPane().add(creardoc, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 120, -1, -1));

        elimdocpen.setBackground(new java.awt.Color(102, 102, 255));
        elimdocpen.setText("Eliminar Documento Pendiente");
        elimdocpen.addActionListener(this::elimdocpenActionPerformed);
        getContentPane().add(elimdocpen, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 120, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        RegistroImpresion atendido = monticulo.eliminar_min();
        if (atendido != null) {
            consolaSalida.append(">> 🖨️ IMPRESIÓN COMPLETADA:\n");
            consolaSalida.append("   Documento: " + atendido.documento.getNombre() + "\n");
            consolaSalida.append("   Etiqueta de tiempo final: " + atendido.etiquetaTiempo + "\n\n");
            actualizarGrafo(); 
        } else {
            consolaSalida.append(">> La cola de impresión está vacía.\n");
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void CargarArchivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CargarArchivoActionPerformed
        // TODO add your handling code here:
        cargarArchivoCSV();
    }//GEN-LAST:event_CargarArchivoActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
    String usuarioACancelar = txtUsuarioEnviar.getText();

    RegistroImpresion reg = tablaHash.buscar(usuarioACancelar);

    if (reg != null) {
        monticulo.cancelarDocumento(reg);

        tablaHash.eliminar(usuarioACancelar, reg);
        consolaSalida.append("\nDocumento de " + usuarioACancelar + " cancelado correctamente.\n");
        actualizarGrafo();
    } else {
        consolaSalida.append("\nError: El usuario no tiene documentos en la cola.\n");
    }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void enviardocacolaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enviardocacolaActionPerformed
String username = txtUsuarioEnviar.getText().trim();
        String docName = txtDocumentoEnviar.getText().trim();

        if (username.isEmpty() || docName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar el Usuario y el Nombre del Documento a enviar.");
            return;
        }

        Usuario userObj = mapaUsuarios.get(username);
        
        if (userObj != null) {

            enviarDocumentoACola(userObj, docName);
            
            txtDocumentoEnviar.setText(""); 
            actualizarGrafo();
        } else {
            JOptionPane.showMessageDialog(this, "Usuario no encontrado en el sistema. Verifique el nombre.");
        }
    }//GEN-LAST:event_enviardocacolaActionPerformed

    private void txtUsuarioEnviarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUsuarioEnviarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUsuarioEnviarActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void NuevousuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NuevousuarioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_NuevousuarioActionPerformed

    private void CrearUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CrearUsuarioActionPerformed
        // TODO add your handling code here:
        String username = Nuevousuario.getText().trim();
        String tipo = jComboBox1.getSelectedItem().toString(); // Toma la prioridad seleccionada
        
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar un nombre de usuario.");
            return;
        }
        if (mapaUsuarios.containsKey(username)) {
            JOptionPane.showMessageDialog(this, "El usuario ya existe en el sistema.");
            return;
        }
        
        // Creamos el usuario y lo metemos al mapa
        Usuario nuevoUser = new Usuario(username, tipo);
        mapaUsuarios.put(username, nuevoUser);
        consolaSalida.append(">> Usuario creado manualmente: " + username + " (" + tipo + ")\n");
        Nuevousuario.setText(""); // Limpiar campo
    }//GEN-LAST:event_CrearUsuarioActionPerformed

    private void ElimUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ElimUsuarioActionPerformed
        // TODO add your handling code here:
        String username = Nuevousuario.getText().trim();
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el nombre del usuario a eliminar.");
            return;
        }
        
        if (mapaUsuarios.containsKey(username)) {
            mapaUsuarios.remove(username); // Lo borramos
            consolaSalida.append(">> Usuario '" + username + "' eliminado. (Sus documentos en cola se mantendrán intactos).\n");
            Nuevousuario.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "El usuario no existe.");
        }
    }//GEN-LAST:event_ElimUsuarioActionPerformed

    private void elimdocpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_elimdocpenActionPerformed
        // TODO add your handling code here:
        String username = txtUsuarioEnviar.getText().trim();
        String docName = txtDocumentoEnviar.getText().trim();
        
        if (username.isEmpty() || docName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar el Usuario y el Nombre del Documento.");
            return;
        }
        
        Usuario user = mapaUsuarios.get(username);
        if (user != null) {
            // Buscamos y eliminamos de su lista de pendientes
            Documento eliminado = user.getDocumentosPendientes().eliminarPorNombre(docName);
            
            if (eliminado != null) {
                consolaSalida.append(">> Documento pendiente '" + docName + "' eliminado correctamente.\n");
                txtDocumentoEnviar.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "El documento no se encontró en la lista de pendientes de este usuario.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "El usuario no existe.");
        }
    }//GEN-LAST:event_elimdocpenActionPerformed

    private void creardocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_creardocActionPerformed
        // TODO add your handling code here:
        String username = txtUsuarioEnviar.getText().trim();
        String docName = txtDocumentoEnviar.getText().trim();
        
        if (username.isEmpty() || docName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar el Usuario y el Nombre del Documento en los campos.");
            return;
        }
        
        Usuario user = mapaUsuarios.get(username);
        if (user != null) {
            // Creamos el documento y se guarda en la lista de pendientes del usuario
            user.crearDocumento(docName, 100, "PDF"); 
            consolaSalida.append(">> Documento '" + docName + "' creado y PENDIENTE para el usuario '" + username + "'.\n");
            txtDocumentoEnviar.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "El usuario no existe. Créelo primero.");
        }
    }//GEN-LAST:event_creardocActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SistemaImpresionGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SistemaImpresionGUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton CargarArchivo;
    private javax.swing.JButton CrearUsuario;
    private javax.swing.JButton ElimUsuario;
    private javax.swing.JTextField Nuevousuario;
    private javax.swing.JButton creardoc;
    private javax.swing.JButton elimdocpen;
    private javax.swing.JButton enviardocacola;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JPanel panelGrafo;
    private javax.swing.JTextField txtDocumentoEnviar;
    private javax.swing.JTextField txtUsuarioEnviar;
    // End of variables declaration//GEN-END:variables

}

