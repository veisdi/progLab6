package lab6.common.commands;

public class ExitCommand extends Command {
    private static final long serialVersionUID = 1L;

    public ExitCommand() {
        super("Завершить работу клиента");
    }

    @Override
    public String execute(Object context) {
        // Серверу не обязательно делать что-то специальное,
        // так как клиент сам закроет соединение после получения ответа.
        return "Соединение с сервером будет разорвано.";
    }
}
