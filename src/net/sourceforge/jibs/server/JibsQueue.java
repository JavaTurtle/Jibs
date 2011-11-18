package net.sourceforge.jibs.server;

import java.util.AbstractQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class JibsQueue extends Thread {
    private AbstractQueue<String> queue;
    private ClientWorker cw;

    public JibsQueue(ClientWorker cw) {
        this.cw = cw;
        queue = new ConcurrentLinkedQueue<String>();
    }

    public void push(String cmd) {
        if (cmd != null) {
            queue.add(cmd);
        }
    }

    public void run() {
        boolean run = true;

        while (run) {
            String cmd = queue.poll();

            if (cmd != null) {
                run = cw.executeCmd(cmd);
            }
        }
    }
}
