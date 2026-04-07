package lab6.common.commands;

import lab6.server.ServerManager;

public class GroupCountingByCreationDateCommand extends Command {
    private static final long serialVersionUID = 1L;

    public GroupCountingByCreationDateCommand() {
        super("Сгруппировать элементы коллекции по значению поля creationDate, вывести количество элементов в каждой группе");
    }

    @Override
    public String execute(Object context) {
        if (context instanceof ServerManager) {
            ServerManager manager = (ServerManager) context;
            return manager.groupCountingByCreationDate();
        }
        return "Ошибка контекста выполнения";
    }
}