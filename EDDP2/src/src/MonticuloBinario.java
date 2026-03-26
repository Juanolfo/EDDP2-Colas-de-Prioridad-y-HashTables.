/**
 * Implementación de un Min-Heap (Montículo Binario de Mínimo).
 * Se utiliza para gestionar la prioridad de la cola de impresión basándose en etiquetas de tiempo.
 * Conserva la variable 'posicionEnHeap' de los registros actualizada en todo momento.
 * * @author Juan B
 * @version 1.3
 */
public class MonticuloBinario {
    public RegistroImpresion[] heap;
    private int tamanoActual;
    private int capacidad;

    /**
     * Crea un montículo con una capacidad inicial definida.
     * * @param capacidad Tamaño máximo inicial del arreglo que representa el árbol.
     */
    public MonticuloBinario(int capacidad) {
        this.capacidad = capacidad;
        this.tamanoActual = 0;
        this.heap = new RegistroImpresion[this.capacidad];
    }

    private int getPadre(int i) { return (i - 1) / 2; }
    private int getHijoIzquierdo(int i) { return (2 * i) + 1; }
    private int getHijoDerecho(int i) { return (2 * i) + 2; }

    /**
     * Intercambia dos nodos dentro del arreglo del montículo y actualiza
     * sus variables internas para reflejar su nueva posición estructural.
     * * @param i Índice del primer elemento.
     * @param j Índice del segundo elemento.
     */
    private void intercambiar(int i, int j) {
        RegistroImpresion temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
        if (heap[i] != null) heap[i].posicionEnHeap = i;
        if (heap[j] != null) heap[j].posicionEnHeap = j;
    }

    /**
     * Mueve un registro hacia arriba en el árbol hasta restablecer la propiedad de Min-Heap.
     * * @param indice El índice inicial del nodo a flotar.
     */
    public void flotar(int indice) {
        while (indice > 0 && heap[indice].etiquetaTiempo < heap[getPadre(indice)].etiquetaTiempo) {
            intercambiar(indice, getPadre(indice));
            indice = getPadre(indice);
        }
    }

    /**
     * Mueve un registro hacia abajo en el árbol hasta restablecer la propiedad de Min-Heap.
     * * @param indice El índice inicial del nodo a hundir.
     */
    public void hundir(int indice) {
        int minimo = indice;
        int izq = getHijoIzquierdo(indice);
        int der = getHijoDerecho(indice);

        if (izq < tamanoActual && heap[izq].etiquetaTiempo < heap[minimo].etiquetaTiempo) {
            minimo = izq;
        }
        if (der < tamanoActual && heap[der].etiquetaTiempo < heap[minimo].etiquetaTiempo) {
            minimo = der;
        }
        if (minimo != indice) {
            intercambiar(indice, minimo);
            hundir(minimo);
        }
    }

    /**
     * Inserta un nuevo registro de impresión en la cola. Complejidad: O(log n).
     * * @param registro El registro a encolar.
     */
    public void insertar(RegistroImpresion registro) {
        if (tamanoActual == capacidad) {
            return; // En un escenario real, aquí se redimensionaría el arreglo.
        }
        heap[tamanoActual] = registro;
        registro.posicionEnHeap = tamanoActual;
        flotar(tamanoActual);
        tamanoActual++;
    }

    /**
     * Extrae y elimina el elemento con la menor etiqueta de tiempo (la raíz).
     * Simula la liberación o avance en la cola de la impresora real.
     * * @return El RegistroImpresion con mayor prioridad extraído, o null si está vacío.
     */
    public RegistroImpresion eliminar_min() {
        if (tamanoActual == 0) return null;
        
        RegistroImpresion min = heap[0];
        min.posicionEnHeap = -1; // Lo desligamos estructuralmente
        
        heap[0] = heap[tamanoActual - 1];
        if (heap[0] != null) heap[0].posicionEnHeap = 0;
        
        heap[tamanoActual - 1] = null;
        tamanoActual--;
        hundir(0);
        
        return min;
    }

    /**
     * Elimina un documento específico de la cola sin imprimirlo, alterando su 
     * etiqueta a prioridad máxima infinita para que suba a la raíz y pueda ser sacado.
     * Complejidad: O(log n).
     * * @param registroACancelar Referencia en memoria O(1) del registro a abortar.
     */
    public void cancelarDocumento(RegistroImpresion registroACancelar) {
        int indice = registroACancelar.posicionEnHeap;

        if (indice != -1 && indice < tamanoActual && heap[indice] == registroACancelar) {
            heap[indice].etiquetaTiempo = -999999; 
            flotar(indice); 
            eliminar_min(); 
        }
    }

    /** @return El arreglo nativo que representa el montículo. */
    public RegistroImpresion[] getHeap() { return heap; }
    
    /** @return Cantidad de elementos actualmente encolados en el montículo. */
    public int getTamanoActual() { return tamanoActual; }
}