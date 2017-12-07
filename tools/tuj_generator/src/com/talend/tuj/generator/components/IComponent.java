package com.talend.tuj.generator.components;

import com.talend.tuj.generator.utils.NodeType;

import java.util.Optional;

public interface IComponent {
    boolean isOfType(NodeType type);
    Optional<String> getAttribute(String attribute);
    void replaceAttribute(String attribute, String value);
    Optional<String> getParameter(String parameter);
    void replaceParameter(String parameter, String value);
}
