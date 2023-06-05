package mx.unam.ciencias.edd.proyecto3;
import java.util.Random;
import mx.unam.ciencias.edd.Pila;
import mx.unam.ciencias.edd.Color;
import mx.unam.ciencias.edd.Lista;

/** 
 * Clase para generar un laberinto
 */
public class Laberinto {
    /** 
     * Clase interna para generar las casillas.
     * Las propiedades de las casillas se haran en un
     * numero de 8 bits para despues pasarlos a un byte que 
     * represente las caracteristicas de la casilla
     */
    protected class Casilla{
        /* Valor -128 - -16: El puntaje de la casilla (1-15)*/
        private byte puntaje;
        /*  Valor 1-15: Las puertas de la casilla */
        private byte puerta;
        /* Las coordenadas de la casilla */
        private int x, y;
        /* Si la casilla es la entrada */
        private boolean entrada;
        /* Si la casilla es la salida */
        private boolean salida;
        /* Tipo de casilla */
        private TipoCasilla tipo;
        /* La casilla anterior */
        private Casilla anterior;
        /* El color de la casilla */
        private Color color;
        
        /* Constructor de la clase */
        public Casilla(int x, int y){
            this.x = x;
            this.y = y;
            setPuntaje();
            puerta = 15;
            color = Color.NEGRO;
        }

        /* Establece el puntaje de la casilla */
        private void setPuntaje(){
            int r = (random.nextInt(15) + 1);
            puntaje = (byte) (r << 4);
        }

        /* Constructor de la clase con un byte */
        public Casilla(byte casilla, int x, int y){
            puntaje = (byte)(casilla & -16);
            puerta = (byte)(casilla & 0x0F);
            this.x = x;
            this.y = y;
            color = Color.ROJO;
        }

        /**
         * Regresa el puntaje de la casilla.
         * El puntaje de la casilla es de 1 al 15.
         * @return el puntaje de la casilla
         */
        public int getPuntaje(){
            return puntaje >>> 4;
        }

        /**
         * Regresa en byte las puertas de la casilla
         * @return las puertas de la casilla
         */
        public byte getPuerta(){
            return puerta;
        }

        /**
         * Cierra una puerta de la casilla.
         * Este metodo solo se usa en la clase GraficaLaberinto
         * @param barrera el byte de la puerta
         */
        public void cierraPuerta(byte barrera){
            puerta |= barrera;
        }

        /**
         * Reabre la puerta que fue cerrada con el metodo <code>cierraPuerta();</code>
         * @param puerta la puerta de la casilla antes de ser cerrada.
         */
        public void recuperaPuerta(byte puerta){
            this.puerta &= puerta;
        }

        /**
         * Regresa las coordenadas en un arreglo
         * @return las coordenadas de la casilla
         */
        public int[] getCoordenadas(){
            int[] coordenadas = {x,y};
            return coordenadas;
        }

        /**
         * Regresa el color de la casilla.
         * @return el color de la casilla.
         */
        public Color getColor(){
            return color;
        }

        /**
         * Establece el color de la casilla
         * @param color el nuevo color de la casilla
         */
        public void setColor(Color color){
            this.color = color;
        }

        /**
         * Nos dice si la casilla es la entrada
         * @return <code>true</code> si la casilla es la entrada,
         *      <code>false</code> en otro caso.
         */
        public boolean esEntrada(){
            return entrada;
        }

        /**
         * Nos dice si la casilla es la salida
         * @return <code>true</code> si la casilla es la salida,
         *      <code>false</code> en otro caso.
         */
        public boolean esSalida(){
            return salida;
        }

        /**
         * Regresa la casilla anterior de la casilla actual
         * @return la casilla anterior
         */
        public Casilla getAnterior(){
            return anterior;
        }

        /**
         * Establece el anterior de la casilla
         * @param anterior el nuevo anterior de la casilla
         */
        public void setAnterior(Casilla anterior){
            this.anterior = anterior;
        }

        /** 
         * Nos regresa en bytes la casilla
         * @return la casiilla en bytes.
         */
        public byte construirByte(){
            return (byte)(puntaje | puerta);
        }

        /** 
         * Nos dice si la casilla es igual al objeto recibido
         * @param objeto el objeto a comparar.
         * @return <code>true</code> si la casilla es igual al objeto recibido;
         *      <code>false</code> en otro caso.
         */
        @Override public boolean equals(Object objeto) {
            if (objeto == null || getClass() != objeto.getClass())
                return false;
            Casilla casilla = (Casilla)objeto;
            if (x != casilla.x || y != casilla.y || puerta != casilla.puerta || puntaje != casilla.puntaje)
                return false;
            return true;
        }

