package mx.unam.ciencias.edd;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * <p>Clase genérica para listas doblemente ligadas.</p>
 *
 * <p>Las listas nos permiten agregar elementos al inicio o final de la lista,
 * eliminar elementos de la lista, comprobar si un elemento está o no en la
 * lista, y otras operaciones básicas.</p>
 *
 * <p>Las listas no aceptan a <code>null</code> como elemento.</p>
 *
 * @param <T> El tipo de los elementos de la lista.
 */
public class Lista<T> implements Coleccion<T> {

    /* Clase interna privada para nodos. */
    private class Nodo {
        /* El elemento del nodo. */
        private T elemento;
        /* El nodo anterior. */
        private Nodo anterior;
        /* El nodo siguiente. */
        private Nodo siguiente;

        /* Construye un nodo con un elemento. */
        private Nodo(T elemento) {
            this.elemento = elemento;
        }
    }

    /* Clase interna privada para iteradores. */
    private class Iterador implements IteradorLista<T> {
        /* El nodo anterior. */
        private Nodo anterior;
        /* El nodo siguiente. */
        private Nodo siguiente;

        /* Construye un nuevo iterador. */
        private Iterador() {
            start();
        }

        /* Nos dice si hay un elemento siguiente. */
        @Override public boolean hasNext() {
            return siguiente != null;
        }

        /* Nos da el elemento siguiente. */
        @Override public T next() {
            if (siguiente == null)
                throw new NoSuchElementException("Elemento siguiente nulo");
            anterior = siguiente;
            siguiente = siguiente.siguiente;
            return anterior.elemento;
        }

        /* Nos dice si hay un elemento anterior. */
        @Override public boolean hasPrevious() {
            return anterior != null;
        }

        /* Nos da el elemento anterior. */
        @Override public T previous() {
            if (anterior == null)
                throw new NoSuchElementException();
            siguiente = anterior;
            anterior = anterior.anterior;
            return siguiente.elemento;
        }

        /* Mueve el iterador al inicio de la lista. */
        @Override public void start() {
            siguiente = cabeza;
            anterior = null;
        }

        /* Mueve el iterador al final de la lista. */
        @Override public void end() {
            anterior = rabo;
            siguiente = null;
        }
    }

    /* Primer elemento de la lista. */
    private Nodo cabeza;
    /* Último elemento de la lista. */
    private Nodo rabo;
    /* Número de elementos en la lista. */
    private int longitud;

    /**
     * Regresa la longitud de la lista. El método es idéntico a {@link
     * #getElementos}.
     * @return la longitud de la lista, el número de elementos que contiene.
     */
    public int getLongitud() {
        return longitud;
    }

    /**
     * Regresa el número elementos en la lista. El método es idéntico a {@link
     * #getLongitud}.
     * @return el número elementos en la lista.
     */
    @Override public int getElementos() {
        return longitud;
    }

    /**
     * Nos dice si la lista es vacía.
     * @return <code>true</code> si la lista es vacía, <code>false</code> en
     *         otro caso.
     */
    @Override public boolean esVacia() {
        return cabeza == null;
    }

