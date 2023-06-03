package mx.unam.ciencias.edd.proyecto3;
import java.io.*;
import java.io.IOException;

/** 
 * Clase auxiliar para generar los bytes que representen un laberinto
 */
public class GenerarArchivo {

    /* El laberinto */
    Laberinto laberinto;
    /* El arreglo de bytes del laberinto */
    byte[] laberintoBytes;

    public GenerarArchivo(int w, int h, long seed){
        this.laberinto = new Laberinto(w, h, seed);
        this.laberinto.construyeLaberinto();
        /* System.out.println(laberinto);  */
        laberintoBytes = laberinto.arregloCasillas((byte)(w & 0xFF), (byte)(h & 0xFF));
    }

    public void creaArchivo(){
        try{
            OutputStreamWriter out = new OutputStreamWriter(System.out);
            for (int i = 0; i < laberintoBytes.length; i++){
                /* System.out.println(laberintoBytes[i]); */
                out.write(laberintoBytes[i]);
            }
            out.close();
        } catch (IOException ioe){}
    }

}
