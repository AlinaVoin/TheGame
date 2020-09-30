import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameStateTest {

    @Test
    void move() {
        GameState gs = new GameState();
        assertEquals(gs.currentMove, 1);

        gs.move(0, 5);
        assertEquals(gs.selectedX,0);
        assertEquals(gs.selectedY, 2);

        gs.move(1,4);
        assertEquals(gs.totalWhite,12);
        assertEquals(gs.totalBlack,12);
        assertEquals(gs.isSelected, false);

    }

    @Test
    void canMove() {
        int cnt = 0;
        GameState gs = new GameState();
        assertEquals(gs.canMove(2,2), true);
        assertEquals(gs.canMove(1,3), false);

        for (int i =0; i<gs.N; i++){
            for (int j =0; j<gs.N; j++){
                if (gs.canMove(i,j)) cnt++;
            }
        }
        assertEquals(cnt,8);
    }

    @Test
    void checkMove() {
        int cnt = 0;
        GameState gs = new GameState();
        int[] deadCell = {-1,-1};
        for (int i =0; i<gs.N; i++){
            for (int j =0; j<gs.N; j++) {
                for (int k =0; k<gs.N; k++) {
                    for (int m = 0; m < gs.N; m++) {
                        if (gs.checkMove(i,j,k,m,deadCell)) cnt++;
                    }
                }
            }
        }
        assertEquals(cnt, 14);
    }

    @Test
    void canEat() {
        GameState gs = new GameState();
        int cnt =0;
        for (int i =0; i<gs.N; i++){
            for (int j =0; j<gs.N; j++) {
                if (gs.canEat(i,j)) cnt++;
            }
        }
        assertEquals(cnt, 0);

        gs.move(2,5);
        gs.move(3,4);

        gs.move(5,2);
        gs.move(4,3);

        for (int i =0; i<gs.N; i++){
            for (int j =0; j<gs.N; j++) {
                if (gs.canEat(i,j)) cnt++;
            }
        }
        assertEquals(cnt, 2);

        gs.move(3,4);
        gs.move(5,2);
        cnt = 0;
        for (int i =0; i<gs.N; i++){
            for (int j =0; j<gs.N; j++) {
                if (gs.canEat(i,j)) cnt++;
            }
        }
        assertEquals(cnt,2);
    }

    @Test
    void canEatSomeoneElse() {
        GameState gs = new GameState();
        // дамка Б4, пешка F2 - белые
        // дамка H2 пешка D6
        for (int i =0; i<gs.N; i++){
            for (int j =0; j<gs.N; j++) {
                gs.A[i][j] = 0;
            }
        }
        gs.totalWhite = 2;
        gs.totalBlack = 2;
        gs.A[3][1] = GameState.WK;
        gs.A[1][5] = GameState.white;

        gs.A[3][7] = GameState.BK;
        gs.A[5][3] = GameState.black;

        assertEquals(gs.canEatSomeoneElse(1,3), false);
        assertEquals(gs.canEatSomeoneElse(5,1), true);
        assertEquals(gs.canEatSomeoneElse(7,3), false);
        assertEquals(gs.canEatSomeoneElse(3,5), true);
    }
}