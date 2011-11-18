package command;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;

import net.sourceforge.jibs.command.Invite_Command;
import net.sourceforge.jibs.command.JibsCommand;
import net.sourceforge.jibs.command.Login_Command;
import net.sourceforge.jibs.server.ClientWorker;
import net.sourceforge.jibs.server.Player;
import net.sourceforge.jibs.server.Server;
import net.sourceforge.jibs.util.JibsWriter;

import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Before;
import org.junit.Test;

import util.JibsFactory;
import util.TestClient;
import util.TestProperties;
import util.TestServer;

public class Invite {
	private TestServer testServer;
	private TestClient testClient1;
	private TestClient testClient2;
	private ByteArrayOutputStream byteArrayOutputStream1;
	private JibsWriter jibsWriter1;
	private ByteArrayOutputStream byteArrayOutputStream2;
	private JibsWriter jibsWriter2;
	private Server server;
	private ClientWorker clientWorker1;
	private ClientWorker clientWorker2;
	private SqlSessionFactory sqlMapper;
	private String login1;
	private String login2;

	@Before
	public void setup() {
		login1 = "login clientHugo 1008 "+TestProperties.getKey("dbUser1.name")+" "+ TestProperties.getKey("dbUser1.password");
		login2 = "login clientHugo 1008 "+TestProperties.getKey("dbUser2.name")+" "+ TestProperties.getKey("dbUser2.password");
		testServer = JibsFactory.createTestServer("conf/jibs.properties");
		server = testServer.server;
		testClient1 = JibsFactory.createTestClient("conf/jibs.properties",
				server);
		testClient2 = JibsFactory.createTestClient("conf/jibs.properties",
				server);
		byteArrayOutputStream1 = testClient1.byteArrayOutputStream;
		byteArrayOutputStream2 = testClient2.byteArrayOutputStream;
		jibsWriter1 = testClient1.jibsWriter;
		jibsWriter2 = testClient2.jibsWriter;
		clientWorker1 = testClient1.clientWorker;
		clientWorker2 = testClient2.clientWorker;
		sqlMapper = testServer.sqlMapper;
	}

	@Test
	public void inviteNormal() {
		Player player1 = Login_Command.login(sqlMapper,login1);
		player1.setOutputStream(jibsWriter1);
		player1.setClientWorker(clientWorker1);
		clientWorker1.connectPlayer(player1);
		Player player2 = Login_Command.login(sqlMapper,login2);
		player2.setOutputStream(jibsWriter2);
		player2.setClientWorker(clientWorker2);
		clientWorker1.connectPlayer(player2);
		JibsCommand invite = new Invite_Command();
		invite.execute(server, player1, "invite bob 1", new String[] {
				"invite", "bob", "1" });
		String result = byteArrayOutputStream1.toString();
		assertEquals("** You invited bob to a 1 point match.\r\n", result);
		result = byteArrayOutputStream2.toString();
		assertEquals("alice wants to play a 1 point match with you.\r\nType 'join alice' to accept.\r\n", result);
	}

	@Test
	public void inviteRefusing() {
		Player player1 = Login_Command.login(sqlMapper, login1);
		player1.setOutputStream(jibsWriter1);
		player1.setClientWorker(clientWorker1);
		clientWorker1.connectPlayer(player1);
		Player player2 = Login_Command.login(sqlMapper, login2);
		player2.setOutputStream(jibsWriter2);
		player2.setClientWorker(clientWorker2);
        player2.changeToggle("ready", Boolean.FALSE);
		clientWorker1.connectPlayer(player2);
		JibsCommand invite = new Invite_Command();
		invite.execute(server, player1, "invite bob 1", new String[] {
				"invite", "bob", "1" });
		String result = byteArrayOutputStream1.toString();
		assertEquals("** bob is refusing games.\r\n", result);
	}

	@Test
	public void invitePlaying() {
		Player player1 = Login_Command.login(sqlMapper, login1);
		player1.setOutputStream(jibsWriter1);
		player1.setClientWorker(clientWorker1);
		clientWorker1.connectPlayer(player1);
		Player player2 = Login_Command.login(sqlMapper, login2);
		player2.setOutputStream(jibsWriter2);
		player2.setClientWorker(clientWorker2);
        player2.setOpponent(player1);
		clientWorker1.connectPlayer(player2);
		JibsCommand invite = new Invite_Command();
		invite.execute(server, player1, "invite bob 1", new String[] {
				"invite", "bob", "1" });
		String result = byteArrayOutputStream1.toString();
		assertEquals("** bob is already playing with someone else.\r\n", result);
	}

	@Test
	public void inviteBadArgument() {
		Player player1 = Login_Command.login(sqlMapper,login1);
		player1.setOutputStream(jibsWriter1);
		player1.setClientWorker(clientWorker1);
		clientWorker1.connectPlayer(player1);
		Player player2 = Login_Command.login(sqlMapper, login2);
		player2.setOutputStream(jibsWriter2);
		player2.setClientWorker(clientWorker2);
        player2.changeToggle("ready", Boolean.TRUE);
		clientWorker1.connectPlayer(player2);
		JibsCommand invite = new Invite_Command();
		invite.execute(server, player1, "invite bob 1", new String[] {
				"invite", "bob", "x" });
		String result = byteArrayOutputStream1.toString();
		assertEquals("** The second argument to 'invite' has to be a number or the word 'unlimited'\r\n", result);
	}

