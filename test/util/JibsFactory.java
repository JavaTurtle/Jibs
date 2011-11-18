package util;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.net.Socket;
import java.util.Properties;

import net.sourceforge.jibs.gui.JibsMessages;
import net.sourceforge.jibs.server.ClientWorker;
import net.sourceforge.jibs.server.JibsConfiguration;
import net.sourceforge.jibs.server.JibsServer;
import net.sourceforge.jibs.server.Server;
import net.sourceforge.jibs.util.JibsWriter;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class JibsFactory {

	public static TestServer createTestServer(String configProperties) {
		TestServer testServer = new TestServer();

		try {
			testServer.jibsConfiguration = new JibsConfiguration(
					configProperties);
			testServer.jibsMessages = new JibsMessages(
					testServer.jibsConfiguration.getResource("MessageFile"));
			testServer.jibsServer = new JibsServer(configProperties);
			testServer.server = new Server(testServer.jibsConfiguration,
					testServer.jibsMessages);
			testServer.server.setJibsServer(testServer.jibsServer);
			String sqlMapConfig = testServer.jibsConfiguration
					.getResource("dbConfiguration");
			Reader reader = Resources.getResourceAsReader(sqlMapConfig);
			testServer.sqlMapper = new SqlSessionFactoryBuilder().build(reader);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return testServer;
	}

	public static TestClient createTestClient(String configProperties,
			Server server) {
		TestClient testClient = new TestClient();
		try {
			testClient.jibsConfiguration = new JibsConfiguration(
					configProperties);
			testClient.byteArrayOutputStream = new ByteArrayOutputStream();
			testClient.jibsWriter = new JibsWriter(
					testClient.byteArrayOutputStream);
			testClient.jibsMessages = new JibsMessages(
					testClient.jibsConfiguration.getResource("MessageFile"));
			testClient.clientWorker = new ClientWorker(
					testClient.jibsConfiguration, testClient.jibsMessages,
					server, new Socket(), null, testClient.jibsWriter);
			String sqlMapConfig = testClient.jibsConfiguration
					.getResource("dbConfiguration");
			Reader reader = Resources.getResourceAsReader(sqlMapConfig);
			testClient.sqlMapper = new SqlSessionFactoryBuilder().build(reader);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return testClient;
	}

}
