package domain;

import java.util.*;

/**
 * Domain of the HungrySnake game.
 * The board is a grid of cells, each one marked with a character:
 * 'g' empty (grass), 'h' snake head, 'b' snake body, 'f' fruit, 'o' obstacle.
 */
public class HungrySnake {

    /** Game board; each cell holds the character of what occupies it. */
    private char[][] board;
    /** Snake positions as {row, column}; first element is the head, last one is the tail. */
    private Deque<int[]> body;
    /** Number of rows of the board. */
    private int rows;
    /** Number of columns of the board. */
    private int columns;
    /** Number of fruits on the board. */
    private int nFruits;
    /** Number of obstacles on the board. */
    private int nObst;

    /**
     * Creates a new game with the snake, fruits and obstacles placed randomly
     * on empty cells, so no two elements share the same cell.
     * @param rows number of rows of the board
     * @param columns number of columns of the board
     * @param nFruits number of fruits to place; must be less than rows * columns
     * @param pObst percentage (0-100) of the remaining free cells to fill with obstacles
     * @throws HungrySnakeException if the board size, the number of fruits or the percentage is invalid
     */
    public HungrySnake(int rows, int columns, int nFruits, int pObst) throws HungrySnakeException{
        // Validate before building anything; invalid values would crash or loop forever
        if (rows <= 0 || columns <= 0){
            throw new HungrySnakeException(HungrySnakeException.INVALID_SIZE);
        }
        if (nFruits < 0 || nFruits >= rows * columns){
            throw new HungrySnakeException(HungrySnakeException.TOO_MANY_FRUITS);
        }
        if (pObst < 0 || pObst > 100){
            throw new HungrySnakeException(HungrySnakeException.INVALID_PERCENTAGE);
        }

        this.rows = rows;
        this.columns = columns;
        this.nFruits = nFruits;

        // Start with an empty board
        board = new char[rows][columns];
        for (char[] fila : board) {
            Arrays.fill(fila, 'g');
        }

        Random rand = new Random();
        int x, y;

        // The snake starts with only its head; the board is empty, so any cell works
        body = new ArrayDeque<>();
        x = rand.nextInt(rows);
        y = rand.nextInt(columns);

        body.addFirst(new int[]{x, y});
        board[x][y] = 'h';

        // Place each fruit on a random empty cell
        for (int i = 0; i < nFruits; i++){
            do {
                x = rand.nextInt(rows);
                y = rand.nextInt(columns);
            } while (board[x][y] != 'g');
            board[x][y] = 'f';
        }

        // Obstacles cover pObst% of the cells left free; multiply before dividing to avoid truncating to 0
        nObst = ((rows * columns) - nFruits - body.size()) * pObst / 100;
        for (int i = 0; i < nObst; i++){
            do {
                x = rand.nextInt(rows);
                y = rand.nextInt(columns);
            } while (board[x][y] != 'g');
            board[x][y] = 'o';
        }
    }

    /**
     * Moves the snake one cell in the given direction.
     * If the new cell has a fruit, the snake eats it, grows by one and a new fruit
     * appears on a random empty cell (if any is left); otherwise the tail moves
     * forward so the size stays the same.
     * @param d direction: 'N' (up), 'S' (down), 'E' (right) or 'W' (left)
     * @return false if the direction is invalid or the snake hits a border,
     *         an obstacle or its own body; true if the move was made
     * @throws HungrySnakeException if the direction is not N, S, E or W
     */
    public boolean move(char d) throws HungrySnakeException{
        if (d != 'N' && d != 'S' && d != 'E' && d != 'W'){
            throw new HungrySnakeException(HungrySnakeException.INVALID_DIRECTION);
        }

        // Copy the head position so the head stored in body is not modified
        int[] head = body.getFirst();
        int row = head[0];
        int column = head[1];

        switch (d){
            case 'N': 
                row -= 1; 
                break;
            case 'S': 
                row += 1; 
                break;
            case 'E': 
                column += 1; 
                break;
            case 'W': 
                column -= 1; 
                break;
            default: 
                return false;
        }

        if (row < 0 || row >= rows || column < 0 || column >= columns){
            return false;
        }
        if (board[row][column] == 'o' || board[row][column] == 'b'){
            return false;
        }

        boolean eats = board[row][column] == 'f';

        // The old head becomes body and the new cell becomes the head
        board[head[0]][head[1]] = 'b';
        body.addFirst(new int[]{row, column});
        board[row][column] = 'h';

        if (eats){
            // Free cells = total - obstacles - snake - fruits still on the board (the eaten one is gone).
            // Respawn only if one exists; otherwise the do-while would never end
            if (rows * columns - nObst - body.size() - (nFruits - 1) > 0){
                Random rand = new Random();
                int x, y;
                do {
                    x = rand.nextInt(rows);
                    y = rand.nextInt(columns);
                } while (board[x][y] != 'g');
                board[x][y] = 'f';
            }
        } else {
            // Without food the tail is released, keeping the same size
            int[] tail = body.removeLast();
            board[tail[0]][tail[1]] = 'g';
        }
        return true;
    }

    /**
     * Returns the size of the snake.
     * @return number of cells the snake occupies
     */
    public int getSize(){
        return body.size();
    }

    /**
     * Returns the game board.
     * @return matrix with the character of what occupies each cell
     */
    public char[][] board(){
        return board;
    }


}