    /**
     * Agrega un elemento a la lista. Si la lista no tiene elementos, el
     * elemento a agregar será el primero y último. El método es idéntico a
     * {@link #agregaFinal}.
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    @Override public void agrega(T elemento) {
        if (elemento == null)
            throw new IllegalArgumentException();
        Nodo n = new Nodo(elemento);
        longitud ++;
        if (esVacia()){
            cabeza = rabo = n;
        }else {
            rabo.siguiente = n;
            n.anterior = rabo;
            rabo = n;
        }
    }

    /**
     * Agrega un elemento al final de la lista. Si la lista no tiene elementos,
     * el elemento a agregar será el primero y último.
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    public void agregaFinal(T elemento) {
        agrega(elemento);
    }

    /**
     * Agrega un elemento al inicio de la lista. Si la lista no tiene elementos,
     * el elemento a agregar será el primero y último.
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    public void agregaInicio(T elemento) {
        if (elemento == null)
            throw new IllegalArgumentException();
        longitud ++;
        Nodo n = new Nodo(elemento);
        if (cabeza == null){
            cabeza = rabo = n;
        }else {
            cabeza.anterior = n;
            n.siguiente = cabeza;
            cabeza = n;
        }
    }

    /**
     * Inserta un elemento en un índice explícito.
     *
     * Si el índice es menor o igual que cero, el elemento se agrega al inicio
     * de la lista. Si el índice es mayor o igual que el número de elementos en
     * la lista, el elemento se agrega al fina de la misma. En otro caso,
     * después de mandar llamar el método, el elemento tendrá el índice que se
     * especifica en la lista.
     * @param i el índice dónde insertar el elemento. Si es menor que 0 el
     *          elemento se agrega al inicio de la lista, y si es mayor o igual
     *          que el número de elementos en la lista se agrega al final.
     * @param elemento el elemento a insertar.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    public void inserta(int i, T elemento) {
        if (elemento == null)
            throw new IllegalArgumentException();
        else if (i <= 0)
            agregaInicio(elemento);
        else if (longitud <= i)
            agregaFinal(elemento);
        else {
            Nodo nodo = buscaNodo(get(i));
            Nodo nuevoNodo = new Nodo(elemento);
            nodo.anterior.siguiente = nuevoNodo;
            nuevoNodo.anterior = nodo.anterior;
            nodo.anterior = nuevoNodo;
            nuevoNodo.siguiente = nodo;
            longitud ++;
        }
    }

    /**
     * Elimina un elemento de la lista. Si el elemento no está contenido en la
     * lista, el método no la modifica.
     * @param elemento el elemento a eliminar.
     */
    @Override public void elimina(T elemento) {
        Nodo nodo = buscaNodo(elemento);
        eliminaNodo(nodo);
    }

    //Metodo Auxiliar buscaNodo
    private Nodo buscaNodo (Object elemento){
        Nodo m = cabeza;
        if(elemento == null)
            return null;
        return auxBN(m, elemento);
    }
    //Metodo Auxiliar de buscaNodo
    private Nodo auxBN(Nodo m,Object elemento){
        if(m == null)
            return null;
        if(m.elemento.equals(elemento))
            return m;
        m = m.siguiente;
        return auxBN(m, elemento);
    }
    //Metodo Auxiliar eliminaNodo
    private void eliminaNodo(Nodo nodo){
        if (nodo == null)
            return;
        else if (longitud == 1 && cabeza.elemento.equals(nodo.elemento))
            limpia();
        else if (nodo.equals(rabo))
            eliminaUltimo();
        else if (nodo.equals(cabeza))
            eliminaPrimero();
        else{
            nodo.anterior.siguiente=nodo.siguiente;
            nodo.siguiente.anterior=nodo.anterior;
            longitud--;
            }
            return;
        }

    /**
     * Elimina el primer elemento de la lista y lo regresa.
     * @return el primer elemento de la lista antes de eliminarlo.
     * @throws NoSuchElementException si la lista es vacía.
     */
    public T eliminaPrimero() {
        if (cabeza == null && rabo == null)
            throw new NoSuchElementException("No hay elementos en esta lista");
        Nodo n = cabeza;
        if(longitud == 1)
            limpia();
        else{
            cabeza = cabeza.siguiente;
            cabeza.anterior = null;
            longitud --;
        }
        return n.elemento;
    }

    /**
     * Elimina el último elemento de la lista y lo regresa.
     * @return el último elemento de la lista antes de eliminarlo.
     * @throws NoSuchElementException si la lista es vacía.
     */
    public T eliminaUltimo() {
        if(cabeza == null && rabo == null)
            throw new NoSuchElementException("No hay elementos en esta lista");
        Nodo n = rabo;
        if(longitud == 1)
            limpia();
        else{
            rabo = rabo.anterior;
            rabo.siguiente = null;
            longitud --;
        }
        return n.elemento;
    }

    /**
     * Nos dice si un elemento está en la lista.
     * @param elemento el elemento que queremos saber si está en la lista.
     * @return <code>true</code> si <code>elemento</code> está en la lista,
     *         <code>false</code> en otro caso.
     */
    @Override public boolean contiene(T elemento) {
        return buscaNodo(elemento) != null;
    }

    /**
     * Regresa la reversa de la lista.
     * @return una nueva lista que es la reversa la que manda llamar el método.
     */
    public Lista<T> reversa() {
        Lista<T> list = new Lista<T>();
        Nodo n = cabeza;
        while (n != null){
            list.agregaInicio(n.elemento);
            n = n.siguiente;
        }
        return list;
    }

