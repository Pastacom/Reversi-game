import java.util.NoSuchElementException;
import java.util.Scanner;

public class Game {
    private final Board board;

    // 0 - easy PVE
    // 1 - hard PVE
    // 2 - PVP
    private final short gameMode;
    private boolean playingBlack;
    private boolean blacksTurn;
    private static final Scanner scanner = new Scanner(System.in);

    Game(short gameMode) {
        board = new Board();
        board.fieldsBackUp = null;
        blacksTurn = true;
        this.gameMode = gameMode;
    }

    public Integer[] startNewGame() {
        if (gameMode == 3) {
            playerVSPlayer();
            return new Integer[]{board.countChips(true), board.countChips(false)};
        } else {
            playerVSCPU();
            if (playingBlack) {
                return new Integer[]{board.countChips(true), 0};
            } else {
                return new Integer[]{0, board.countChips(false)};
            }
        }

    }

    private void pCommand(String command) {
        Scanner scan = new Scanner(command.substring(1));
        try {
            playerMove(scan.nextInt() - 1, scan.nextInt() - 1);
            blacksTurn = !blacksTurn;
        } catch (ArrayIndexOutOfBoundsException exception) {
            System.out.println("\n***" + exception.getMessage() + "***\n");
        } catch (NoSuchElementException exception) {
            System.out.println("\n***Введен некорректный формат данных.***\n");
        }
    }

    private void rCommand() {
        if (board.fieldsBackUp != null) {
            board.fields = board.fieldsBackUp;
            board.fieldsBackUp = null;
        } else {
            System.out.println("""

                    ***Невозможно отменить ход, потому что Вами не было сделано
                     ни одного хода, либо вы уже отменяли ход 1 раз.***
                    """);
        }
    }

    private void playerVSPlayer() {
        do {
            board.printBoard(blacksTurn);
            String command;
            do {
                System.out.printf("""
                        Ход %s!
                        Напишите p <num1> <num2> для установки фишки на строку num1 и столбец num2.
                        """, blacksTurn ? "черных" : "белых");
                System.out.print(">> ");
                command = scanner.nextLine();
            } while (!"p".equals(command.substring(0, 1)));
            pCommand(command);
        } while (EndGame());
    }

    private void playerVSCPU() {
        chooseSide();
        do {
            board.printBoard(blacksTurn);
            if (playingBlack == blacksTurn) {
                String command;
                do {
                    System.out.print("""
                            Ваш ход!
                            Напишите p <num1> <num2> для установки фишки на строку num1 и столбец num2.
                            Напишите r для отмены хода.
                            """);
                    System.out.print(">> ");
                    command = scanner.nextLine();
                } while (!"p".equals(command.substring(0, 1)) && !"r".equals(command));
                if ("r".equals(command)) {
                    rCommand();
                } else {
                    pCommand(command);
                }
            } else {
                botsMove();
                blacksTurn = !blacksTurn;
            }
        } while (EndGame());
    }

    private void playerMove(int row, int column) {
        if (board.availableField(row, column, blacksTurn)) {
            board.fieldsBackUp = board.MakeBackUp();
            board.changeColors(row, column, blacksTurn);
        } else {
            throw new ArrayIndexOutOfBoundsException("По правилам вы не можете" +
                    " поставить фишку на эту позицию!");
        }
    }

    private Integer[] FindBestMove(boolean black) {
        double maximumValue = 0;
        int bestRow = 0;
        int bestColumn = 0;
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                if (board.availableField(i, j, black)) {
                    double value = board.computeFieldValue(i, j, black);
                    if (value > maximumValue) {
                        maximumValue = value;
                        bestRow = i;
                        bestColumn = j;
                    }
                }
            }
        }
        return new Integer[]{bestRow, bestColumn};
    }

    private void botsMove() {
        if (gameMode == 1) {
            Integer[] result = FindBestMove(!playingBlack);
            board.changeColors(result[0], result[1], !playingBlack);
            System.out.printf("\nЛегкий ИИ ходит на поле %d,%d\n\n", result[0] + 1, result[1] + 1);
        } else {
            double maximumValue = -2048;
            int bestRow = 0;
            int bestColumn = 0;
            for (int i = 0; i < 8; ++i) {
                for (int j = 0; j < 8; ++j) {
                    if (board.availableField(i, j, !playingBlack)) {
                        double value = board.computeFieldValue(i, j, !playingBlack);
                        Field[][] backUp = board.MakeBackUp();
                        board.changeColors(i, j, !playingBlack);
                        Integer[] result = FindBestMove(playingBlack);
                        value -= board.computeFieldValue(result[0], result[1], playingBlack);
                        board.fields = backUp;
                        if (value > maximumValue) {
                            maximumValue = value;
                            bestRow = i;
                            bestColumn = j;
                        }
                    }
                }
            }
            board.changeColors(bestRow, bestColumn, !playingBlack);
            System.out.printf("\nПродвинутый ИИ ходит на поле %d,%d\n\n", bestRow + 1, bestColumn + 1);
        }
    }

    private boolean EndGame() {
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                if (board.availableField(i, j, blacksTurn)) {
                    return true;
                }
            }
        }
        board.printBoard(playingBlack);
        System.out.println("Игра окончена!");
        System.out.printf("Черные: %d\n", board.countChips(true));
        System.out.printf("Белые: %d\n", board.countChips(false));
        return false;
    }

    private void chooseSide() {
        String command;
        do {
            System.out.print("""
                    Выберите цвет за который хотите играть:
                    x - черные.
                    o - белые.
                    Введите лишь один символ.
                    """);
            System.out.print(">> ");
            command = scanner.nextLine();
        } while (!"x".equals(command) && !"o".equals(command));
        playingBlack = "x".equals(command);
    }
}
