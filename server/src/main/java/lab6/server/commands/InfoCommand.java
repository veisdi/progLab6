package lab6.server.commands;

import lab6.server.ServerManager;

public class InfoCommand extends Command {
    private static final long serialVersionUID = 1L;

    public InfoCommand() {
        super("Вывести информацию о коллекции (тип, дата инициализации, количество элементов)");
    }

    @Override
    public String execute(Object context) {
        if (context instanceof ServerManager) {
            ServerManager manager = (ServerManager) context;
            return manager.getInfo(); // Вызываем метод из менеджера
        }
        return "Ошибка контекста выполнения";
    }
}