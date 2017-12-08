package com.talend.tuj.generator.io;

import com.talend.tuj.generator.utils.Job;
import com.talend.tuj.generator.utils.TUJ;
import com.talend.tuj.generator.conf.TUJGeneratorConfiguration;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class TUJExporter {
    private static TransformerFactory tFactory = TransformerFactory.newInstance();

    public static void exportTUJ(TUJGeneratorConfiguration conf, List<TUJ> tujs){
        for(TUJ tuj : tujs){
            writeTUJ(tuj, conf);
        }
    }

    private static void writeTUJ(TUJ tuj, TUJGeneratorConfiguration conf){
        Path tujRoot = FileSystems.getDefault().getPath(conf.get("output"), tuj.getName());
        tujRoot.toFile().mkdirs();

        try {
            for (Path inputResourcePath : tuj.getResources()){
                Files.copy(inputResourcePath, tujRoot.resolve(inputResourcePath.getFileName().toString()));
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to copy resources");
        }


        List<Job> jobs = tuj.getStarterJob().getChildJobs();
        jobs.add(tuj.getStarterJob());

        try {
            Transformer transformer = tFactory.newTransformer();

            for (Job job : jobs){
                String processFolder = "process";

                switch (job.getType()){
                    case BIG_DATA_BATCH:
                        processFolder += "_mr";
                        break;
                    case BIG_DATA_STREAMING:
                        processFolder += "_storm";
                        break;
                }

                Path jobFolder = tujRoot.resolve(tuj.getProjectName()).resolve(processFolder);
                if(job.getFsPath().isPresent()) jobFolder = jobFolder.resolve(job.getFsPath().get());
                jobFolder.toFile().mkdirs();

                File itemFile = jobFolder.resolve(job.getName()+"_"+job.getVersion()+".item").toFile();
                itemFile.createNewFile();
                transformer.transform(new DOMSource(job.getItem()), new StreamResult(new FileWriter(itemFile)));

                File propertiesFile = jobFolder.resolve(job.getName()+"_"+job.getVersion()+".properties").toFile();
                propertiesFile.createNewFile();
                transformer.transform(new DOMSource(job.getProperties()), new StreamResult(new FileWriter(propertiesFile)));

                File screenshotFile = jobFolder.resolve(job.getName()+"_"+job.getVersion()+".screenshot").toFile();
                screenshotFile.createNewFile();
                transformer.transform(new DOMSource(job.getProperties()), new StreamResult(new FileWriter(screenshotFile)));
            }

        } catch (IOException | TransformerException e) {
            e.printStackTrace();
        }


    }
}
