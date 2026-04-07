package lab6.common.commands;

public class HelpCommand extends Command {
    private static final long serialVersionUID = 1L;

    public HelpCommand() {
        super("Вывести справку по доступным командам");
    }

    @Override
    public String execute(Object context) {
        return """
                Доступные команды:
                help - Вывести справку по доступным командам
                info - Вывести информацию о коллекции (тип, дата инициализации, количество элементов)
                show - Вывести все элементы коллекции в строковом представлении
                add {element} - Добавить новый элемент в коллекцию
                update_id {element} - Обновить значение элемента коллекции, id которого равен заданному
                remove_by_id {id} - Удалить элемент из коллекции по его id
                clear - Очистить коллекцию
                exit - Завершить программу (без сохранения в файл)
                group_counting_by_creation_date - Сгруппировать элементы коллекции по значению поля creationDate, вывести количество элементов в каждой группе
                filter_starts_with_achievements {achievements} - Вывести элементы, значение поля achievements которых начинается с заданной подстроки
                """;
    }
}
