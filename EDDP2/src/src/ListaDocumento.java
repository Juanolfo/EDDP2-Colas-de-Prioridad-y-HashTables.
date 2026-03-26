/**
 * Nodo básico para la estructura de Lista Enlazada Simple.
 */
class NodoDocumento {
    Documento documento;
    NodoDocumento siguiente;

    /**
     * Crea un nodo que contiene un objeto Documento.
     * @param documento El documento a almacenar.
     */
    public NodoDocumento(Documento documento) {
        this.documento = documento;
        this.siguiente = null;
    }
}

/**
 * Estructura de Lista Enlazada Simple para gestionar los documentos 
 * que un usuario ha creado pero aún no ha enviado a la cola de impresión.
 * * @author Juan B
 * @version 1.1
 */
public class ListaDocumento {
    private NodoDocumento cabeza;

    /**
     * Inicializa una lista de documentos vacía.
     */
    public ListaDocumento() {
        this.cabeza = null;
    }

    /**
     * Inserta un documento nuevo al inicio de la lista.
     * Complejidad: O(1).
     * * @param doc El objeto Documento a agregar.
     */
    public void agregar(Documento doc) {
        NodoDocumento nuevo = new NodoDocumento(doc);
        nuevo.siguiente = cabeza;
        cabeza = nuevo;
    }

    /**
     * Busca y elimina un documento de la lista de pendientes por su nombre.
     * Útil cuando un usuario envía el documento a la cola o decide eliminarlo.
     * * @param nombreDoc El nombre del documento a buscar y extraer.
     * @return El objeto Documento eliminado, o null si no se encontró en la lista.
     */
    public Documento eliminarPorNombre(String nombreDoc) {
        NodoDocumento actual = cabeza;
        NodoDocumento anterior = null;

        while (actual != null) {
            if (actual.documento.getNombre().equals(nombreDoc)) {
                if (anterior == null) {
                    cabeza = actual.siguiente; // Borrar en cabeza
                } else {
                    anterior.siguiente = actual.siguiente; // Borrar en medio/final
                }
                return actual.documento;
            }
            anterior = actual;
            actual = actual.siguiente;
        }
        return null; // Documento no encontrado
    }
}