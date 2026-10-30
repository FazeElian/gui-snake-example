package domain;

/**
 * Exception thrown when the HungrySnake game receives invalid data.
 */
public class HungrySnakeException extends Exception {

    public static final String INVALID_SIZE = "El tablero debe tener al menos una fila y una columna";
    public static final String TOO_MANY_FRUITS = "El número de frutas debe estar entre 0 y las casillas libres del tablero";
    public static final String INVALID_PERCENTAGE = "El porcentaje de obstáculos debe estar entre 0 y 100";
    public static final String INVALID_DIRECTION = "La dirección debe ser N, S, E o W";

    /**
     * Creates a new exception with the given message.
     * @param message description of the error
     */
    public HungrySnakeException(String message){
        super(message);
    }
}
