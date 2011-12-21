package command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;

import net.sourceforge.jibs.command.Login_Command;
import net.sourceforge.jibs.command.NewUser_Command;
import net.sourceforge.jibs.server.JibsServer;
import net.sourceforge.jibs.server.Player;
import net.sourceforge.jibs.server.Server;
import net.sourceforge.jibs.util.JibsWriter;

import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Before;
import org.junit.Test;

import util.JibsFactory;
import util.TestClient;
import util.TestServer;

public class CreateUser {
	private TestServer testServer;
	private SqlSessionFactory sqlMapper;
	private TestClient testClient;
	private ByteArrayOutputStream byteArrayOutputStream;
	private JibsWriter jibsWriter;
	private JibsServer jibsServer;
	private Server server;
	@Before
	public void setup() {
		testServer = JibsFactory.createTestServer("conf/jibs.properties");
		testClient = JibsFactory.createTestClient("conf/jibs.properties",
				testServer.server);
		byteArrayOutputStream = testClient.byteArrayOutputStream;
		jibsWriter = testClient.jibsWriter;
		jibsServer = testServer.jibsServer;
		server = testServer.server;
		sqlMapper = testServer.sqlMapper;
	}
//	@Test
//	public void testMsg() {
//		JibsMessages jibsMessages = testClient.jibsMessages;
//		Map<String, String> messageMap = jibsMessages.getMessageMap();
//		Set<String> keySet = messageMap.keySet();
//		for (String key : keySet) {
//			String id = messageMap.get(key);
//			String convert = jibsMessages.convert(key, 17,18,19,20);
//			System.out.println(key+":"+convert);
//		}
//	}

	@Test
	public void createNewUser() {
		String s = "name cleucht\r\nhugo\r\nhugo";
		BufferedReader in = new BufferedReader(new StringReader(s));
		Player player1 = Login_Command.login(sqlMapper, "guest");
		assertNotNull(player1);
		NewUser_Command newUser = new NewUser_Command(jibsServer, in,
				jibsWriter);
		Player newPlayer = newUser.createNewUser(jibsServer, server, null, "",
				null, false);
		assertNotNull(newPlayer);
		assertEquals("cleucht", newPlayer.getName());
		String result = byteArrayOutputStream.toString();
		assertNotNull(result);
		assertTrue(result.indexOf("You are registered.") >= 0);
	}

	@Test
	public void createUserAlreadyStored() {
		String s = "name alice\r\nhugo\r\nhugo";
		BufferedReader in = new BufferedReader(new StringReader(s));
		Player player1 = Login_Command.login(sqlMapper, "guest");
		assertNotNull(player1);
		NewUser_Command newUser = new NewUser_Command(jibsServer, in,
				jibsWriter);
		Player newPlayer = newUser.createNewUser(jibsServer, server, null, "",
				null, false);
		assertNull(newPlayer);
		String result = byteArrayOutputStream.toString();
		assertNotNull(result);
		assertTrue(result
				.endsWith("** Please use another name. 'alice' is already used by someone else.\r\n"));
	}

	@Test
	public void createShortPassword() {
		String s = "name cleucht\r\nabc\r\nabc";
		BufferedReader in = new BufferedReader(new StringReader(s));
		Player player1 = Login_Command.login(sqlMapper, "guest");
		assertNotNull(player1);
		NewUser_Command newUser = new NewUser_Command(jibsServer, in,
				jibsWriter);
		Player newPlayer = newUser.createNewUser(jibsServer, server, null, "",
				null, false);
		assertNull(newPlayer);
		String result = byteArrayOutputStream.toString();
		assertNotNull(result);
		assertTrue(result
				.endsWith("Minimal password length is 4 characters.\r\n"));
	}

	@Test
	public void createNoName() {
		String s = "cleucht cleucht\r\nhugo\r\nhugo";
		BufferedReader in = new BufferedReader(new StringReader(s));
		Player player1 = Login_Command.login(sqlMapper, "guest");
		assertNotNull(player1);
		NewUser_Command newUser = new NewUser_Command(jibsServer, in,
				jibsWriter);
		Player newPlayer = newUser.createNewUser(jibsServer, server, null, "",
				null, false);
		assertNull(newPlayer);
		String result = byteArrayOutputStream.toString();
		assertNotNull(result);
		assertTrue(result
				.endsWith("** Your name may only contain letters and the unserscore character _ .\r\n"));
	}

	@Test
	public void createDifferentPassword() {
		String s = "name cleucht\r\nhugo1\r\nhugo2";
		BufferedReader in = new BufferedReader(new StringReader(s));
		Player player1 = Login_Command.login(sqlMapper, "guest");
		assertNotNull(player1);
		NewUser_Command newUser = new NewUser_Command(jibsServer, in,
				jibsWriter);
		Player newPlayer = newUser.createNewUser(jibsServer, server, null, "",
				null, false);
		assertNull(newPlayer);
		String result = byteArrayOutputStream.toString();
		assertNotNull(result);
		assertTrue(result
				.endsWith("** The two passwords were not identical. Please give them again.\r\n"));
	}
}
