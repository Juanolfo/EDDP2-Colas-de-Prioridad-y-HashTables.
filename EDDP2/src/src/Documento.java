/**
 * Representa un archivo o documento que el usuario desea imprimir.
 * Contiene metadatos básicos como nombre, tamaño y tipo.
 * * @author Juan B
 * @version 1.0
 */
public class Documento {
    private String nombre;
    private int tamano;
    private String tipo;

    /**
     * Constructor completo para crear un documento con todos sus atributos.
     * * @param nombre El nombre identificador del documento.
     * @param tamano El tamaño del archivo (generalmente en KB o páginas).
     * @param tipo   La extensión o tipo de archivo (ej. PDF, DOCX).
     */
    public Documento(String nombre, int tamano, String tipo) {
        this.nombre = nombre;
        this.tamano = tamano;
        this.tipo = tipo;
    }

    /**
     * Constructor simplificado que asigna valores por defecto.
     * Útil para creación rápida desde la interfaz gráfica.
     * * @param nombre El nombre identificador del documento.
     */
    public Documento(String nombre) {
        this.nombre = nombre;
        this.tamano = 100; // Valor por defecto
        this.tipo = "PDF"; // Valor por defecto
    }

    /** @return El nombre del documento. */
    public String getNombre() { return nombre; }
    
    /** @return El tamaño del documento. */
    public int getTamano() { return tamano; }
    
    /** @return El tipo/extensión del documento. */
    public String getTipo() { return tipo; }
    
    /** @param nombre El nuevo nombre para el documento. */
    public void setNombre(String nombre) { this.nombre = nombre; }

    /**
     * Representación en texto del documento.
     * @return Cadena con formato "Nombre (Tipo)".
     */
    @Override
    public String toString() {
        return nombre + " (" + tipo + ")";
    }
}