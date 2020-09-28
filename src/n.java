import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class n extends Application {
    final int N = 8;
    final int W = 720;
    int[][] A = new int[N][N];
    final int D = 72;
    Color light = Color.rgb(250,206,159);
    Color dark = Color.rgb(203,139,74);
    Image darkCircle = new Image(new FileInputStream("black.png"));
    Image lightCircle = new Image(new FileInputStream("white.png"));
    Image darkKing = new Image(new FileInputStream("black_queen.png"));
    Image lightKing = new Image(new FileInputStream("white_queen.png"));
    int currentMove = 1;
    final int white = 1;
    final int black = 2;
    final int WK = 10;
    final int BK = 20;
    String gameMSG = "hello";
    int totalWhite = 12;
    int totalBlack = 12;
    boolean gameOn = true;

    boolean isSelected = false;
    int selectedX = 0;
    int selectedY = 0;

    public n() throws FileNotFoundException { }

    public static void main(String[] args) {
        launch(args);
    }

    Canvas canvas = new Canvas(W,W);
    GraphicsContext gc = null;

    @Override
    public void start(Stage primaryStage){

        primaryStage.setTitle("Русские шашки");

        Group root = new Group();
        root.getChildren().add(canvas);
        //выполняем нач расстановку
        for (int i = 0; i<N; i++){
            for (int j = 0; j<N; j++){
                A[i][j] = 0;
                if (i>2 && i<5) continue;
                if ((i+j)%2==0){
                    if (i<3){
                        A[i][j] = white;
                    } else A[i][j] = black;
                }
            }
        }

        gc = canvas.getGraphicsContext2D();

        EventHandler<MouseEvent> clickHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (!gameOn){
                    gameMSG = "Текущая игра закончена. Чтобы начать новую игру нажмите <Enter>";
                    repaintBoard();
                    return;
                }
                int x = (int) mouseEvent.getX();
                int y = (int) mouseEvent.getY();
                x -= (W-N*D)/2;
                y -= (W-N*D)/2;
                if (x<=0 || y<=0) return;
                else {
                    int horCor = x/D;
                    int verCor = y/D;
                    int[] deadCell = {-1,-1};
                    if (horCor >= N || verCor >= N) return;
                    if ((horCor + verCor)% 2 == 0) return;
                    if (currentMove == 1){
                        //проверка, что белые не заблокированы
                        boolean whiteCanMove = false;
                        for (int i =0; i < N; i++){
                            for (int j =0; j<N; j++){
                                if ((A[i][j] == white || A[i][j] == WK) && canMove(j,i)){
                                    whiteCanMove = true;
                                    break;
                                }
                            }
                            if (whiteCanMove) break;
                        }
                        if (!whiteCanMove){
                            gameMSG = "Победа черных! Белые заблокированы.";
                            gameOn = false;
                            return;
                        }
                        if (A[N-1-verCor][horCor] == black || A[N-1-verCor][horCor] == BK) return;
                        if (A[N-1-verCor][horCor] == white || A[N-1-verCor][horCor] == WK) {
                            if (!canMove(horCor, N - 1 - verCor)) return;
                            if (!canEat(horCor, N - 1 - verCor) && canEatSomeoneElse(horCor, N - 1 - verCor)) {
                                gameMSG = "Вы должны рубить";
                            }
                            else{
                                isSelected = true;
                                selectedX = horCor;
                                selectedY = N - 1 - verCor;
                            }
                            repaintBoard();
                        }
                        else{
                            if (isSelected && checkMove(selectedX,selectedY,horCor,N-1-verCor,deadCell)){
                                if (canEat(selectedX, selectedY) && deadCell[0] == -1){
                                    gameMSG = "Нельзя ходить, если можно съесть";
                                    repaintBoard();
                                    return;
                                }
                                isSelected = false;
                                A[N-1-verCor][horCor] = A[selectedY][selectedX];
                                A[selectedY][selectedX] = 0;
                                if (verCor == 0 && A[N-1-verCor][horCor] == white){
                                    A[N-1-verCor][horCor] = WK;
                                }
                                if (deadCell[0] >= 0){
                                    A[deadCell[1]][deadCell[0]] = 0;
                                    totalBlack--;
                                    if (totalBlack == 0){ //проверка на победу
                                        gameMSG = "Белые выиграли!";
                                        gameOn = false;
                                    }
                                    else {
                                        if (canEat(horCor, N - 1 - verCor)) {
                                            isSelected = true;
                                            selectedX = horCor;
                                            selectedY = N - 1 - verCor;

                                        } else {
                                            currentMove = 2;
                                        }
                                    }
                                    repaintBoard();
                                }
                                else {
                                    repaintBoard();
                                    currentMove = 2;
                                }
                            }
                        }
                    }
                    else{
                        boolean blackCanMove = false;
                        for (int i =0; i < N; i++){
                            for (int j =0; j<N; j++){
                                if ((A[i][j] == black || A[i][j] == BK) && canMove(j,i)){
                                    blackCanMove = true;
                                    break;
                                }
                            }
                            if (blackCanMove) break;
                        }
                        if (!blackCanMove){
                            gameMSG = "Победа белых! Черные заблокированы.";
                            gameOn = false;
                            return;
                        }
                        if (A[N-1-verCor][horCor] == white || A[N-1-verCor][horCor] == WK) return;
                        if (A[N-1-verCor][horCor] == black || A[N-1-verCor][horCor] == BK){
                            if (!canMove(horCor,N-1-verCor)) return;
                            if (!canEat(horCor, N - 1 - verCor) && canEatSomeoneElse(horCor, N - 1 - verCor)) {
                                gameMSG = "Вы должны рубить";
                            }
                            else {
                                isSelected = true;
                                selectedX = horCor;
                                selectedY = N - 1 - verCor;
                            }
                            repaintBoard();
                        }
                        else{
                            if (isSelected && checkMove(selectedX,selectedY,horCor,N-1-verCor,deadCell)){
                                if (canEat(selectedX, selectedY) && deadCell[0] == -1){
                                    gameMSG = "Нельзя ходить, если можно съесть";
                                    repaintBoard();
                                    return;
                                }
                                isSelected = false;
                                A[N-1-verCor][horCor] = A[selectedY][selectedX];
                                A[selectedY][selectedX] = 0;
                                if (verCor == N-1 && A[N-1-verCor][horCor] == black){
                                    A[N-1-verCor][horCor] = BK;
                                }
                                if (deadCell[0] >= 0){ // сюда не заходит
                                    System.out.println("съесть");
                                    A[deadCell[1]][deadCell[0]] = 0;
                                    totalWhite--;
                                    if (totalWhite == 0){ //проверка на победу
                                        gameMSG = "Черные выиграли!";
                                        gameOn = false;
                                    }
                                    else {
                                        if (canEat(horCor, N - 1 - verCor)) {
                                            isSelected = true;
                                            selectedX = horCor;
                                            selectedY = N - 1 - verCor;

                                        } else {
                                            currentMove = 1;
                                        }
                                    }
                                    repaintBoard();
                                }
                                else {
                                    repaintBoard();
                                    currentMove = 1;
                                }
                            }
                        }
                    }
                }
            }
        };

        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, clickHandler);
        repaintBoard();
        Scene scene = new Scene(root,W,W);
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER){
                if(!gameOn){
                    totalBlack = 12;
                    totalWhite = 12;
                    for (int i = 0; i<N; i++){
                        for (int j = 0; j<N; j++){
                            A[i][j] = 0;
                            if (i>2 && i<5) continue;
                            if ((i+j)%2==0){
                                if (i<3){
                                    A[i][j] = white;
                                } else A[i][j] = black;
                            }
                        }
                    }

                    gameOn = true;
                    gameMSG = "Новая игра! Ход белых.";
                    repaintBoard();
                }
            }
        });
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();
    }

    public void repaintBoard() {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, W, W);
        gc.setFont(new Font("Comic Sans MS", 18));

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if ((i + j) % 2 == 0) {
                    gc.setFill(dark);
                } else gc.setFill(light);
                gc.fillRect(W / 2 - (N / 2) * D + j * D, W / 2 + (N / 2) * D - i * D - D, D, D);
            }
        }
        gc.setFill(Color.WHITE);
        for (int i = 0; i < N; i++){
            gc.fillText(String.valueOf((char)('0' + i+1)),30, W / 2 + (N / 2) * D - i * D - D + D/2 + 5);
        }
        for (int i = 0; i<N; i++){
            gc.fillText(String.valueOf((char)('A' + i)), W / 2 - (N / 2) * D + i * D + D/2 - 5, 60);
        }

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                int weight = W / 2 - (N / 2) * D + j * D;
                int length = W / 2 + (N / 2) * D - i * D - D;
                if (A[i][j] == black) {
                    gc.drawImage(darkCircle, weight, length);
                } else if (A[i][j] == white) {
                    gc.drawImage(lightCircle, weight, length);
                } else if (A[i][j] == WK) {
                    gc.drawImage(lightKing, weight, length);
                } else if (A[i][j] == BK) {
                    gc.drawImage(darkKing, weight, length);
                }
            }
            if (isSelected) {
                gc.setLineWidth(4);
                gc.setStroke(Color.LIGHTGREEN);
                gc.strokeRect(W / 2 - (N / 2) * D + selectedX * D, W / 2 + (N / 2) * D - selectedY * D - D, D, D);
            }
        }
        if (gameMSG.length()>0){
            Color msgColor = Color.color(1,0,0);
            gc.setFill(msgColor);
            gc.setFont(new Font("Comic Sans MS", 18.0));
            gc.fillText(gameMSG, 20, W-20);
            gameMSG = "";
        }
    }

    public boolean canMove(int x, int y){ //может ли фишка походить
        if (A[y][x] == 0) return false;

        if (A[y][x] == white){
            if (y<N-1 && x>0 && A[y+1][x-1] == 0) return true;
            if (y<N-1 && x<N-1 && A[y+1][x+1] == 0) return true;

            if( y < N-2 && x> 1 && (A[y+1][x-1] == black || A[y+1][x-1] == BK) && A[y+2][x-2] == 0) return true;
            if( y < N-2 && x< N - 2 && (A[y+1][x+1] == black || A[y+1][x+1] == BK) && A[y+2][x+2] == 0) return true;
            if( y > 1 && x> 1 && (A[y-1][x-1] == black || A[y-1][x-1] == BK) && A[y-2][x-2] == 0) return true;
            if( y > 1 && x< N - 2 && (A[y-1][x+1] == black || A[y-1][x+1] == BK) && A[y-2][x+2] == 0) return true;
        }
        else if (A[y][x] == black){
            if (y>0 && x>0 && A[y-1][x-1] == 0) return true;
            if (y>0 && x<N-1 && A[y-1][x+1] == 0) return true;

            if( y < N-2 && x> 1 && (A[y+1][x-1] == white || A[y+1][x-1] == WK) && A[y+2][x-2] == 0) return true;
            if( y < N-2 && x< N - 2 && (A[y+1][x+1] == white || A[y+1][x+1] == WK) && A[y+2][x+2] == 0) return true;
            if( y > 1 && x> 1 && (A[y-1][x-1] == white || A[y-1][x-1] ==  WK) && A[y-2][x-2] == 0) return true;
            if( y > 1 && x< N - 2 && (A[y-1][x+1] == white || A[y-1][x+1] == WK) && A[y-2][x+2] == 0) return true;
        }
        else if (A[y][x] == WK){
            if (y<N-1 && x>0 && A[y+1][x-1] == 0) return true;
            if (y<N-1 && x<N-1 && A[y+1][x+1] == 0) return true;
            if (y>0 && x>0 && A[y-1][x-1] == 0) return true;
            if (y>0 && x<N-1 && A[y-1][x+1] == 0) return true;

            if( y < N-2 && x> 1 && (A[y+1][x-1] == black || A[y+1][x-1] == BK) && A[y+2][x-2] == 0) return true;
            if( y < N-2 && x< N - 2 && (A[y+1][x+1] == black || A[y+1][x+1] == BK) && A[y+2][x+2] == 0) return true;
            if( y > 1 && x> 1 && (A[y-1][x-1] == black || A[y-1][x-1] == BK) && A[y-2][x-2] == 0) return true;
            if( y > 1 && x< N - 2 && (A[y-1][x+1] == black || A[y-1][x+1] == BK) && A[y-2][x+2] == 0) return true;

        }
        else{
            if (y<N-1 && x>0 && A[y+1][x-1] == 0) return true;
            if (y<N-1 && x<N-1 && A[y+1][x+1] == 0) return true;
            if (y>0 && x>0 && A[y-1][x-1] == 0) return true;
            if (y>0 && x<N-1 && A[y-1][x+1] == 0) return true;

            if( y < N-2 && x> 1 && (A[y+1][x-1] == white || A[y+1][x-1] == WK) && A[y+2][x-2] == 0) return true;
            if( y < N-2 && x< N - 2 && (A[y+1][x+1] == white || A[y+1][x+1] == WK) && A[y+2][x+2] == 0) return true;
            if( y > 1 && x> 1 && (A[y-1][x-1] == white || A[y-1][x-1] == WK) && A[y-2][x-2] == 0) return true;
            if( y > 1 && x< N - 2 && (A[y-1][x+1] == white || A[y-1][x+1] == WK) && A[y-2][x+2] == 0) return true;
        }
        return false;
    }

    public boolean checkMove(int x, int y, int x2, int y2, int[] deadCell){ //может ли кто-то быть съеден, если да, передай его координаты
        if (A[y][x] == 0) return false;
        if ((x2 + y2)%2 != 0) return false;
        if (A[y2][x2]!= 0) return false;
        deadCell[0] = -1;

        if (A[y][x] == white){ //ХОД БЕЛЫХ ПЕШЕК
            if (y2==y+1 && (x2==x+1 || x2==x-1)) return true;
            if (y2==y-1) return false;

            if(y2==y+2 && x2==x-2 && (A[y+1][x-1] == black || A[y+1][x-1] == BK) && A[y2][x2] == 0){ //проверяем свободна ли клетка через одну слева сверху, является ли фигура рядом с выбранной вражеской пешкой или дамкой
                deadCell[0] = x-1; //если фигура оказалась врагом, присваиваем ее координаты
                deadCell[1] = y+1;
                return true;
            }
            if(y2==y+2 && x2==x+2 && (A[y+1][x+1] == black || A[y+1][x+1] == BK) && A[y2][x2] == 0){
                deadCell[0] = x+1;
                deadCell[1] = y+1;
                return true;
            }
            if(y2==y-2 && x2==x-2 && (A[y-1][x-1] == black || A[y-1][x-1] == BK) && A[y2][x2] == 0){
                deadCell[0] = x-1;
                deadCell[1] = y-1;
                return true;
            }
            if(y2==y-2 && x2==x+2 && (A[y-1][x+1] == black || A[y-1][x+1] == BK) && A[y2][x2] == 0){
                deadCell[0] = x+1;
                deadCell[1] = y-1;
                return true;
            }
        }
        if (A[y][x] == black){ //ХОД ЧЕРНЫХ ПЕШЕК
            if (y2==y-1 && (x2==x+1 || x2==x-1)) return true;
            if (y2==y+1) return false;

            if(y2==y+2 && x2==x-2 && (A[y+1][x-1] == white || A[y+1][x-1] == WK) && A[y2][x2] == 0){
                deadCell[0] = x-1;
                deadCell[1] = y+1;
                return true;
            }
            if(y2==y+2 && x2==x+2 && (A[y+1][x+1] == white || A[y+1][x+1] == WK) && A[y2][x2] == 0){
                deadCell[0] = x+1;
                deadCell[1] = y+1;
                return true;
            }
            if(y2==y-2 && x2==x-2 && (A[y-1][x-1] == white || A[y-1][x-1] == WK) && A[y2][x2] == 0){
                deadCell[0] = x-1;
                deadCell[1] = y-1;
                return true;
            }
            if(y2==y-2 && x2==x+2 && (A[y-1][x+1] == white || A[y-1][x+1] == WK) && A[y2][x2] == 0){
                deadCell[0] = x+1;
                deadCell[1] = y-1;
                return true;
            }
        }
        if (A[y][x] == WK) { // ХОД БЕЛЫХ ДАМОК
            if (Math.abs(x - x2) != Math.abs(y - y2)) return false;
            int wCnt = 0;
            int bCnt = 0;
            int dX = (x2 - x) / Math.abs(x2 - x);
            int dY = (y2 - y) / Math.abs(y2 - y);
            int nStep = 0;
            int firstBX = -1;
            int firstBY = -1;

            for (int i = y + dY; i != y2; i += dY) {
                nStep++;
                if (A[i][x + nStep*dX] == WK || A[i][x + nStep*dX] == white) {
                    wCnt++;
                }
                if (A[i][x + nStep * dX] == BK || A[i][x + nStep * dX] == black) {
                    firstBX = x + nStep * dX;
                    firstBY = i;
                    bCnt++;
                    }
            }
            if (wCnt > 0) return false; //если на пути встречаются пешки своего цвета
            if (bCnt <= 1) {
                if (bCnt == 1) { //если на пути от нашей начальной позиции до желаемой находится только одна вражеская точка, мы можем ее съесть
                    deadCell[0] = firstBX;
                    deadCell[1] = firstBY;
                }
                return true;
            }
            else return false;
        }
        else{ // ХОД ЧЕРНЫХ ДАМОК
            if (Math.abs(x-x2)!= Math.abs(y-y2)) return false;
            int wCnt = 0;
            int bCnt = 0;
            int dX = (x2-x)/Math.abs(x2-x);
            int dY = (y2-y)/Math.abs(y2-y);
            int nStep = 0;
            int firstBX = -1;
            int firstBY = -1;


            for (int i = y + dY; i!=y2; i+=dY){
                nStep++;
                if (A[i][x+ nStep*dX] == BK || A[i][x+ nStep*dX] == black){
                    bCnt++;
                }
                if (A[i][x+ nStep*dX] == WK || A[i][x+ nStep*dX] == white){
                    firstBX = x + nStep * dX;
                    firstBY = i;
                    wCnt++;
                }
            }
            if (bCnt>0) return false;
            if (wCnt <= 1){
                if (wCnt == 1){
                    deadCell[0] = firstBX;
                    deadCell[1] = firstBY;
                }
                return true;
            }
            else return false;
        }

    }

    public boolean canEat(int x, int y){
        if (A[y][x] == 0) return false;
        if (A[y][x] == white){

            if( y < N-2 && x> 1 && (A[y+1][x-1] == black || A[y+1][x-1] == BK) && A[y+2][x-2] == 0) return true;
            if( y < N-2 && x< N - 2 && (A[y+1][x+1] == black || A[y+1][x+1] == BK) && A[y+2][x+2] == 0) return true;
            if( y > 1 && x> 1 && (A[y-1][x-1] == black || A[y-1][x-1] == BK) && A[y-2][x-2] == 0) return true;
            if( y > 1 && x< N - 2 && (A[y-1][x+1] == black || A[y-1][x+1] == BK) && A[y-2][x+2] == 0) return true;
        }
        else if (A[y][x] == black){

            if( y < N-2 && x> 1 && (A[y+1][x-1] == white || A[y+1][x-1] == WK) && A[y+2][x-2] == 0) return true;
            if( y < N-2 && x< N - 2 && (A[y+1][x+1] == white || A[y+1][x+1] == WK) && A[y+2][x+2] == 0) return true;
            if( y > 1 && x> 1 && (A[y-1][x-1] == white || A[y-1][x-1] ==  WK) && A[y-2][x-2] == 0) return true;
            if( y > 1 && x< N - 2 && (A[y-1][x+1] == white || A[y-1][x+1] == WK) && A[y-2][x+2] == 0) return true;
        }
        else if (A[y][x] == WK){

            //проверка взятия по всем направлениям на любую дальность
            for (int dir = 0; dir<4; dir++){
                int dx = dir%2;
                int dy = dir/2;
                if (dx == 0) dx =-1;
                if (dy == 0) dy =-1;
                int curX = x+dx;
                int curY = y+dy;
                boolean success = false;
                while (curX >=0 && curX <N && curY >=0 && curY < N){
                    if (A[curY][curX] == white || A[curY][curX] == WK) break;
                    if (A[curY][curX] == 0){
                        curX +=dx;
                        curY +=dy;
                        continue;
                    }
                    if (A[curY][curX] == black || A[curY][curX] == BK){
                        if (curY+dy>=0 && curY+dy<N && curX+dx>=0 && curX+dx<N && A[curY+dy][curX+dx] == 0){
                            success = true;
                        }
                        break;
                    }
                }
                if (success) return true;
            }
            return false;
        }

        else{

            for (int dir = 0; dir<4; dir++){
                int dx = dir%2;
                int dy = dir/2;
                if (dx == 0) dx =-1;
                if (dy == 0) dy =-1;
                int curX = x+dx;
                int curY = y+dy;
                boolean success = false;
                while (curX >=0 && curX <N && curY >=0 && curY < N){
                    if (A[curY][curX] == black || A[curY][curX] == BK) break;
                    if (A[curY][curX] == 0){
                        curX +=dx;
                        curY +=dy;
                        continue;
                    }
                    if (A[curY][curX] == white || A[curY][curX] == WK){
                        if (curY+dy>=0 && curY+dy<N && curX+dx>=0 && curX+dx<N && A[curY+dy][curX+dx] == 0){
                            success = true;
                        }
                        break;
                    }
                }
                if (success) return true;
            }
            return false;
        }
        return false;
    }

    public boolean canEatSomeoneElse(int x, int y){ //может ли фишка съесть кого-то
        for (int i =0; i < N; i++){
            for (int j = 0; j < N; j++){
                if (y==i && x==j) continue;
                if (A[i][j] == 0) continue;
                if (A[y][x] == white || A[y][x] == WK){
                    if (A[i][j] == white || A[i][j] == WK){
                        if (canEat(j,i)) return true;
                    }
                }
                if (A[y][x] == black || A[y][x] == BK){
                    if (A[i][j] == black || A[i][j] == BK){
                        if (canEat(j,i)) return true;
                    }
                }
            }
        }
        return false;
    }
}