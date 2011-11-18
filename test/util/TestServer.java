package util;

import net.sourceforge.jibs.gui.JibsMessages;
import net.sourceforge.jibs.server.JibsConfiguration;
import net.sourceforge.jibs.server.JibsServer;
import net.sourceforge.jibs.server.Server;

import org.apache.ibatis.session.SqlSessionFactory;

public class TestServer {
	public JibsConfiguration jibsConfiguration;
	public JibsMessages jibsMessages;
	public JibsServer jibsServer;
	public Server server;
	public SqlSessionFactory sqlMapper;
}
