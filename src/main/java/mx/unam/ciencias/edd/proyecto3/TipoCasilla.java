package mx.unam.ciencias.edd.proyecto3;

/** 
 * Enumeracion para el tipo de casilla, util para distinguir y evitar abrir puertas
 * de casillas laterales
 */
public enum TipoCasilla {
    
    /* Casillas que se encuentran en las esquinas */
    ESQUINA_IZQ_N,
    ESQUINA_DER_N,
    ESQUINA_DER_SUR,
    ESQUINA_IZQ_SUR,
    /* Casillas que se enucuentran en los bordes */
    LATERAL_NORTE,
    LATERAL_ESTE,
    LATERAL_SUR,
    LATERAL_OESTE,
    /* Casillas que se encuentran en el centro */
    CENTRO;
}
