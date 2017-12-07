package com.talend.tuj.generator.io;

import com.talend.tuj.generator.utils.Job;
import com.talend.tuj.generator.utils.TUJ;
import com.talend.tuj.generator.conf.TUJGeneratorConfiguration;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class TUJImporter {
    public static List<TUJ> importTUJ(TUJGeneratorConfiguration conf){

        List<Path> contextFiles = new ArrayList<>();
        List<Path> jobRootFolders = new ArrayList<>();
        Path tujProject = null;

        Path root = FileSystems.getDefault().getPath("test","original", "spark_local_basics_j01_210");

        String tujName = root.getFileName().toString();
        String projectName = "";

        if(Files.isDirectory(root)){
        try(DirectoryStream<Path> rootFolder = Files.newDirectoryStream(root)){
            for(Path studioProject : rootFolder){ // list of projects
                projectName = studioProject.getFileName().toString();
                if(Files.isDirectory(studioProject)){
                try(DirectoryStream<Path> studioProjectFolder = Files.newDirectoryStream(studioProject)){
                    for(Path process : studioProjectFolder){ // Context, process, process_mr, process_storm
                        if(process.endsWith("context")){
                            try(DirectoryStream<Path> contextFolder = Files.newDirectoryStream(process)){
                                for(Path contextFile : contextFolder){ // context files
                                    contextFiles.add(contextFile);
                                }
                            }
                        }
                        else if(process.endsWith("process") || process.endsWith("process_mr") || process.endsWith("process_storm")){
                            try(DirectoryStream<Path> processFolder = Files.newDirectoryStream(process)){
                                for(Path jobRoot : processFolder){ // process folders
                                    jobRootFolders.add(jobRoot);
                                }
                            }
                        }
                        else if(process.endsWith("talend.project")){
                            tujProject = process;
                        }
                    }
                }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        }

        Map<String, Job> jobs = new HashMap<>();
        try {
            DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            for(Path rootFolder : jobRootFolders){
                Path itemTmp = null, propertiesTmp = null;

                try(DirectoryStream<Path> jobFiles = Files.newDirectoryStream(rootFolder)){
                    for(Path file : jobFiles){
                        if(file.toString().contains(".item")){
                            itemTmp = file;
                        }
                        else if(file.toString().contains(".properties")){
                            propertiesTmp = file;
                        }

                    }
                }

                Job jobTmp = new Job(
                        dBuilder.parse(propertiesTmp.toString()),
                        dBuilder.parse(itemTmp.toString()),
                        rootFolder.getFileName()
                );
                jobs.put(jobTmp.getId(), jobTmp);
            }
            List<TUJ> tujs = new ArrayList<>();

            TUJ tuj = new TUJ(
                    generateJobHierarchy(jobs),
                    dBuilder.parse(tujProject.toString()),
                    tujName,
                    projectName
            );
            tujs.add(tuj);

            return tujs;

        } catch (ParserConfigurationException | NullPointerException | IOException | SAXException e) {
            e.printStackTrace();
        }
        System.exit(2);
        return null;
    }

    private static Job generateJobHierarchy(Map<String, Job> jobMap){
        Map<String, List<String>> jobHierarchy = new HashMap<>();

        for (Job job : jobMap.values()){
            List<String> childs = job.findChildjobs();
            jobHierarchy.put(job.getId(), childs);
            for(String child : childs){
                job.addChildJob(jobMap.get(child));
            }
        }

        Set<String> jobIds = jobMap.keySet();
        for (List<String> childs : jobHierarchy.values()){
            jobIds.removeAll(childs);
        }

        if(jobIds.size() == 1){
            return jobMap.get(jobIds.iterator().next());
        }

        System.err.println("TUJ is not well made");
        System.exit(1);
        return null;
    }


}
