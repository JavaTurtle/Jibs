package net.sourceforge.jibs.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JWindow;

import net.sourceforge.jibs.server.JibsServer;

public class SplashWindowJibs extends JWindow {
    private static final long serialVersionUID = -8877839601052661726L;
    @SuppressWarnings("unused")
    private JibsServer jibsServer;

    public SplashWindowJibs(JibsServer jibsServer, String filename, Frame f) {
        super(f);

        this.jibsServer = jibsServer;

        URL imgUrl = ClassLoader.getSystemResource(filename);
        JLabel l = new JLabel(new ImageIcon(imgUrl));

        getContentPane().add(l, BorderLayout.CENTER);
        pack();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension labelSize = l.getPreferredSize();

        setLocation((screenSize.width / 2) - (labelSize.width / 2),
                    (screenSize.height / 2) - (labelSize.height / 2));

        addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    setVisible(false);
                    dispose();
                }
            });

        setVisible(true);
    }
}
