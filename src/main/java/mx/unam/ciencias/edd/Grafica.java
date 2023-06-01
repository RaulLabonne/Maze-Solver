package mx.unam.ciencias.edd;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Clase para gráficas. Una gráfica es un conjunto de vértices y aristas, tales
 * que las aristas son un subconjunto del producto cruz de los vértices.
 */
public class Grafica<T> implements Coleccion<T> {

    /* Clase interna privada para iteradores. */
    private class Iterador implements Iterator<T> {

        /* Iterador auxiliar. */
        private Iterator<Vertice> iterador;

        /* Construye un nuevo iterador, auxiliándose de la lista de vértices. */
        public Iterador() {
            iterador = vertices.iterator();
        }

        /* Nos dice si hay un siguiente elemento. */
        @Override public boolean hasNext() {
            return iterador.hasNext();
        }

        /* Regresa el siguiente elemento. */
        @Override public T next() {
            return iterador.next().elemento;
        }
    }

    /* Clase interna privada para vértices. */
    private class Vertice implements VerticeGrafica<T>,
                          ComparableIndexable<Vertice> {

        /* El elemento del vértice. */
        private T elemento;
        /* El color del vértice. */
        private Color color;
        /* La distancia del vértice. */
        private double distancia;
        /* El índice del vértice. */
        private int indice;
        /* El diccionario de vecinos del vértice. */
        private Diccionario<T, Vecino> vecinos;

        /* Crea un nuevo vértice a partir de un elemento. */
        public Vertice(T elemento) {
            this.elemento = elemento;
            color = Color.NINGUNO;
            vecinos = new Diccionario<>();
        }

        /* Regresa el elemento del vértice. */
        @Override public T get() {
            return elemento;
        }

        /* Regresa el grado del vértice. */
        @Override public int getGrado() {
            return vecinos.getElementos();
        }

        /* Regresa el color del vértice. */
        @Override public Color getColor() {
            return color;
        }

        /* Regresa un iterable para los vecinos. */
        @Override public Iterable<? extends VerticeGrafica<T>> vecinos() {
            return vecinos;
        }

        /* Define el índice del vértice. */
        @Override public void setIndice(int indice) {
            this.indice = indice;
        }

        /* Regresa el índice del vértice. */
        @Override public int getIndice() {
            return indice;
        }

        /* Compara dos vértices por distancia. */
        @Override public int compareTo(Vertice vertice) {
            return Double.compare(distancia, vertice.distancia);
        }
    }

    /* Clase interna privada para vértices vecinos. */
    private class Vecino implements VerticeGrafica<T> {

        /* El vértice vecino. */
        public Vertice vecino;
        /* El peso de la arista conectando al vértice con su vértice vecino. */
        public double peso;

        /* Construye un nuevo vecino con el vértice recibido como vecino y el
         * peso especificado. */
        public Vecino(Vertice vecino, double peso) {
            this.vecino = vecino;
            this.peso = peso;
        }

        /* Regresa el elemento del vecino. */
        @Override public T get() {
            return vecino.elemento;
        }

        /* Regresa el grado del vecino. */
        @Override public int getGrado() {
            return vecino.getGrado();
        }

        /* Regresa el color del vecino. */
        @Override public Color getColor() {
            return vecino.color;
        }

        /* Regresa un iterable para los vecinos del vecino. */
        @Override public Iterable<? extends VerticeGrafica<T>> vecinos() {
            return vecino.vecinos;
        }
    }

    /* Interface para poder usar lambdas al buscar el elemento que sigue al
     * reconstruir un camino. */
    @FunctionalInterface
    private interface BuscadorCamino<T> {
        /* Regresa true si el vértice se sigue del vecino. */
        public boolean seSiguen(Grafica<T>.Vertice v, Grafica<T>.Vecino a);
    }

    /* Vértices. */
    private Diccionario<T, Vertice> vertices;
    /* Número de aristas. */
    private int aristas;

    /**
     * Constructor único.
     */
    public Grafica() {
        vertices = new Diccionario<>();
    }

    /**
     * Regresa el número de elementos en la gráfica. El número de elementos es
     * igual al número de vértices.
     * @return el número de elementos en la gráfica.
     */
    @Override public int getElementos() {
        return vertices.getElementos();
    }

