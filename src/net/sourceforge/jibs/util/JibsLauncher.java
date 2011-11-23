package net.sourceforge.jibs.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sourceforge.jibs.server.JibsConfiguration;
import net.sourceforge.jibs.server.JibsServer;

public class JibsLauncher {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// JibsServer jibsServer = new JibsServer(args[0]);
		JibsLauncher jibsLauncher = new JibsLauncher();
		jibsLauncher.doIt(null, args[0]);
	}

	public void doIt(JibsServer jibsServer, String fileName) {
		int exitValue = 11;
		JibsConfiguration fConf = new JibsConfiguration(fileName);
		String restartCmd = fConf.getResource("RestartCmd");
		while (exitValue > 10) {
			try {
				Runtime rt = Runtime.getRuntime();
				Process process = rt.exec(restartCmd);
				exitValue = process.waitFor();
				System.err.println("jIBS exited with code:" + exitValue);
				if (exitValue > 10) {
					SimpleDateFormat sf = new SimpleDateFormat("mm:ss");
					Date dt = new Date(Long.valueOf(exitValue));
					String s = sf.format(dt);
					System.out.println("Restarting jIBS in " + s + " minutes.");
					Thread.sleep(exitValue);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}