	@Test
	public void inviteBadExperience() {
		Player player1 = Login_Command.login(sqlMapper, login1);
		player1.setOutputStream(jibsWriter1);
		player1.setClientWorker(clientWorker1);
		player1.setExperience(3);
		clientWorker1.connectPlayer(player1);
		Player player2 = Login_Command.login(sqlMapper, login2);
		player2.setOutputStream(jibsWriter2);
		player2.setClientWorker(clientWorker2);
        player2.changeToggle("ready", Boolean.TRUE);
		clientWorker1.connectPlayer(player2);
		JibsCommand invite = new Invite_Command();
		invite.execute(server, player1, "", new String[] {
				"invite", "bob", "10" });
		String result = byteArrayOutputStream1.toString();
		assertEquals("** You're not experienced enough to play a match of that length.\r\n", result);
	}

	@Test
	public void inviteUnlimited() {
		Player player1 = Login_Command.login(sqlMapper, login1);
		player1.setOutputStream(jibsWriter1);
		player1.setClientWorker(clientWorker1);
		player1.setExperience(300);
		clientWorker1.connectPlayer(player1);
		Player player2 = Login_Command.login(sqlMapper, login2);
		player2.setOutputStream(jibsWriter2);
		player2.setClientWorker(clientWorker2);
		clientWorker1.connectPlayer(player2);
		JibsCommand invite = new Invite_Command();
		invite.execute(server, player1, "", new String[] {
				"invite", "bob", "unlimited" });
		String result = byteArrayOutputStream1.toString();
		assertEquals("** You invited bob to an unlimited match.\r\n", result);
	}

	@Test
	public void inviteAlreadyPlaing() {
		Player player1 = Login_Command.login(sqlMapper, login1);
		player1.setOutputStream(jibsWriter1);
		player1.setClientWorker(clientWorker1);
		Player player2 = Login_Command.login(sqlMapper, login2);
		player2.setOutputStream(jibsWriter2);
		player2.setClientWorker(clientWorker2);
		player1.setOpponent(player2);
		clientWorker1.connectPlayer(player1);
		clientWorker1.connectPlayer(player2);
		JibsCommand invite = new Invite_Command();
		invite.execute(server, player1, "", new String[] {
				"invite", "bob", "1" });
		String result = byteArrayOutputStream1.toString();
		assertEquals("** You are already playing.\r\n", result);
	}

	@Test
	public void inviteAwayPlayer() {
		Player player1 = Login_Command.login(sqlMapper, login1);
		player1.setOutputStream(jibsWriter1);
		player1.setClientWorker(clientWorker1);
		Player player2 = Login_Command.login(sqlMapper, login2);
		player2.setOutputStream(jibsWriter2);
		player2.setClientWorker(clientWorker2);
        player2.changeToggle("away", Boolean.TRUE);
        player2.setAwayMsg("Be right back");
		clientWorker1.connectPlayer(player1);
		clientWorker1.connectPlayer(player2);
		JibsCommand invite = new Invite_Command();
		invite.execute(server, player1, "", new String[] {
				"invite", "bob", "1" });
		String result = byteArrayOutputStream1.toString();
		assertEquals("** You invited bob to a 1 point match.\r\nbob is away: Be right back\r\n", result);
		result = byteArrayOutputStream2.toString();
		assertEquals("alice wants to play a 1 point match with you.\r\nType 'join alice' to accept.\r\n", result);
	}
	
	@Test
	public void inviteWho() {
		Player player1 = Login_Command.login(sqlMapper, login1);
		player1.setOutputStream(jibsWriter1);
		player1.setClientWorker(clientWorker1);
		JibsCommand invite = new Invite_Command();
		invite.execute(server, player1, "invite", new String[] {
				"invite"});
		String result = byteArrayOutputStream1.toString();
		assertEquals("** invite who?\r\n", result);
	}
	
	@Test
	public void inviteYouself() {
		Player player1 = Login_Command.login(sqlMapper, login1);
		player1.setOutputStream(jibsWriter1);
		player1.setClientWorker(clientWorker1);
		JibsCommand invite = new Invite_Command();
		invite.execute(server, player1, "", new String[] {
				"invite", "alice", "1" });
		String result = byteArrayOutputStream1.toString();
		assertEquals("** You can't invite yourself.\r\n", result);
	}
	@Test
	public void inviteUnknownUser() {
		Player player1 = Login_Command.login(sqlMapper, login1);
		player1.setOutputStream(jibsWriter1);
		player1.setClientWorker(clientWorker1);
		clientWorker1.connectPlayer(player1);
		JibsCommand invite = new Invite_Command();
		invite.execute(server, player1, "invite bob 1", new String[] {
				"invite", "bob", "1" });
		String result = byteArrayOutputStream1.toString();
		assertEquals("** There is no one called bob.\r\n", result);
	}
}
