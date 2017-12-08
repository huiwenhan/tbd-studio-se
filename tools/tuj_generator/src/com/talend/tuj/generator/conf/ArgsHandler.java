package com.talend.tuj.generator.conf;

import org.apache.commons.cli.*;

public class ArgsHandler {
    public static TUJGeneratorConfiguration handle(String[] args){
        Options options = new Options();

        Option input = new Option("i", "input", true, "input TUJ folder");
        input.setRequired(true);
        options.addOption(input);

        Option output = new Option("o", "output", true, "output TUJ folder");
        output.setRequired(true);
        options.addOption(output);

        Option distribName = new Option("N", "distribution-name", true, "distribution name as in the Studio XML. See mapping.md for examples");
        distribName.setRequired(true);
        options.addOption(distribName);

        Option distribVersion = new Option("V", "distribution-version", true, "distribution version as in the Studio XML. See mapping.md for examples");
        distribVersion.setRequired(true);
        options.addOption(distribVersion);

        Option tuj = new Option("t", "tuj", true, "single TUJ processing");
        tuj.setRequired(false);
        options.addOption(tuj);

        // TODO Can be ignored as Colibri overrides contexts
        Option contextSubstitutions = new Option("s", "context-substitution", true, "context substitutions. -s cdh512=>cdh513;zookeeper0=>zookeeper0-cdh513 for example will replace talend-cdh512.weave.local by talend-cdh513.weave.local in context variables");
        contextSubstitutions.setRequired(false);
        options.addOption(contextSubstitutions);

        Option fileSubstitutions = new Option("S", "file-substitution", true, "file substitutions. -s localspark210=>localspark220;210=>220 for example will replace localspark210 by localspark220 in folder or file names");
        fileSubstitutions.setRequired(false);
        options.addOption(fileSubstitutions);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
            if(cmd.hasOption('S') && !cmd.getOptionValue('S').matches("^(\\w+=>\\w*;?)+$")){
                throw new ParseException(cmd.getOptionValue('S') + "does not match the required pattern");
            }
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);

            System.exit(1);
            return null;
        }

        TUJGeneratorConfiguration conf = new TUJGeneratorConfiguration();

        conf.put("input", cmd.getOptionValue('i'));
        conf.put("output", cmd.getOptionValue('o'));
        conf.put("distributionName", cmd.getOptionValue('N'));
        conf.put("distributionVersion", cmd.getOptionValue('V'));
        if(cmd.hasOption('t')) conf.put("tuj", cmd.getOptionValue('t'));
        if(cmd.hasOption('S')) conf.put("fileSubstitution", cmd.getOptionValue('S'));

        return conf;
    }
}
