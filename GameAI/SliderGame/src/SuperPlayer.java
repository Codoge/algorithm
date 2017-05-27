import aiproj.slider.Move;
import aiproj.slider.SliderPlayer;
import java.awt.*;
import java.util.*;
import java.util.List;

public class SuperPlayer implements SliderPlayer{
    // a integer store max access depth in game tree, will reset in initialization
    private int maxDepth = 5;

    // a 2D array depict the game board
    private Piece[][] sliderBoard;

    // use array list to store current position of own side and other side
    private ArrayList<Point> sliders;
    private ArrayList<Point> opponents;

    // indexer about own side player
    private int curPlayer;

    /** Enumeration of all of the possible states of a board position */
    private static enum Piece { BLANK, BLOCK, HSLIDER, VSLIDER, }

    /** Collection of game helper functions and constants */
    private static interface Player {
        static final int H = 0, V = 1;
        static final Piece[] pieces = new Piece[]{Piece.HSLIDER, Piece.VSLIDER};
        // 1 - 0 is 1, and 1 - 1 is 0, so 1 - H is V, and 1 - V is H!
        static int other(int player) { return 1 - player; }
    }

    /**
     * initialize start state of current game, reset max search depth in game tree,
     * initialize all variables set in current class
     * @param dimension current game's board dimension
     * @param board current game's start state of game board
     * @param player current instance stands for which player
     * */
    @Override
    public void init(int dimension, String board, char player) {
        sliderBoard = new Piece[dimension][dimension];
        if (13 - dimension > maxDepth)
            maxDepth = 13 - dimension;

        if (player == 'H')
            curPlayer = Player.H;
        else
            curPlayer = Player.V;
        sliders = new ArrayList<>();
        opponents = new ArrayList<>();
        Scanner scanner = new Scanner(board);
        int i = 1;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            for (int j = 0, k = 0; j < dimension && k < line.length(); j++, k+=2) {
                char c = line.charAt(k);
                if (c == 'H') {
                    sliderBoard[dimension - i][j] = Piece.HSLIDER;
                } else if (c == 'V') {
                    sliderBoard[dimension - i][j] = Piece.VSLIDER;
                } else if (c == 'B') {
                    sliderBoard[dimension - i][j] = Piece.BLOCK;
                } else {
                    sliderBoard[dimension - i][j] = Piece.BLANK;
                }

                if (sliderBoard[dimension - i][j] == Player.pieces[curPlayer]) {
                    sliders.add(new Point(dimension-i, j));
                } else if (sliderBoard[dimension - i][j] == Player.pieces[Player.other(curPlayer)]) {
                    opponents.add(new Point(dimension-i, j));
                }
            }
            i++;
        }
    }

    /**
     * receive opponents' move and update current game state
     * @param move opponents' move
     * */
    @Override
    public void update(Move move) {
        // if move is null, we just do nothing but judge whether game is over or not in later
        if (move == null)
            return;
        // call doMove function to perform the move
        if (sliderBoard[move.j][move.i] == Player.pieces[Player.other(curPlayer)]) {
            doMove(move, Player.other(curPlayer));
        }
    }

    /**
     * base on current game state, give a best move decision.
     * */
    @Override
    public Move move() {
        Move bestMove = null;
        int bestVal = Integer.MIN_VALUE;
        List<Move> moveList = possibleMove(curPlayer);
        // get all possible of own side player, perform minimax algorithm choose the
        // best valued move
        for (Move m : moveList) {
            Piece piece = sliderBoard[m.j][m.i];
            doMove(m, curPlayer);
            int val = minimax(0, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
            if (val > bestVal) {
                bestVal = val;
                bestMove = m;
            }
            // revert the current move, for next loop
            revertMove(m, curPlayer, piece);
        }
        // perform the best move
        doMove(bestMove, curPlayer);
        return bestMove;
    }

    /**
     * perform a move of arbitrary player
     * @param move move to perform
     * @param player which player's move
     * */
    public void doMove(Move move, int player) {
        if (move == null) {
            return;
        }
        int toj = move.j;
        int toi = move.i;
        Piece piece = sliderBoard[toj][toi];
        if (piece != Player.pieces[player])
            return;
        sliderBoard[toj][toi] = Piece.BLANK;
        switch (move.d) {
            case UP: toj++; break;
            case DOWN: toj--; break;
            case LEFT: toi--; break;
            case RIGHT: toi++; break;
        }
        if (player == curPlayer) {
            sliders.remove(new Point(move.j, move.i));
            if (piece == Piece.VSLIDER && toj == sliderBoard.length ||
                    piece == Piece.HSLIDER && toi == sliderBoard.length) {
                return;
            } else {
                sliders.add(new Point(toj, toi));
                sliderBoard[toj][toi] = piece;
            }
        } else {
            opponents.remove(new Point(move.j, move.i));
            if (piece == Piece.VSLIDER && toj == sliderBoard.length ||
                    piece == Piece.HSLIDER && toi == sliderBoard.length) {
                return;
            } else {
                opponents.add(new Point(toj, toi));
                sliderBoard[toj][toi] = piece;
            }
        }
    }

    /**
     * revert move
     * @param move move to revert
     * @param player which player's move
     * @param piece which piece to revert back
     * */
    public void revertMove(Move move, int player, Piece piece) {
        if (move == null)
            return;
        int toj = move.j;
        int toi = move.i;
        if (piece != Player.pieces[player]) {
            System.out.println("piece not as same with player piece");
            return;
        }
        switch (move.d) {
            case UP: toj++; break;
            case DOWN: toj--; break;
            case LEFT: toi--; break;
            case RIGHT: toi++; break;
        }
        if (player == curPlayer) {
            sliders.add(new Point(move.j, move.i));
            if (!(piece == Piece.VSLIDER && toj == sliderBoard.length ||
                    piece == Piece.HSLIDER && toi == sliderBoard.length)) {
                sliders.remove(new Point(toj, toi));
                sliderBoard[toj][toi] = Piece.BLANK;
            }
        } else {
            opponents.add(new Point(move.j, move.i));
            if (!(piece == Piece.VSLIDER && toj == sliderBoard.length ||
                    piece == Piece.HSLIDER && toi == sliderBoard.length)) {
                opponents.remove(new Point(toj, toi));
                sliderBoard[toj][toi] = Piece.BLANK;
            }
        }
        sliderBoard[move.j][move.i] = piece;
    }

    /**
     * get possible move for player
     * @param player which player's possible move
     * */
    public List<Move> possibleMove(int player) {
        List<Move> moveList = new ArrayList<>();
        List<Point> curSliders;
        if (player == curPlayer) {
            curSliders = sliders;
        } else {
            curSliders = opponents;
        }
        // for each player's current point, check whether it can move to 4 direction
        for (Point p : curSliders) {
            if ((p.x+1 < sliderBoard.length && sliderBoard[p.x+1][p.y] == Piece.BLANK) || (p.x+1 == sliderBoard.length && player == Player.V)) {
                moveList.add(new Move(p.y, p.x, Move.Direction.UP));
            }
            if ((p.y+1 < sliderBoard.length && sliderBoard[p.x][p.y+1] == Piece.BLANK) || (p.y+1 == sliderBoard.length && player == Player.H)) {
                moveList.add(new Move(p.y, p.x, Move.Direction.RIGHT));
            }
            if (p.x-1 >= 0 && sliderBoard[p.x-1][p.y] == Piece.BLANK && player != Player.V) {
                moveList.add(new Move(p.y, p.x, Move.Direction.DOWN));
            }
            if (p.y-1 >= 0 && sliderBoard[p.x][p.y-1] == Piece.BLANK && player != Player.H) {
                moveList.add(new Move(p.y, p.x, Move.Direction.LEFT));
            }
        }
        return moveList;
    }

    /**
     * minimax algorithm performed in slider game
     * @param depth current visited depth in the game tree
     * @param isMax whether is get max turn
     * */
    public int minimax(int depth, boolean isMax, int alpha, int beta) {
        // if depth limit exceed or one player win, just return val
        if (depth == maxDepth || sliders.isEmpty() || opponents.isEmpty())
            return evaluate(depth);
        List<Move> moveList = possibleMove(curPlayer);
        int oppoPlayer = Player.other(curPlayer);
        List<Move> oppoMoveList = possibleMove(oppoPlayer);
        // if both two player have not move to perform, game is over
        if (moveList.isEmpty() && oppoMoveList.isEmpty())
            return evaluate(depth);

        if (isMax) {
            int best = Integer.MIN_VALUE;
            // just one of two player has not move, return neutral value 0
            if (moveList.isEmpty())
                return 0;
            for (Move m : moveList) {
                Piece piece = sliderBoard[m.j][m.i];
                doMove(m, curPlayer);
                best = Math.max(best, minimax(depth+1, !isMax, alpha, beta));
                alpha = Math.max(alpha, best);
                revertMove(m, curPlayer, piece);
                if (beta <= alpha)
                    break;
            }
            return best;
        } else {
            int best = Integer.MAX_VALUE;
            if (oppoMoveList.isEmpty())
                return 0;
            for (Move m : oppoMoveList) {
                Piece piece = sliderBoard[m.j][m.i];
                doMove(m, oppoPlayer);
                best = Math.min(best, minimax(depth+1, !isMax, alpha, beta));
                beta = Math.min(beta, best);
                revertMove(m, oppoPlayer, piece);
                if (beta <= alpha)
                    break;
            }
            return best;
        }
    }

    /**
     * evaluation function of minimax algorithm
     * evaluate current board's score for own side
     * positive value for more likely to win
     * negative value for more likely to lose
     * */
    public int evaluate(int depth) {
        if (sliders.size() == 0) {
            return 10 * sliderBoard.length + (maxDepth - depth)* 100;
        }
        if (opponents.size() == 0) {
            return -10 * sliderBoard.length + (maxDepth - depth)* 100;
        }
        int sum = 0;
        if (curPlayer == Player.V) {
            // there are sliders in the board, it's likely to lose
            for (Point p : sliders) {
                sum -= (sliderBoard.length - p.x) * 10;
            }
            // there ara opponent's pieces still in board, it's likely to win
            for (Point p : opponents) {
                sum += (sliderBoard.length - p.y) * 10;
            }
        } else {
            for (Point p : sliders) {
                sum -= (sliderBoard.length - p.y) * 10;
            }
            for (Point p : opponents) {
                sum += (sliderBoard.length - p.x) * 10;
            }
        }

        return sum;
    }

    public static void main(String[] args) {
        Scanner s = new Scanner("H + + +\nH + B +\nH B + +\n+ V V V\n");
        while (s.hasNextLine())
            System.out.println(s.nextLine());
        System.out.println("test");
        SuperPlayer sp = new SuperPlayer();
        sp.init(4, "H + + +\nH + B +\nH B + +\n+ V V V\n", 'H');

    }
}
