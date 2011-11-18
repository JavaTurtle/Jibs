package net.sourceforge.jibs.command;

import net.sourceforge.jibs.gui.JibsMessages;
import net.sourceforge.jibs.server.Player;
import net.sourceforge.jibs.server.Server;
import net.sourceforge.jibs.util.JibsToggle;
import net.sourceforge.jibs.util.JibsWriter;

/**
 * The Toggle command.
 */
public class Toggle_Command implements JibsCommand {
    private static int mapToggle(String[] toggleMap, String key) {
        for (int i = 0; i < toggleMap.length; i++) {
            if (toggleMap[i].equals(key)) {
                return i;
            }
        }

        return -1;
    }

    public boolean execute(Server server, Player player,
                           String strArgs, String[] args) {
        String[] toggleMap = new String[] {
                                 "allowpip", "autoboard", "autodouble",
                                 "automove", "bell", "crawford", "double",
                                 "greedy", "moreboards", "moves", "notify",
                                 "ratings", "ready", "report", "silent",
                                 "telnet", "wrap"
                             };
        JibsMessages jibsMessages = server.getJibsMessages();
        JibsWriter out = player.getOutputStream();
        JibsToggle jibsToggle = player.getJibsToggles();

        if (args.length <= 1) {
            out.println("The current settings are:");

            for (int i = 0; i < toggleMap.length; i++) {
                String key = toggleMap[i];
                Boolean value = jibsToggle.get(key);
                StringBuffer sBuffer = new StringBuffer();
                StringBuffer sKey = new StringBuffer();

                sKey.append(key);
                sKey.append("        ");
                sBuffer.append(sKey.toString().substring(0, 12));
                sBuffer.append("\t");

                if (value.booleanValue() == true) {
                    sBuffer.append("YES");
                } else {
                    sBuffer.append("NO");
                }

                out.println(sBuffer.toString());
            }
        }

        for (int i = 1; i < args.length; i++) {
            String key = args[i];
            int mode = Toggle_Command.mapToggle(toggleMap, key);
            String msg = null;
            Boolean value = null;
            boolean newValue = false;

            switch (mode) {
            case 0: // allowpip
                value = jibsToggle.get(key);
                newValue = !value.booleanValue();
                jibsToggle.set(key, Boolean.valueOf(newValue));

                if (newValue == true) {
                    msg = jibsMessages.convert("m_toggle_allowpip_on");
                    out.println(msg);
                } else {
                    msg = jibsMessages.convert("m_toggle_allowpip_off");
                    out.println(msg);
                }

                break;

            case 1: // autoboard
                value = jibsToggle.get(key);
                newValue = !value.booleanValue();
                jibsToggle.set(key, Boolean.valueOf(newValue));

                if (newValue == true) {
                    msg = jibsMessages.convert("m_toggle_autoboard_on");
                    out.println(msg);
                } else {
                    msg = jibsMessages.convert("m_toggle_autoboard_off");
                    out.println(msg);
                }

                break;

            case 2: // autodouble
                value = jibsToggle.get(key);
                newValue = !value.booleanValue();
                jibsToggle.set(key, Boolean.valueOf(newValue));

                if (newValue == true) {
                    msg = jibsMessages.convert("m_toggle_autodouble_on");
                    out.println(msg);
                } else {
                    msg = jibsMessages.convert("m_toggle_autodouble_off");
                    out.println(msg);
                }

                break;

            case 3: // automove
                value = jibsToggle.get(key);
                newValue = !value.booleanValue();
                jibsToggle.set(key, Boolean.valueOf(newValue));

                if (newValue == true) {
                    msg = jibsMessages.convert("m_toggle_automove_on");
                    out.println(msg);
                } else {
                    msg = jibsMessages.convert("m_toggle_automove_off");
                    out.println(msg);
                }

                break;

            case 4: // bell
                value = jibsToggle.get(key);
                newValue = !value.booleanValue();
                jibsToggle.set(key, Boolean.valueOf(newValue));

                if (newValue == true) {
                    msg = jibsMessages.convert("m_toggle_bell_on");
                    out.println(msg);
                } else {
                    msg = jibsMessages.convert("m_toggle_bell_off");
                    out.println(msg);
                }

                break;

            case 5: // crawford
                value = jibsToggle.get(key);
                newValue = !value.booleanValue();
                jibsToggle.set(key, Boolean.valueOf(newValue));

                if (newValue == true) {
                    msg = jibsMessages.convert("m_toggle_crawford_on");
                    out.println(msg);
                } else {
                    msg = jibsMessages.convert("m_toggle_crawford_off");
                    out.println(msg);
                }

                break;

            case 6: // double
                value = jibsToggle.get(key);
                newValue = !value.booleanValue();
                jibsToggle.set(key, Boolean.valueOf(newValue));

                if (newValue == true) {
                    msg = jibsMessages.convert("m_toggle_double_off");
                    out.println(msg);
                } else {
                    msg = jibsMessages.convert("m_toggle_double_on");
                    out.println(msg);
                }

                break;

            case 7: // greedy
                value = jibsToggle.get(key);
                newValue = !value.booleanValue();
                jibsToggle.set(key, Boolean.valueOf(newValue));

                if (newValue == true) {
                    msg = jibsMessages.convert("m_toggle_greedy_on");
                    out.println(msg);
                } else {
                    msg = jibsMessages.convert("m_toggle_greedy_off");
                    out.println(msg);
                }

                break;

            case 8: // moreboards
                value = jibsToggle.get(key);
                newValue = !value.booleanValue();
                jibsToggle.set(key, Boolean.valueOf(newValue));

                if (newValue == true) {
                    msg = jibsMessages.convert("m_toggle_moreboards_on");
                    out.println(msg);
                } else {
                    msg = jibsMessages.convert("m_toggle_moreboards_off");
                    out.println(msg);
                }

                player.informToggleChange();

                break;

            case 9: // moves
                value = jibsToggle.get(key);
                newValue = !value.booleanValue();
                jibsToggle.set(key, Boolean.valueOf(newValue));

                if (newValue == true) {
                    msg = jibsMessages.convert("m_toggle_moves_on");
                    out.println(msg);
                } else {
                    msg = jibsMessages.convert("m_toggle_moves_off");
                    out.println(msg);
                }

                break;

            case 10: // notify
                value = jibsToggle.get(key);
                newValue = !value.booleanValue();
                jibsToggle.set(key, Boolean.valueOf(newValue));

                if (newValue == true) {
                    msg = jibsMessages.convert("m_toggle_notify_on");
                    out.println(msg);
                } else {
                    msg = jibsMessages.convert("m_toggle_notify_off");
                    out.println(msg);
                }

                break;

            case 11: // ratings
                value = jibsToggle.get(key);
                newValue = !value.booleanValue();
                jibsToggle.set(key, Boolean.valueOf(newValue));

                if (newValue == true) {
                    msg = jibsMessages.convert("m_toggle_ratings_on");
                    out.println(msg);
                } else {
                    msg = jibsMessages.convert("m_toggle_ratings_off");
                    out.println(msg);
                }

                break;

            case 12: // ready
                value = jibsToggle.get(key);
                newValue = !value.booleanValue();
                jibsToggle.set(key, Boolean.valueOf(newValue));

                if (newValue == true) {
                    msg = jibsMessages.convert("m_toggle_ready_on");
                    out.println(msg);
                } else {
                    msg = jibsMessages.convert("m_toggle_ready_off");
                    out.println(msg);
                }

                player.informToggleChange();

                break;

            case 13: // report
                value = jibsToggle.get(key);
                newValue = !value.booleanValue();
                jibsToggle.set(key, Boolean.valueOf(newValue));

                if (newValue == true) {
                    msg = jibsMessages.convert("m_toggle_report_on");
                    out.println(msg);
                } else {
                    msg = jibsMessages.convert("m_toggle_report_off");
                    out.println(msg);
                }

                break;

            case 14: // silent
                value = jibsToggle.get(key);
                newValue = !value.booleanValue();
                jibsToggle.set(key, Boolean.valueOf(newValue));

                if (newValue == true) {
                    msg = jibsMessages.convert("m_toggle_silent_on");
                    out.println(msg);
                } else {
                    msg = jibsMessages.convert("m_toggle_silent_off");
                    out.println(msg);
                }

                break;

            case 15: // telnet
                value = jibsToggle.get(key);
                newValue = !value.booleanValue();
                jibsToggle.set(key, Boolean.valueOf(newValue));

                if (newValue == true) {
                    msg = jibsMessages.convert("m_toggle_telnet_on");
                    out.println(msg);
                } else {
                    msg = jibsMessages.convert("m_toggle_telnet_off");
                    out.println(msg);
                }

                break;

            case 16: // wrap
                value = jibsToggle.get(key);
                newValue = !value.booleanValue();
                jibsToggle.set(key, Boolean.valueOf(newValue));

                if (newValue == true) {
                    msg = jibsMessages.convert("m_toggle_wrap_on");
                    out.println(msg);
                } else {
                    msg = jibsMessages.convert("m_toggle_wrap_off");
                    out.println(msg);
                }

                break;

            default:

                // m_toggle_unknown** Don't know how to toggle %0
                Object[] obj = new Object[] { key };
                msg = jibsMessages.convert("m_toggle_unknown", obj);
                out.println(msg);

                break;
            }
        }

        return true;
    }
}
