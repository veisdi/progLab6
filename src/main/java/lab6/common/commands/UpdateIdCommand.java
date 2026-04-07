package lab6.common.commands;

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

            // Генерируем новый ID для обновленного объекта (если нужно)
            // Но обычно при обновлении мы сохраняем старый ID
            // Если требуется генерация нового - раскомментируй следующие строки:
            // long newId = SpaceMarine.generateId();
            // marine.setId(newId);

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