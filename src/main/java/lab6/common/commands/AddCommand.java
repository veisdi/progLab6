package lab6.common.commands;

import lab6.common.models.SpaceMarine;
import lab6.server.ServerManager;

import java.time.ZonedDateTime;

public class AddCommand extends Command {
    private static final long serialVersionUID = 1L;
    private SpaceMarine marine;

    public AddCommand(SpaceMarine marine) {
        super("Добавить нового морпеха в коллекцию");
        this.marine = marine;
    }

    public SpaceMarine getMarine() {
        return marine;
    }

    @Override
    public String execute(Object context) {
        if (context instanceof ServerManager) {
            ServerManager manager = (ServerManager) context;

            long newId = SpaceMarine.generateId();
            marine.setId(newId);

            // Установка даты создания (если она еще не установлена)
            if (marine.getCreationDate() == null) {
                marine.setCreationDate(ZonedDateTime.now());
            }

            manager.addMarine(marine);
            return "Морпех успешно добавлен с ID: " + newId;
        }
        return "Ошибка контекста выполнения";
    }
}
