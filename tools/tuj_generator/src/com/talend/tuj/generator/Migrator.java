package com.talend.tuj.generator;

import com.talend.tuj.generator.components.ComponentFactory;
import com.talend.tuj.generator.components.IComponent;
import com.talend.tuj.generator.conf.TUJGeneratorConfiguration;
import com.talend.tuj.generator.processors.IProcessor;
import com.talend.tuj.generator.processors.JobIDProcessor;
import com.talend.tuj.generator.processors.SparkConfigurationLocalSparkProcessor;
import com.talend.tuj.generator.utils.Job;
import com.talend.tuj.generator.utils.TUJ;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Migrator {
    private List<IProcessor> processors = new ArrayList<>();
    private static ComponentFactory cpnFactory = ComponentFactory.getInstance();

    public Migrator(TUJGeneratorConfiguration conf){
        //processors.add(new PrintProcessor());
        processors.add(new SparkConfigurationLocalSparkProcessor("SPARK_2_2_0"));
        processors.add(new JobIDProcessor());
    }

    public TUJ migrate(TUJ tuj){
        navigateJob(tuj.getStarterJob());
        return tuj;
    }

    public List<TUJ> migrate(List<TUJ> tujs){
        return tujs.stream().map(this::migrate).collect(Collectors.toList());
    }

    private void navigateJob(Job job){
        iterateNodes(job.getProperties().getChildNodes());
        iterateNodes(job.getItem().getChildNodes());

        job.getChildJobs().forEach(
                childJob -> {
                    iterateNodes(childJob.getProperties().getChildNodes());
                    iterateNodes(childJob.getItem().getChildNodes());
                }
        );
    }

    private void iterateNodes(NodeList nodes){
        for (int nodeIndex = 0 ; nodeIndex < nodes.getLength() ; nodeIndex++){
            Node node = nodes.item(nodeIndex);

            IComponent component = cpnFactory.createComponent(node);

            processors.forEach(
                processor -> {
                    if (processor.shouldBeProcessed(component)) processor.process(component);
                }
            );

            if(node.hasChildNodes()) iterateNodes(node.getChildNodes());
        }
    }

}
