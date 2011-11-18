package command;

import static org.junit.Assert.*;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;

import net.sourceforge.jibs.command.JibsCommand;
import net.sourceforge.jibs.command.Login_Command;
import net.sourceforge.jibs.command.Unknown_Command;
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

public class UnknownCommand {
	private TestServer testServer;
	private TestClient testClient;
	private ByteArrayOutputStream byteArrayOutputStream;
	private JibsWriter jibsWriter;
	private Server server;
	private ClientWorker clientWorker;
	private SqlSessionFactory sqlMapper;
	@Before
	public void setup() {
		testServer = JibsFactory.createTestServer("conf/jibs.properties");
		testClient = JibsFactory.createTestClient("conf/jibs.properties", testServer.server);
		byteArrayOutputStream = testClient.byteArrayOutputStream;
		jibsWriter = testClient.jibsWriter;
		server = testServer.server;
		clientWorker = testClient.clientWorker;
		sqlMapper = testServer.sqlMapper;
	}

	@Test
	public void unknownCommand() {
		String login1 = "login clientHugo 1008 "+TestProperties.getKey("dbUser1.name")+" "+ TestProperties.getKey("dbUser1.password");
		Player player = Login_Command.login(sqlMapper,login1);
		assertNotNull(player);
		assertTrue(player.is_valid());
		player.setOutputStream(jibsWriter);
		player.setClientWorker(clientWorker);
		JibsCommand cmd = new Unknown_Command();
		cmd.execute(server, player, "badCmd", null);
		String result = byteArrayOutputStream.toString();
		assertEquals("** Unknown command: 'badCmd'\r\n", result);
	}


}
