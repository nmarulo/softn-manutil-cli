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
    
    private static final String OPTION_LONG_VALUE_EDIT_PROPERTIES = "edit-properties";
    
    private static final String OPTION_LONG_VALUE_CREATE_CLASSES = "create-classes";
    
    private static final String OPTION_LONG_VALUE_JSON = "json";
    
    private static final String OPTION_LONG_VALUE_PROPERTIES_JSON = "properties-json";
    
    private CommandLine commandLine;
    
    private Options options;
    
    private boolean hasOptHelp;
    
    private boolean hasOptEditProperties;
    
    private boolean hasOptCreateClasses;
    
    private boolean hasOptPropertiesJson;
    
    public ProjectManagerCli(String[] args) throws Exception {
        this(args, new DefaultParser());
    }
    
    public ProjectManagerCli(String[] args, CommandLineParser commandLineParser) throws Exception {
        if (commandLineParser == null) {
            throw new Exception("El valor de \"CommandLineParse\" no puede ser nulo.");
        }
        
        beforeInitOption(args);
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
    
    public String getValueJson() {
        return getOptionValue(OPTION_LONG_VALUE_JSON);
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
    
    public boolean isHasOptEditProperties() {
        return hasOptEditProperties;
    }
    
    public boolean isHasOptCreateClasses() {
        return hasOptCreateClasses;
    }
    
    private void beforeInitOption(String[] args) {
        String opts = Arrays.stream(args)
                            .collect(Collectors.joining(" "));
        //Si se envía el comando "HELP", ninguna de las opciones requeridas sera obligatoria.
        this.hasOptHelp = StringUtils.containsIgnoreCase(opts, "-".concat(OPTION_LONG_VALUE_H));
        this.hasOptCreateClasses = StringUtils.containsIgnoreCase(opts, "-".concat(OPTION_LONG_VALUE_CREATE_CLASSES));
        this.hasOptEditProperties = StringUtils.containsIgnoreCase(opts, "-".concat(OPTION_LONG_VALUE_EDIT_PROPERTIES));
        this.hasOptPropertiesJson = StringUtils.containsIgnoreCase(opts, "-".concat(OPTION_LONG_VALUE_PROPERTIES_JSON));
    }
    
    private String getOptionValue(String opt) {
        if (hasOption(opt)) {
            return this.commandLine.getOptionValue(opt);
        }
        
        return null;
    }
    
    private void initOptions() {
        this.options = new Options();
        optionsGroup();
        genericOptions();
        
        if (this.hasOptHelp || this.hasOptEditProperties) {
            //Datos en formato json del fichero ".properties".
            this.options.addOption(Option.builder()
                                         .longOpt(OPTION_LONG_VALUE_JSON)
                                         .required(!this.hasOptHelp && this.hasOptEditProperties)
                                         .hasArg()
                                         .build());
        }
        
        if (this.hasOptHelp || this.hasOptCreateClasses) {
            options.addOption(Option.builder(OPTION_VALUE_M)
                                    .longOpt(OPTION_LONG_VALUE_M)
                                    .hasArg()
                                    .build());
            options.addOption(Option.builder(OPTION_VALUE_C)
                                    .longOpt(OPTION_LONG_VALUE_C)
                                    .required(!this.hasOptHelp && this.hasOptCreateClasses)
                                    .hasArg()
                                    .build());
        }
    }
    
    private void genericOptions() {
        options.addOption(Option.builder()
                                .longOpt(OPTION_LONG_VALUE_DEBUG)
                                .desc("Imprime la traza de los mensajes de error.")
                                .build());
        options.addOption(Option.builder()
                                .longOpt(OPTION_LONG_VALUE_H)
                                .desc("Imprime este menu.")
                                .build());
        options.addOption(Option.builder(OPTION_VALUE_P)
                                .longOpt(OPTION_LONG_VALUE_P)
                                .required(!this.hasOptHelp && (this.hasOptCreateClasses || this.hasOptEditProperties))
                                .hasArg()
                                .build());
    }
    
    private void optionsGroup() {
        OptionGroup optionGroup = new OptionGroup();
        
        /*
         * Indica que se editara el fichero ".properties".
         * Esta opción debe ir junto con "--json" y "-p"
         */
        optionGroup.addOption(Option.builder()
                                    .longOpt(OPTION_LONG_VALUE_EDIT_PROPERTIES)
                                    .required(!this.hasOptHelp)
                                    .build());
        /*
         * Indica que se creara un fichero.
         * Esta opción debe ir junto con "-p", "-m" y "-c"
         */
        optionGroup.addOption(Option.builder()
                                    .longOpt(OPTION_LONG_VALUE_CREATE_CLASSES)
                                    .required(!this.hasOptHelp)
                                    .build());
        /*
         * Indica que se retornara el fichero properties en formato json.
         * Esta opción debe ir junto con "-p"
         */
        optionGroup.addOption(Option.builder()
                                    .longOpt(OPTION_LONG_VALUE_PROPERTIES_JSON)
                                    .required(!this.hasOptHelp)
                                    .build());
        this.options.addOptionGroup(optionGroup);
    }
    
    public boolean isHasOptPropertiesJson() {
        return this.hasOptPropertiesJson;
    }
}
