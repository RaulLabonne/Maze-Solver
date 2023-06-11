package mx.unam.ciencias.edd.proyecto3;

import mx.unam.ciencias.edd.Grafica;
import mx.unam.ciencias.edd.Lista;
import mx.unam.ciencias.edd.proyecto3.Laberinto.Casilla;

/** 
 * Clase para genera un archvio svg que represente al laberinto dado.
 */
public class GeneradorSVG {
    
    /* El laberinto a graficar */
    Laberinto laberinto;
    /* La grafica asociada al laberinto */
    Grafica<Casilla> graficaL;
    /* La solucion al laberinto */
    Lista<Casilla> solucion;

    /* Constructor de la clase */
    public GeneradorSVG(byte[] archivo){
        laberinto = new Laberinto(archivo);
        GraficaLaberinto grafica = new GraficaLaberinto(laberinto);
        graficaL = grafica.getGrafica();
        solucion = grafica.getTrayectoria();
    }

    /* Ejecuta el programa */
    public void ejecuta(){
        Lista<Casilla> laberintoSVG = new Lista<>();
        int[] entrada = laberinto.getEntrada().getCoordenadas();
        int[] salida = laberinto.getSalida().getCoordenadas();
        for (Casilla casilla : graficaL)
            laberintoSVG.agrega(casilla);
        LaberintoSVG svg = new LaberintoSVG(laberintoSVG, solucion, laberinto.getDimensiones(), entrada, salida);
        String archivoSVG = svg.svg();
        System.out.println(archivoSVG);
    }
}
