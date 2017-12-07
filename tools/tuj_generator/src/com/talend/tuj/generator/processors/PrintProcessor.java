package com.talend.tuj.generator.processors;

import com.talend.tuj.generator.components.IComponent;
import com.talend.tuj.generator.utils.NodeType;

public class PrintProcessor implements IProcessor {
    @Override
    public boolean shouldBeProcessed(IComponent component) {
        return component.isOfType(NodeType.COMPONENT);
    }

    @Override
    public void process(IComponent component) {
        System.out.println("PrintProcessor: " + component.getAttribute("componentName").orElse("component has no name"));
    }
}
