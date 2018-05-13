package red.softn.utils.cli;

import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;
import red.softn.utils.files.Projects;

public class Main {
    
    public static void main(String[] args) {
        String            propertiesPath    = null;
        String            moduleName        = null;
        String            className         = null;
        CommandLineParser commandLineParser = new DefaultParser();
        Options           options           = new Options();
        
        options.addOption(Option.builder("p")
                                .longOpt("properties")
                                .required()
                                .hasArg()
                                .build());
        options.addOption(Option.builder("m")
                                .longOpt("module")
                                .hasArg()
                                .build());
        options.addOption(Option.builder("c")
                                .longOpt("class")
                                .required()
                                .hasArg()
                                .build());
        
        try {
            CommandLine commandLine = commandLineParser.parse(options, args);
            
            if (commandLine.hasOption("p")) {
                propertiesPath = commandLine.getOptionValue("p");
            }
            
            if (commandLine.hasOption("m")) {
                moduleName = commandLine.getOptionValue("m");
            }
            
            if (commandLine.hasOption("c")) {
                className = commandLine.getOptionValue("c");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        if (StringUtils.isNotEmpty(propertiesPath) && (StringUtils.isNotEmpty(moduleName) || StringUtils.isNotEmpty(className))) {
            try {
                System.out.println("Estableciendo fichero \"properties\"...");
                Projects projects = new Projects(propertiesPath);
    
                System.out.println("Creando clases...");
                if (StringUtils.isEmpty(moduleName)) {
                    projects.createClasses(className);
                } else {
                    projects.createClassModule(moduleName, className);
                }
                
                System.out.println("Finalizado correctamente.");
            }catch (Exception ex){
                System.out.println(ex.getMessage());
            }
        }
    }
}
