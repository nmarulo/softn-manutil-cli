package red.softn.utils.properties.objects;

import java.util.LinkedList;
import java.util.List;

public class GenericPropertyDO {
    
    private String projectDirectory;
    
    private String projectDirectoryPackages;
    
    private String projectMainPackages;
    
    private String projectClassType;
    
    private String projectPackageSeparator;
    
    private String projectModuleFormatSeparator;
    
    private String projectModuleFormatPositionDirectory;
    
    private String projectModuleFormatPositionPackage;
    
    private List<ModulePropertyDO> projectModules;
    
    public GenericPropertyDO() {
        this.projectModules = new LinkedList<>();
    }
    
    public String getProjectDirectory() {
        return projectDirectory;
    }
    
    public void setProjectDirectory(String projectDirectory) {
        this.projectDirectory = projectDirectory;
    }
    
    public String getProjectDirectoryPackages() {
        return projectDirectoryPackages;
    }
    
    public void setProjectDirectoryPackages(String projectDirectoryPackages) {
        this.projectDirectoryPackages = projectDirectoryPackages;
    }
    
    public String getProjectMainPackages() {
        return projectMainPackages;
    }
    
    public void setProjectMainPackages(String projectMainPackages) {
        this.projectMainPackages = projectMainPackages;
    }
    
    public String getProjectClassType() {
        return projectClassType;
    }
    
    public void setProjectClassType(String projectClassType) {
        this.projectClassType = projectClassType;
    }
    
    public String getProjectPackageSeparator() {
        return projectPackageSeparator;
    }
    
    public void setProjectPackageSeparator(String projectPackageSeparator) {
        this.projectPackageSeparator = projectPackageSeparator;
    }
    
    public String getProjectModuleFormatSeparator() {
        return projectModuleFormatSeparator;
    }
    
    public void setProjectModuleFormatSeparator(String projectModuleFormatSeparator) {
        this.projectModuleFormatSeparator = projectModuleFormatSeparator;
    }
    
    public String getProjectModuleFormatPositionDirectory() {
        return projectModuleFormatPositionDirectory;
    }
    
    public void setProjectModuleFormatPositionDirectory(String projectModuleFormatPositionDirectory) {
        this.projectModuleFormatPositionDirectory = projectModuleFormatPositionDirectory;
    }
    
    public String getProjectModuleFormatPositionPackage() {
        return projectModuleFormatPositionPackage;
    }
    
    public void setProjectModuleFormatPositionPackage(String projectModuleFormatPositionPackage) {
        this.projectModuleFormatPositionPackage = projectModuleFormatPositionPackage;
    }
    
    public List<ModulePropertyDO> getProjectModules() {
        return projectModules;
    }
    
    public void setProjectModules(List<ModulePropertyDO> projectModules) {
        this.projectModules = projectModules;
    }
}
