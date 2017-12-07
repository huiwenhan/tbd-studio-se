package com.talend.tuj.generator.processors;

import com.talend.tuj.generator.components.IComponent;
import com.talend.tuj.generator.utils.JobID;
import com.talend.tuj.generator.utils.NodeType;

import java.util.HashMap;
import java.util.Map;

public class JobIDProcessor implements IProcessor {
    private Map<String, String> oldNewIdRelation = new HashMap<>();

    @Override
    public boolean shouldBeProcessed(IComponent component) {
        String componentName =  component.getAttribute("componentName").orElse("");

        return (component.isOfType(NodeType.COMPONENT) && componentName.equals("tRunJob")) || component.isOfType(NodeType.TALENDPROPERTY);
    }

    @Override
    public void process(IComponent component) {
        if(component.isOfType(NodeType.COMPONENT)){
            String newId = JobID.generateJobID();
            oldNewIdRelation.put(component.getParameter("PROCESS:PROCESS_TYPE_PROCESS").orElse(""), newId);
            component.replaceParameter("PROCESS:PROCESS_TYPE_PROCESS", newId);
        }
        else if(component.isOfType(NodeType.TALENDPROPERTY)){
            String oldId = component.getAttribute("id").orElse("");
            if (!oldNewIdRelation.containsKey(oldId)){
                oldNewIdRelation.put(oldId, JobID.generateJobID());
            }
            String newId = oldNewIdRelation.get(oldId);
            component.replaceAttribute("id", newId);
            System.out.println("replaced : " + oldId + " by " + newId);
        }


    }
}
