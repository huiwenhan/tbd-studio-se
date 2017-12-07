package com.talend.tuj.generator.utils;

import com.talend.tuj.generator.utils.Job;
import org.w3c.dom.Document;

public class TUJ {
    private Document project;
    private Job starter;
    private String name;
    private String projectName;

    public TUJ(Job job, Document project, String name, String projectName){
        this.starter = job;
        this.project = project;
        this.name = name;
        this.projectName = projectName;
    }

    public Job getStarterJob() {
        return starter;
    }

    public String getName() {
        return name;
    }

    public String getProjectName() {
        return projectName;
    }
}