        /** 
         * Establece el tipo de casilla de acuerdo a sus coordenadas.
         * @param x la coordenada en x de la casilla.
         * @param y la coordenada en y de la casilla
         */
        public void setTipoCasilla(int x, int y){
            if (y == 0){
                if (x == 0){
                    tipo = TipoCasilla.ESQUINA_IZQ_N;
                    return;
                }
                else if (x != 0 && x != laberinto[y].length - 1){
                    tipo = TipoCasilla.LATERAL_NORTE;
                    return;
                }
                else {
                    tipo = TipoCasilla.ESQUINA_DER_N;
                    return;
                }
            } else if (y == laberinto.length - 1){
                if (x == 0){
                    tipo = TipoCasilla.ESQUINA_IZQ_SUR;
                    return;
                }
                else if (x != 0 && x != laberinto[y].length - 1){
                    tipo = TipoCasilla.LATERAL_SUR;
                    return;
                }
                else {
                    tipo = TipoCasilla.ESQUINA_DER_SUR;
                    return;
                }
            } else if (x == 0){
                tipo = TipoCasilla.LATERAL_OESTE;
                return;
            }
            else if (x == laberinto[x].length - 1){
                tipo = TipoCasilla.LATERAL_ESTE;
                return;
            }
            tipo = TipoCasilla.CENTRO;
        }

        @Override public String toString(){
            return String.format("(%s,%s)", x, y);
        }
    }

    /* El laberinto */
    Casilla[][] laberinto;
    /* La casilla de entrada */
    Casilla entrada;
    /* La casilla de salida */
    Casilla salida;
    /* Nuestro random */
    Random random;

    /**
     * Construye un laberinto con un ancho y altura determinados
     * @param w el ancho del laberinto
     * @param h la altura del laberinto
     * @param semilla la semilla del laberinto
     */
    public Laberinto(int w, int h, long semilla){
        laberinto = new Casilla[h][w];
        random = new Random(semilla);
        for (int i = 0; i < h; i++){
            for (int j = 0; j < w; j++){
                laberinto[i][j] = new Casilla(j, i);
                laberinto[i][j].setTipoCasilla(j, i);
            }
        }
        entrada = entradaAleatoria();
        salida = salidaAleatoria();
        verificaEntradaYSalida();
    }

    
    /**
     * Construye un laberinto a partir de un arreglo de bytes
     * @param laberinto el arreglo de bytes de un archivo
     */
    public Laberinto(byte[] laberinto){
        this.laberinto = new Casilla[laberinto[4]&0xFF][laberinto[5]&0xFF];
        int b = 6;
        for (int i = 0; i < this.laberinto.length ; i++)
            for (int j = 0; j < this.laberinto[i].length; j++){
                this.laberinto[i][j] = new Casilla(laberinto[b], j, i);
                b++;
            }
        buscaEntradaYSalida();
    }

    /**
     * Regresa la matriz de casillas
     * @return la matriz de casillas.
     */
    public Casilla[][] getLaberinto(){
        return laberinto;
    }

    public int[] getDimensiones(){
        int [] dimensiones = {laberinto.length, laberinto[0].length};
        return dimensiones;
    }
    /**
     * Obtiene la casilla de coordenadas x,y.
     * @param x la coordenada x de la casilla
     * @param y la coordenada y de la casilla
     * @return la casilla de coordenada x,y.
     */
    public Casilla getCasilla(int x, int y){
        if (y < 0 || y >= laberinto.length || x < 0 || x >= laberinto[0].length)
            return null;
        return laberinto[y][x];
    }

    /**
     * Regresa la entrada del laberinto
     * @return  la entrada del laberinto.
     */
    public Casilla getEntrada(){
        return entrada;
    }

    /**
     * Regresa la salida del laberinto
     * @return la salida del laberinto.
     */
    public Casilla getSalida(){
        return salida;
    }

