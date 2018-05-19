package red.softn.utils.files;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectManager {
    
    private File pathProperties;
    
    private ProjectManagerPropertiesFile projectManagerPropertiesFile;
    
    public ProjectManager(String pathProperties) {
        this.pathProperties = new File(pathProperties);
        
        if (!this.pathProperties.exists()) {
            throw new RuntimeException(String.format("El fichero \"properties\" no existe. Ruta: %1$s", this.pathProperties.getAbsolutePath()));
        }
        
        this.projectManagerPropertiesFile = new ProjectManagerPropertiesFile(this.pathProperties);
        this.projectManagerPropertiesFile.initProject();
    }
    
    public boolean createClassModule(String moduleName, String className) {
        return createFiles(getModuleProject(moduleName), className);
    }
    
    public boolean createClasses(String className) {
        return createFiles(this.projectManagerPropertiesFile.getProjectModules(), className);
    }
    
    private TemplateFile prepareFileTemplate(ModuleProject moduleProject, String className) {
        return this.projectManagerPropertiesFile.getClassTemplateFile(moduleProject, className);
    }
    
    private List<ModuleProject> getModuleProject(String moduleName) {
        if (!checkModule(moduleName)) {
            throw new RuntimeException("El directorio no existe.");
        }
        
        return this.projectManagerPropertiesFile.getProjectModules()
                                                .stream()
                                                .filter(value -> value.getDirectoryName()
                                                           .equals(moduleName))
                                                .collect(Collectors.toList());
    }
    
    private boolean checkModule(String moduleName) {
        return this.projectManagerPropertiesFile.getProjectModules()
                                                .stream()
                                                .map(ModuleProject::getDirectoryName)
                                                .anyMatch(value -> value.equals(moduleName));
    }
    
    private boolean createFiles(List<ModuleProject> moduleProjectList, String className) {
        try {
            TemplateFile[] files = moduleProjectList.stream()
                                                    .map(value -> prepareFileTemplate(value, className))
                                                    .toArray(TemplateFile[]::new);
            
            for (TemplateFile file : files) {
                FileUtils.writeStringToFile(file.getFile(), file.getContent(), file.getEncoding());
            }
        } catch (Exception ex) {
            throw new RuntimeException("No se logro crear el fichero.", ex);
        }
        
        return true;
    }
}