    /**
     * Regresa el número de aristas.
     * @return el número de aristas.
     */
    public int getAristas() {
        return aristas;
    }

    /**
     * Agrega un nuevo elemento a la gráfica.
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si el elemento ya había sido agregado a
     *         la gráfica.
     */
    @Override public void agrega(T elemento) {
        if (elemento == null)
            throw new IllegalArgumentException("EL elemento es nulo");
        if (contiene(elemento))
            throw new IllegalArgumentException("EL elemento ya existe en la grafica");
        Vertice v = new Vertice(elemento);
        vertices.agrega(elemento, v);
    }

    /**
     * Conecta dos elementos de la gráfica. Los elementos deben estar en la
     * gráfica. El peso de la arista que conecte a los elementos será 1.
     * @param a el primer elemento a conectar.
     * @param b el segundo elemento a conectar.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     * @throws IllegalArgumentException si a o b ya están conectados, o si a es
     *         igual a b.
     */
    public void conecta(T a, T b) {
        conecta(a, b, 1);
    }

    /**
     * Conecta dos elementos de la gráfica. Los elementos deben estar en la
     * gráfica.
     * @param a el primer elemento a conectar.
     * @param b el segundo elemento a conectar.
     * @param peso el peso de la nueva vecino.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     * @throws IllegalArgumentException si a o b ya están conectados, si a es
     *         igual a b, o si el peso es no positivo.
     */
    public void conecta(T a, T b, double peso) {
        if (a.equals(b))
            throw new IllegalArgumentException("Los elementos son los mismos");
        if (peso <= 0)
            throw new IllegalArgumentException("El peso debe ser mayor a 0");
        Vertice v1 = (Vertice) vertice(a);
        Vertice v2 = (Vertice) vertice(b);
        if (sonVecinos(v1.elemento, v2.elemento))
            throw new IllegalArgumentException("Los vertices ya estan conectados");
        v1.vecinos.agrega(b, new Vecino(v2, peso));
        v2.vecinos.agrega(a, new Vecino(v1, peso)); 
        aristas ++;
    }

    /**
     * Desconecta dos elementos de la gráfica. Los elementos deben estar en la
     * gráfica y estar conectados entre ellos.
     * @param a el primer elemento a desconectar.
     * @param b el segundo elemento a desconectar.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     * @throws IllegalArgumentException si a o b no están conectados.
     */
    public void desconecta(T a, T b) {
        Vertice v1 = (Vertice) vertice(a);
        Vertice v2 = (Vertice) vertice(b);
        if (!sonVecinos(a, b))
            throw new IllegalArgumentException("Los vertices no estan conectados"); 
        for (Vecino vecino : v1.vecinos)
            if (vecino.vecino.elemento.equals(v2.elemento))
                    v1.vecinos.elimina(b);
                /* v1.vecinos.elimina(vecino); */
        for (Vecino vecino2 : v2.vecinos)
            if (vecino2.vecino.elemento.equals(v1.elemento))
                v2.vecinos.elimina(a);
        aristas --;
    }

    /**
     * Nos dice si el elemento está contenido en la gráfica.
     * @return <code>true</code> si el elemento está contenido en la gráfica,
     *         <code>false</code> en otro caso.
     */
    @Override public boolean contiene(T elemento) {
        for (Vertice vertice : vertices) {
            if (vertice.elemento.equals(elemento))
                return true;
        }
        return false;
    }

    /**
     * Elimina un elemento de la gráfica. El elemento tiene que estar contenido
     * en la gráfica.
     * @param elemento el elemento a eliminar.
     * @throws NoSuchElementException si el elemento no está contenido en la
     *         gráfica.
     */
    @Override public void elimina(T elemento) {
        Vertice v = (Vertice) vertice(elemento);
        for (Vecino vertice : v.vecinos)
            desconecta(v.elemento, vertice.vecino.elemento);
        vertices.elimina(elemento);
    }

    /**
     * Nos dice si dos elementos de la gráfica están conectados. Los elementos
     * deben estar en la gráfica.
     * @param a el primer elemento.
     * @param b el segundo elemento.
     * @return <code>true</code> si a y b son vecinos, <code>false</code> en otro caso.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     */
    public boolean sonVecinos(T a, T b) {
        Vertice v1 = (Vertice) vertice(a);
        Vertice v2 = (Vertice) vertice(b);
        for (Vecino vecino : v1.vecinos)
            if (vecino.vecino.elemento.equals(v2.elemento))
                return true;
        return false;
    }

