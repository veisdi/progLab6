package lab6.common.commands;

import java.io.Serializable;

public abstract class Command implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String description;

    public Command(String description){
        this.description = description;
    }

    public String getDescription(){
        return description;
    }

    public abstract String execute(Object context);

}
