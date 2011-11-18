package net.sourceforge.jibs.command;

import net.sourceforge.jibs.server.Player;
import net.sourceforge.jibs.server.Server;

public class Test_Command implements JibsCommand {
    public boolean execute(Server server, Player player,
                           String strArgs, String[] args) {
        return true;
    }
}
