package mx.unam.ciencias.edd;

/**
 * <p>Clase para árboles AVL.</p>
 *
 * <p>Un árbol AVL cumple que para cada uno de sus vértices, la diferencia entre
 * la áltura de sus subárboles izquierdo y derecho está entre -1 y 1.</p>
 */
public class ArbolAVL<T extends Comparable<T>>
    extends ArbolBinarioOrdenado<T> {

    /**
     * Clase interna protegida para vértices.
     */
    protected class VerticeAVL extends Vertice {

        /** La altura del vértice. */
        public int altura;

        /**
         * Constructor único que recibe un elemento.
         * @param elemento el elemento del vértice.
         */
        public VerticeAVL(T elemento) {
            super(elemento);
        }

        /**
         * Regresa la altura del vértice.
         * @return la altura del vértice.
         */
        @Override public int altura() {
            return altura;
        }

        /**
         * Regresa una representación en cadena del vértice AVL.
         * @return una representación en cadena del vértice AVL.
         */
        @Override public String toString() {
            return String.format("%s %d/%d", elemento, altura, balance(this));
        }

        
        
        /**
         * Compara el vértice con otro objeto. La comparación es
         * <em>recursiva</em>.
         * @param objeto el objeto con el cual se comparará el vértice.
         * @return <code>true</code> si el objeto es instancia de la clase
         *         {@link VerticeAVL}, su elemento es igual al elemento de éste
         *         vértice, los descendientes de ambos son recursivamente
         *         iguales, y las alturas son iguales; <code>false</code> en
         *         otro caso.
         */
        @Override public boolean equals(Object objeto) {
            if (objeto == null || getClass() != objeto.getClass())
            return false;
            @SuppressWarnings("unchecked") VerticeAVL vertice = (VerticeAVL)objeto;
            return (altura == vertice.altura && super.equals(objeto));
        }
    }
    
    /**
     * Constructor sin parámetros. Para no perder el constructor sin parámetros
     * de {@link ArbolBinarioOrdenado}.
     */
    public ArbolAVL() { super(); }
    
    /**
     * Construye un árbol AVL a partir de una colección. El árbol AVL tiene los
     * mismos elementos que la colección recibida.
     * @param coleccion la colección a partir de la cual creamos el árbol AVL.
     */
    public ArbolAVL(Coleccion<T> coleccion) {
        super(coleccion);
    }
    
    /**
     * Construye un nuevo vértice, usando una instancia de {@link VerticeAVL}.
     * @param elemento el elemento dentro del vértice.
     * @return un nuevo vértice con el elemento recibido dentro del mismo.
     */
    @Override protected Vertice nuevoVertice(T elemento) {
        return new VerticeAVL(elemento);
    }
    
    /**
     * Agrega un nuevo elemento al árbol. El método invoca al método {@link
     * ArbolBinarioOrdenado#agrega}, y después balancea el árbol girándolo como
     * sea necesario.
     * @param elemento el elemento a agregar.
     */
    @Override public void agrega(T elemento) {
        super.agrega(elemento);
        rebalanceo((VerticeAVL) ultimoAgregado);
    }
    
    /**
     * Elimina un elemento del árbol. El método elimina el vértice que contiene
     * el elemento, y gira el árbol como sea necesario para rebalancearlo.
     * @param elemento el elemento a eliminar del árbol.
     */
    @Override public void elimina(T elemento) {
        VerticeAVL v = (VerticeAVL) busca(elemento);
        if (v == null)
            return;
        elementos--;
        if (v.izquierdo != null && v.derecho != null)
            v = (VerticeAVL)intercambiaEliminable(v);
        eliminaVertice(v);
        rebalanceo((VerticeAVL)v.padre);
    }
    
    private void rebalanceo(VerticeAVL v){
        if (v == null)
            return;
        nuevaAltura(v);
        if (balance(v) == -2){
            VerticeAVL vd = (VerticeAVL) v.derecho;
            VerticeAVL hIzq = (VerticeAVL) vd.izquierdo;
            if (balance(vd) == 1){
                super.giraDerecha(vd);
                nuevaAltura(vd);
                nuevaAltura(hIzq);
                vd = (VerticeAVL) v.derecho;
                hIzq = (VerticeAVL) vd.izquierdo;
            }
            super.giraIzquierda(v);
            nuevaAltura(v);
            nuevaAltura(vd);
        } else if (balance(v) == 2){
            VerticeAVL vi = (VerticeAVL) v.izquierdo;
            VerticeAVL hDer = (VerticeAVL) vi.derecho;
            if (balance(vi) == -1){
                super.giraIzquierda(vi);
                nuevaAltura(vi);
                nuevaAltura(hDer);
                vi = (VerticeAVL) v.izquierdo;
                hDer = (VerticeAVL) vi.derecho;
            }
            super.giraDerecha(v);
            nuevaAltura(v);
            nuevaAltura(vi);
        }
        rebalanceo((VerticeAVL)v.padre);
    }
        
    private void nuevaAltura(VerticeAVL v){
        v.altura = 1 + Math.max(altura((VerticeAVL)v.izquierdo), altura((VerticeAVL)v.derecho)); 
    }
        
    private int altura(VerticeAVL vertice) {
        return vertice == null ? -1 : vertice.altura;
    }
        
    private int balance(VerticeAVL vavl){
        return altura((VerticeAVL)vavl.izquierdo) - altura((VerticeAVL)vavl.derecho);
    }

    /**
     * Lanza la excepción {@link UnsupportedOperationException}: los árboles AVL
     * no pueden ser girados a la derecha por los usuarios de la clase, porque
     * se desbalancean.
     * @param vertice el vértice sobre el que se quiere girar.
     * @throws UnsupportedOperationException siempre.
     */
    @Override public void giraDerecha(VerticeArbolBinario<T> vertice) {
        throw new UnsupportedOperationException("Los árboles AVL no  pueden " +
                                                "girar a la izquierda por el " +
                                                "usuario.");
    }


    /**
     * Lanza la excepción {@link UnsupportedOperationException}: los árboles AVL
     * no pueden ser girados a la izquierda por los usuarios de la clase, porque
     * se desbalancean.
     * @param vertice el vértice sobre el que se quiere girar.
     * @throws UnsupportedOperationException siempre.
     */
    @Override public void giraIzquierda(VerticeArbolBinario<T> vertice) {
        throw new UnsupportedOperationException("Los árboles AVL no  pueden " +
                                                "girar a la derecha por el " +
                                                "usuario.");
    }
}
