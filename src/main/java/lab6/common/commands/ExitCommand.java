package lab6.common.commands;

public class ExitCommand extends Command {
    private static final long serialVersionUID = 1L;

    public ExitCommand() {
        super("Завершить работу клиента");
    }

    @Override
    public String execute(Object context) {
        return "Соединение с сервером будет разорвано.";
    }
}
