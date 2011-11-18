package net.sourceforge.jibs.gui;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;

public class JibsDocument extends DefaultStyledDocument {
    private static final long serialVersionUID = 8502172113439079411L;
    private String[] content = null;
    private int maxLines = 0;
    private int nLine = 0;
    private int curLine;

    public JibsDocument(int maxLines) {
        this.maxLines = maxLines;
        content = new String[maxLines];
        curLine = 0;
    }

    public void insertString(int offset, String str, AttributeSet a)
                      throws BadLocationException {
        if (nLine >= maxLines) {
            this.remove(0, getLength());

            for (int y = 1; y < maxLines; y++) {
                String curLine = (String) content[y];

                content[y - 1] = curLine;
                super.insertString(getLength(), curLine, null);
            }

            curLine = maxLines - 1;
        }

        super.insertString(getLength(), str, null);
        content[curLine++] = str;
        nLine++;
    }
}
