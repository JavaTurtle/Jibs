package net.sourceforge.jibs.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import net.sourceforge.jibs.server.Server;
import net.sourceforge.jibs.util.JibsConvert;

import org.apache.ibatis.io.Resources;
import org.apache.log4j.Logger;

import com.jeta.forms.components.panel.FormPanel;

@SuppressWarnings( "serial" )
public class VMPanel extends FormPanel {
	private static Logger logger = Logger.getLogger(VMPanel.class);

	private Server server;
    private JDialog frame;
    private JTextField java_version;
    private JTextField java_vm;
    private JTextField java_vm_version;
    private JTextField os_system;
    private JTextField os_version;
    private JTextField os_arch;
    private JTextField os_processors;
    private JTextField os_language;
    private JTextField totMemory;
    private JTextField freeMemory;
    private JTextField maxMemory;
    private JLabel aboutLabel;
    private JLabel jibsDateBuild;
    private JButton btnOK;

    public VMPanel(JDialog dialog, Server server) {
        super("frm/vmPanel.jfrm");
        this.frame = dialog;
        this.server = server;
        aboutLabel = getLabel("aboutLabel");
        jibsDateBuild= getLabel("jibsDateBuild");
        java_version = getTextField("version");
        java_vm = getTextField("vm");
        java_vm_version = getTextField("vmversion");
        os_system = getTextField("os_system");
        os_version = getTextField("os_version");
        os_arch = getTextField("os_arch");
        os_processors = getTextField("os_processors");
        os_language = getTextField("os_language");
        totMemory = getTextField("totMemory");
        freeMemory = getTextField("freeMemory");
        maxMemory = getTextField("maxMemory");
        btnOK = (JButton) getButton("btnOk");

        frame.getRootPane().setDefaultButton(btnOK);
        btnOK.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    btn_OkActionPerformed(e);
                }
            });
        dialog.addWindowListener(new WindowListener() {
                public void windowActivated(WindowEvent e) {
                }

                public void windowClosed(WindowEvent e) {
                }

                public void windowClosing(WindowEvent e) {
                    btn_OkActionPerformed(null);
                }

                public void windowDeactivated(WindowEvent e) {
                }

                public void windowDeiconified(WindowEvent e) {
                }

                public void windowIconified(WindowEvent e) {
                }

                public void windowOpened(WindowEvent e) {
                }
            });
    }

    public void doShow() {
        try {
			StringBuffer buffer = new StringBuffer();

			buffer.append(server.getConfiguration().getResource("aboutVersion"));
			aboutLabel.setText(buffer.toString());
			aboutLabel.setForeground(Color.RED);
			Properties props = Resources.getResourceAsProperties("net/sourceforge/jibs/util/JibsConstants.properties");
			jibsDateBuild.setText((String) props.get("jibsDateBuild"));
			java_version.setText(System.getProperty("java.runtime.version"));
			java_vm.setText(System.getProperty("java.vm.name"));
			java_vm_version.setText(System.getProperty("java.vm.info"));
			os_system.setText(System.getProperty("os.name"));
			os_version.setText(System.getProperty("os.version"));
			os_arch.setText(System.getProperty("os.arch"));
			os_processors.setText(Integer.toString(Runtime.getRuntime()
			                                              .availableProcessors()));
			os_language.setText(System.getProperty("user.country"));

			double mem = Runtime.getRuntime().freeMemory() / (1024.0 * 1024.0);
			double x = JibsConvert.convdouble(mem, 3);
			String x1 = Double.toString(x);

			freeMemory.setText(x1 + " MB");
			mem = Runtime.getRuntime().maxMemory() / (1024.0 * 1024.0);
			x = JibsConvert.convdouble(mem, 3);
			x1 = Double.toString(x);
			maxMemory.setText(x1 + " MB");
			mem = Runtime.getRuntime().totalMemory() / (1024.0 * 1024.0);
			x = JibsConvert.convdouble(mem, 3);
			x1 = Double.toString(x);
			totMemory.setText(x1 + " MB");
		} catch (IOException e) {
			logger.warn(e);
		}
    }

    public void btn_OkActionPerformed(ActionEvent event) {
        frame.dispose();
    }
}
