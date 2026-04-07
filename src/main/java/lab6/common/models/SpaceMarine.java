package lab6.common.models;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

public class SpaceMarine implements Comparable<SpaceMarine>, Serializable {
    private static final long serialVersionUID = 34L;

    // Генератор ID
    private static long nextId = 1;

    private Long id; // Убрал final, чтобы сервер мог установить ID после десериализации
    private String name;
    private Coordinates coordinates;
    private ZonedDateTime creationDate;
    private long health;
    private Boolean loyal;
    private String achievements;
    private MeleeWeapon meleeWeapon;
    private Chapter chapter;

    // Конструктор для загрузки из файла (с известным ID)
    public SpaceMarine(Long id, String name, Coordinates coordinates, ZonedDateTime creationDate,
                       long health, Boolean loyal, String achievements, MeleeWeapon meleeWeapon, Chapter chapter) {
        if (id == null) throw new IllegalArgumentException("ID не может быть null");
        this.id = id;
        initFields(name, coordinates, creationDate, health, loyal, achievements, meleeWeapon, chapter);

        // Обновляем счетчик, если загруженный ID больше текущего
        if (this.id >= nextId) {
            nextId = this.id + 1;
        }
    }

    // Конструктор для создания нового объекта (клиентом) - БЕЗ ID и ДАТЫ
    public SpaceMarine(String name, Coordinates coordinates, long health, Boolean loyal,
                       String achievements, MeleeWeapon meleeWeapon, Chapter chapter) {
        this.id = null; // ID будет установлен сервером
        this.creationDate = null; // Дата будет установлена сервером
        initFields(name, coordinates, null, health, loyal, achievements, meleeWeapon, chapter);
    }

    private void initFields(String name, Coordinates coordinates, ZonedDateTime creationDate,
                            long health, Boolean loyal, String achievements, MeleeWeapon meleeWeapon, Chapter chapter) {
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("Имя не может быть пустым");
        this.name = name.trim();

        if (coordinates == null) throw new IllegalArgumentException("Координаты не могут быть null");
        this.coordinates = coordinates;

        this.creationDate = creationDate;

        if (health <= 0) throw new IllegalArgumentException("Здоровье должно быть > 0");
        this.health = health;

        if (loyal == null) throw new IllegalArgumentException("Поле loyal не может быть null");
        this.loyal = loyal;

        this.achievements = achievements;
        this.meleeWeapon = meleeWeapon;
        this.chapter = chapter;
    }

    /**
     * Генерирует новый уникальный ID. Вызывается СЕРВЕРОМ перед добавлением.
     */
    public static synchronized long generateId() {
        return nextId++;
    }

    public static synchronized void setNextId(long id) {
        if (id >= nextId) {
            nextId = id;
        }
    }

    public static long getNextId() {
        return nextId;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }

    // Метод для установки ID (вызывается сервером)
    public void setId(Long id) {
        if (id == null) throw new IllegalArgumentException("ID не может быть null");
        this.id = id;
    }

    public String getName() { return name; }
    public Coordinates getCoordinates() { return coordinates; }
    public ZonedDateTime getCreationDate() { return creationDate; }

    // Метод для установки даты (вызывается сервером)
    public void setCreationDate(ZonedDateTime date) {
        this.creationDate = date;
    }

    public long getHealth() { return health; }
    public Boolean getLoyal() { return loyal; }
    public String getAchievements() { return achievements; }
    public MeleeWeapon getMeleeWeapon() { return meleeWeapon; }
    public Chapter getChapter() { return chapter; }

    // Сеттеры для остальных полей (без изменений)
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("Имя не может быть пустым");
        this.name = name.trim();
    }
    public void setCoordinates(Coordinates coordinates) {
        if (coordinates == null) throw new IllegalArgumentException("Координаты не могут быть null");
        this.coordinates = coordinates;
    }
    public void setHealth(long health) {
        if (health <= 0) throw new IllegalArgumentException("Здоровье должно быть > 0");
        this.health = health;
    }
    public void setLoyal(Boolean loyal) {
        if (loyal == null) throw new IllegalArgumentException("Поле loyal не может быть null");
        this.loyal = loyal;
    }
    public void setAchievements(String achievements) { this.achievements = achievements; }
    public void setMeleeWeapon(MeleeWeapon meleeWeapon) { this.meleeWeapon = meleeWeapon; }
    public void setChapter(Chapter chapter) { this.chapter = chapter; }

    @Override
    public String toString() {
        return "SpaceMarine{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate +
                ", health=" + health +
                ", loyal=" + loyal +
                ", achievements='" + achievements + '\'' +
                ", meleeWeapon=" + meleeWeapon +
                ", chapter=" + chapter +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpaceMarine that = (SpaceMarine) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(SpaceMarine other) {
        return this.id.compareTo(other.id);
    }
}