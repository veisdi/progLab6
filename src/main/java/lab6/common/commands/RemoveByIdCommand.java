package lab6.common.commands;

import lab6.server.ServerManager;

public class RemoveByIdCommand extends Command {
    private static final long serialVersionUID = 1L;
    private long id;

    public RemoveByIdCommand(long id) {
        super("Удалить элемент из коллекции по его id");
        this.id = id;
    }

    @Override
    public String execute(Object context) {
        if (context instanceof ServerManager) {
            ServerManager manager = (ServerManager) context;
            boolean removed = manager.removeById(id);
            if (removed) {
                return "Элемент с ID " + id + " успешно удален.";
            } else {
                return "Элемент с ID " + id + " не найден в коллекции.";
            }
        }
        return "Ошибка контекста выполнения";
    }
}
