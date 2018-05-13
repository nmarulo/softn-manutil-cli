package red.softn.utils.files;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class ProjectProperties {
    
    private File fileProperties;
    
    private Properties properties;
    
    private File directory;
    
    private String packageNameBase;
    
    private String modulesValueFormatSeparator;
    
    private String classesValueNameSeparator;
    
    private int modulePositionDirectory;
    
    private int modulePositionPackage;
    
    private List<ModuleProject> projectModules;
    
    private String directoryPackages;
    
    private String classType;
    
    private boolean packageCreate;
    
    private boolean packageModuleCreate;
    
    private String classesReplaceSeparator;
    
    public ProjectProperties(File fileProperties) {
        if (!fileProperties.exists()) {
            throw new RuntimeException("El fichero no existe.");
        }
        
        this.fileProperties = fileProperties;
        this.properties = new Properties();
        
        try {
            this.properties.load(new FileInputStream(fileProperties));
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
    
    public void initProject() {
        this.setDirectory(new File(getProperty(PropertyKeysConstants.KEY_DIRECTORY)));
        this.packageNameBase = getProperty(PropertyKeysConstants.KEY_PACKAGE);
        this.directoryPackages = getProperty(PropertyKeysConstants.KEY_DIRECTORY_PACKGES);
        this.modulesValueFormatSeparator = this.properties.getProperty(PropertyKeysConstants.KEY_MODULE_FORMAT_SEPARATOR, PropertyKeysConstants.DEFAULT_VALUE_FORMAT_SEPARATOR);
        this.modulePositionPackage = getProperty(PropertyKeysConstants.KEY_MODULE_FORMAT_POSITION_PACKAGE, PropertyKeysConstants.DEFAULT_VALUE_POSITION_PACKAGE);
        this.modulePositionDirectory = getProperty(PropertyKeysConstants.KEY_MODULE_FORMAT_POSITION_DIRECTORY, PropertyKeysConstants.DEFAULT_VALUE_POSITION_DIRECTORY);
        this.classesValueNameSeparator = this.properties.getProperty(PropertyKeysConstants.KEY_CLASSES_NAME_SEPARATOR, PropertyKeysConstants.DEFAULT_VALUE_CLASSES_NAME_SEPARATOR);
        this.classType = getProperty(PropertyKeysConstants.KEY_PROJECT_CLASS_TYPE);
        this.packageCreate = getProperty(PropertyKeysConstants.KEY_PROJECT_PACKAGE_CREATE, PropertyKeysConstants.DEFAULT_VALUE_PROJECT_PACKAGE_CREATE);
        this.packageModuleCreate = getProperty(PropertyKeysConstants.KEY_PROJECT_MODULE_PACKAGE_CREATE, PropertyKeysConstants.DEFAULT_VALUE_PROJECT_MODULE_PACKAGE_CREATE);
        this.classesReplaceSeparator = this.properties.getProperty(PropertyKeysConstants.KEY_CLASSES_TEMPLATE_REPLACE_SEPARATOR, PropertyKeysConstants.DEFAULT_VALUE_CLASSES_TEMPLATE_REPLACE_SEPARATOR);
        
        initProjectModules();
    }
    
    public List<ModuleProject> getProjectModules() {
        return projectModules;
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
        
        if (!directoryModule.exists() || !directoryModule.isDirectory() || !directoryModule.canWrite()) {
            //TODO: agregar diferentes exceptions
            throw new RuntimeException("El directorio no existe.");
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
        String        propertyValue            = this.properties.getProperty(keyProperty);
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
        
        moduleProject.setClassTemplateFile(new File(getProperty(classTemplateFileKey)));
        moduleProject.setClassNameTemplate(getProperty(classNameTemplateKey));
        moduleProject.setClassExtension(this.properties.getProperty(classTemplateTypeKey, this.classType));
        
        this.properties.stringPropertyNames()
                       .stream()
                       .filter(value -> StringUtils.startsWith(value, classesTemplateReplaceKey))
                       .map(this.properties::getProperty)
                       .map(value -> StringUtils.split(value, this.classesReplaceSeparator))
                       .forEach(value -> moduleProject.addStringReplaceTemplate(value[0], value[1]));
        
        return moduleProject;
    }
    
    private void setDirectory(File directory) {
        this.directory = directory;
        
        if (!this.directory.exists() || !this.directory.isDirectory()) {
            throw new RuntimeException("El directorio base del proyecto no existe.");
        }
    }
    
    private String getProperty(String key) {
        String value = this.properties.getProperty(key, "");
        
        if (StringUtils.isEmpty(value)) {
            throw new RuntimeException(String.format("La propiedad \"%1$s\" es requerida.", key));
        }
        
        return value;
    }
    
    private int getProperty(String key, String defaultValue) {
        return Integer.parseInt(this.properties.getProperty(key, defaultValue));
    }
    
    private boolean getProperty(String key, boolean defaultValue) {
        String value = this.properties.getProperty(key, "");
        
        if (StringUtils.isEmpty(value)) {
            return defaultValue;
        }
        
        return Boolean.parseBoolean(value);
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
    
    public TemplateFile getClassTemplateFile(ModuleProject moduleProject, String className) {
        className = StringUtils.replaceFirst(moduleProject.getClassNameTemplate(), this.classesValueNameSeparator, className);
        
        File   directoryPathClass = getDirectoryPathModuleProject(moduleProject);
        String fileName           = String.format("%1$s%2$s%3$s.%4$s", directoryPathClass.getAbsolutePath(), File.separator, className, moduleProject.getClassExtension());
        File   file               = new File(fileName);
        
        if (file.exists()) {
            throw new RuntimeException(String.format("El fichero ya existe. Path:\"%1$s\"", file.getAbsolutePath()));
        }
        
        return new TemplateFile(file, moduleProject.stringReplaceTemplate(className));
    }
}
