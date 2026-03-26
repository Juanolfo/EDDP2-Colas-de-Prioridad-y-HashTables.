import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.View;
import javax.swing.JPanel;
import java.awt.Component;
import org.graphstream.ui.swing_viewer.SwingViewer;

/**
 * Clase encargada de la visualización gráfica del Montículo Binario utilizando GraphStream.
 * Renderiza el heap como un árbol binario posicionado en niveles.
 * * @author Utente Locale
 */
public class VisorArbol {

    /**
     * Crea un componente Swing que contiene la representación gráfica del montículo.
     * * @param monticulo El MonticuloBinario que se desea visualizar.
     * @return Un Component de Java AWT/Swing con el grafo dibujado.
     */
    public static Component crearPanelGrafo(MonticuloBinario monticulo) {
        System.setProperty("org.graphstream.ui", "swing"); 

        Graph grafo = new SingleGraph("MonticuloBinario");
        RegistroImpresion[] heap = monticulo.getHeap(); 
        int tamano = monticulo.getTamanoActual();       

        if (tamano == 0) {
            return new JPanel(); 
        }

        // Estilos CSS para los nodos y aristas
        grafo.setAttribute("ui.quality");
        grafo.setAttribute("ui.antialias");
        grafo.setAttribute("ui.stylesheet", 
            "node { " +
            "   fill-color: #3498db; " +
            "   text-color: white; " +
            "   text-size: 16px; " +
            "   size: 80px, 60px; " +
            "   text-alignment: center; " +
            "   text-mode: normal; " +
            "   text-padding: 8px; " +
            "   stroke-mode: plain; " +
            "   stroke-color: #2c3e50; " +
            "   stroke-width: 1px; " +
            "} " +
            "edge { fill-color: #2c3e50; size: 2px; }");

        // Construcción de nodos con posicionamiento manual (Pirámide)
        for (int i = 0; i < tamano; i++) {
            Node n = grafo.addNode(String.valueOf(i));
            
            String nombreDoc = (heap[i].documento != null) ? heap[i].documento.getNombre() : "S/N";
            String label = heap[i].etiquetaTiempo + "\n(" + nombreDoc + ")";
            n.setAttribute("ui.label", label);

            // Lógica de coordenadas para visualización jerárquica
            int nivel = (int) (Math.log(i + 1) / Math.log(2)); 
            int nodosEnNivel = (int) Math.pow(2, nivel);
            int posH = i - (nodosEnNivel - 1); 

            double anchoNivel = 100.0; 
            double x = (posH - (nodosEnNivel - 1) / 2.0) * (anchoNivel / nodosEnNivel);
            double y = nivel * -20.0; 

            n.setAttribute("xyz", x, y, 0);
        }

        // Construcción de conexiones (Padre -> Hijos)
        for (int i = 0; i < tamano; i++) {
            int hijoIzq = (2 * i) + 1;
            int hijoDer = (2 * i) + 2;

            if (hijoIzq < tamano) {
                grafo.addEdge(i + "-" + hijoIzq, String.valueOf(i), String.valueOf(hijoIzq), true);
            }
            if (hijoDer < tamano) {
                grafo.addEdge(i + "-" + hijoDer, String.valueOf(i), String.valueOf(hijoDer), true);
            }
        }

        SwingViewer viewer = new SwingViewer(grafo, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        viewer.disableAutoLayout(); 
        
        View view = viewer.addDefaultView(false);
        return (Component) view;
    }
}