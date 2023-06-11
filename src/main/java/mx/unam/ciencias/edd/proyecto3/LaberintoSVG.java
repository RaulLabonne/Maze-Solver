package mx.unam.ciencias.edd.proyecto3;

import mx.unam.ciencias.edd.Lista;
import mx.unam.ciencias.edd.proyecto3.Laberinto.Casilla;

/** 
 * Clase que genera un svg que represente al laberinto
 */
public class LaberintoSVG {
    
    /* La lista de casillas del laberinto */
    Lista<Casilla> laberinto;
    /* La lista de casillas que forman la solucion */
    Lista<Casilla> solucion;
    /* Las coordenadas de la entradab y de la salida */
    int[] entrada, salida;
    /* La altura */
    int h;
    /* El ancho */
    int w;

    /* Constructor de la calse */
    public LaberintoSVG(Lista<Casilla> laberinto, Lista<Casilla> solucion, int[] dimensiones, int[] entrada, int[] salida){
        this.laberinto = laberinto;
        this.solucion = solucion;
        h = 40 + (dimensiones[0]*40);
        w = 40 + (dimensiones[1]*40);
        this.entrada = entrada;
        this.salida = salida;
    }

    /**
     * Regresa, en una cadena, el codigo svg del laberinto.
     * @return el codigo svg que grafica el laberinto.
     */
    public String svg(){
        String svg = "";
        svg += inicio();
        svg += empezarGraficar(w, h);
        for (Casilla casilla : laberinto)
            svg += casilla(casilla);
        for (Casilla casilla2 : solucion) 
            svg += solucion(casilla2);
        int coorXentrada = 40 + (40*entrada[0]);
        int coorYentrada = 40 + (40*entrada[1]);
        int coorXsalida = 40 + (40*salida[0]);
        int coorYsalida = 40 + (40*salida[1]);
        svg += punto(coorXentrada, coorYentrada, "mediumspringgreen");
        svg += punto(coorXsalida, coorYsalida, "maroon");
        svg += cierre();
        return svg;
    }

    /**
     * Regresa el codigo svg de una casilla del laberinto
     * @param casilla la casilla a graficar
     * @return  el codigo svg de una casilla del laberinto
     */
    private String casilla(Casilla casilla){
        String puertas = "";
        int constante = 40;
        int coorX = 20 + (constante*casilla.getCoordenadas()[0]);
        int coorY = 20 + (constante*casilla.getCoordenadas()[1]);
        if (casilla.getTipoCasilla() == TipoCasilla.ESQUINA_DER_N || casilla.getTipoCasilla() == TipoCasilla.LATERAL_ESTE || casilla.getTipoCasilla() == TipoCasilla.ESQUINA_DER_SUR)
            if ((casilla.getPuerta() | 1) == casilla.getPuerta())
                puertas += linea(coorX+constante, coorY, coorX+constante, coorY+constante);
        if ((casilla.getPuerta() | 2) == casilla.getPuerta())
            puertas += linea(coorX, coorY, coorX+constante, coorY);
        if ((casilla.getPuerta() | 4) == casilla.getPuerta())
            puertas += linea(coorX, coorY, coorX, coorY+constante);
        if (casilla.getTipoCasilla() == TipoCasilla.ESQUINA_DER_SUR || casilla.getTipoCasilla() == TipoCasilla.LATERAL_SUR || casilla.getTipoCasilla() == TipoCasilla.ESQUINA_IZQ_SUR)
            if ((casilla.getPuerta() | 8) == casilla.getPuerta())
                puertas += linea(coorX, coorY+constante, coorX+constante, coorY+constante);
        return puertas;
    }

    /**
     * Regresa el codigo svg de la solucion al laberinto
     * @param casilla la casilla que froma parte de la soolucion
     * @return la solucion del laberinto en svg
     */
    private String solucion(Casilla casilla){
        String solucion = "";
        int constante = 40;
        int coorX = 40 + (constante*casilla.getCoordenadas()[0]);
        int coorY = 40 + (constante*casilla.getCoordenadas()[1]);
        if (casilla.getAnterior() == null)
            return solucion;
        int coorXB = 40 + (constante*casilla.getAnterior().getCoordenadas()[0]);
        int coorYB = 40 + (constante*casilla.getAnterior().getCoordenadas()[1]);
        solucion += lineaSolucion(coorX, coorY, coorXB, coorYB);
        return solucion;
    }

    /**
     * Regresa, en forma de codigo, una linea que representara un muro de la casilla.
     * @param inicioX la coordenada en el eje X de donde empieza.
     * @param inicioY la coordenada en el eje Y de donde empieza.
     * @param finalX la coordenada en el eje X de donde termina.
     * @param finalY la coordenada en el eje Y de donde termina.
     * @return una linea que representa un muro.
     */
    private String linea(int inicioX, int inicioY, int finalX, int finalY){
        return String.format("\t<line x1='%s' y1='%s' x2='%s' y2='%s' stroke='black' stroke-width='5' />\n"
                            , inicioX, inicioY, finalX, finalY);
    }

    /**
     * Regresa, en forma de codigo, una linea que sera parte de la solucion al laberinto.
     * @param inicioX la coordenada en el eje X de donde empieza.
     * @param inicioY la coordenada en el eje Y de donde empieza.
     * @param finalX la coordenada en el eje X de donde termina.
     * @param finalY la coordenada en el eje Y de donde termina.
     * @return una linea que representa la solucion al laberinto.
     */
    private String lineaSolucion(int inicioX, int inicioY, int finalX, int finalY){
        return String.format("\t<line x1='%s' y1='%s' x2='%s' y2='%s' stroke='deeppink' stroke-width='5' />\n"
                            , inicioX, inicioY, finalX, finalY);
    }
    /**
     * Regresa, en forma de codigo , la representación grafica de un vertice.
     * @param coordenadaX coordenada en el eje X del centro del vertice.
     * @param coordenadaY coordenada en el eje Y del centro del vertice.
     * @param v el vertice a graficar.
     * @param color el color del vertice.
     * @param letra el color de la letra.
     * @return la representación grafica de un vertice.
     */
    private String punto(int coordenadaX, int coordenadaY, String color){
        String vertice = String.format("\t<circle cx='%s' cy='%s' r='10' stroke='%s' stroke-width='1' fill='%s' /> \n", 
                                        coordenadaX, coordenadaY, color, color);
        return vertice;
}

        /** 
     * Regresa el codigo para iniciar la graficación.
     * @return el codigo para inicial la graficación.
     */
    private static String inicio(){
        return "<?xml version='1.0' encoding='UTF-8' ?>\n";
    }

    /**
     * Regresa el codigo para terminar el codigo.
     * @return el codigo para terminar el codigo.
     */
    private static String cierre(){
        return "\t</g> \n</svg>";
    }

    /**
     * Regresa el codigo para darle el tamaño necesario para empezar a graficar.
     * @param ancho el ancho de la imagen.
     * @param altura la altura de la imagen.
     * @return el codigo para darle el tamaño necesario para empezar a graficar.
     */
    private static String empezarGraficar(int ancho, int altura){
        return String.format("<svg width='%s' height='%s'>\n \t<g>\n", ancho, altura);
    }
}