    /** 
     * Recorre la matriz de casillas empezando desde la entrada
     * generando puertas aleatorias formando el laberinto.
     */
    public void construyeLaberinto(){
        Pila<Casilla> pila = new Pila<>();
        pila.mete(entrada);
        while (!pila.esVacia()){
            Casilla actual = pila.mira();
            actual.color = Color.ROJO;
            Lista<Casilla> puerta = vecinosCasilla(actual);
            if (!puerta.esVacia()){
                if(puerta.getLongitud() == 1){
                    puerta.getPrimero().anterior = actual;
                    abrirPuerta(puerta.getPrimero());
                    pila.mete(puerta.getPrimero());
                    continue;
                } else {
                    int r = random.nextInt(puerta.getLongitud());
                    puerta.get(r).anterior = actual;
                    abrirPuerta(puerta.get(r));
                    pila.mete(puerta.get(r));
                }
            } else
                pila.saca();
        }
        puertaEntradaSalida(entrada);
        puertaEntradaSalida(salida);
    }

    /**
     * Obtiene los bytes de las casillas del laberinto y los guarda en un arreglo de bytes
     * que representa al laberinto.
     * @param renglones los renglones del laberinto
     * @param columnas las columnas del laberinto
     * @return un arreglo de bytes que representa al laberinto
     */
    public byte[] arregloCasillas(byte renglones, byte columnas){
        byte[] casillas = new byte[(laberinto.length * laberinto[0].length) + 6];
        casillas[0] = (byte)0x4d; 
        casillas[1] = (byte)0x41; 
        casillas[2] = (byte)0x5a; 
        casillas[3] = (byte)0x45;
        casillas[4] = columnas; 
        casillas[5] = renglones;
        int indice = 6;
        for (int i = 0; i < laberinto.length; i++){
            for (int j = 0; j < laberinto[0].length; j++){
                casillas[indice] = laberinto[i][j].construirByte();
                indice ++;
            }
        }
        return casillas;
    }


    /**
     * Abre la puerta de la casilla dada.
     * La casilla siempre sera la entrada o la salida.
     * @param casilla la casilla a abrir la puerta.
     */
    public void puertaEntradaSalida(Casilla casilla){
        if (casilla.tipo == TipoCasilla.ESQUINA_IZQ_N){
            casilla.puerta &= 9;
            bordeEntradaSalida(entrada, 2, 4);
        } else if (casilla.tipo == TipoCasilla.LATERAL_NORTE)
            casilla.puerta &= 13;
        else if (casilla.tipo == TipoCasilla.ESQUINA_DER_N){
            casilla.puerta &= 12;
            bordeEntradaSalida(casilla, 2, 1);
        } else if (casilla.tipo == TipoCasilla.LATERAL_ESTE)
            casilla.puerta &= 14;
        else if (casilla.tipo == TipoCasilla.ESQUINA_DER_SUR){
            casilla.puerta &= 6;
            bordeEntradaSalida(casilla, 1, 8);
        } else if (casilla.tipo == TipoCasilla.LATERAL_SUR)
            casilla.puerta &= 7;
        else if (casilla.tipo == TipoCasilla.ESQUINA_IZQ_SUR){
            casilla.puerta &= 3;
            bordeEntradaSalida(casilla, 8, 4);
        } else
            casilla.puerta &= 11;
    }

    /**
     * Abre la puerta de una casilla con su antesecor;
     * @param casilla la casilla a abrir la puerta
     */
    private void abrirPuerta(Casilla casilla){
        if (casilla.x == casilla.anterior.x)
            if(casilla.y - 1 == casilla.anterior.y){
                casilla.puerta &= 13;
                casilla.anterior.puerta &= 7;
                bordes(casilla);
                return;
            } else {
                casilla.puerta &= 7;
                casilla.anterior.puerta &= 13;
                bordes(casilla);
                return;
            }
        if (casilla.x - 1 == casilla.anterior.x){
            casilla.puerta &= 11;
            casilla.anterior.puerta &= 14;
            bordes(casilla);
            return;
        } else {
            casilla.puerta &= 14;
            casilla.anterior.puerta &= 11;
            bordes(casilla);
            return;
        }
    }

    /**
     * Mantiene los bordes de la casilla si es uno de los extremos
     * si se llega a abrir.
     * @param casilla la casilla a reponer el borde.
     */
    private void bordes(Casilla casilla){
        switch(casilla.tipo){
            case ESQUINA_IZQ_N:
                casilla.puerta |= 6;
                break;
            case LATERAL_NORTE: 
                casilla.puerta |= 2;
                break;
            case ESQUINA_DER_N:
                casilla.puerta |= 3;
                break;
            case LATERAL_ESTE:
                casilla.puerta |= 1;
                break;
            case ESQUINA_DER_SUR:
                casilla.puerta |= 9;
                break;
            case LATERAL_SUR:
                casilla.puerta |= 8;
                break;
            case ESQUINA_IZQ_SUR:
                casilla.puerta |= 12;
                break;
            case LATERAL_OESTE:
                casilla.puerta |= 4;
            default:
                break;
        }
    }

