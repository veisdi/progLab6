package lab6.common.models;

import java.io.Serializable;
import java.util.Objects;

/**
 * Класс для хранения координат
 */
public class Coordinates implements Serializable, Comparable<Coordinates> {
    private int x; // Значение поля должно быть больше -255
    private double y; // Максимальное значение поля: 457

    public Coordinates(int x, double y) {
        setX(x);
        setY(y);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        if (x <= -255) {
            throw new IllegalArgumentException("X должно быть больше -255");
        }
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        if (y > 457) {
            throw new IllegalArgumentException("Y не может быть больше 457");
        }
        this.y = y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return x == that.x && Double.compare(that.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public int compareTo(Coordinates other) {
        if (this.x != other.x) {
            return Integer.compare(this.x, other.x);
        }
        return Double.compare(this.y, other.y);
    }
}