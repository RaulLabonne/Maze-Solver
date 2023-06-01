package mx.unam.ciencias.edd;

/**
 * Clase para árboles rojinegros. Un árbol rojinegro cumple las siguientes
 * propiedades:
 *
 * <ol>
 *  <li>Todos los vértices son NEGROS o ROJOS.</li>
 *  <li>La raíz es NEGRA.</li>
 *  <li>Todas las hojas (<code>null</code>) son NEGRAS (al igual que la raíz).</li>
 *  <li>Un vértice ROJO siempre tiene dos hijos NEGROS.</li>
 *  <li>Todo camino de un vértice a alguna de sus hojas descendientes tiene el
 *      mismo número de vértices NEGROS.</li>
 * </ol>
 *
 * Los árboles rojinegros se autobalancean.
 */
public class ArbolRojinegro<T extends Comparable<T>>
    extends ArbolBinarioOrdenado<T> {

    /**
     * Clase interna protegida para vértices.
     */
    protected class VerticeRojinegro extends Vertice {

        /** El color del vértice. */
        public Color color;

        /**
         * Constructor único que recibe un elemento.
         * @param elemento el elemento del vértice.
         */
        public VerticeRojinegro(T elemento) {
                super(elemento);
                color = Color.NINGUNO;
        }

        /**
         * Regresa una representación en cadena del vértice rojinegro.
         * @return una representación en cadena del vértice rojinegro.
         */
        @Override public String toString() {
            return String.format("%s{%s}", 
                color == Color.ROJO ? "R" : "N", elemento.toString());
        }

        /**
         * Compara el vértice con otro objeto. La comparación es
         * <em>recursiva</em>.
         * @param objeto el objeto con el cual se comparará el vértice.
         * @return <code>true</code> si el objeto es instancia de la clase
         *         {@link VerticeRojinegro}, su elemento es igual al elemento de
         *         éste vértice, los descendientes de ambos son recursivamente
         *         iguales, y los colores son iguales; <code>false</code> en
         *         otro caso.
         */
        @Override public boolean equals(Object objeto) {
            if (objeto == null || getClass() != objeto.getClass())
                return false;
            @SuppressWarnings("unchecked")
                VerticeRojinegro vertice = (VerticeRojinegro)objeto;
            return (color == vertice.color && super.equals(objeto));
        }
    }

    /**
     * Constructor sin parámetros. Para no perder el constructor sin parámetros
     * de {@link ArbolBinarioOrdenado}.
     */
    public ArbolRojinegro() { super(); }

    /**
     * Construye un árbol rojinegro a partir de una colección. El árbol
     * rojinegro tiene los mismos elementos que la colección recibida.
     * @param coleccion la colección a partir de la cual creamos el árbol
     *        rojinegro.
     */
    public ArbolRojinegro(Coleccion<T> coleccion) {
        super(coleccion);
    }

    /**
     * Construye un nuevo vértice, usando una instancia de {@link
     * VerticeRojinegro}.
     * @param elemento el elemento dentro del vértice.
     * @return un nuevo vértice rojinegro con el elemento recibido dentro del mismo.
     */
    @Override protected Vertice nuevoVertice(T elemento) {
        return new VerticeRojinegro(elemento);
    }

    /**
     * Regresa el color del vértice rojinegro.
     * @param vertice el vértice del que queremos el color.
     * @return el color del vértice rojinegro.
     * @throws ClassCastException si el vértice no es instancia de {@link
     *         VerticeRojinegro}.
     */
    public Color getColor(VerticeArbolBinario<T> vertice) {
        VerticeRojinegro verticeRN = (VerticeRojinegro) vertice;
        return verticeRN.color;
    }

    /**
     * Agrega un nuevo elemento al árbol. El método invoca al método {@link
     * ArbolBinarioOrdenado#agrega}, y después balancea el árbol recoloreando
     * vértices y girando el árbol como sea necesario.
     * @param elemento el elemento a agregar.
     */
    @Override public void agrega(T elemento) {
        super.agrega(elemento);
        VerticeRojinegro vRojinegro = (VerticeRojinegro) ultimoAgregado;
        vRojinegro.color = Color.ROJO;
        rebalanceo(vRojinegro);
    }

    private void rebalanceo(VerticeRojinegro v){
        if (v.padre == null){
            v.color = Color.NEGRO;
            return;
        }
        VerticeRojinegro padre = (VerticeRojinegro) v.padre;
        if (!esRojo(padre))
            return;

        VerticeRojinegro abuelo = (VerticeRojinegro) v.padre.padre;
        VerticeRojinegro tio = abuelo.izquierdo == padre ? (VerticeRojinegro)abuelo.derecho : (VerticeRojinegro)abuelo.izquierdo;

        if (esRojo(tio)){
            tio.color = padre.color = Color.NEGRO;
            abuelo.color = Color.ROJO;
            rebalanceo(abuelo);
            return;
        }

        if (!enderezados(v, padre)){
            if (esIzquierdo(padre))
                super.giraIzquierda(padre);
            else 
                super.giraDerecha(padre);
            VerticeRojinegro cambio = v;
            v = padre;
            padre = cambio;
        }

        padre.color = Color.NEGRO;
        abuelo.color = Color.ROJO;

        if(esIzquierdo(v))
            super.giraDerecha(abuelo);
        else 
            super.giraIzquierda(abuelo);
        return;

    }

    private boolean esRojo(VerticeRojinegro v){
        return (v != null && v.color == Color.ROJO);
    }

    private boolean enderezados(VerticeRojinegro v, VerticeRojinegro p){
        return (esIzquierdo(v) && esIzquierdo(p) || !esIzquierdo(v) && !esIzquierdo(p));
    }

    private boolean esIzquierdo(VerticeRojinegro v){
        return v.padre.izquierdo == v;
    }

    /**
     * Elimina un elemento del árbol. El método elimina el vértice que contiene
     * el elemento, y recolorea y gira el árbol como sea necesario para
     * rebalancearlo.
     * @param elemento el elemento a eliminar del árbol.
     */
    @Override public void elimina(T elemento) {
        VerticeRojinegro v = (VerticeRojinegro) busca(elemento);
        if (v == null)
            return;
        elementos --;
        if (v.izquierdo != null && v.derecho != null){
            v = (VerticeRojinegro) intercambiaEliminable(v);
        }
        VerticeRojinegro hijo;
        VerticeRojinegro casper = null;
        if (v.izquierdo == null && v.derecho == null){
            casper = (VerticeRojinegro) nuevoVertice(null);
            casper.color = Color.NEGRO;
            casper.padre = v;
            v.izquierdo = casper;
            hijo = casper;
        }
        else
            hijo = v.izquierdo != null ? (VerticeRojinegro)v.izquierdo : (VerticeRojinegro)v.derecho;

        eliminaVertice(v);
        if(hijo.color == Color.ROJO || v.color == Color.ROJO)
            hijo.color = Color.NEGRO;
        else if (hijo.color == Color.NEGRO && v.color == Color.NEGRO){
            rebalanceador(hijo);
        }
        if (casper != null)
            eliminaVertice(casper);
    }

    private void rebalanceador(VerticeRojinegro v){
        if (v.padre == null)
            return;
        VerticeRojinegro padre = (VerticeRojinegro)v.padre;
        VerticeRojinegro hermano = padre.izquierdo == v ? (VerticeRojinegro)padre.derecho : (VerticeRojinegro)padre.izquierdo;

        if (hermano.color == Color.ROJO){
            padre.color = Color.ROJO;
            hermano.color = Color.NEGRO;
            if(esIzquierdo(v))
                super.giraIzquierda(padre);
            else
                super.giraDerecha(padre);
            padre = (VerticeRojinegro)v.padre;
            hermano = padre.izquierdo == v ? (VerticeRojinegro)padre.derecho : (VerticeRojinegro)padre.izquierdo;
            }
        VerticeRojinegro hi = (VerticeRojinegro) hermano.izquierdo;
        VerticeRojinegro hd = (VerticeRojinegro) hermano.derecho;

        if (!esRojo(padre) && !esRojo(hermano) && !esRojo(hi) && !esRojo(hd)){
            hermano.color = Color.ROJO;
            rebalanceador(padre);
            return;
        }

        if (esRojo(padre) && !esRojo(hermano) && !esRojo(hi) && !esRojo(hd)){
            hermano.color = Color.ROJO;
            padre.color = Color.NEGRO;
            return;
        }

        VerticeRojinegro hijo = esIzquierdo(v) ? hi : hd;
        if (esIzquierdo(v) && esRojo(hi) && !esRojo(hd) || !esIzquierdo(v) && !esRojo(hi) && esRojo(hd)){
            hermano.color = Color.ROJO;
            hijo.color = Color.NEGRO;
            if(esIzquierdo(v))
                super.giraDerecha(hermano);
            else
                super.giraIzquierda(hermano);
        }
        hermano = padre.izquierdo == v ? (VerticeRojinegro)padre.derecho : (VerticeRojinegro)padre.izquierdo;
        hi = (VerticeRojinegro) hermano.izquierdo;
        hd = (VerticeRojinegro) hermano.derecho;

        hermano.color = padre.color;
        padre.color = Color.NEGRO;
        if (esIzquierdo(v)){
            hd.color = Color.NEGRO;
            super.giraIzquierda(padre);
        }
        else{
            hi.color = Color.NEGRO;
            super.giraDerecha(padre);
        }
    }

    /**
     * Lanza la excepción {@link UnsupportedOperationException}: los árboles
     * rojinegros no pueden ser girados a la izquierda por los usuarios de la
     * clase, porque se desbalancean.
     * @param vertice el vértice sobre el que se quiere girar.
     * @throws UnsupportedOperationException siempre.
     */
    @Override public void giraIzquierda(VerticeArbolBinario<T> vertice) {
        throw new UnsupportedOperationException("Los árboles rojinegros no " +
                                                "pueden girar a la izquierda " +
                                                "por el usuario.");
    }

    /**
     * Lanza la excepción {@link UnsupportedOperationException}: los árboles
     * rojinegros no pueden ser girados a la derecha por los usuarios de la
     * clase, porque se desbalancean.
     * @param vertice el vértice sobre el que se quiere girar.
     * @throws UnsupportedOperationException siempre.
     */
    @Override public void giraDerecha(VerticeArbolBinario<T> vertice) {
        throw new UnsupportedOperationException("Los árboles rojinegros no " +
                                                "pueden girar a la derecha " +
                                                "por el usuario.");
    }
}
