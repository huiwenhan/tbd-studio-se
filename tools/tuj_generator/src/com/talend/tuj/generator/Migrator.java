package com.talend.tuj.generator;

import com.talend.tuj.generator.elements.ElementFactory;
import com.talend.tuj.generator.elements.IElement;
import com.talend.tuj.generator.conf.TUJGeneratorConfiguration;
import com.talend.tuj.generator.exception.UnknownDistributionException;
import com.talend.tuj.generator.processors.*;
import com.talend.tuj.generator.utils.Job;
import com.talend.tuj.generator.utils.SubstitutionCmdHandler;
import com.talend.tuj.generator.utils.TUJ;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Migrator {
    private List<IProcessor> processors = new ArrayList<>();
    private static ElementFactory cpnFactory = ElementFactory.getInstance();

    public Migrator(TUJGeneratorConfiguration conf){
        processors.add(new JobIDProcessor());
        if(conf.containsKey("fileSubstitution")){
            processors.add(new FileNameSubstitutionProcessor(SubstitutionCmdHandler.processArgument(conf.get("fileSubstitution"))));
        }

        try {
            switch (conf.getDistributionName()){ // Register processors depending on distribution
                case SPARK_LOCAL:
                    processors.add(new SparkConfigurationLocalSparkProcessor(conf.get("distributionVersion")));
                    break;
                case CDH:
                case HDI:
                case HDP:
                case MAPR:
                    // TODO
                    processors.add(new GenericDistributionConfigurationProcessor(conf.getDistributionName().getXmlDistributionName(), conf.get("distributionName")));
                    processors.add(new GenericDistributionComponentConfigurationProcessor(conf.getDistributionName().getXmlDistributionName(), conf.get("distributionName")));
                    break;
            }
        } catch (UnknownDistributionException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public TUJ migrate(TUJ tuj){
        System.out.println("Processing TUJ : " + tuj.getName());
        transformJob(tuj.getStarterJob());
        return tuj;
    }

    public List<TUJ> migrate(List<TUJ> tujs){
        return tujs.stream().map(this::migrate).collect(Collectors.toList());
    }

    private void transformJob(Job job){
        iterateNodes(job.getProperties().getChildNodes(), job);
        iterateNodes(job.getItem().getChildNodes(), job);

        job.getChildJobs().forEach(this::transformJob);
    }

    private void iterateNodes(NodeList nodes, Job job){
        for (int nodeIndex = 0 ; nodeIndex < nodes.getLength() ; nodeIndex++){
            Node node = nodes.item(nodeIndex);

            IElement component = cpnFactory.createElement(node, job);

            processors.forEach(
                processor -> {
                    if (processor.shouldBeProcessed(component)) processor.process(component);
                }
            );

            if(node.hasChildNodes()) iterateNodes(node.getChildNodes(), job);
        }
    }

}
