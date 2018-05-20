package red.softn.utils.files;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectManager {
    
    private File fileName;
    
    private ProjectManagerPropertiesFile projectManagerPropertiesFile;
    
    public ProjectManager(String fileName) {
        this.fileName = new File(fileName);
        
        if (!this.fileName.exists()) {
            throw new RuntimeException(String.format("El fichero no existe. Verifique la ruta: %1$s", this.fileName.getAbsolutePath()));
        }
    }
    
    public void initProperties() {
        this.projectManagerPropertiesFile = new ProjectManagerPropertiesFile(this.fileName);
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
            throw new RuntimeException(String.format("El directorio \"%1$s\" no existe.", moduleName));
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
        TemplateFile[] files = null;
        
        try {
            files = moduleProjectList.stream()
                                     .map(value -> prepareFileTemplate(value, className))
                                     .toArray(TemplateFile[]::new);
            
            for (TemplateFile file : files) {
                FileUtils.writeStringToFile(file.getFile(), file.getContent(), file.getEncoding());
            }
        } catch (Exception ex) {
            //Si falla uno se borran todos los ficheros creados.
            if (files == null || !deleteFiles(files)) {
                throw new RuntimeException(ex);
            }
        }
        
        return true;
    }
    
    private boolean deleteFiles(TemplateFile[] files) {
        List<File> filesErrorDelete = new LinkedList<>();
        File[] filesExist = Arrays.stream(files)
                                  .map(TemplateFile::getFile)
                                  .filter(File::exists)
                                  .toArray(File[]::new);
        
        for (File file : filesExist) {
            if (!FileUtils.deleteQuietly(file)) {
                filesErrorDelete.add(file);
            }
        }
        
        if (!filesErrorDelete.isEmpty()) {
            String filePaths = filesErrorDelete.stream()
                                               .map(File::getAbsolutePath)
                                               .collect(Collectors.joining(" "));
            throw new RuntimeException(String.format("No se lograron eliminar los siguientes ficheros, por favor, borrarlos manualmente, rutas: %1$s", filePaths));
        }
        
        return true;
    }
}