    /**
     * Regresa el peso de la arista que comparten los vértices que contienen a
     * los elementos recibidos.
     * @param a el primer elemento.
     * @param b el segundo elemento.
     * @return el peso de la arista que comparten los vértices que contienen a
     *         los elementos recibidos.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     * @throws IllegalArgumentException si a o b no están conectados.
     */
    public double getPeso(T a, T b) {
        Vertice v1 = (Vertice) vertice(a);
        Vertice v2 = (Vertice) vertice(b);
        for (Vecino vecino : v1.vecinos)
            if (vecino.vecino.elemento.equals(v2.elemento))
                return vecino.peso;
        throw new IllegalArgumentException("Los vertices no estan conectados");
    }

    /**
     * Define el peso de la arista que comparten los vértices que contienen a
     * los elementos recibidos.
     * @param a el primer elemento.
     * @param b el segundo elemento.
     * @param peso el nuevo peso de la arista que comparten los vértices que
     *        contienen a los elementos recibidos.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     * @throws IllegalArgumentException si a o b no están conectados, o si peso
     *         es menor o igual que cero.
     */
    public void setPeso(T a, T b, double peso) {
        Vertice v1 = (Vertice) vertice(a);
        Vertice v2 = (Vertice) vertice(b);
        if (peso <= 0)
            throw new IllegalArgumentException("El peso debe ser mayor a 0");
        if (!sonVecinos(a, b))
            throw new IllegalArgumentException("Los elementos no estan conectados");
        for (Vecino vecino : v1.vecinos)
            if (vecino.vecino.elemento.equals(v2.elemento)){
                vecino.peso = peso;
                break;
            }
            for (Vecino vecino : v2.vecinos)
                if (vecino.vecino.elemento.equals(v1.elemento)){
                    vecino.peso = peso;
                    break;
            }
    }

    /**
     * Regresa el vértice correspondiente el elemento recibido.
     * @param elemento el elemento del que queremos el vértice.
     * @throws NoSuchElementException si elemento no es elemento de la gráfica.
     * @return el vértice correspondiente el elemento recibido.
     */
    public VerticeGrafica<T> vertice(T elemento) {
        for (Vertice vertice : vertices)
            if(vertice.elemento.equals(elemento))
                return vertice;
        throw new NoSuchElementException("El elemento no esta en la grafica");
    }

    /**
     * Define el color del vértice recibido.
     * @param vertice el vértice al que queremos definirle el color.
     * @param color el nuevo color del vértice.
     * @throws IllegalArgumentException si el vértice no es válido.
     */
    public void setColor(VerticeGrafica<T> vertice, Color color) {
        if (vertice == null ||
        (vertice.getClass() != Vertice.class &&
         vertice.getClass() != Vecino.class))
            throw new IllegalArgumentException("El vértice no es válido.");

        if (vertice.getClass() == Vertice.class) {
        Vertice verticeAux = (Vertice) vertice;
        verticeAux.color = color;
        }
        if (vertice.getClass() == Vecino.class) {
            Vecino verticeAux = (Vecino) vertice;
            verticeAux.vecino.color = color;
        }
    }

    /**
     * Nos dice si la gráfica es conexa.
     * @return <code>true</code> si la gráfica es conexa, <code>false</code> en
     *         otro caso.
     */
    public boolean esConexa() {
        for (Vertice vertice : vertices){
            recorrido(vertice.elemento, e -> {}, new Cola<Vertice>());
            break;
        }
        for (Vertice vertice : vertices)
            if(vertice.color == Color.ROJO)
                return false;
        return true;
    }

    /**
     * Realiza la acción recibida en cada uno de los vértices de la gráfica, en
     * el orden en que fueron agregados.
     * @param accion la acción a realizar.
     */
    public void paraCadaVertice(AccionVerticeGrafica<T> accion) {
        for (Vertice vertice : vertices)
            accion.actua(vertice);
    }

