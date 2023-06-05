package mx.unam.ciencias.edd.proyecto3;
import java.io.OutputStreamWriter;
import java.io.IOException;

/** 
 * Clase auxiliar para generar los bytes que representen un laberinto
 */
public class GenerarArchivo {

    /* El laberinto */
    Laberinto laberinto;
    /* El arreglo de bytes del laberinto */
    byte[] laberintoBytes;

    /* Constructor de la clase */
    public GenerarArchivo(int w, int h, long seed){
        this.laberinto = new Laberinto(w, h, seed);
        this.laberinto.construyeLaberinto();
        System.out.println(laberinto);
        laberintoBytes = laberinto.arregloCasillas((byte)(w & 0xFF), (byte)(h & 0xFF));
    }

    /* Metodo auxiliar que guarda los bytes en la salida estandar */
    public void creaArchivo(){
        try{
            OutputStreamWriter out = new OutputStreamWriter(System.out, "ISO-8859-1");
            for (int i = 0; i < laberintoBytes.length; i++){
                out.write(laberintoBytes[i] & 0xFF);
            }
            out.close();
        } catch (IOException ioe){
            System.err.println("Ocurrio un error al escribir en el archivo");
        }
    }
}
