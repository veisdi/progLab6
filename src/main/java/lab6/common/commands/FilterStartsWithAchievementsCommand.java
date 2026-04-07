package lab6.common.commands;

import lab6.server.ServerManager;

public class FilterStartsWithAchievementsCommand extends Command {
    private static final long serialVersionUID = 1L;
    private String prefix;

    public FilterStartsWithAchievementsCommand(String prefix) {
        super("Вывести элементы, значение поля achievements которых начинается с заданной подстроки");
        this.prefix = prefix;
    }

    @Override
    public String execute(Object context) {
        if (context instanceof ServerManager) {
            ServerManager manager = (ServerManager) context;
            return manager.filterStartsWithAchievements(prefix);
        }
        return "Ошибка контекста выполнения";
    }
}