    /**
     * Regresa una copia de la lista. La copia tiene los mismos elementos que la
     * lista que manda llamar el método, en el mismo orden.
     * @return una copiad de la lista.
     */
    public Lista<T> copia() {
        Nodo n = cabeza;
        Lista<T> copyList = new Lista<T>();
        while (n != null){
            copyList.agregaFinal(n.elemento);
            n = n.siguiente;
        }
        return copyList;
    }

    /**
     * Limpia la lista de elementos, dejándola vacía.
     */
    @Override public void limpia() {
        cabeza = rabo = null;
        longitud = 0;
    }

    /**
     * Regresa el primer elemento de la lista.
     * @return el primer elemento de la lista.
     * @throws NoSuchElementException si la lista es vacía.
     */
    public T getPrimero() {
        if(cabeza != null)
            return cabeza.elemento;
        throw new NoSuchElementException("Lista vacia");
    }

    /**
     * Regresa el último elemento de la lista.
     * @return el primer elemento de la lista.
     * @throws NoSuchElementException si la lista es vacía.
     */
    public T getUltimo() {
        if(rabo != null)
            return rabo.elemento;
        throw new NoSuchElementException("Lista vacia");
    }

    /**
     * Regresa el <em>i</em>-ésimo elemento de la lista.
     * @param i el índice del elemento que queremos.
     * @return el <em>i</em>-ésimo elemento de la lista.
     * @throws ExcepcionIndiceInvalido si <em>i</em> es menor que cero o mayor o
     *         igual que el número de elementos en la lista.
     */
    public T get(int i) {
        if(i < 0 || i >= longitud)
            throw new ExcepcionIndiceInvalido("Numero mayor a los que hay en la lista");
        Nodo n = cabeza;
        return auxGet(i,n);
    }

    //Metodo auxiliar de get
    private T auxGet(int i, Nodo n) {
        if(i > 0){
            n = n.siguiente;
            return auxGet(i-1, n);
        }
        return n.elemento;
    }

    /**
     * Regresa el índice del elemento recibido en la lista.
     * @param elemento el elemento del que se busca el índice.
     * @return el índice del elemento recibido en la lista, o -1 si el elemento
     *         no está contenido en la lista.
     */
    public int indiceDe(T elemento) {
        Nodo aux = this.cabeza;
        int i = 0;
        return auxInd(i, aux, elemento);
    }

    //Metodo auxiliar de indiceDe
    private int auxInd(int i, Nodo aux, Object elemento){
        if (aux == null)
            return -1;
        if(aux.elemento.equals(elemento))
            return i;
        else{
            aux = aux.siguiente;
            return auxInd(i+1, aux, elemento);
        }
    }

    /**
     * Regresa una representación en cadena de la lista.
     * @return una representación en cadena de la lista.
     */
    @Override public String toString() {
        if(esVacia())
        return "[]";
        int acc = 0;
        Nodo viajero = cabeza;
        return "[" + auxSt(acc, viajero);
    }

    //Metodo auxiliar de toString 
    private String auxSt(int acc, Nodo viajero) {
        if(viajero == rabo)
        return String.format("%s]", get(acc));
        viajero = viajero.siguiente;
        return String.format("%s, ", get(acc)) + auxSt(acc+1, viajero);
    }

    /**
     * Nos dice si la lista es igual al objeto recibido.
     * @param objeto el objeto con el que hay que comparar.
     * @return <code>true</code> si la lista es igual al objeto recibido;
     *         <code>false</code> en otro caso.
     */
    @Override public boolean equals(Object objeto) {
        if (objeto == null || getClass() != objeto.getClass())
            return false;
        @SuppressWarnings("unchecked") Lista<T> lista = (Lista<T>)objeto;
        if (lista == null || longitud != lista.longitud)
            return false;
        if (lista.getLongitud() == 0 && longitud == 0)
            return true;
        Nodo n1 = cabeza;
        Nodo n2 = lista.cabeza;
        return auxEquals(n1, n2);
    }

    //Metodo auxiliar de equals
    private boolean auxEquals(Nodo n1, Nodo n2){
        if(!n1.elemento.equals(n2.elemento))
            return false;
        n1 = n1.siguiente;
        n2 = n2.siguiente;
        if(n1 == null && n2 == null)
            return true;
        return auxEquals(n1, n2);
    }