    /**
     * Realiza la acción recibida en todos los vértices de la gráfica, en el
     * orden determinado por BFS, comenzando por el vértice correspondiente al
     * elemento recibido. Al terminar el método, todos los vértices tendrán
     * color {@link Color#NINGUNO}.
     * @param elemento el elemento sobre cuyo vértice queremos comenzar el
     *        recorrido.
     * @param accion la acción a realizar.
     * @throws NoSuchElementException si el elemento no está en la gráfica.
     */
    public void bfs(T elemento, AccionVerticeGrafica<T> accion) {
        Cola<Vertice> cola = new Cola<>();
        recorrido(elemento, accion, cola);
        paraCadaVertice((v) -> setColor(v, Color.NINGUNO));
    }

    /**
     * Realiza la acción recibida en todos los vértices de la gráfica, en el
     * orden determinado por DFS, comenzando por el vértice correspondiente al
     * elemento recibido. Al terminar el método, todos los vértices tendrán
     * color {@link Color#NINGUNO}.
     * @param elemento el elemento sobre cuyo vértice queremos comenzar el
     *        recorrido.
     * @param accion la acción a realizar.
     * @throws NoSuchElementException si el elemento no está en la gráfica.
     */
    public void dfs(T elemento, AccionVerticeGrafica<T> accion) {
        Pila<Vertice> pila = new Pila<>();
        recorrido(elemento, accion, pila);
        paraCadaVertice((v) -> setColor(v, Color.NINGUNO));
    }

    private void recorrido(T elemento, AccionVerticeGrafica<T> accion, MeteSaca<Vertice> metesaca){
        Vertice vertice = (Vertice) vertice(elemento);
        paraCadaVertice((v) -> setColor(v, Color.ROJO));
        vertice.color = Color.NEGRO;
        metesaca.mete(vertice);
        while(!metesaca.esVacia()){
            vertice = metesaca.saca();
            accion.actua(vertice);
            for (Vecino vecino : vertice.vecinos)
                if (vecino.vecino.color == Color.ROJO){
                    vecino.vecino.color = Color.NEGRO;
                    metesaca.mete(vecino.vecino);
                }
        }
    }

    /**
     * Nos dice si la gráfica es vacía.
     * @return <code>true</code> si la gráfica es vacía, <code>false</code> en
     *         otro caso.
     */
    @Override public boolean esVacia() {
        return vertices.esVacia();
    }

    /**
     * Limpia la gráfica de vértices y aristas, dejándola vacía.
     */
    @Override public void limpia() {
        vertices.limpia();
        aristas = 0;
    }

    /**
     * Regresa una representación en cadena de la gráfica.
     * @return una representación en cadena de la gráfica.
     */
    @Override public String toString() {
        String s = "{";
        for (Vertice vertice : vertices) {
            s += String.format("%s, ", vertice.get());
        }
        s += "}, {";
        Lista<T> verticesRepetidos = new Lista<>();
        for (Vertice vertice : vertices){
            for (Vecino vecino : vertice.vecinos)
                if (!verticesRepetidos.contiene(vecino.vecino.elemento))
                    s += String.format("(%s, %s), ",vertice.elemento.toString() ,vecino.vecino.elemento.toString());
            verticesRepetidos.agrega(vertice.elemento);
        }
        s += "}";
        return s;
    }

    /**
     * Nos dice si la gráfica es igual al objeto recibido.
     * @param objeto el objeto con el que hay que comparar.
     * @return <code>true</code> si la gráfica es igual al objeto recibido;
     *         <code>false</code> en otro caso.
     */
    @Override public boolean equals(Object objeto) {
        if (objeto == null || getClass() != objeto.getClass())
            return false;
        @SuppressWarnings("unchecked") Grafica<T> grafica = (Grafica<T>)objeto;
        if (getElementos() != grafica.getElementos() || aristas != grafica.aristas)
            return false;
        for (Vertice vertice : vertices) {
            if(!grafica.contiene(vertice.elemento))
                return false;
            Vertice verticeG = (Vertice) grafica.vertice(vertice.elemento);
            if(vertice.vecinos.getElementos() != verticeG.vecinos.getElementos())
                return false;
            for (Vecino vecino : vertice.vecinos){
                boolean esVecino = false;
                for (Vecino vecinoG : verticeG.vecinos)
                    if (vecino.vecino.elemento.equals(vecinoG.vecino.elemento)){
                        esVecino = true;
                        break;
                    }
                if (!esVecino)
                    return false;
            }
        }
        return true;
    }