    /** 
     * Metodo auxiliar que determina que lado cerrar de una esquina si 
     * la entrada o salida esta en una esquina del laberinto
     */
    private void bordeEntradaSalida(Casilla esquina, int borde1, int borde2){
        int r = random.nextInt(2);
        if (r == 1)
            esquina.puerta |= borde1;
        else
            esquina.puerta |= borde2;
    }

    /**
     * Metodo que da todos los vecinos sin recorrer de la casilla dada para
     * poder construir el laberinto.
     * @param casilla la casilla a obtener sus vecinos disponibles
     * @return una lista de los vecinos disponibles de la casilla
     */
    private Lista<Casilla> vecinosCasilla(Casilla casilla) {
        Lista<Casilla> vecinos = new Lista<>();
        Casilla vecinoSur, vecinoEste, vecinoOeste, vecinoNorte;
        switch (casilla.tipo){
            case ESQUINA_IZQ_N:
                vecinoSur = laberinto[casilla.y+1][casilla.x];
                vecinoEste = laberinto[casilla.y][casilla.x+1];
                vecinos.agrega(vecinoSur);
                vecinos.agrega(vecinoEste);
                break;
            case LATERAL_NORTE:
                vecinoOeste = laberinto[casilla.y][casilla.x-1];
                vecinoSur = laberinto[casilla.y+1][casilla.x];
                vecinoEste = laberinto[casilla.y][casilla.x+1];
                vecinos.agrega(vecinoOeste);
                vecinos.agrega(vecinoEste);
                vecinos.agrega(vecinoSur);
                break;
            case ESQUINA_DER_N:
                vecinoSur = laberinto[casilla.y+1][casilla.x];
                vecinoOeste = laberinto[casilla.y][casilla.x-1];
                vecinos.agrega(vecinoSur);
                vecinos.agrega(vecinoOeste);
                break;
            case LATERAL_ESTE:
                vecinoNorte = laberinto[casilla.y-1][casilla.x];
                vecinoSur = laberinto[casilla.y+1][casilla.x];
                vecinoOeste = laberinto[casilla.y][casilla.x-1];
                vecinos.agrega(vecinoOeste);
                vecinos.agrega(vecinoNorte);
                vecinos.agrega(vecinoSur);
                break;
            case ESQUINA_DER_SUR:
                vecinoNorte = laberinto[casilla.y-1][casilla.x];
                vecinoOeste = laberinto[casilla.y][casilla.x-1];
                vecinos.agrega(vecinoNorte);
                vecinos.agrega(vecinoOeste);
                break;
            case LATERAL_SUR:
                vecinoOeste = laberinto[casilla.y][casilla.x-1];
                vecinoNorte = laberinto[casilla.y-1][casilla.x];
                vecinoEste = laberinto[casilla.y][casilla.x+1];
                vecinos.agrega(vecinoOeste);
                vecinos.agrega(vecinoEste);
                vecinos.agrega(vecinoNorte);
                break;
            case ESQUINA_IZQ_SUR:
                vecinoNorte = laberinto[casilla.y-1][casilla.x];
                vecinoEste = laberinto[casilla.y][casilla.x+1];
                vecinos.agrega(vecinoNorte);
                vecinos.agrega(vecinoEste);
                break;
            case LATERAL_OESTE:
                vecinoNorte = laberinto[casilla.y-1][casilla.x];
                vecinoSur = laberinto[casilla.y+1][casilla.x];
                vecinoEste = laberinto[casilla.y][casilla.x+1];
                vecinos.agrega(vecinoEste);
                vecinos.agrega(vecinoNorte);
                vecinos.agrega(vecinoSur);
                break;
            case CENTRO:
                vecinoNorte = laberinto[casilla.y-1][casilla.x];
                vecinoSur = laberinto[casilla.y+1][casilla.x];
                vecinoEste = laberinto[casilla.y][casilla.x+1];
                vecinoOeste = laberinto[casilla.y][casilla.x-1];
                vecinos.agrega(vecinoEste);
                vecinos.agrega(vecinoOeste);
                vecinos.agrega(vecinoNorte);
                vecinos.agrega(vecinoSur);
        }
        for (Casilla vecino : vecinos) {
            if (vecino.color == Color.ROJO)
                vecinos.elimina(vecino);
        }
        return vecinos;
    }

