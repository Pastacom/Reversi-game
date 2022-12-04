public class Field {
    private boolean free;
    private boolean black;

    Field() {
        free = true;
    }

    Field(boolean free, boolean black) {
        this.free = free;
        this.black = black;
    }

    protected boolean isFree() {
        return free;
    }

    protected boolean isBlack() {
        return black;
    }

    protected void setBlack(boolean black) {
        this.black = black;
    }

    protected void putChip(boolean black) {
        setBlack(black);
        free = false;
    }

    @Override
    public String toString() {
        return isFree() ? " * " : isBlack() ? " x " : " o ";
    }
}