    /**
     * Regresa un iterador para iterar la gráfica. La gráfica se itera en el
     * orden en que fueron agregados sus elementos.
     * @return un iterador para iterar la gráfica.
     */
    @Override public Iterator<T> iterator() {
        return new Iterador();
    }

    /**
     * Calcula una trayectoria de distancia mínima entre dos vértices.
     * @param origen el vértice de origen.
     * @param destino el vértice de destino.
     * @return Una lista con vértices de la gr            grafica.paraCadaVertice(v -> Assert.assertTrue(v.getGrado() ==
áfica, tal que forman una
     *         trayectoria de distancia mínima entre los vértices <code>a</code> y
     *         <code>b</code>. Si los elementos se encuentran en componentes conexos
     *         distintos, el algoritmo regresa una lista vacía.
     * @throws NoSuchElementException si alguno de los dos elementos no está en
     *         la gráfica.
     */
    public Lista<VerticeGrafica<T>> trayectoriaMinima(T origen, T destino) {
        if (!contiene(origen) || !contiene(destino))
            throw new NoSuchElementException("Los elementos no esatn en la grafica");
        Vertice v = (Vertice) vertice(origen);
        if (origen.equals(destino)){
            Lista<VerticeGrafica<T>> lista = new Lista<>();
            lista.agrega(v); 
            return lista;
        }
        for (Vertice vertice : vertices) 
            vertice.distancia = Double.MAX_VALUE;
        v.distancia = 0;
        Cola<Vertice> cola = new Cola<>();
        cola.mete(v);
        while(!cola.esVacia()){
            v = cola.saca();
            for (Vecino vecino : v.vecinos)
                if (vecino.vecino.distancia == Double.MAX_VALUE){
                    vecino.vecino.distancia = v.distancia + 1;
                    cola.mete(vecino.vecino);
                }
        }
        return reconstruirTrayectoria((aux, vecino) -> vecino.vecino.distancia == aux.distancia - 1,
        (Vertice) vertice(destino));
    }

    /**
     * Calcula la ruta de peso mínimo entre el elemento de origen y el elemento
     * de destino.
     * @param origen el vértice origen.
     * @param destino el vértice destino.
     * @return una trayectoria de peso mínimo entre el vértice <code>origen</code> y
     *         el vértice <code>destino</code>. Si los vértices están en componentes
     *         conexas distintas, regresa una lista vacía.
     * @throws NoSuchElementException si alguno de los dos elementos no está en
     *         la gráfica.
     */
    public Lista<VerticeGrafica<T>> dijkstra(T origen, T destino) {
        if (!contiene(origen) || !contiene(destino))
            throw new NoSuchElementException("Los elementos no estan en la grafica");
        for (Vertice vertice : vertices) 
            vertice.distancia = Double.MAX_VALUE;
        Vertice v = (Vertice) vertice(origen);
        v.distancia = 0;
        MonticuloDijkstra<Vertice> monticulo;
        int n = getElementos();
        if (aristas > ((n*(n - 1))/2)-n)
            monticulo = new MonticuloArreglo<>(vertices, vertices.getElementos());
        else
            monticulo = new MonticuloMinimo<>(vertices, vertices.getElementos());
        while (!monticulo.esVacia()){
            Vertice raiz = monticulo.elimina();
            for (Vecino vecino : raiz.vecinos)
                if (vecino.vecino.distancia > raiz.distancia + vecino.peso){
                    vecino.vecino.distancia = raiz.distancia + vecino.peso;
                    monticulo.reordena(vecino.vecino);
                }
        }
        return reconstruirTrayectoria((vertice, vecino) -> vecino.vecino.distancia + vecino.peso == vertice.distancia, (Vertice) vertice(destino));
    }

    private Lista<VerticeGrafica<T>> reconstruirTrayectoria(BuscadorCamino<T> buscador, Vertice destino){
        Vertice vertice = destino;
        Lista<VerticeGrafica<T>> lista = new Lista<>();
        if (destino.distancia == Double.MAX_VALUE)
            return new Lista<VerticeGrafica<T>>();
        lista.agrega(vertice);
        while (vertice.distancia != 0){
            for (Vecino vecino : vertice.vecinos){
                if (buscador.seSiguen(vertice, vecino)){
                    lista.agrega(vecino.vecino);
                    vertice = vecino.vecino;
                    break;
                }
            }
        }
        return lista.reversa();
    }
}
