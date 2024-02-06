import org.example.list.Movements;
import org.example.model.Hand;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChekCoordinateChanges {

    @Test
    public void testMove() {
        Hand hand = new Hand(10,10,10,10,10);
        hand.addCoordinate(15,10);
        var result = hand.addCoordinate(21,10);
        Movements expect = Movements.right;
        assertEquals(expect, result, "move");
    }

    @Test
    public void testNotMove() {
        Hand hand = new Hand(10,10,10,10,10);
        hand.addCoordinate(15,10);
        var result = hand.addCoordinate(20,10);
        String expect = null;
        assertEquals(expect, result, "not move");
    }
}
