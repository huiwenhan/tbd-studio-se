package com.talend.tuj.generator.utils;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Job {
    private List<Job> childJobs;
    private Document properties;
    private Document item;
    private Document screenshot; // Historical file needed but unused
    private String id;
    private String name;
    private JobType jobType;
    private JobFramework framework;
    private String version;
    private Optional<TUJ> tuj = Optional.empty();

    private Optional<Path> fsPath = Optional.empty();

    public Job(Document properties, Document item, Document screenshot, Optional<Path> jobPath){
        this.properties = properties;
        this.item = item;
        this.screenshot = screenshot;
        this.childJobs = new ArrayList<>();
        this.fsPath = jobPath;
        writeId();
        writeJobTypeAndFramework();
        writeNameAndVersion();
    }

    private void writeId(){
        this.id = Optional.of(properties.getElementsByTagName("TalendProperties:Property").item(0).getAttributes().getNamedItem("id").getNodeValue()).orElse("null");
    }

    public Optional<TUJ> getTuj() {
        return tuj;
    }

    public void setTuj(TUJ tuj){
        this.tuj = Optional.of(tuj);
    }

    public String getId() {
        return id;
    }

    public String getVersion() {
        return version;
    }

    public void addChildJob(Job job){
        this.childJobs.add(job);
    }

    public Document getItem(){
        return item;
    }

    public Document getProperties(){
        return properties;
    }

    public Document getScreenshot(){
        return screenshot;
    }

    public List<Job> getChildJobs() {
        return childJobs;
    }

    public Optional<Path> getFsPath() {
        return fsPath;
    }

    public void setFsPath(Optional<Path> fsPath) {
        this.fsPath = fsPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JobType getType() {
        return jobType;
    }

    public JobFramework getFramework() {
        return framework;
    }

    public List<String> findChildjobs(){
        List<String> childJobs = new ArrayList<>();

        NodeList components = this.item.getElementsByTagName("node");
        for (int nodeIndex = 0 ; nodeIndex < components.getLength() ; nodeIndex++){
            Node component = components.item(nodeIndex);
            try{
                if(component.getAttributes().getNamedItem("componentName").getNodeValue().equals("tRunJob")){
                    NodeList componentParameters = component.getChildNodes();
                    for (int parameterIndex = 0 ; parameterIndex < componentParameters.getLength() ; parameterIndex++){
                        Node componentParameter = componentParameters.item(parameterIndex);
                        try{
                            if (componentParameter.getAttributes().getNamedItem("name").getNodeValue().equals("PROCESS:PROCESS_TYPE_PROCESS")){
                                childJobs.add(componentParameter.getAttributes().getNamedItem("value").getNodeValue());
                            }
                        } catch (NullPointerException ignored){}
                    }
                }
            } catch (NullPointerException ignored){}
        }
        return childJobs;
    }

    private void writeJobTypeAndFramework(){
        NamedNodeMap attributes = this.item.getElementsByTagName("talendfile:ProcessType").item(0).getAttributes();
        this.jobType = JobType.valueOf(attributes.getNamedItem("jobType").getNodeValue().toUpperCase());
        try{
            this.framework = JobFramework.valueOf(attributes.getNamedItem("framework").getNodeValue().toUpperCase());
        } catch (NullPointerException e){
            this.framework = JobFramework.NONE;
        }
    }

    private void writeNameAndVersion(){
        try{
            NamedNodeMap attributes = this.properties.getElementsByTagName("TalendProperties:Property").item(0).getAttributes();
            this.name = attributes.getNamedItem("label").getNodeValue();
            this.version = attributes.getNamedItem("version").getNodeValue();
        } catch (NullPointerException ignored){}
    }
}
