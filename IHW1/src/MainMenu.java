import java.util.InputMismatchException;
import java.util.Scanner;

public class MainMenu {
    private int bestScoreWhite = 0;
    private int bestScoreBlack = 0;
    private static final Scanner scanner = new Scanner(System.in);

    protected void run() {
        short command = 0;
        do {
            do {
                System.out.printf("""
                        Консольная игра "Реверси"
                        Лучший счет за черных: %d
                        Лучший счет за белых: %d
                                        
                        1. Новая игра против легкого ИИ.
                        2. Новая игра против сложного ИИ.
                        3. Новая игра против игрока.
                        4. Выход
                        Введите лишь один символ: цифру от 1 до 4.
                        """, bestScoreBlack, bestScoreWhite);
                System.out.print(">> ");
                try {
                    String string = scanner.nextLine();
                    Scanner scan = new Scanner(string);
                    command = scan.nextShort();
                } catch (InputMismatchException exception) {
                    System.out.println("\n***Введен некорректный формат данных.***\n");
                }
            } while (command < 1 || 4 < command);
            if (command == 4) {
                System.out.println("Заканчиваем работу программы.");
                continue;
            }
            Game game = new Game(command);
            Integer[] scores = game.startNewGame();
            bestScoreBlack = Math.max(bestScoreBlack, scores[0]);
            bestScoreWhite = Math.max(bestScoreWhite, scores[1]);
        } while (command != 4);
    }
}
