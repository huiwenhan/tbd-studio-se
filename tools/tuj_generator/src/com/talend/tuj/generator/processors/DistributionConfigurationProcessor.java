package com.talend.tuj.generator.processors;

import com.talend.tuj.generator.components.IComponent;
import com.talend.tuj.generator.utils.NodeType;

public class DistributionConfigurationProcessor implements IProcessor {
    @Override
    public boolean shouldBeProcessed(IComponent component) {
        String componentName =  component.getAttribute("componentName").orElse("");

        return component.isOfType(NodeType.COMPONENT) && (componentName.contains("Configuration") || componentName.contains("Connection"));
    }

    @Override
    public void process(IComponent component) {

    }
}
