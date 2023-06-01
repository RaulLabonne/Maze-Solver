package mx.unam.ciencias.edd;

import java.util.Iterator;

/**
 * Clase para conjuntos.
 */
public class Conjunto<T> implements Coleccion<T> {

    /* El conjunto de elementos. */
    private Diccionario<T, T> conjunto;

    /**
     * Crea un nuevo conjunto.
     */
    public Conjunto() {
        conjunto = new Diccionario<>();
    }

    /**
     * Crea un nuevo conjunto para un número determinado de elementos.
     * @param n el número tentativo de elementos.
     */
    public Conjunto(int n) {
        conjunto = new Diccionario<>(n);
    }

    /**
     * Agrega un elemento al conjunto.
     * @param elemento el elemento que queremos agregar al conjunto.
     * @throws IllegalArgumentException si el elemento es <code>null</code>.
     */
    @Override public void agrega(T elemento) {
        conjunto.agrega(elemento, elemento);
    }

    /**
     * Nos dice si el elemento está en el conjunto.
     * @param elemento el elemento que queremos saber si está en el conjunto.
     * @return <code>true</code> si el elemento está en el conjunto,
     *         <code>false</code> en otro caso.
     */
    @Override public boolean contiene(T elemento) {
        return conjunto.contiene(elemento);
    }

    /**
     * Elimina el elemento del conjunto, si está.
     * @param elemento el elemento que queremos eliminar del conjunto.
     */
    @Override public void elimina(T elemento) {
        if (elemento == null)
            return;
        conjunto.elimina(elemento);
    }

    /**
     * Nos dice si el conjunto es vacío.
     * @return <code>true</code> si el conjunto es vacío, <code>false</code> en
     *         otro caso.
     */
    @Override public boolean esVacia() {
        return conjunto.esVacia();
    }

    /**
     * Regresa el número de elementos en el conjunto.
     * @return el número de elementos en el conjunto.
     */
    @Override public int getElementos() {
        return conjunto.getElementos();
    }

    /**
     * Limpia el conjunto de elementos, dejándolo vacío.
     */
    @Override public void limpia() {
        conjunto.limpia();
    }

    /**
     * Regresa la intersección del conjunto y el conjunto recibido.
     * @param conjunto el conjunto que queremos intersectar con éste.
     * @return la intersección del conjunto y el conjunto recibido.
     */
    public Conjunto<T> interseccion(Conjunto<T> conjunto) {
        Conjunto<T> interseccion =  new Conjunto<>();
        for (T t : conjunto)
            if (this.conjunto.contiene(t))
                interseccion.agrega(t);
        return interseccion;
    }

    /**
     * Regresa la unión del conjunto y el conjunto recibido.
     * @param conjunto el conjunto que queremos unir con éste.
     * @return la unión del conjunto y el conjunto recibido.
     */
    public Conjunto<T> union(Conjunto<T> conjunto) {
        Conjunto<T> union = new Conjunto<>();
        for (T t : conjunto)
            union.agrega(t);
        for (T t : this.conjunto)
            union.agrega(t);
        return union;
    }

    /**
     * Regresa una representación en cadena del conjunto.
     * @return una representación en cadena del conjunto.
     */
    @Override public String toString() {
        if (esVacia())
            return "{}";
        String s = "{ ";
        Iterator<T> iterador = iterator();
        while (iterador.hasNext())
            s += iterador.next() + ", ";
        return s.substring(0, s.length()-2) + " }";
    }

    /**
     * Nos dice si el conjunto es igual al objeto recibido.
     * @param o el objeto que queremos saber si es igual al conjunto.
     * @return <code>true</code> si el objeto recibido es instancia de Conjunto,
     *         y tiene los mismos elementos.
     */
    @Override public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        @SuppressWarnings("unchecked") Conjunto<T> c = (Conjunto<T>)o;
        return conjunto.equals(c.conjunto);
    }

    /**
     * Regresa un iterador para iterar el conjunto.
     * @return un iterador para iterar el conjunto.
     */
    @Override public Iterator<T> iterator() {
        return conjunto.iterator();
    }
}
