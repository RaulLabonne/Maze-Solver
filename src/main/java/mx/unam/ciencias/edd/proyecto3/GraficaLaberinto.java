package mx.unam.ciencias.edd.proyecto3;
import mx.unam.ciencias.edd.Grafica;
import mx.unam.ciencias.edd.Pila;
import mx.unam.ciencias.edd.proyecto3.Laberinto.Casilla;
import mx.unam.ciencias.edd.Color;
import mx.unam.ciencias.edd.Lista;
import mx.unam.ciencias.edd.VerticeGrafica;

public class GraficaLaberinto {
    

    Casilla[][] casillas;
    Laberinto laberinto;
    Grafica<Casilla> lGrafica;
    Lista<VerticeGrafica<Casilla>> trajectoriaPMinimo;
    Lista<Casilla> dijkstra;

    public GraficaLaberinto(Laberinto laberinto){
        this.laberinto = laberinto;
        casillas = laberinto.getLaberinto();
        lGrafica = new Grafica<>();
        dijkstra = new Lista<>();
        for (int i = 0; i < this.casillas.length ; i++)
            for (int j = 0; j < this.casillas[i].length; j++)
                lGrafica.agrega(laberinto.getCasilla(j, i));
        conectaCasillas();
        trajectoriaPMinimo = lGrafica.dijkstra(laberinto.getEntrada(), laberinto.getSalida());
        for (VerticeGrafica<Casilla> casilla : trajectoriaPMinimo) {
            dijkstra.agrega(casilla.get());
        }
        if (dijkstra.esVacia()){
            System.err.println("Archivo invalido: Laberinto sin solucion.");
            System.exit(1);
        }
    }

    public Lista<Casilla> getTrayectoria(){
        return dijkstra;
    }

    public Grafica<Casilla> getGrafica(){
        return lGrafica;
    }

    public void conectaCasillas(){
        Pila<Casilla> pila = new Pila<>();
        Casilla entrada = laberinto.getEntrada();
        pila.mete(entrada);
        while (!pila.esVacia()){
            Casilla actual = pila.mira();
            actual.setColor(Color.NEGRO);
            Lista<Casilla> puerta = vecinos(actual);
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
        if ((actual.getPuerta() & 11) == actual.getPuerta())
            vecinos.agrega(laberinto.getCasilla(coor[0]-1, coor[1]));
        if ((actual.getPuerta() & 13) == actual.getPuerta())
            vecinos.agrega(laberinto.getCasilla(coor[0], coor[1]-1));
        if ((actual.getPuerta() & 14) == actual.getPuerta())
            vecinos.agrega(laberinto.getCasilla(coor[0]+1, coor[1]));
            if ((actual.getPuerta() & 7) == casilla.getPuerta())
            vecinos.agrega(laberinto.getCasilla(coor[0], coor[1]+1));
        for (Casilla vecino : vecinos)
            if (vecino.getColor() == Color.NEGRO)
                vecinos.elimina(vecino);
        casilla.recuperaPuerta(entrada);
        return vecinos;
    }

    private Casilla ignoraPuerta(Casilla casilla){
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
}