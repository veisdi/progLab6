package lab6.server.commands;

import lab6.server.ServerManager;

public class ShowCommand extends Command {
    private static final long serialVersionUID = 1L;

    public ShowCommand() {
        super("Вывести все элементы коллекции в строковом представлении");
    }

    @Override
    public String execute(Object context) {
        if (context instanceof ServerManager) {
            ServerManager manager = (ServerManager) context;
            return manager.show(); // Вызываем метод из менеджера
        }
        return "Ошибка контекста выполнения";
    }
}