package com.ogawa.fico.command;

import com.ogawa.fico.command.argument.CommandWithNoArgs;
import com.ogawa.fico.db.Model;
import com.ogawa.fico.exception.ModelError;

public class CreateCommand extends DatabaseModelCommand implements CommandWithNoArgs {

    static final public String KEY_WORD = "create";

    CreateCommand(String[] commandArguments) {
        super(commandArguments);
    }

    @Override
    public String getName() {
        return KEY_WORD;
    }

    public void execute() {

        try {

            Model model = new Model(getConnection());
            model.create();
            model.close();

        } catch (ModelError modelError) {
            throw modelError;
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

}
