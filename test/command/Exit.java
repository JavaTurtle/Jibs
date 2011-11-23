package command;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.util.Date;

import net.sourceforge.jibs.command.Exit_Command;
import net.sourceforge.jibs.command.JibsCommand;
import net.sourceforge.jibs.server.ClientWorker;
import net.sourceforge.jibs.server.Player;
import net.sourceforge.jibs.server.Server;
import net.sourceforge.jibs.util.JibsWriter;

import org.junit.Before;
import org.junit.Test;

import util.JibsFactory;
import util.TestClient;
import util.TestServer;

public class Exit {
	private TestServer testServer;
	private TestClient testClient;
	private ByteArrayOutputStream byteArrayOutputStream;
	private JibsWriter jibsWriter;
	private Server server;
	private ClientWorker clientWorker;

	@Before
	public void setup() {
		testServer = JibsFactory.createTestServer("conf/jibs.properties");
		testClient = JibsFactory.createTestClient("conf/jibs.properties",
				testServer.server);
		byteArrayOutputStream = testClient.byteArrayOutputStream;
		jibsWriter = testClient.jibsWriter;
		server = testServer.server;
		clientWorker = testClient.clientWorker;
	}

	@Test
	public void goodLogout() {
		Player player = new Player("client", 1008, "aleucht", "ankaba22",
				1500.0, 222, "Email", false,
				new Timestamp(new Date().getTime()), "lastHost");
		player.setOutputStream(jibsWriter);
		player.setClientWorker(clientWorker);
		JibsCommand exit = new Exit_Command();
		exit.execute(server, player, "logout", null);
		String string = byteArrayOutputStream.toString();
		assertTrue(string.indexOf("Goodbye") >= 0);
	}

}
