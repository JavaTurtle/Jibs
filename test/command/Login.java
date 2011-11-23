package command;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.Reader;

import net.sourceforge.jibs.command.Login_Command;
import net.sourceforge.jibs.server.JibsConfiguration;
import net.sourceforge.jibs.server.Player;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;

import util.TestProperties;

public class Login {
	private SqlSessionFactory sqlMapper;

	@Before
	public void setUp() {
		try {
			JibsConfiguration configuration = new JibsConfiguration(
					"conf/jibs.properties");
			String sqlMapConfig = configuration.getResource("dbConfiguration");
			Reader reader = Resources.getResourceAsReader(sqlMapConfig);
			sqlMapper = new SqlSessionFactoryBuilder().build(reader);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void goodLogin() {
		String login1 = "login clientHugo 1008 "
				+ TestProperties.getKey("dbUser1.name") + " "
				+ TestProperties.getKey("dbUser1.password");

		Player player = Login_Command.login(sqlMapper, login1);
		assertNotNull(player);
		assertTrue(player.is_valid());
	}

	@Test
	public void badPassword() {
		Player player = Login_Command.login(sqlMapper,
				"login clientHugo 1008 aleucht badPassword");
		assertNull(player);
	}

	@Test
	public void badUser() {
		Player player = Login_Command.login(sqlMapper,
				"login clientHugo 1008 badUser badPassword");
		assertNull(player);
	}
}
