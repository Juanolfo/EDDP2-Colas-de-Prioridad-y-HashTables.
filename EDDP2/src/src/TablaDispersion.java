/**
 * Nodo para el encadenamiento (Chaining) en la Tabla de Dispersión.
 */
class NodoHash {
    String username;
    RegistroImpresion registro;
    NodoHash siguiente;

    public NodoHash(String username, RegistroImpresion registro) {
        this.username = username;
        this.registro = registro;
        this.siguiente = null;
    }
}

/**
 * Implementación de una Tabla Hash para acceso rápido a los documentos en cola.
 * Permite buscar un registro de impresión por el nombre del usuario en tiempo constante promedio O(1).
 * * @author Juan B
 * @version 1.2
 */
public class TablaDispersion {
    private NodoHash[] tabla;
    private int capacidad;

    /**
     * Crea la tabla con una capacidad fija.
     * * @param capacidad Número de cubetas (buckets) disponibles.
     */
    public TablaDispersion(int capacidad) {
        this.capacidad = capacidad;
        this.tabla = new NodoHash[capacidad];
    }

    /**
     * Función hash para transformar el nombre de usuario en un índice válido.
     * * @param username Nombre del usuario.
     * @return Índice dentro del rango de la tabla (0 a capacidad-1).
     */
    private int funcionHash(String username) {
        return Math.abs(username.hashCode()) % capacidad;
    }

    /**
     * Inserta un registro en la tabla. Si hay colisión, usa encadenamiento al inicio (Lista Enlazada).
     * * @param username El dueño del registro a insertar.
     * @param registro El registro de impresión empaquetado.
     */
    public void insertar(String username, RegistroImpresion registro) {
        int indice = funcionHash(username);
        NodoHash nuevoNodo = new NodoHash(username, registro);

        if (tabla[indice] == null) {
            tabla[indice] = nuevoNodo;
        } else {
            nuevoNodo.siguiente = tabla[indice];
            tabla[indice] = nuevoNodo;
        }
    }

    /**
     * Busca entre todos los documentos de un usuario y devuelve aquél que tenga 
     * la mayor prioridad encolada (la etiqueta de tiempo más pequeña).
     * * @param username El nombre de usuario a buscar.
     * @return El RegistroImpresion con mayor prioridad encontrado, o null si el usuario no tiene documentos.
     */
    public RegistroImpresion buscar(String username) {
        int indice = funcionHash(username);
        NodoHash actual = tabla[indice];
        
        RegistroImpresion registroMayorPrioridad = null;
        int menorTiempo = Integer.MAX_VALUE; 

        while (actual != null) {
            if (actual.username.equals(username)) {
                if (actual.registro.etiquetaTiempo < menorTiempo) {
                    menorTiempo = actual.registro.etiquetaTiempo;
                    registroMayorPrioridad = actual.registro;
                }
            }
            actual = actual.siguiente;
        }
        
        return registroMayorPrioridad;
    }

    /**
     * Elimina un registro de impresión específico asociado a un usuario de la tabla hash.
     * Maneja de forma segura las colisiones para evitar borrar un documento incorrecto.
     * * @param username Nombre del dueño del documento.
     * @param registroAEliminar La referencia exacta en memoria del documento a dar de baja.
     */
    public void eliminar(String username, RegistroImpresion registroAEliminar) {
        int indice = funcionHash(username);
        NodoHash actual = tabla[indice];
        NodoHash anterior = null;

        while (actual != null) {
            if (actual.username.equals(username) && actual.registro == registroAEliminar) {
                if (anterior == null) {
                    tabla[indice] = actual.siguiente;
                } else {
                    anterior.siguiente = actual.siguiente;
                }
                return; // Documento encontrado y dado de baja
            }
            anterior = actual;
            actual = actual.siguiente;
        }
    }
}