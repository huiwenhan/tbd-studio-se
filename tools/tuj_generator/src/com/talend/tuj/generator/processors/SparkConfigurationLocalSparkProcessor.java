package com.talend.tuj.generator.processors;

import com.talend.tuj.generator.components.IComponent;
import com.talend.tuj.generator.utils.NodeType;

public class SparkConfigurationLocalSparkProcessor implements IProcessor {
    private String distribution_version;

    public SparkConfigurationLocalSparkProcessor(String local_spark_version){
        this.distribution_version = local_spark_version;
    }

    @Override
    public boolean shouldBeProcessed(IComponent component) {
        return component.isOfType(NodeType.JOBCONFIG);
    }

    @Override
    public void process(IComponent component) {
        try{
            component.replaceParameter("SPARK_LOCAL_MODE", "true");
            component.replaceParameter("SPARK_LOCAL_VERSION", distribution_version);
        }catch(NullPointerException ignored){}
    }
}
