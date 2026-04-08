package lab6.common.commands;

public class ExecuteScriptCommand extends Command {
    private static final long serialVersionUID = 1L;
    private String fileName;

    public ExecuteScriptCommand(String fileName) {
        super("Выполнить скрипт из указанного файла");
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public String execute(Object context) {
        // Эта команда не выполняется на сервере напрямую!
        // Её обработка происходит в ClientManager перед отправкой на сервер.
        // Но чтобы интерфейс был единым, вернем заглушку или пустую строку.
        return "Скрипт обрабатывается на клиенте.";
    }
}