package net.sourceforge.jibs.util;

import net.sourceforge.jibs.server.JibsQuestion;

public class ResumeQuestion implements JibsQuestion {
    private SavedGameParam resumeData;

    public ResumeQuestion(SavedGameParam resumeData) {
        this.resumeData = resumeData;
    }

    public SavedGameParam getResumeData() {
        return resumeData;
    }
}
