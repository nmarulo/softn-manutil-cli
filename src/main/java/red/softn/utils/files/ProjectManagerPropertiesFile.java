package red.softn.utils.files;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.stream.Collectors;

public class ProjectManagerPropertiesFile {
    
    private File fileProperties;
    
    private Properties properties;
    
    private File directory;
    
    private String packageNameBase;
    
    private String modulesValueFormatSeparator;
    
    private int modulePositionDirectory;
    
    private int modulePositionPackage;
    
    private List<ModuleProject> projectModules;
    
    private String directoryPackages;
    
    private String classType;
    
    private boolean packageCreate;
    
    private boolean packageModuleCreate;
    
    public ProjectManagerPropertiesFile(File fileProperties) {
        this.fileProperties = fileProperties;
        this.properties = new Properties();
        
        try {
            this.properties.load(new FileInputStream(fileProperties));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public void initProject() {
        this.setDirectory(getPropertyParseFile(PropertyKeysConstants.KEY_DIRECTORY));
        this.packageNameBase = getProperty(PropertyKeysConstants.KEY_PACKAGE);
        this.directoryPackages = getProperty(PropertyKeysConstants.KEY_DIRECTORY_PACKGES);
        this.modulesValueFormatSeparator = getProperty(PropertyKeysConstants.KEY_MODULE_FORMAT_SEPARATOR, PropertyKeysConstants.DEFAULT_VALUE_FORMAT_SEPARATOR);
        this.modulePositionPackage = getPropertyParseInt(PropertyKeysConstants.KEY_MODULE_FORMAT_POSITION_PACKAGE, PropertyKeysConstants.DEFAULT_VALUE_POSITION_PACKAGE);
        this.modulePositionDirectory = getPropertyParseInt(PropertyKeysConstants.KEY_MODULE_FORMAT_POSITION_DIRECTORY, PropertyKeysConstants.DEFAULT_VALUE_POSITION_DIRECTORY);
        this.classType = getProperty(PropertyKeysConstants.KEY_PROJECT_CLASS_TYPE);
        this.packageCreate = getPropertyParseBool(PropertyKeysConstants.KEY_PROJECT_PACKAGE_CREATE, PropertyKeysConstants.DEFAULT_VALUE_PROJECT_PACKAGE_CREATE);
        this.packageModuleCreate = getPropertyParseBool(PropertyKeysConstants.KEY_PROJECT_MODULE_PACKAGE_CREATE, PropertyKeysConstants.DEFAULT_VALUE_PROJECT_MODULE_PACKAGE_CREATE);
        
        initProjectModules();
    }
    
    public List<ModuleProject> getProjectModules() {
        return projectModules;
    }
    
    public TemplateFile getClassTemplateFile(ModuleProject moduleProject, String className) {
        String classNameFinal = StringUtils.replaceAll(moduleProject.getClassNameTemplate(), ModuleProject.REGEX_CLASS_NAME, className);
        
        File   directoryPathClass = getDirectoryPathModuleProject(moduleProject);
        String fileName           = String.format("%1$s%2$s%3$s.%4$s", directoryPathClass.getAbsolutePath(), File.separator, classNameFinal, moduleProject.getClassExtension());
        File   file               = new File(fileName);
        
        if (file.exists()) {
            throw new RuntimeException(String.format("El fichero ya existe. Verifique la ruta: %1$s", file.getAbsolutePath()));
        }
        
        String content = moduleProject.stringReplaceTemplate(className);
        content = moduleProject.replaceNameClass(content, classNameFinal);
        
        return new TemplateFile(file, content);
    }
    
    private File getDirectoryPathModuleProject(ModuleProject moduleProject) {
        StringBuilder directory = new StringBuilder(this.directory.getAbsolutePath());
        directory.append(File.separator);
        directory.append(moduleProject.getDirectoryName());
        directory.append(File.separator);
        directory.append(removeSlashes(this.directoryPackages));
        directory.append(splitPackages(this.packageNameBase, PropertyKeysConstants.DEFAULT_VALUE_PACKAGE_SEPARATOR));
        directory.append(splitPackages(moduleProject.getPackageName(), PropertyKeysConstants.DEFAULT_VALUE_PACKAGE_SEPARATOR));
        
        File directoryModule = new File(directory.toString());
        
        if (!directoryModule.exists() || !directoryModule.isDirectory()) {
            String message = "El directorio no existe.";
            
            if (!directoryModule.canWrite()) {
                message = "No se puede escribir en el directorio.";
            }
            
            throw new RuntimeException(String.format("%1$s Verifique la ruta: %1$s", message, directoryModule.getAbsolutePath()));
        }
        
        return directoryModule;
    }
    
    private void initProjectModules() {
        this.projectModules = this.properties.stringPropertyNames()
                                             .stream()
                                             .filter(value -> StringUtils.startsWith(value, ModuleProject.KEY_MODULES))
                                             .map(this::instanceModuleProject)
                                             .collect(Collectors.toList());
    }
    
    private ModuleProject instanceModuleProject(String keyProperty) {
        String        propertyValue            = getProperty(keyProperty);
        String[]      modulesValue             = StringUtils.split(propertyValue, this.modulesValueFormatSeparator);
        ModuleProject projectModule            = new ModuleProject();
        String        projectModuleKeyProperty = StringUtils.removeStart(keyProperty, ModuleProject.KEY_MODULES);
        
        projectModule.setDirectoryName(modulesValue[this.modulePositionDirectory]);
        projectModule.setPackageName(modulesValue[this.modulePositionPackage]);
        projectModule.setKeyProperty(projectModuleKeyProperty);
        
        return initClassesTemplate(projectModule);
    }
    
    private ModuleProject initClassesTemplate(ModuleProject moduleProject) {
        String projectModuleKeyProperty  = moduleProject.getKeyProperty();
        String classTemplateFileKey      = String.format(ModuleProject.KEY_CLASSES_TEMPLATE_PATH, projectModuleKeyProperty);
        String classNameTemplateKey      = String.format(ModuleProject.KEY_CLASSES_TEMPLATE_NAME, projectModuleKeyProperty);
        String classesTemplateReplaceKey = String.format(ModuleProject.KEY_CLASSES_TEMPLATE_REPLACE, projectModuleKeyProperty);
        String classTemplateTypeKey      = String.format(ModuleProject.KEY_CLASSES_TEMPLATE_TYPE, projectModuleKeyProperty);
        
        moduleProject.setClassTemplateFile(getPropertyParseFile(classTemplateFileKey));
        moduleProject.setClassNameTemplate(getProperty(classNameTemplateKey));
        moduleProject.setClassExtension(getProperty(classTemplateTypeKey, this.classType));
        moduleProject.setStringReplaceTemplate(getMapStringReplaceTemplate(classesTemplateReplaceKey));
        
        return moduleProject;
    }
    
    private Map<Integer, String> getMapStringReplaceTemplate(String classesTemplateReplaceKey) {
        return this.properties.stringPropertyNames()
                              .stream()
                              .filter(value -> StringUtils.startsWith(value, classesTemplateReplaceKey))
                              .map(this::splitKeyGetValueProperty)
                              .collect(Collectors.toMap(value -> Integer.parseInt(value[0]), value -> value[1], (oldValue, newValue) -> newValue, TreeMap::new));
    }
    
    private String[] splitKeyGetValueProperty(String value) {
        String[] keySplit = StringUtils.split(value, ".");
        
        return new String[] {
            keySplit[keySplit.length - 1],
            this.getProperty(value)
        };
    }
    
    private void setDirectory(File directory) {
        this.directory = directory;
        
        if (!this.directory.exists() || !this.directory.isDirectory()) {
            throw new RuntimeException("El directorio base del proyecto no existe.");
        }
    }
    
    private String getProperty(String key) {
        String value = getProperty(key, "");
        
        if (StringUtils.isEmpty(value)) {
            throw new RuntimeException(String.format("La propiedad \"%1$s\" es requerida.", key));
        }
        
        return value;
    }
    
    private String getProperty(String key, String defaultValue) {
        return this.properties.getProperty(key, defaultValue);
    }
    
    private int getPropertyParseInt(String key, String defaultValue) {
        return Integer.parseInt(getProperty(key, defaultValue));
    }
    
    private boolean getPropertyParseBool(String key, boolean defaultValue) {
        String value = getProperty(key, "");
        
        if (StringUtils.isEmpty(value)) {
            return defaultValue;
        }
        
        return Boolean.parseBoolean(value);
    }
    
    private File getPropertyParseFile(String key) {
        return new File(getProperty(key));
    }
    
    private String splitPackages(String value, String regex) {
        if (StringUtils.isEmpty(value)) {
            return "";
        }
        
        return File.separator.concat(Arrays.stream(value.split(regex))
                                           .collect(Collectors.joining(File.separator)));
    }
    
    private String removeSlashes(String value) {
        if (StringUtils.isEmpty(value)) {
            return "";
        }
        
        return StringUtils.removeEnd(StringUtils.removeStart(value, "/"), "/");
    }
}
