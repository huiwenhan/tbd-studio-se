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
    private String id;
    private String name;
    private String jobType;
    private String framework;

    private String fsPath;

    public Job(Document properties, Document item, Path jobPath){
        this.properties = properties;
        this.item = item;
        this.childJobs = new ArrayList<>();
        this.fsPath = jobPath.getFileName().toString();
        writeId();
        writeJobTypeAndFramework();
        writeName();
    }

    private void writeId(){
        this.id = Optional.of(properties.getElementsByTagName("TalendProperties:Property").item(0).getAttributes().getNamedItem("id").getNodeValue()).orElse("null");
    }

    public String getId() {
        return id;
    }

    public void addChildJob(Job job){
        this.childJobs.add(job);
    }

    public Node getItem(){
        return item;
    }

    public Node getProperties(){
        return properties;
    }

    public List<Job> getChildJobs() {
        return childJobs;
    }

    public String getFsPath() {
        return fsPath;
    }

    public String getName() {
        return name;
    }

    public String getJobType() {
        return jobType;
    }

    public String getFramework() {
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
        try{
            NamedNodeMap attributes = this.item.getElementsByTagName("talendfile:ProcessType").item(0).getAttributes();
            this.jobType = attributes.getNamedItem("jobType").getNodeValue();
            this.framework = attributes.getNamedItem("framework").getNodeValue();
        } catch (NullPointerException ignored){}
    }

    private void writeName(){
        try{
            this.name = this.properties.getElementsByTagName("TalendProperties:Property").item(0).getAttributes().getNamedItem("label").getNodeValue();
        } catch (NullPointerException ignored){}
    }
}