    /**
     * Regresa un iterador para recorrer la lista en una dirección.
     * @return un iterador para recorrer la lista en una dirección.
     */
    @Override public Iterator<T> iterator() {
        return new Iterador();
    }

    /**
     * Regresa un iterador para recorrer la lista en ambas direcciones.
     * @return un iterador para recorrer la lista en ambas direcciones.
     */
    public IteradorLista<T> iteradorLista() {
        return new Iterador();
    }

    /**
     * Regresa una copia de la lista, pero ordenada. Para poder hacer el
     * ordenamiento, el método necesita una instancia de {@link Comparator} para
     * poder comparar los elementos de la lista.
     * @param comparador el comparador que la lista usará para hacer el
     *                   ordenamiento.
     * @return una copia de la lista, pero ordenada.
     */
    public Lista<T> mergeSort(Comparator<T> comparador) {
        return mergeSort(copia(), comparador);
    }

    private Lista<T> mergeSort(Lista<T> l, Comparator<T> comparador){
        if (l.esVacia() || l.getLongitud() == 1) 
        return l;
        int mitad = l.getLongitud() / 2 ;
        Lista<T> l1 = new Lista<T>();
        Lista<T> l2;
        while (l.getLongitud() != mitad){
            l1.agregaFinal(l.getPrimero());
            l.eliminaPrimero();
        }
        l2 = l.copia();
        return mezcla(mergeSort(l1, comparador), mergeSort(l2, comparador), comparador);
      }
      
      private Lista<T> mezcla(Lista<T> a, Lista<T> b, Comparator<T> comparador){
          Lista<T> listaOrdenada = new Lista<T>();
          while (a.cabeza != null && b.cabeza != null){
              int i = comparador.compare(a.cabeza.elemento, b.cabeza.elemento);
              if (i <= 0){
                listaOrdenada.agregaFinal(a.getPrimero());
                a.eliminaPrimero();
              }else {
                listaOrdenada.agregaFinal(b.getPrimero());
                b.eliminaPrimero();
              }
          }
          while (a.cabeza != null){
            listaOrdenada.agregaFinal(a.getPrimero());
            a.eliminaPrimero();
          }
          while (b.cabeza != null){
            listaOrdenada.agregaFinal(b.getPrimero());
            b.eliminaPrimero();
          }
          return listaOrdenada;
      }  

    /**
     * Regresa una copia de la lista recibida, pero ordenada. La lista recibida
     * tiene que contener nada más elementos que implementan la interfaz {@link
     * Comparable}.
     * @param <T> tipo del que puede ser la lista.
     * @param lista la lista que se ordenará.
     * @return una copia de la lista recibida, pero ordenada.
     */
    public static <T extends Comparable<T>>
    Lista<T> mergeSort(Lista<T> lista) {
        return lista.mergeSort((a, b) -> a.compareTo(b));
    }

    /**
     * Busca un elemento en la lista ordenada, usando el comparador recibido. El
     * método supone que la lista está ordenada usando el mismo comparador.
     * @param elemento el elemento a buscar.
     * @param comparador el comparador con el que la lista está ordenada.
     * @return <code>true</code> si el elemento está contenido en la lista,
     *         <code>false</code> en otro caso.
     */
    public boolean busquedaLineal(T elemento, Comparator<T> comparador) {
        Nodo n = cabeza;
        while (n != null){
            if (comparador.compare(elemento, n.elemento) == 0) return true;
            n = n.siguiente;
        }
        return false;
    }

    /**
     * Busca un elemento en una lista ordenada. La lista recibida tiene que
     * contener nada más elementos que implementan la interfaz {@link
     * Comparable}, y se da por hecho que está ordenada.
     * @param <T> tipo del que puede ser la lista.
     * @param lista la lista donde se buscará.
     * @param elemento el elemento a buscar.
     * @return <code>true</code> si el elemento está contenido en la lista,
     *         <code>false</code> en otro caso.
     */
    public static <T extends Comparable<T>>
    boolean busquedaLineal(Lista<T> lista, T elemento) {
        return lista.busquedaLineal(elemento, (a, b) -> a.compareTo(b));
    }
}
