package lab6.common.models;

import java.io.Serializable;
import java.util.Objects;


/**
 * Класс для хранения информации о главе
 */
public class Chapter implements Serializable, Comparable<Chapter> {
    private String name; // Поле не может быть null, Строка не может быть пустой
    private String world; // Поле может быть null

    public Chapter(String name, String world) {
        setName(name);
        this.world = world;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name не может быть null или пустым");
        }
        this.name = name;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    @Override
    public String toString() {
        return "Chapter{name='" + name + "', world='" + world + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chapter chapter = (Chapter) o;
        return Objects.equals(name, chapter.name) &&
                Objects.equals(world, chapter.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, world);
    }

    @Override
    public int compareTo(Chapter other) {
        if (other == null) return 1;
        int nameCompare = this.name.compareTo(other.name);
        if (nameCompare != 0) return nameCompare;
        if (this.world == null && other.world == null) return 0;
        if (this.world == null) return -1;
        if (other.world == null) return 1;
        return this.world.compareTo(other.world);
    }
}