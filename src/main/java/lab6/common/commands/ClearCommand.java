package lab6.common.commands;

import lab6.server.ServerManager;

public class ClearCommand extends Command {
    private static final long serialVersionUID = 1L;

    public ClearCommand() {
        super("Очистить коллекцию");
    }

    @Override
    public String execute(Object context) {
        if (context instanceof ServerManager) {
            ServerManager manager = (ServerManager) context;
            manager.clearCollection();
            return "Коллекция успешно очищена.";
        }
        return "Ошибка контекста выполнения";
    }
}
