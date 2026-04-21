package lab6.server.commands;

import lab6.common.models.SpaceMarine;
import lab6.server.ServerManager;

public class UpdateIdCommand extends Command {
    private static final long serialVersionUID = 1L;
    private SpaceMarine marine;

    public UpdateIdCommand(SpaceMarine marine) {
        super("Обновить значение элемента коллекции, id которого равен заданному");
        this.marine = marine;
    }

    @Override
    public String execute(Object context) {
        if (context instanceof ServerManager) {
            ServerManager manager = (ServerManager) context;

            boolean updated = manager.updateMarine(marine);
            if (updated) {
                return "Элемент с ID " + marine.getId() + " успешно обновлен.";
            } else {
                return "Элемент с ID " + marine.getId() + " не найден в коллекции.";
            }
        }
        return "Ошибка контекста выполнения";
    }
}