    /**
     * Metodo que selecciona una entrada en uno de los bordes del laberinto
     * @return la casilla que sera la entrada
     */
    private Casilla entradaAleatoria(){
        int r = random.nextInt(4);
        Casilla c = null;
        switch (r){
            case 0:
                c = laberinto[random.nextInt(laberinto.length)][0];
                c.entrada = true;
                c.puerta &= 11;
                break;
            case 1:
                c = laberinto[0][random.nextInt(laberinto[0].length)];
                c.entrada = true;
                c.puerta &= 13;
                break;
            case 2:
                c = laberinto[random.nextInt(laberinto.length)][laberinto[0].length-1];
                c.entrada = true;
                c.puerta &= 14;
                break;
            case 3:
                c = laberinto[laberinto.length-1][random.nextInt(laberinto[0].length)];
                c.entrada = true;
                c.puerta &= 7;
                break;
        }
        return c;
    }
    
    /**
     * Metodo que selecciona una salida en uno de los bordes del laberinto
     * @return la casilla que sera la salida
     */
    private Casilla salidaAleatoria(){
        int r = random.nextInt(4);
        Casilla c = null;
        switch (r){
            case 2:
                c = laberinto[random.nextInt(laberinto.length)][0];
                c.salida = true;
                c.puerta = 11;
                break;
            case 3:
                c = laberinto[0][random.nextInt(laberinto[0].length)];
                c.salida = true;
                c.puerta = 13;
                break;
            case 0:
                c = laberinto[random.nextInt(laberinto.length)][laberinto[0].length-1];
                c.salida = true;
                c.puerta = 14;
                break;
            case 1:
                c = laberinto[laberinto.length-1][random.nextInt(laberinto[0].length)];
                c.salida = true;
                c.puerta = 7;
                break;
        }
        return c;
    }

    /** 
     * Metodo que cambia la salida si la entrada y la salida son la misma casilla
     */
    private void verificaEntradaYSalida(){
        while(entrada.salida){
            entrada.salida = false;
            salidaAleatoria();
        }
    }

/*       @Override public String toString(){
        String s = "";
        for (int i = 0; i < laberinto.length; i++){
            for ( int j = 0; j < laberinto[i].length; j++)
                s += String.format("| %s |", laberinto[i][j].tipo);
            s += "\n";
        }
        for (int i = 0; i < laberinto.length; i++){
            for ( int j = 0; j < laberinto[i].length; j++)
                s += String.format("|Entrada:%s, Salida:%s, Byte:%s|", laberinto[i][j].entrada,laberinto[i][j].salida,laberinto[i][j].puerta);
            s += "\n";
        }
        return s;
    }  */
    
    /**
     * Metodo que busca a partir del valor de las puertas la entrada y la salida
     * del laberinto
     */
    private void buscaEntradaYSalida(){
        Lista<Casilla> huecos = new Lista<>();
        for (int i = 0; i < laberinto.length; i++){
            if ((laberinto[i][0].puerta & 11) == laberinto[i][0].puerta){
                huecos.agrega(laberinto[i][0]);
            }
        }

        for (int i = 0; i < laberinto[0].length; i++)
            if ((laberinto[0][i].puerta & 13) == laberinto[0][i].puerta){
                huecos.agrega(laberinto[0][i]);
            }

        for (int j = 0; j < laberinto.length; j++) 
            if ((laberinto[j][laberinto[0].length-1].puerta & 14) == laberinto[j][laberinto[0].length-1].puerta){
                huecos.agrega(laberinto[j][laberinto[0].length-1]);
            }

        for (int i = 0; i < laberinto[0].length; i++)
            if ((laberinto[laberinto.length-1][i].puerta & 7) == laberinto[laberinto.length-1][i].puerta){
                huecos.agrega(laberinto[laberinto.length-1][i]);
            }
        if (huecos.getElementos() != 2){
            System.err.println("Archivo invalido: El laberinto debe tener una entrada y una salida");
            System.exit(1);
        }
        entrada = huecos.get(0);
        entrada.entrada = true;
        salida = huecos.get(1);
        salida.salida = true;
    }
} 