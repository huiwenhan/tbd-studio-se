package com.talend.tuj.generator.components;

import org.w3c.dom.Node;

import java.util.Optional;

public class ComponentFactory {
    private static Optional<ComponentFactory> instance = Optional.empty();

    private ComponentFactory() {
    }

    public static ComponentFactory getInstance(){
        return instance.orElse(new ComponentFactory());
    }

    public IComponent createComponent(Node node){
        return new StudioComponent(node);
    }
}
