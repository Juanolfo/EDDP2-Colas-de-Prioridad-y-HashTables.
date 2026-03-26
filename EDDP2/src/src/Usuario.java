/**
 * Representa a un usuario del sistema que posee documentos pendientes y un nivel de prioridad.
 * * @author Juan B
 * @version 1.1
 */
public class Usuario {
    private String username;
    private String tipo;
    private ListaDocumento documentosPendientes; 

    /**
     * Crea un nuevo usuario con su lista de documentos pendientes vacía.
     * * @param username Identificador único del usuario (ej. "Jperez").
     * @param tipo Nivel de prioridad (ej. "prioridad_alta", "prioridad_baja").
     */
    public Usuario(String username, String tipo) {
        this.username = username;
        this.tipo = tipo;
        this.documentosPendientes = new ListaDocumento();
    }

    /** @return El nombre de usuario único. */
    public String getUsername() { return username; }
    
    /** @return El tipo de prioridad del usuario. */
    public String getTipo() { return tipo; }
    
    /**
     * Crea un nuevo documento y lo añade a la lista local de pendientes del usuario.
     * El documento NO se encola automáticamente en el sistema de impresión con esto.
     * * @param nombre Nombre del documento.
     * @param tamano Tamaño en KB.
     * @param tipoDoc Extensión del archivo.
     */
    public void crearDocumento(String nombre, int tamano, String tipoDoc) {
        Documento nuevoDoc = new Documento(nombre, tamano, tipoDoc);
        documentosPendientes.agregar(nuevoDoc);
    }

    /** * @return La lista enlazada de documentos que el usuario aún no ha mandado a imprimir. 
     */
    public ListaDocumento getDocumentosPendientes() {
        return documentosPendientes;
    }
}