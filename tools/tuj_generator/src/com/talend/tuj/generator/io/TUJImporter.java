package com.talend.tuj.generator.io;

import com.talend.tuj.generator.exception.NotWellMadeTUJException;
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
    private DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

    public List<TUJ> importTUJ(TUJGeneratorConfiguration conf){
        Path rootFolder = FileSystems.getDefault().getPath(conf.get("input"));

        return findTUJInFolder(rootFolder);
    }

    private Job generateJobHierarchy(List<Job> jobList) throws NotWellMadeTUJException {
        Map<String, Job> jobMap = new HashMap<>();

        for (Job job : jobList){
            jobMap.put(job.getId(), job);
        }

        return generateJobHierarchy(jobMap);
    }

    private Job generateJobHierarchy(Map<String, Job> jobMap) throws NotWellMadeTUJException {
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

        throw new NotWellMadeTUJException("TUJ is not well made");
    }

    private List<TUJ> findTUJInFolder(Path rootFolder){
        List<TUJ> tujs = new ArrayList<>();

        try(DirectoryStream<Path> root = Files.newDirectoryStream(rootFolder)){
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            for(Path tujRoot : root){
                try {
                if(Files.isDirectory(tujRoot)){
                    String projectName = findProjectName(tujRoot);
                        tujs.add(new TUJ(
                                generateJobHierarchy(findJobInProcessFolders(tujRoot.resolve(projectName))),
                                dBuilder.parse(tujRoot.resolve(projectName).resolve("talend.project").toString()),
                                findResourceInFolder(tujRoot),
                                tujRoot.getFileName().toString(),
                                projectName
                        ));
                }
                } catch (NotWellMadeTUJException notWellMadeTUJ) {
                    System.err.println(notWellMadeTUJ.getMessage());
                    System.err.println("ignoring TUJ : "+tujRoot.getFileName().toString());
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            System.err.println("Failed to create DocumentBuilder (XML file loader)");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to read TUJ folders");
        } catch (SAXException e) {
            e.printStackTrace();
            System.err.println("Failed to load talend.project file");
        }

        return tujs;
    }

    private List<Job> findJobInProcessFolders(Path rootFolder){
        List<Job> jobs = new ArrayList<>();

        if (Files.isDirectory(rootFolder.resolve("process"))){
            jobs.addAll(findJobInFolder(rootFolder.resolve("process")));
        } //else System.err.println("No DI folder");

        if (Files.isDirectory(rootFolder.resolve("process_mr"))){
            jobs.addAll(findJobInFolder(rootFolder.resolve("process_mr")));
        } //else System.err.println("No Batch folder");

        if (Files.isDirectory(rootFolder.resolve("process_storm"))){
            jobs.addAll(findJobInFolder(rootFolder.resolve("process_storm")));
        } //else System.err.println("No Streaming folder");

        return jobs;
    }

    private List<Job> findJobInFolder(Path rootFolder){
        return findJobInFolder(rootFolder, Optional.empty());
    }

    private List<Job> findJobInFolder(Path rootFolder, Optional<Path> folderStructure){
        List<Job> jobs = new ArrayList<>();
        Set<String> jobNames = new HashSet<>();

        try(DirectoryStream<Path> root = Files.newDirectoryStream(rootFolder)){
            for (Path file : root){
                if(Files.isDirectory(file)) {
                    if(folderStructure.isPresent()) {
                        jobs.addAll(findJobInFolder(
                                file,
                                Optional.of(folderStructure.get().resolve(file.getFileName()))
                        ));
                    }
                    else jobs.addAll(findJobInFolder(file, Optional.of(file.getFileName())));
                }
                else {
                    String jobName = file.getFileName().toString();
                    jobNames.add(jobName.substring(0, jobName.lastIndexOf('.')));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to read : " + rootFolder.toString());
        }

        try {
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            for(String jobName : jobNames){
                jobs.add(new Job(
                        dBuilder.parse(rootFolder.resolve(jobName + ".properties").toString()),
                        dBuilder.parse(rootFolder.resolve(jobName + ".item").toString()),
                        dBuilder.parse(rootFolder.resolve(jobName + ".screenshot").toString()),
                        folderStructure
                ));
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
            System.err.println("Missing job file");
        }

        return jobs;
    }

    private List<Path> findResourceInFolder(Path rootFolder){
        List<Path> resources = new ArrayList<>();

        try(DirectoryStream<Path> root = Files.newDirectoryStream(rootFolder)){
            for(Path resourceFile : root){
                if(!Files.isDirectory(resourceFile)) resources.add(resourceFile.toAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return resources;
    }

    private String findProjectName(Path rootFolder){
        try(DirectoryStream<Path> root = Files.newDirectoryStream(rootFolder)){
            for(Path projectRoot : root){
                if(Files.isDirectory(projectRoot)) return projectRoot.getFileName().toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.err.println("Empty TUJ");
        System.exit(2);
        return "";
    }
}
