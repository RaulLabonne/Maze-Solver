package mx.unam.ciencias.edd.proyecto3;
import mx.unam.ciencias.edd.Grafica;
import mx.unam.ciencias.edd.Pila;
import mx.unam.ciencias.edd.proyecto3.Laberinto.Casilla;
import mx.unam.ciencias.edd.Color;
import mx.unam.ciencias.edd.Lista;

public class GraficaLaberinto {
    

    Casilla[][] casillas;
    Laberinto laberinto;
    Grafica<Casilla> lGrafica;

    public GraficaLaberinto(Laberinto laberinto){
        this.laberinto = laberinto;
        casillas = laberinto.getLaberinto();
        lGrafica = new Grafica<>();
        for (int i = 0; i < this.casillas.length ; i++)
            for (int j = 0; j < this.casillas[i].length; j++)
                lGrafica.agrega(laberinto.getCasilla(j, i));
        conectaCasillas();
        System.out.println(lGrafica);
    }

    public void conectaCasillas(){
        Pila<Casilla> pila = new Pila<>();
        Casilla entrada = laberinto.getEntrada();
        System.out.println(entrada.getCoordenadas()[0] + ", "+ entrada.getCoordenadas()[1]);
        /* int c = 0; */
        pila.mete(entrada);
        while (!pila.esVacia()){
            Casilla actual = pila.mira();
            /* System.out.println("Ciclo " + c + "\nCasilla actual: " + actual.getCoordenadas()[0] + ", "+ actual.getCoordenadas()[1] + "\tColor: " + actual.getColor());
            c++; */
            actual.setColor(Color.NEGRO);
            Lista<Casilla> puerta = vecinos(actual);
            /* System.out.println("\tVecinos: " + puerta); */
            if (!puerta.esVacia()){
                if(puerta.getLongitud() == 1){
                    puerta.getPrimero().setAnterior(actual);;
                    nuevaArista(puerta.getPrimero());
                    pila.mete(puerta.getPrimero());
                    continue;
                } else {
                    puerta.get(1).setAnterior(actual);;
                    nuevaArista(puerta.get(1));
                    pila.mete(puerta.get(1));
                }
            } else
                pila.saca();
        }
    }

    private Lista<Casilla> vecinos(Casilla casilla){
        Lista<Casilla> vecinos = new Lista<>();
        int[] coor = casilla.getCoordenadas();
        byte entrada = casilla.getPuerta();
        Casilla actual = ignoraPuerta(casilla);
        /* System.out.println("Puerta de casilla: " + entrada + "\tCasilla parcheada: " + actual.getPuerta()); */

        /* System.out.println("Caso 1: Puerta " + (actual.getPuerta() & 4)); */
        if ((actual.getPuerta() & 11) == actual.getPuerta())
            vecinos.agrega(laberinto.getCasilla(coor[0]-1, coor[1]));
        /* System.out.println("Caso 2: Puerta " + (actual.getPuerta() & 2)); */
        if ((actual.getPuerta() & 13) == actual.getPuerta())
            vecinos.agrega(laberinto.getCasilla(coor[0], coor[1]-1));
        /* System.out.println("Caso 3: Puerta " + (actual.getPuerta() & 1)); */
        if ((actual.getPuerta() & 14) == actual.getPuerta())
            vecinos.agrega(laberinto.getCasilla(coor[0]+1, coor[1]));
        /* System.out.println("Caso 4: Puerta " + (actual.getPuerta() & 8)); */
            if ((actual.getPuerta() & 7) == casilla.getPuerta())
            vecinos.agrega(laberinto.getCasilla(coor[0], coor[1]+1));
       /*  System.out.println("\tVecinos (sin eliminar): " + vecinos); */
        for (Casilla vecino : vecinos)
            if (vecino.getColor() == Color.NEGRO)
                vecinos.elimina(vecino);
        casilla.recuperaPuerta(entrada);
        /* System.out.println(casilla.getPuerta()); */
        return vecinos;
    }

    private Casilla ignoraPuerta(Casilla casilla){
        /* System.out.println("Es entrada: " + casilla.esEntrada() + "\tEs salida: " + casilla.esSalida()); */
        if (casilla.esEntrada() || casilla.esSalida()){
            Casilla conPuerta = casilla;
            int[] coor = conPuerta.getCoordenadas();
            if (coor[0] == 0)
            conPuerta.cierraPuerta((byte)4);
            if (coor[0] == casillas[0].length-1)
                conPuerta.cierraPuerta((byte)1);
            if (coor[1] == 0)
                conPuerta.cierraPuerta((byte)2);
            if (coor[1] == casillas.length-1)
                conPuerta.cierraPuerta((byte)8);
            return conPuerta;
        }
        return casilla;
    }

    private void nuevaArista(Casilla actual){
        Casilla anterior = actual.getAnterior();
        int[] coor = actual.getCoordenadas(), bcoor = anterior.getCoordenadas();
        int peso = actual.getPuntaje() + anterior.getPuntaje() + 1;
        if (coor[0] == bcoor[0]){
            if ((actual.getPuerta() & 13) == actual.getPuerta() && (anterior.getPuerta() & 7) == anterior.getPuerta())
                lGrafica.conecta(actual, anterior, peso);
            else if ((actual.getPuerta() & 7) == actual.getPuerta() && (anterior.getPuerta() & 13) == anterior.getPuerta())
                lGrafica.conecta(actual, anterior, peso);
            else 
                uso(coor, bcoor);
        } else if (coor[1] == bcoor[1]){
            if ((actual.getPuerta() & 11) == actual.getPuerta() && (anterior.getPuerta() & 14) == anterior.getPuerta())
                lGrafica.conecta(actual, anterior, peso);
            else if ((actual.getPuerta() & 14) == actual.getPuerta() && (anterior.getPuerta() & 11) == anterior.getPuerta())
                lGrafica.conecta(actual, anterior, peso);
            else 
                uso(coor, bcoor);
        }
    }

    private void uso(int[] coor, int [] bcoor){
        String s = String.format("Archivo invalido: Las casillas de coordenadas (%s,%s) y (%s,%s) no son consecutivas", coor[0], coor[1], bcoor[0], bcoor[1]);
        System.err.println(s);
        System.exit(1);
    }
    /* lGrafica.conecta(casilla, vecino, peso + vecino.getPuntaje()); */
}