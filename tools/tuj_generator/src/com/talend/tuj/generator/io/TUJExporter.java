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
import java.nio.file.Path;
import java.util.List;

public class TUJExporter {
    private static TransformerFactory tFactory = TransformerFactory.newInstance();

    public static void exportTUJ(TUJGeneratorConfiguration conf, List<TUJ> tujs){
        for(TUJ tuj : tujs){
            writeTUJ(tuj);
        }
    }

    private static void writeTUJ(TUJ tuj){
        Path root = FileSystems.getDefault().getPath("test","migrated", tuj.getName(), tuj.getProjectName());

        List<Job> jobs = tuj.getStarterJob().getChildJobs();
        jobs.add(tuj.getStarterJob());

        try {
            Transformer transformer = tFactory.newTransformer();

            for (Job job : jobs){
                String processFolder = "process";

                switch (job.getJobType()){
                    case "Big_Data_Batch":
                        processFolder += "_mr";
                        break;
                }

                Path jobFolder = root.resolve(processFolder).resolve(job.getFsPath());
                jobFolder.toFile().mkdirs();

                File itemFile = jobFolder.resolve(job.getName()+".item").toFile();
                itemFile.createNewFile();
                transformer.transform(new DOMSource(job.getItem()), new StreamResult(new FileWriter(itemFile)));

                File propertiesFile = jobFolder.resolve(job.getName()+".properties").toFile();
                propertiesFile.createNewFile();
                transformer.transform(new DOMSource(job.getProperties()), new StreamResult(new FileWriter(propertiesFile)));

            }

        } catch (IOException | TransformerException e) {
            e.printStackTrace();
        }


    }
}
