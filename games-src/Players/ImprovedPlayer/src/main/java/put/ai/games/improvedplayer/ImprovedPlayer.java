/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package put.ai.games.improvedplayer;

import java.util.List;
import put.ai.games.game.Board;
import put.ai.games.game.Move;
import put.ai.games.game.Player;
import put.ai.games.game.Player.Color;

public class ImprovedPlayer extends Player {

    private long startTime;
    private long timeLimit;

    private static class TimeOverException extends RuntimeException {}

    @Override
    public String getName() {
        return "Jakub Doman 159579 Krzysztof Swiatek 160155";
    }

    @Override
    public Move nextMove(Board b) {
        List<Move> moves = b.getMovesFor(getColor());
        // W razie konca czasu przechowuje ruch
        Move bestMove = moves.isEmpty() ? null : moves.get(0);

        // obsługa czasu
        startTime = System.currentTimeMillis();
        long totalTime = getTime();
        long safetyBuffer = Math.min((long) (totalTime * 0.05), 200); // w sytuacjach z dużym dostępnym czasem używaj tylko 200 ms zamiast marnowania procent
        timeLimit = totalTime - safetyBuffer;

        int currentDepth = 1;

        try {

            while (true) {
                Move bestMoveForCurrentDepth = null;
                double maxEval = Double.NEGATIVE_INFINITY;
                double alpha = Double.NEGATIVE_INFINITY;
                double beta = Double.POSITIVE_INFINITY;

                for (Move move : moves) {
                    checkTime();

                    b.doMove(move);
                    double eval = minimax(b, currentDepth - 1, alpha, beta, false); // ewaluacja nowego ruchu
                    b.undoMove(move);

                    if (eval > maxEval) {
                        maxEval = eval;
                        bestMoveForCurrentDepth = move;
                    }
                    alpha = Math.max(alpha, eval);
                }


                bestMove = bestMoveForCurrentDepth;
                currentDepth++;
            }
        } catch (TimeOverException e) {

        }

        return bestMove;
    }


    private void checkTime() {
        if ((System.currentTimeMillis() - startTime) >= timeLimit) {
            throw new TimeOverException();
        }
    }


    private double minimax(Board b, int depth, double alpha, double beta, boolean maximizingPlayer) {
        checkTime();

        Color winner = b.getWinner(getColor());
        if (winner == getColor()) return 100000.0 + depth;
        if (winner == getOpponent(getColor())) return -100000.0 - depth;
        if (winner == Color.EMPTY) return 0.0;

        if (depth == 0) {
            return evaluate(b);
        }

        Color currentPlayer = maximizingPlayer ? getColor() : getOpponent(getColor());
        List<Move> moves = b.getMovesFor(currentPlayer);

        if (moves.isEmpty()) return evaluate(b);

        if (maximizingPlayer) { // gracz
            double maxEval = Double.NEGATIVE_INFINITY;
            for (Move move : moves) {
                b.doMove(move);
                double eval = minimax(b, depth - 1, alpha, beta, false);
                b.undoMove(move);

                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) break;
            }
            return maxEval;
        } else { // przeciwnik
            double minEval = Double.POSITIVE_INFINITY;
            for (Move move : moves) {
                b.doMove(move);
                double eval = minimax(b, depth - 1, alpha, beta, true);
                b.undoMove(move);

                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) break;
            }
            return minEval;
        }
    }

    private double evaluate(Board b) { // prosta ewaluacaj sumująca pionki na planszy
        int myPoints = 0;
        int enemyPoints = 0;
        int size = b.getSize();
        Color myColor = getColor();
        Color enemyColor = getOpponent(myColor);

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Color c = b.getState(x, y);
                if (c == myColor) {
                    myPoints++;
                } else if (c == enemyColor) {
                    enemyPoints++;
                }
            }
        }
        return myPoints - enemyPoints;
    }
}