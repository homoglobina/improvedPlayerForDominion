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

public class improvedPlayer extends Player {

    private Random random = new Random(0xdeadbeef);


    @Override
    public String getName() {
        return "Jakub Domań 159579 Krzysztof Świątek 160155 ";
    }


    @Override
    public Move nextMove(Board b) {
        List<Move> moves = b.getMovesFor(getColor());



        return moves.get(random.nextInt(moves.size()));
    }
}
