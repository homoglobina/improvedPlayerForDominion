/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package put.ai.games.improvedplayer;

import java.util.List;
import java.util.Random;
import put.ai.games.game.Board;
import put.ai.games.game.Move;
import put.ai.games.game.Player;

public class ImprovedPlayer extends Player {

    private static final int MAX_DEPTH = 5;

    @Override
    public String getName() {
        return "Jakub Domań 159579 Krzysztof Świątek 160155 ";
    }

    @Override
    public Move nextMove(Board b) {
        List<Move> moves = b.getMovesFor(getColor());
        Move bestMove = null;
        double maxEval = Double.NEGATIVE_INFINITY;
        double alpha = Double.NEGATIVE_INFINITY;
        double beta = Double.POSITIVE_INFINITY;

        for (Move move : moves) {
            b.doMove(move);
            double eval = minimax(b, MAX_DEPTH - 1, alpha, beta, false);
            b.undoMove(move);

            if (eval > maxEval) {
                maxEval = eval;
                bestMove = move;
            }
            alpha = Math.max(alpha, eval);
        }

        if (bestMove == null && !moves.isEmpty()) {
            bestMove = moves.get(0);
        }
        return bestMove;
    }


    private double minimax(Board b, int depth, double alpha, double beta, boolean maximizingPlayer) {
        Color winner = b.getWinner(getColor());
        if (winner == getColor()) {
            return 100000.0 + depth;  // + inf
        } else if (winner == getOpponent(getColor())) {
            return -100000.0 - depth; // - inf
        } else if (winner == Color.EMPTY) {
            return 0.0;
        }

        if (depth == 0) {
            return evaluate(b);
        }

        Color currentPlayerColor = maximizingPlayer ? getColor() : getOpponent(getColor());
        List<Move> moves = b.getMovesFor(currentPlayerColor);

        if (moves.isEmpty()) {
            return evaluate(b);
        }

        if (maximizingPlayer) {
            double maxEval = Double.NEGATIVE_INFINITY;
            for (Move move : moves) {
                b.doMove(move);
                double eval = minimax(b, depth - 1, alpha, beta, false);
                b.undoMove(move);

                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break; // odciecie
                }
            }
            return maxEval;
        } else {
            double minEval = Double.POSITIVE_INFINITY;
            for (Move move : moves) {
                b.doMove(move);
                double eval = minimax(b, depth - 1, alpha, beta, true);
                b.undoMove(move);

                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    break; // odciecie
                }
            }
            return minEval;
        }
    }

    private double evaluate(Board b) {
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