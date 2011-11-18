package util;

import java.io.ByteArrayOutputStream;

import net.sourceforge.jibs.gui.JibsMessages;
import net.sourceforge.jibs.server.ClientWorker;
import net.sourceforge.jibs.server.JibsConfiguration;
import net.sourceforge.jibs.util.JibsWriter;

import org.apache.ibatis.session.SqlSessionFactory;

public class TestClient {
	public ByteArrayOutputStream byteArrayOutputStream;
	public JibsWriter jibsWriter;
	public JibsConfiguration jibsConfiguration;
	public JibsMessages jibsMessages;
	public ClientWorker clientWorker;
	public SqlSessionFactory sqlMapper;
}
