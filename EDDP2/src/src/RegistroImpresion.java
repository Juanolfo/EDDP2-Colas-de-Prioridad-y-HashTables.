/**
 * Representa la entrada empaquetada de un documento en la cola de impresión.
 * Vincula un Documento con su prioridad (etiqueta de tiempo) y su ubicación en el Montículo.
 * * @author Juan B
 * @version 1.2
 */
public class RegistroImpresion {
    
    Documento documento; 
    int etiquetaTiempo;
    /** Índice actual dentro del arreglo del Montículo Binario. Facilita la cancelación O(log n). */
    int posicionEnHeap; 
    private String nombreUsuario; 

    /**
     * Crea un registro de impresión listo para ser encolado.
     * * @param documento El documento a imprimir.
     * @param etiquetaTiempo El valor de prioridad calculado tras aplicar modificadores.
     * @param nombreUsuario El identificador del usuario propietario del documento.
     */
    public RegistroImpresion(Documento documento, int etiquetaTiempo, String nombreUsuario) {
        this.documento = documento;
        this.etiquetaTiempo = etiquetaTiempo;
        this.nombreUsuario = nombreUsuario; 
        this.posicionEnHeap = -1; 
    }

    /** @return El nombre del usuario dueño de la impresión. */
    public String getNombreUsuario() {
        return nombreUsuario;
    }

    /** @return Representación visual del registro para la consola o interfaz. */
    @Override
    public String toString() {
        return "[Doc: " + documento.getNombre() + " - Usuario: " + nombreUsuario + " - tiempo: " + etiquetaTiempo + "]";
    }
}