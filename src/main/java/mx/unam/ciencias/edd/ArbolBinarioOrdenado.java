package mx.unam.ciencias.edd;

import java.util.Iterator;

/**
 * <p>Clase para árboles binarios ordenados. Los árboles son genéricos, pero
 * acotados a la interfaz {@link Comparable}.</p>
 *
 * <p>Un árbol instancia de esta clase siempre cumple que:</p>
 * <ul>
 *   <li>Cualquier elemento en el árbol es mayor o igual que todos sus
 *       descendientes por la izquierda.</li>
 *   <li>Cualquier elemento en el árbol es menor o igual que todos sus
 *       descendientes por la derecha.</li>
 * </ul>
 */
public class ArbolBinarioOrdenado<T extends Comparable<T>>
    extends ArbolBinario<T> {

    /* Clase interna privada para iteradores. */
    private class Iterador implements Iterator<T> {

        /* Pila para recorrer los vértices en DFS in-order. */
        private Pila<Vertice> pila;

        /* Inicializa al iterador. */
        private Iterador() {
            pila = new Pila<Vertice>();
            if(raiz == null)
                return;
            pila.mete(raiz);
            Vertice v = raiz;
            while ((v = v.izquierdo) != null){
                pila.mete(v);
            }
        }

        /* Nos dice si hay un elemento siguiente. */
        @Override public boolean hasNext() {
            return !pila.esVacia();
        }

        /* Regresa el siguiente elemento en orden DFS in-order. */
        @Override public T next() {
            Vertice v = pila.saca();

            if (v.derecho != null) {
                Vertice vAux = v.derecho;
                pila.mete(vAux);

                while ((vAux = vAux.izquierdo) != null)
                    pila.mete(vAux);
            }

            return v.elemento;
        }
    }

    /**
     * El vértice del último elemento agegado. Este vértice sólo se puede
     * garantizar que existe <em>inmediatamente</em> después de haber agregado
     * un elemento al árbol. Si cualquier operación distinta a agregar sobre el
     * árbol se ejecuta después de haber agregado un elemento, el estado de esta
     * variable es indefinido.
     */
    protected Vertice ultimoAgregado;

    /**
     * Constructor sin parámetros. Para no perder el constructor sin parámetros
     * de {@link ArbolBinario}.
     */
    public ArbolBinarioOrdenado() { super(); }

    /**
     * Construye un árbol binario ordenado a partir de una colección. El árbol
     * binario ordenado tiene los mismos elementos que la colección recibida.
     * @param coleccion la colección a partir de la cual creamos el árbol
     *        binario ordenado.
     */
    public ArbolBinarioOrdenado(Coleccion<T> coleccion) {
        super(coleccion);
    }

    /**
     * Agrega un nuevo elemento al árbol. El árbol conserva su orden in-order.
     * @param elemento el elemento a agregar.
     */
    @Override public void agrega(T elemento) {
        if (elemento == null)
            throw new IllegalArgumentException("Imposible agregar un elemento nulo");
        Vertice v = nuevoVertice(elemento);
        elementos ++;
        if(raiz == null)
            raiz = v;
        else
            agrega(raiz, v);
        ultimoAgregado = v;
    }

    private void agrega(Vertice actual, Vertice agregar){
        if(agregar.elemento.compareTo(actual.elemento) <= 0)
            if(actual.izquierdo == null){
                actual.izquierdo = agregar;
                agregar.padre = actual;
            } else 
                agrega(actual.izquierdo, agregar);
        else {
            if(actual.derecho == null){
                actual.derecho = agregar;
                agregar.padre = actual;
            } else {
                agrega(actual.derecho, agregar);
            }
        }
    }

    /**
     * Elimina un elemento. Si el elemento no está en el árbol, no hace nada; si
     * está varias veces, elimina el primero que encuentre (in-order). El árbol
     * conserva su orden in-order.
     * @param elemento el elemento a eliminar.
     */
    @Override public void elimina(T elemento) {
        Vertice eliminar = vertice(busca(elemento));
        if (eliminar == null)
            return;
        elementos --;
        if (eliminar.hayDerecho() && eliminar.hayIzquierdo())
            eliminar = intercambiaEliminable(eliminar);
        eliminaVertice(eliminar);
    }

    private Vertice maximoEnSubarbol(Vertice v){
        return (!v.hayDerecho()) ? v : maximoEnSubarbol(v.derecho);
    }

    /**
     * Intercambia el elemento de un vértice con dos hijos distintos de
     * <code>null</code> con el elemento de un descendiente que tenga a lo más
     * un hijo.
     * @param vertice un vértice con dos hijos distintos de <code>null</code>.
     * @return el vértice descendiente con el que vértice recibido se
     *         intercambió. El vértice regresado tiene a lo más un hijo distinto
     *         de <code>null</code>.
     */
    protected Vertice intercambiaEliminable(Vertice vertice) {
        Vertice intercambia = maximoEnSubarbol(vertice.izquierdo);
        T elemIntercambiar = intercambia.elemento;
        intercambia.elemento = vertice.elemento;
        vertice.elemento = elemIntercambiar;
        return intercambia;
    }

    /**
     * Elimina un vértice que a lo más tiene un hijo distinto de
     * <code>null</code> subiendo ese hijo (si existe).
     * @param vertice el vértice a eliminar; debe tener a lo más un hijo
     *                distinto de <code>null</code>.
     */
    protected void eliminaVertice(Vertice vertice) {
        Vertice hijo = vertice.hayIzquierdo() ? vertice.izquierdo : vertice.derecho;
        if (vertice.hayPadre())
            if(vertice.padre.izquierdo == vertice)
                vertice.padre.izquierdo = hijo;
            else
                vertice.padre.derecho = hijo;
        else 
            raiz = hijo;
        if (hijo != null)
            hijo.padre = vertice.padre;
    }

    /**
     * Busca un elemento en el árbol recorriéndolo in-order. Si lo encuentra,
     * regresa el vértice que lo contiene; si no, regresa <code>null</code>.
     * @param elemento el elemento a buscar.
     * @return un vértice que contiene al elemento buscado si lo
     *         encuentra; <code>null</code> en otro caso.
     */
    @Override public VerticeArbolBinario<T> busca(T elemento) {
        return busca(raiz, elemento);
    }

    private VerticeArbolBinario<T> busca(Vertice v, T elemento){
        if(v == null)
            return null;
        if(elemento.equals(v.elemento))
            return v;
        if (elemento.compareTo(v.elemento) <= 0)
            return busca(v.izquierdo, elemento);
        return busca(v.derecho, elemento);
    }

    /**
     * Regresa el vértice que contiene el último elemento agregado al
     * árbol. Este método sólo se puede garantizar que funcione
     * <em>inmediatamente</em> después de haber invocado al método {@link
     * agrega}. Si cualquier operación distinta a agregar sobre el árbol se
     * ejecuta después de haber agregado un elemento, el comportamiento de este
     * método es indefinido.
     * @return el vértice que contiene el último elemento agregado al árbol, si
     *         el método es invocado inmediatamente después de agregar un
     *         elemento al árbol.
     */
    public VerticeArbolBinario<T> getUltimoVerticeAgregado() {
        return ultimoAgregado;
    }

    /**
     * Gira el árbol a la derecha sobre el vértice recibido. Si el vértice no
     * tiene hijo izquierdo, el método no hace nada.
     * @param vertice el vértice sobre el que vamos a girar.
     */
    public void giraDerecha(VerticeArbolBinario<T> vertice) {
        Vertice v = vertice(vertice);

        if (v.izquierdo == null)
            return;

        Vertice hijo = v.izquierdo;
        hijo.padre = v.padre;

        if (v.padre == null)
            raiz = hijo;
        else {
            if (v.padre.izquierdo == v)
                v.padre.izquierdo = hijo;
            else
                v.padre.derecho = hijo;
        }

        v.izquierdo = hijo.derecho;

        if (v.izquierdo != null)
            v.izquierdo.padre = v;

        hijo.derecho = v;
        v.padre = hijo;
    }

    /**
     * Gira el árbol a la izquierda sobre el vértice recibido. Si el vértice no
     * tiene hijo derecho, el método no hace nada.
     * @param vertice el vértice sobre el que vamos a girar.
     */
    public void giraIzquierda(VerticeArbolBinario<T> vertice) {
        Vertice v = vertice(vertice);

        if (v.derecho == null)
            return;

        Vertice hijo = v.derecho;
        hijo.padre = v.padre;

        if (v.padre == null)
            raiz = hijo;
        else {
            if (v.padre.derecho == v)
                v.padre.derecho = hijo;
            else
                v.padre.izquierdo = hijo;
        }

        v.derecho = hijo.izquierdo;

        if (v.derecho != null)
            v.derecho.padre = v;

        hijo.izquierdo = v;
        v.padre = hijo;
    }

    /**
     * Realiza un recorrido DFS <em>pre-order</em> en el árbol, ejecutando la
     * acción recibida en cada elemento del árbol.
     * @param accion la acción a realizar en cada elemento del árbol.
     */
    public void dfsPreOrder(AccionVerticeArbolBinario<T> accion) {
        dfsPreOrder(accion, raiz);
    }

    private void dfsPreOrder(AccionVerticeArbolBinario<T> accion, Vertice v){
    if (v == null)
        return;
    accion.actua(v);
    dfsPreOrder(accion, v.izquierdo);
    dfsPreOrder(accion, v.derecho);
    }
    /**
     * Realiza un recorrido DFS <em>in-order</em> en el árbol, ejecutando la
     * acción recibida en cada elemento del árbol.
     * @param accion la acción a realizar en cada elemento del árbol.
     */
    public void dfsInOrder(AccionVerticeArbolBinario<T> accion) {
        dfsInOrder(accion, raiz);
    }

    private void dfsInOrder(AccionVerticeArbolBinario<T> accion, Vertice v){
        if (v == null)
            return;
        dfsInOrder(accion, v.izquierdo);
        accion.actua(v);
        dfsInOrder(accion, v.derecho);
    }

    /**
     * Realiza un recorrido DFS <em>post-order</em> en el árbol, ejecutando la
     * acción recibida en cada elemento del árbol.
     * @param accion la acción a realizar en cada elemento del árbol.
     */
    public void dfsPostOrder(AccionVerticeArbolBinario<T> accion) {
        dfsPostOrder(accion, raiz);
    }

    private void dfsPostOrder(AccionVerticeArbolBinario<T> accion, Vertice v){
        if (v == null)
            return;
        dfsPostOrder(accion, v.izquierdo);
        dfsPostOrder(accion, v.derecho);
        accion.actua(v);
    }
    /**
     * Regresa un iterador para iterar el árbol. El árbol se itera en orden.
     * @return un iterador para iterar el árbol.
     */
    @Override public Iterator<T> iterator() {
        return new Iterador();
    }
}
