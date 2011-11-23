/*
 * Created on 30.05.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package net.sourceforge.jibs.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.Date;
import java.util.StringTokenizer;

import net.sourceforge.jibs.gui.JibsTextArea;
import net.sourceforge.jibs.server.JibsServer;
import net.sourceforge.jibs.server.Player;
import net.sourceforge.jibs.server.Server;
import net.sourceforge.jibs.util.Encoder;
import net.sourceforge.jibs.util.JibsWriter;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

/**
 * The NewUser_Command will be invoked by jIBS when a user tries to register a
 * new account.
 */
public class NewUser_Command {
	private BufferedReader in;
	private JibsWriter out;
	private JibsServer jibsServer;

	public NewUser_Command(JibsServer jibsServer, BufferedReader in,
			JibsWriter out) {
		this.in = in;
		this.out = out;
		this.jibsServer = jibsServer;
	}

	public Player createNewUser(JibsServer jibsServer, Server server,
			Socket client, String strArgs, String[] args, boolean persistUser) {
		SqlSessionFactory sqlSessionFactory = jibsServer.getSqlSessionFactory();
		Player player = null;
		SqlSession sqlSession = sqlSessionFactory.openSession(true);

		try {
			out.println("Welcome to JIBS.You just logged in as guest.");
			out.println("Please register before using this server:");

			JibsName strNewUser = chooseName(jibsServer);
			if (strNewUser == null)
				return null;
			if (!strNewUser.isValid())
				return null;
			out.println("Your name will be " + strNewUser.getName());
			Password passWord = choosePassword();
			if (passWord == null)
				return null;
			String hostName = "";
			if (client != null) {
				InetAddress iadr = client.getInetAddress();
				hostName = iadr.getHostName();
			}
			if (!passWord.getPassWord().equals(passWord.getConfirmedPassword()))
				return null;

			player = new Player("client", -1, strNewUser.getName(),
					Encoder.encrypt(
							strNewUser.getName() + passWord.getPassWord(),
							"MD5"), 1500.0, 0, "", false, new Timestamp(
							new Date().getTime()), hostName);
			// The inital settings
			player.setToggle("1101001101010110100"); // all boolean toggles
			player.setBoardStyle("2");
			player.setLineLength("0");
			player.setPageLength("0");
			player.setRedoubles("none");
			player.setSortwho("login");
			player.setTimezone("UTC");
			if (persistUser)
				sqlSession.insert("Player.insertPlayer", player);
			player.setPassword(passWord.getPassWord());
			JibsTextArea.log(jibsServer,
					"User " + strNewUser + "/" + passWord.getPassWord()
							+ " registered.", true);
			out.println("\r\nYou are registered.");
			out.println("Type 'help beginner' to get started.");
		} finally {
			sqlSession.close();
		}

		return player;
	}

	private JibsName chooseName(JibsServer jibsServer) {
		JibsName name = new JibsName();
		name.setValid(false);
		SqlSessionFactory sqlSessionFactory = jibsServer.getSqlSessionFactory();
		SqlSession sqlSession = sqlSessionFactory.openSession(true);
		int count = 0;
		String strNewUser = null;

		try {
			out.println("");
			out.println("Type 'name username' where name is the word 'name' and");
			out.println("username is the login name you want to use.");
			out.println("The username may not contain blanks ' ' or colons ':'.");
			out.println("The system will then ask you for your password twice.");
			out.println("Please make sure that you don't forget your password. All");
			out.println("passwords are encrypted before they are saved. If you forget");
			out.println("your password there is no way to find out what it was.");
			out.println("Please type 'bye' if you don't want to register now.");
			out.println("");
			out.println("ONE USERNAME PER PERSON ONLY!!!");

			String loginCmd = null;

			try {
				loginCmd = in.readLine();
			} catch (IOException e) {
				jibsServer.logException(e);
			}

			StringTokenizer stoken = new StringTokenizer(loginCmd);
			count = 0;

			while (stoken.hasMoreTokens()) {
				String token = stoken.nextToken();

				if ((count == 0) && (token.equalsIgnoreCase("name"))) {
					count++;
				}

				if (count == 1) {
					strNewUser = token;
				}
			}

			// Did the user input name <X> ?
			if (count != 1) {
				out.println("");
				out.println("** Your name may only contain letters and the unserscore character _ .");
				return name;
			}
			name.setName(strNewUser);
			// check if strNewUser can be chosen
			Integer sqlCount = 0;

			sqlCount = (Integer) sqlSession.selectOne("Player.checkPlayer",
					strNewUser);

			if (sqlCount > 0) {
				// m_use_another_name=** Please use another name. '%0' is
				// already used by someone else.
				Object[] obj = new Object[] { strNewUser };
				String msg = jibsServer.getJibsMessages().convert(
						"m_use_another_name", obj);
				out.println(msg);
			} else {
				name.setValid(true);
			}
		} finally {
			sqlSession.close();
		}

		return name;
	}

	private Password choosePassword() {
		String passWord = "";
		String repeatPassword = "";

		out.println("Type in no password and hit Enter/Return if you want to change it now.");
		out.println("Please give your password: ");
		out.flush();

		try {
			passWord = in.readLine(); // Passwort of <x>
		} catch (IOException e) {
			jibsServer.logException(e);
		}
		if (passWord.length() < 4) {
			out.println("");
			out.println("Minimal password length is 4 characters.");
			return null;
		}

		out.println("");
		out.println("Please retype your password: ");
		out.flush();

		try {
			repeatPassword = in.readLine(); // Repeated Passwort of <x>
		} catch (IOException e) {
			jibsServer.logException(e);
		}

		if (!passWord.equals(repeatPassword)) {
			out.println("** The two passwords were not identical. Please give them again.");
		}

		return (new Password(passWord, repeatPassword));
	}
}

class JibsName {
	private String name;
	private boolean valid;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

}

class Password {
	private String passWord;
	private String confirmedPassword;

	public Password(String passWord, String confirmedPassWord) {
		this.passWord = passWord;
		this.confirmedPassword = confirmedPassWord;
	}

	public String getConfirmedPassword() {
		return confirmedPassword;
	}

	public String getPassWord() {
		return passWord;
	}
};
