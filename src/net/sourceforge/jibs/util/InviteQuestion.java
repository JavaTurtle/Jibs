package net.sourceforge.jibs.util;

import net.sourceforge.jibs.backgammon.JibsMatch;
import net.sourceforge.jibs.server.JibsQuestion;
import net.sourceforge.jibs.server.Player;

public class InviteQuestion implements JibsQuestion {
    private int matchLength;
    private Player inviter;
    private Player invitee;
    private JibsMatch matchVersion;

    public InviteQuestion(int matchLength, Player player, Player opponent,
                          JibsMatch matchVersion) {
        this.matchLength = matchLength;
        this.inviter = player;
        this.invitee = opponent;
        this.matchVersion = matchVersion;
    }

    public Player getInviter() {
        return inviter;
    }

    public int getMatchLength() {
        return matchLength;
    }

    public Player getInvitee() {
        return invitee;
    }

    public JibsMatch getMatchVersion() {
        return matchVersion;
    }

    public void setMatchVersion(JibsMatch matchVersion) {
        this.matchVersion = matchVersion;
    }
}
