import java.util.ArrayList;

public class Board {
    protected Field[][] fields;
    protected Field[][] fieldsBackUp = null;

    Board() {
        fields = new Field[8][8];
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                fields[i][j] = new Field();
            }
        }
        fields[4][3].putChip(false);
        fields[3][4].putChip(false);
        fields[3][3].putChip(true);
        fields[4][4].putChip(true);
    }

    protected Field[][] MakeBackUp() {
        Field[][] array = new Field[8][8];
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                array[i][j] = new Field(fields[i][j].isFree(), fields[i][j].isBlack());
            }
        }
        return array;
    }

    protected void changeColors(int row, int column, boolean black) {
        fields[row][column].putChip(black);
        ArrayList<Field> buffer = new ArrayList<>();
        for (int i = -1; i < 2; ++i) {
            for (int j = -1; j < 2; ++j) {
                if (i == 0 && j == 0) {
                    continue;
                }
                int shiftRow = row;
                int shiftColumn = column;
                int counter = 0;
                do {
                    shiftRow += i;
                    shiftColumn += j;
                    if (shiftRow < 0 || 7 < shiftRow || shiftColumn < 0 || 7 < shiftColumn) {
                        continue;
                    }
                    if (fields[shiftRow][shiftColumn].isFree()) {
                        break;
                    }
                    if (black == fields[shiftRow][shiftColumn].isBlack()) {
                        if (!fields[shiftRow - i][shiftColumn - j].isFree()) {
                            for (Field field : buffer) {
                                field.setBlack(black);
                            }
                            break;
                        }
                    } else {
                        if (!fields[shiftRow - i][shiftColumn - j].isFree()) {
                            buffer.add(counter++, fields[shiftRow][shiftColumn]);
                        }
                    }
                } while (0 <= shiftRow && shiftRow <= 7 && 0 <= shiftColumn && shiftColumn <= 7);
                buffer.clear();
            }
        }
    }

    protected boolean availableField(int row, int column, boolean black) {
        if (row < 0 || 7 < row) {
            throw new ArrayIndexOutOfBoundsException("Неверное значение для строки поля." +
                    " Попробуйте еще раз!");
        }
        if (column < 0 || 7 < column) {
            throw new ArrayIndexOutOfBoundsException("Неверное значение для столбца поля." +
                    " Попробуйте еще раз!");
        }
        Field field = fields[row][column];
        if (!field.isFree()) {
            return false;
        }
        for (int i = -1; i < 2; ++i) {
            for (int j = -1; j < 2; ++j) {
                int shiftRow = row + i;
                int shiftColumn = column + j;
                if (shiftRow < 0 || 7 < shiftRow || shiftColumn < 0 || 7 < shiftColumn) {
                    continue;
                }
                if (fields[shiftRow][shiftColumn].isFree()) {
                    continue;
                }
                if (black != fields[shiftRow][shiftColumn].isBlack()) {
                    do {
                        shiftRow += i;
                        shiftColumn += j;
                        if (shiftRow < 0 || 7 < shiftRow || shiftColumn < 0 || 7 < shiftColumn) {
                            continue;
                        }
                        if (fields[shiftRow][shiftColumn].isFree()) {
                            break;
                        }
                        if (black == fields[shiftRow][shiftColumn].isBlack()) {
                            return true;
                        }
                    } while (0 <= shiftRow && shiftRow <= 7 && 0 <= shiftColumn && shiftColumn <= 7);
                }
            }
        }
        return false;
    }

    private double getFieldValue(int row, int column) {
        if (row == 0 || row == 7) {
            if (column == 0 || column == 7) {
                return 0.8;
            } else {
                return 0.4;
            }
        } else if (column == 0 || column == 7) {
            return 0.4;
        } else {
            return 0;
        }
    }

    private double getAttackedFieldValue(int row, int column) {
        if (row == 0 || row == 7) {
            if (column == 0 || column == 7) {
                return 0;
            } else {
                return 2;
            }
        } else if (column == 0 || column == 7) {
            return 2;
        } else {
            return 1;
        }
    }

    protected double computeFieldValue(int row, int column, boolean black) {
        double result = 0;
        ArrayList<Integer> buffer = new ArrayList<>();
        for (int i = -1; i < 2; ++i) {
            for (int j = -1; j < 2; ++j) {
                if (i == 0 && j == 0) {
                    continue;
                }
                int shiftRow = row;
                int shiftColumn = column;
                int counter = 0;
                do {
                    shiftRow += i;
                    shiftColumn += j;
                    if (shiftRow < 0 || 7 < shiftRow || shiftColumn < 0 || 7 < shiftColumn) {
                        continue;
                    }
                    if (fields[shiftRow][shiftColumn].isFree()) {
                        break;
                    }
                    if (black == fields[shiftRow][shiftColumn].isBlack()) {
                        if (!fields[shiftRow - i][shiftColumn - j].isFree()) {
                            for (int iter = 0; iter < buffer.size(); iter += 2) {
                                result += getAttackedFieldValue(buffer.get(iter), buffer.get(iter + 1));
                            }
                            break;
                        }
                    } else {
                        if (!fields[shiftRow - i][shiftColumn - j].isFree() ||
                                (shiftRow - i == row && shiftColumn - j == column)) {
                            buffer.add(counter++, shiftRow);
                            buffer.add(counter++, shiftColumn);
                        }
                    }
                } while (0 <= shiftRow && shiftRow <= 7 && 0 <= shiftColumn && shiftColumn <= 7);
                buffer.clear();
            }
        }
        return result + getFieldValue(row, column);
    }

    protected int countChips(boolean black) {
        int counter = 0;
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                if (!fields[i][j].isFree() && fields[i][j].isBlack() == black) {
                    ++counter;
                }
            }
        }
        return counter;
    }

    protected void printBoard(boolean black) {
        System.out.println("+ —— —— —— —— —— —— —— —— —— +");
        for (int i = 0; i < 8; ++i) {
            System.out.printf("%d |", i + 1);
            for (int j = 0; j < 8; ++j) {
                if (" * ".equals(fields[i][j].toString())) {
                    if (availableField(i, j, black)) {
                        System.out.print(" # ");
                        continue;
                    }
                }
                System.out.print(fields[i][j]);
            }
            System.out.println("|");
        }
        System.out.println("+ —— —— —— —— —— —— —— —— —— +");
        System.out.print("   ");
        for (int i = 0; i < 8; ++i) {
            System.out.printf(" %d ", i + 1);
        }
        System.out.println();
    }
}
