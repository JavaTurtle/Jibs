package net.sourceforge.jibs.gui;

import java.util.List;

import javax.swing.JPanel;

import net.sourceforge.jibs.server.JibsServer;
import net.sourceforge.jibs.server.Player;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public class JibsUserPanel extends JPanel {
    private static final long serialVersionUID = 2091053677555167005L;
    private JibsServer jibsServer;
    private JibsUserTableModel jibsUserTableModel = null;

    public JibsUserPanel(JibsServer jibsServer) {
        this.jibsServer = jibsServer;
        this.jibsUserTableModel = jibsServer.getUserTableModel();
    }

    @SuppressWarnings("unchecked")
    public void readAllPlayers() {
        SqlSessionFactory sqlMap = jibsServer.getSqlSessionFactory();
        SqlSession sqlSession = sqlMap.openSession(true);
        try {
		List<Player> map = sqlSession.selectList("Player.readAllPlayer", null);
		jibsUserTableModel.getDataVector().removeAllElements();

		for (Player player : map) {
		    jibsUserTableModel.getDataVector().add(player);
		}

		jibsUserTableModel.fireTableDataChanged();
        } finally {
        	sqlSession.close();
        }
    }

    public JibsUserTableModel getJibsUserTableModel() {
        return jibsUserTableModel;
    }
}
