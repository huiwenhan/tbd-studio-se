package com.talend.tuj.generator.components;

import com.talend.tuj.generator.utils.NodeType;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Optional;

public abstract class AbstractComponent implements IComponent{
    protected Node xmlNode;

    public AbstractComponent(Node node){
        this.xmlNode = node;
    }

    @Override
    public boolean isOfType(NodeType type) {
        return xmlNode.getNodeName().equals(type.getXmlNodeName());
    }

    @Override
    public Optional<String> getAttribute(String attribute) {
        try{
            return Optional.of(xmlNode.getAttributes().getNamedItem(attribute).getNodeValue());
        }
        catch (NullPointerException e){
            return Optional.empty();
        }
    }

    @Override
    public void replaceAttribute(String attribute, String value) {
        try{
            xmlNode.getAttributes().getNamedItem(attribute).setNodeValue(value);
        }
        catch (NullPointerException ignored){}
    }

    @Override
    public Optional<String> getParameter(String parameter) {
        System.out.println(parameter);
        if (xmlNode.hasChildNodes()) {
            NodeList childs = xmlNode.getChildNodes();
            System.out.println(childs.getLength());
            for (int nodeIndex = 0 ; nodeIndex < childs.getLength() ; nodeIndex++){
                Node childNode = childs.item(nodeIndex);
                try{
                    if(childNode.getAttributes().getNamedItem("name").getNodeValue().equals(parameter)){
                        return Optional.of(childNode.getAttributes().getNamedItem("value").getNodeValue());
                    }
                }
                catch (NullPointerException ignored){}
            }
        }
        return Optional.empty();
    }

    @Override
    public void replaceParameter(String parameter, String value) {
        if (xmlNode.hasChildNodes()) {
            NodeList childs = xmlNode.getChildNodes();
            for (int nodeIndex = 0 ; nodeIndex < childs.getLength() ; nodeIndex++){
                Node childNode = childs.item(nodeIndex);
                try{
                    if(childNode.getAttributes().getNamedItem("name").getNodeValue().equals(parameter)){
                        childNode.getAttributes().getNamedItem("value").setNodeValue(value);
                    }
                }
                catch (NullPointerException ignored){}
            }
        }
    }
}
