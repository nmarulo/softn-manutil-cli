package red.softn.utils.cli;

import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ProjectManagerCli {
    
    private static final String OPTION_VALUE_P = "p";
    
    private static final String OPTION_VALUE_M = "m";
    
    private static final String OPTION_VALUE_C = "c";
    
    private static final String OPTION_LONG_VALUE_P = "properties";
    
    private static final String OPTION_LONG_VALUE_M = "module";
    
    private static final String OPTION_LONG_VALUE_C = "class";
    
    private static final String OPTION_LONG_VALUE_DEBUG = "debug";
    
    private static final String OPTION_LONG_VALUE_H = "help";
    
    private CommandLine commandLine;
    
    private Options options;
    
    private boolean hasOptHelp;
    
    public ProjectManagerCli(String[] args) throws Exception {
        this(args, new DefaultParser());
    }
    
    public ProjectManagerCli(String[] args, CommandLineParser commandLineParser) throws Exception {
        if (commandLineParser == null) {
            throw new Exception("El valor de \"CommandLineParse\" no puede ser nulo.");
        }
        
        checkOptHelp(args);
        initOptions();
        this.commandLine = commandLineParser.parse(this.options, args);
    }
    
    public boolean hasOption(String opt) {
        return this.commandLine.hasOption(opt);
    }
    
    public String getValueProperties() {
        return getOptionValue(OPTION_VALUE_P);
    }
    
    public String getValueModule() {
        return getOptionValue(OPTION_VALUE_M);
    }
    
    public String getValueClass() {
        return getOptionValue(OPTION_VALUE_C);
    }
    
    public boolean isNotOptionHelp() {
        if (this.hasOptHelp) {
            String        header    = "\nInformación sobre los comandos disponibles.\n\n";
            String        footer    = "\nRepositorio https://github.com/nmarulo/softn-manutil-cli";
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(OPTION_LONG_VALUE_H, header, this.options, footer);
            
            return false;
        }
        
        return true;
    }
    
    private void checkOptHelp(String[] args) {
        String opts = Arrays.stream(args)
                            .collect(Collectors.joining(" "));
        //Si se envia el comando "HELP", ninguna de las opciones requeridas sera obligatoria.
        this.hasOptHelp = StringUtils.containsIgnoreCase(opts, "-".concat(OPTION_LONG_VALUE_H));
    }
    
    private String getOptionValue(String opt) {
        if (hasOption(opt)) {
            return this.commandLine.getOptionValue(opt);
        }
        
        return null;
    }
    
    private void initOptions() {
        this.options = new Options();
        
        options.addOption(Option.builder(OPTION_VALUE_P)
                                .longOpt(OPTION_LONG_VALUE_P)
                                .required(!this.hasOptHelp)
                                .hasArg()
                                .build());
        options.addOption(Option.builder(OPTION_VALUE_M)
                                .longOpt(OPTION_LONG_VALUE_M)
                                .hasArg()
                                .build());
        options.addOption(Option.builder(OPTION_VALUE_C)
                                .longOpt(OPTION_LONG_VALUE_C)
                                .required(!this.hasOptHelp)
                                .hasArg()
                                .build());
        options.addOption(Option.builder()
                                .longOpt(OPTION_LONG_VALUE_DEBUG)
                                .desc("Imprime la traza de los mensajes de error.")
                                .build());
        options.addOption(Option.builder()
                                .longOpt(OPTION_LONG_VALUE_H)
                                .desc("Imprime este menu.")
                                .build());
    }
}
