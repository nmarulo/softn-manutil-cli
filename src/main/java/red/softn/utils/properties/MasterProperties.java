package red.softn.utils.properties;

import org.apache.commons.lang3.StringUtils;
import red.softn.utils.files.FileHelper;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class MasterProperties extends AProperty {
    
    private File projectDirectory;
    
    private String projectDirectoryPackages;
    
    private String projectMainPackages;
    
    private String projectClassType;
    
    private int projectModuleFormatPositionDirectory;
    
    private int projectModuleFormatPositionPackage;
    
    private String projectModuleFormatSeparator;
    
    private String projectPackageSeparator;
    
    private List<ProjectModuleProperties> projectModuleList;
    
    public MasterProperties(Properties properties) {
        super(properties);
        initProperties();
    }
    
    public File getProjectDirectory() {
        return projectDirectory;
    }
    
    public String getProjectDirectoryPackages() {
        return projectDirectoryPackages;
    }
    
    public String getProjectMainPackages() {
        return projectMainPackages;
    }
    
    public String getProjectClassType() {
        return projectClassType;
    }
    
    public int getProjectModuleFormatPositionDirectory() {
        return projectModuleFormatPositionDirectory;
    }
    
    public int getProjectModuleFormatPositionPackage() {
        return projectModuleFormatPositionPackage;
    }
    
    public String getProjectModuleFormatSeparator() {
        return projectModuleFormatSeparator;
    }
    
    public String getProjectPackageSeparator() {
        return projectPackageSeparator;
    }
    
    public List<ProjectModuleProperties> getProjectModuleList() {
        return projectModuleList;
    }
    
    private void initProperties() {
        setProjectDirectory();
        this.projectDirectoryPackages = getProperty(PropertyConstants.KEY_DIRECTORY_PACKAGES);
        this.projectMainPackages = getProperty(PropertyConstants.KEY_MAIN_PACKAGES);
        this.projectClassType = getProperty(PropertyConstants.KEY_CLASS_TYPE);
        this.projectPackageSeparator = getProperty(PropertyConstants.KEY_PROJECT_PACKAGE_SEPARATOR);
        setOptionalProperties();
        this.projectModuleList = this.masterProperties.stringPropertyNames()
                                                      .stream()
                                                      .filter(value -> StringUtils.startsWith(value, KEY_MODULES))
                                                      .map(this::instanceProjectModules)
                                                      .collect(Collectors.toList());
    }
    
    private ProjectModuleProperties instanceProjectModules(String keyProperty) {
        ProjectModuleProperties projectModuleProperties = new ProjectModuleProperties();
        String                  keyModule               = StringUtils.removeStart(keyProperty, KEY_MODULES);
        String[]                modulesValue            = getModuleDirectoryNamePackages(keyProperty);
        
        projectModuleProperties.setKeyModule(keyModule);
        projectModuleProperties.setDirectoryName(modulesValue[this.projectModuleFormatPositionDirectory]);
        projectModuleProperties.setPackageNames(modulesValue[this.projectModuleFormatPositionPackage]);
        projectModuleProperties.setProjectClassesTemplatePath(getPropertyParseFile(KEY_CLASSES_TEMPLATE_PATH.concat(keyModule)));
        projectModuleProperties.setProjectClassesTemplateName(getProperty(KEY_CLASSES_TEMPLATE_NAME.concat(keyModule)));
        projectModuleProperties.setProjectClassesTemplateType(getProperty(KEY_CLASSES_TEMPLATE_TYPE.concat(keyModule), this.projectClassType));
        projectModuleProperties.setProjectClassesTemplateReplace(getProjectClassesTemplateReplace(KEY_CLASSES_TEMPLATE_REPLACE.concat(keyModule)));
        
        return projectModuleProperties;
    }
    
    private Map<Integer, String> getProjectClassesTemplateReplace(String filterKey) {
        return this.masterProperties.stringPropertyNames()
                                    .stream()
                                    .filter(value -> StringUtils.startsWith(value, filterKey))
                                    .map(this::mapLastKeyValue)
                                    .collect(Collectors.toMap(value -> Integer.parseInt(value[0]), value -> value[1], (oldValue, newValue) -> newValue, TreeMap::new));
    }
    
    private String[] getModuleDirectoryNamePackages(String keyProperty) {
        String   propertyValue = getProperty(keyProperty);
        String[] modulesValue  = StringUtils.split(propertyValue, this.projectModuleFormatSeparator);
        
        if (modulesValue.length <= 1) {
            modulesValue = Arrays.copyOf(modulesValue, 2);
        }
        
        if (this.projectModuleFormatPositionDirectory >= modulesValue.length) {
            throw new RuntimeException("El valor de la posición del nombre del directorio es incorrecto. Comprobar la propiedad: ".concat(PropertyConstants.KEY_MODULE_FORMAT_POSITION_DIRECTORY));
        }
        
        if (this.projectModuleFormatPositionPackage >= modulesValue.length) {
            throw new RuntimeException("El valor de la posición de la ruta de los paquete es incorrecto. Comprobar la propiedad: ".concat(PropertyConstants.KEY_MODULE_FORMAT_POSITION_PACKAGE));
        }
        
        if (StringUtils.isEmpty(modulesValue[this.projectModuleFormatPositionDirectory])) {
            throw new RuntimeException("El nombre del directory no puede estar vacío. Comprobar la propiedad: ".concat(keyProperty));
        }
        
        return modulesValue;
    }
    
    private void setOptionalProperties() {
        this.projectModuleFormatPositionDirectory = getPropertyParseInt(PropertyConstants.KEY_MODULE_FORMAT_POSITION_DIRECTORY, PropertyConstants.DEFAULT_VALUE_MODULE_POSITION_DIRECTORY);
        this.projectModuleFormatPositionPackage = getPropertyParseInt(PropertyConstants.KEY_MODULE_FORMAT_POSITION_PACKAGE, PropertyConstants.DEFAULT_VALUE_MODULE_POSITION_PACKAGE);
        this.projectModuleFormatSeparator = getProperty(PropertyConstants.KEY_MODULE_FORMAT_SEPARATOR, PropertyConstants.DEFAULT_VALUE_MODULE_FORMAT_SEPARATOR);
        
        if (this.projectModuleFormatPositionDirectory == this.projectModuleFormatPositionPackage) {
            throw new RuntimeException(String.format("Los valores de las propiedades \"%1$s\" y \"%2$s\" no pueden ser iguales.", PropertyConstants.KEY_MODULE_FORMAT_POSITION_DIRECTORY, PropertyConstants.KEY_MODULE_FORMAT_POSITION_PACKAGE));
        }
        
        if (this.projectModuleFormatPositionDirectory < 0 || this.projectModuleFormatPositionDirectory > 1 || this.projectModuleFormatPositionPackage < 0 || this.projectModuleFormatPositionPackage > 1) {
            throw new RuntimeException(String.format("El valor de las propiedades \"%1$s\" y \"%2$s\", debe ser consecutivas (0, 1).", PropertyConstants.KEY_MODULE_FORMAT_POSITION_DIRECTORY, PropertyConstants.KEY_MODULE_FORMAT_POSITION_PACKAGE));
        }
    }
    
    private void setProjectDirectory() {
        this.projectDirectory = FileHelper.stringToDirectory(getProperty(PropertyConstants.KEY_DIRECTORY));
    }
    
}
