package net.sourceforge.jibs.command;

import net.sourceforge.jibs.backgammon.JibsGame;
import net.sourceforge.jibs.backgammon.JibsMatch;
import net.sourceforge.jibs.gui.JibsMessages;
import net.sourceforge.jibs.gui.JibsTextArea;
import net.sourceforge.jibs.server.ClientWorker;
import net.sourceforge.jibs.server.JibsQuestion;
import net.sourceforge.jibs.server.Player;
import net.sourceforge.jibs.server.Server;
import net.sourceforge.jibs.util.InviteQuestion;
import net.sourceforge.jibs.util.JibsWriter;
import net.sourceforge.jibs.util.ResumeQuestion;
import net.sourceforge.jibs.util.SavedGameParam;

/**
 * The Invite command.
 */
public class Invite_Command implements JibsCommand {
	public boolean execute(Server server, Player player, String strArgs,
			String[] args) {
		String msg = null;
		JibsWriter out = player.getOutputStream();
		JibsMessages jibsMessages = server.getJibsMessages();

		if (args.length <= 1) {
			// m_invite_who=** invite who?
			msg = jibsMessages.convert("m_invite_who");

			out.println(msg);

			return true;
		}

		if (args[1].equals(player.getName())) {
			// m_invite_yourself=** You can't invite yourself.
			msg = jibsMessages.convert("m_invite_yourself");

			out.println(msg);

			return true;
		}

		if (player.getOpponent() != null) {
			// m_you_play_already=** You are already playing.
			msg = jibsMessages.convert("m_you_play_already");

			out.println(msg);

			return true;
		}

		Player invitee = ClientWorker.getPlayer(server, args[1]); // invited
																	// player

		if (invitee != null) {
			if (!invitee.checkToggle("ready")) {
				// m_join_refuse=** %0 is refusing games.
				msg = jibsMessages.convert("m_join_refuse", invitee.getName());

				out.println(msg);

				return true;
			}

			if (invitee.getOpponent() != null) {
				// m_invite_already_play=** %0 is already playing with someone
				// else.
				msg = jibsMessages.convert("m_invite_already_play", invitee.getName());

				out.println(msg);

				return true;
			}

			JibsWriter out_invitee = invitee.getOutputStream();
			SavedGameParam resumeData = JibsGame
					.loadGame(server.getJibsServer(), player.getName(),
							invitee.getName());

			if (args.length == 2) {
				// is there a game to be resumeable?
				if (resumeData != null) {
					// m_you_resume=** You invited %0 to resume a saved match.
					msg = jibsMessages.convert("m_you_resume", invitee.getName());
					out.println(msg);
					// m_other_resume=%0 wants to resume a saved match with you.
					msg = jibsMessages.convert("m_other_resume", player.getName());
					out_invitee.println(msg);
					// m_join_accept=Type 'join %0' to accept.
					msg = jibsMessages.convert("m_join_accept", player.getName());
					out_invitee.println(msg);
					resumeData.setPlayer1(player);
					resumeData.setPlayer2(invitee);
					JibsQuestion inviteQuestion = new ResumeQuestion(resumeData);
					player.ask(inviteQuestion);
					return true;
				} else {
					// m_no_saved_game=** There's no saved match with %0. Please
					// give a match length.
					msg = jibsMessages.convert("m_no_saved_game", invitee.getName());
					out.println(msg);
					return true;
				}
			}

			int nMatch = 1;
			JibsMatch jibsMatch = null;

			if (args.length > 2) {
				try {
					if (args[2].equalsIgnoreCase("unlimited")) {
						jibsMatch = new JibsMatch(JibsMatch.unlimitedMatch);
					} else {
						nMatch = Integer.parseInt(args[2]);
						jibsMatch = new JibsMatch(JibsMatch.nPointMatch);
					}
				} catch (NumberFormatException e) {
					// ** The second argument to 'invite' has to be a number or
					// the word 'unlimited'
					msg = jibsMessages.convert("m_invite_argError");

					out.println(msg);
					JibsTextArea.log(server.getJibsServer(), player.getName()
							+ ":" + msg + " ( invite " + strArgs + ")", true);

					return true;
				}
			}

			if (!checkExperience(jibsMatch, nMatch, player, invitee,
					jibsMessages)) {
				return true;
			}

			JibsQuestion inviteQuestion = new InviteQuestion(nMatch, player,
					invitee, jibsMatch);

			player.ask(inviteQuestion);

			JibsWriter outOpponenent = invitee.getOutputStream();

			if (jibsMatch.getVersion() == JibsMatch.nPointMatch) {
				// m_other_invite=%0 wants to play a %1 point match with you.
				msg = jibsMessages.convert("m_other_invite", player.getName(), Integer.valueOf(nMatch));

				outOpponenent.println(msg);
			} else {
				// m_other_invite_unlimited=%0 wants to play an unlimited match
				// with you.
				msg = jibsMessages.convert("m_other_invite_unlimited", player.getName());

				outOpponenent.println(msg);
			}

			if (resumeData != null) {
				if (resumeData.getMatchVersion() == JibsMatch.nPointMatch) {
					// m_invite_saved_match=WARNING: Don't accept if you want to
					// continue the saved %0 point match!
					msg = jibsMessages.convert("m_invite_saved_match", Integer.valueOf(resumeData
							.getMatchlength()));
				} else {
					// m_invite_saved_match_unlimited=WARNING: Don't accept if
					// you want to continue the saved unlimited match!
					msg = jibsMessages
							.convert("m_invite_saved_match_unlimited");
				}

				outOpponenent.println(msg);
			}

			// m_join_accept=Type 'join %0' to accept.
			msg = jibsMessages.convert("m_join_accept", player.getName());
			outOpponenent.println(msg);

			if (jibsMatch.getVersion() == JibsMatch.nPointMatch) {
				// m_you_invite=** You invited %0 to a %1 point match.
				msg = jibsMessages.convert("m_you_invite", invitee.getName(), Integer.valueOf(nMatch));
				out.println(msg);
			} else {
				// m_invite_unlimited=** You invited %0 to an unlimited match.
				msg = jibsMessages.convert("m_invite_unlimited", invitee.getName());
				out.println(msg);
			}
			if (invitee.checkToggle("away")) {
				// m_other_away=%0 is away: %1
				msg = jibsMessages.convert("m_other_away", invitee.getName(), invitee.getAwayMsg());

				out.println(msg);

				// return true;
			}
		} else {
			// m_noone=** There is no one called %0.
			msg = jibsMessages.convert("m_noone", args[1]);
			out.println(msg);
		}
		return true;
	}

	private boolean checkExperience(JibsMatch jibsMatch, int ml, Player player,
			Player invitee, JibsMessages jibsMessages) {
		String msg = null;
		JibsWriter out1 = player.getOutputStream();
		int expA = player.getExperience();

		if (jibsMatch.getVersion() == JibsMatch.nPointMatch) {
			if (ml > 9) {
				if (expA < 20) {
					// m_invite_experience=** You're not experienced enough to
					// play a match of that length.
					msg = jibsMessages.convert("m_invite_experience");
					out1.println(msg);

					return false;
				}
			}
		} else {
			if (expA < 20) {
				// m_invite_experience=** You're not experienced enough to play
				// a match of that length.
				msg = jibsMessages.convert("m_invite_experience");
				out1.println(msg);

				return false;
			}
		}

		return true;
	}
}
