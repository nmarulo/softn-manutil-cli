package red.softn.utils.properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import red.softn.utils.files.FileHelper;
import red.softn.utils.files.TemplateFile;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class ProjectManagerProperties {
    
    private MasterProperties masterProperties;
    
    public ProjectManagerProperties(String fileProperties) {
        Properties properties = new Properties();
        
        try {
            properties.load(new FileInputStream(FileHelper.stringToFile(fileProperties)));
            this.masterProperties = new MasterProperties(properties);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public void createClasses(String className) {
        createFiles(className, this.masterProperties.getProjectModuleList());
    }
    
    public void createClassesModule(String ClassName, String moduleName) {
        createFiles(ClassName, getProjectModule(moduleName.split(",")));
    }
    
    private List<ProjectModuleProperties> getProjectModule(String[] moduleName) {
        if (!anyMatchProjectModuleDirectory(moduleName)) {
            throw new RuntimeException(String.format("El nombre del directorio \"%1$s\" no existe en el fichero \"properties\".", StringUtils.join(moduleName, ",")));
        }
        
        return this.masterProperties.getProjectModuleList()
                                    .stream()
                                    .filter(value -> Arrays.stream(moduleName)
                                                           .anyMatch(value.getDirectoryName()::equals))
                                    .collect(Collectors.toList());
    }
    
    private boolean anyMatchProjectModuleDirectory(String[] moduleName) {
        return this.masterProperties.getProjectModuleList()
                                    .stream()
                                    .map(ProjectModuleProperties::getDirectoryName)
                                    .anyMatch(value -> Arrays.stream(moduleName)
                                                             .anyMatch(value::equals));
    }
    
    private void createFiles(String className, List<ProjectModuleProperties> projectModulePropertiesList) {
        TemplateFile[] files = null;
        
        try {
            files = prepareTemplateFiles(className, projectModulePropertiesList);
            
            for (TemplateFile file : files) {
                FileUtils.writeStringToFile(file.getFile(), file.getContent(), file.getEncoding());
            }
        } catch (Exception ex) {
            if (!deleteFiles(files, className)) {
                throw new RuntimeException(ex);
            }
        }
    }
    
    private TemplateFile[] prepareTemplateFiles(String classNameCli, List<ProjectModuleProperties> projectModulePropertiesList) {
        String[] classPackages = null;
        
        if (StringUtils.contains(classNameCli, PropertyConstants.VALUE_DOT_SEPARATOR)) {
            classPackages = StringUtils.split(classNameCli, PropertyConstants.REGEX_DOT_SEPARATOR);
        }
        
        String className        = getClassNameFromClassPackages(classPackages, classNameCli);
        String classPackagePath = joiningPackage(classPackages, File.separator, className);
        String onlyPackage      = joiningPackage(classPackages, this.masterProperties.getProjectPackageSeparator(), className);
        
        return projectModulePropertiesList.stream()
                                          .map(value -> createTemplateFile(value, classPackagePath, onlyPackage, className))
                                          .toArray(TemplateFile[]::new);
        
    }
    
    private String joiningPackage(String[] classPackages, String separator, String className) {
        if (classPackages == null || classPackages.length == 0) {
            return "";
        }
        
        return StringUtils.removeEnd(Arrays.stream(classPackages)
                                           .collect(Collectors.joining(separator)), separator.concat(className));
    }
    
    private TemplateFile createTemplateFile(ProjectModuleProperties projectModuleProperties, String classPackagePath, String onlyPackage, String className) {
        String classNameFinal = StringUtils.replaceAll(projectModuleProperties.getProjectClassesTemplateName(), ProjectModuleProperties.REGEX_CLASS_NAME, className);
        String directoryPath  = getDirectoryPathModuleProject(projectModuleProperties);
        String fileName       = String.format("%1$s%2$s%3$s%2$s%4$s.%5$s", directoryPath, File.separator, classPackagePath, classNameFinal, projectModuleProperties.getProjectClassesTemplateType());
        File   file           = new File(fileName);
        
        if (file.exists()) {
            throw new RuntimeException(String.format("El fichero ya existe. Verifique la ruta: %1$s", file.getAbsolutePath()));
        }
        
        String classWithPackage = this.masterProperties.getProjectPackageSeparator()
                                                       .concat(classNameFinal);
        
        if (StringUtils.isNotEmpty(onlyPackage)) {
            onlyPackage = this.masterProperties.getProjectPackageSeparator()
                                               .concat(onlyPackage);
            classWithPackage = String.format("%2$s%1$s%3$s", this.masterProperties.getProjectPackageSeparator(), onlyPackage, classNameFinal);
        }
        
        String content = projectModuleProperties.getContentFileTemplate(className, classWithPackage, onlyPackage, classNameFinal, this.masterProperties.getProjectPackageSeparator());
        
        return new TemplateFile(file, content);
    }
    
    private String getDirectoryPathModuleProject(ProjectModuleProperties projectModuleProperties) {
        StringBuilder directory = new StringBuilder(this.masterProperties.getProjectDirectory()
                                                                         .getAbsolutePath());
        directory.append(File.separator);
        directory.append(projectModuleProperties.getDirectoryName());
        directory.append(File.separator);
        directory.append(removeSlashes(this.masterProperties.getProjectDirectoryPackages()));
        directory.append(splitPackages(this.masterProperties.getProjectMainPackages(), PropertyConstants.REGEX_DOT_SEPARATOR));
        directory.append(splitPackages(projectModuleProperties.getPackageNames(), PropertyConstants.REGEX_DOT_SEPARATOR));
        
        return FileHelper.stringToDirectory(directory.toString())
                         .getAbsolutePath();
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
    
    private String getClassNameFromClassPackages(String[] classPackages, String defaultValue) {
        if (classPackages == null || classPackages.length == 0) {
            return defaultValue;
        }
        
        return classPackages[classPackages.length - 1];
    }
    
    private boolean deleteFiles(TemplateFile[] files, String className) {
        if (files == null) {
            return false;
        }
        
        List<File> filesErrorDelete = new LinkedList<>();
        File[] filesExist = Arrays.stream(files)
                                  .map(TemplateFile::getFile)
                                  .filter(File::exists)
                                  .map(value -> mapFileToDirectory(value, className))
                                  .toArray(File[]::new);
        
        for (File file : filesExist) {
            if (!FileUtils.deleteQuietly(file)) {
                filesErrorDelete.add(file);
            }
        }
        
        if (!filesErrorDelete.isEmpty()) {
            String filePaths = filesErrorDelete.stream()
                                               .map(File::getAbsolutePath)
                                               .collect(Collectors.joining(" - "));
            throw new RuntimeException(String.format("No se lograron eliminar los siguientes ficheros, por favor, borrarlos manualmente, rutas: %1$s", filePaths));
        }
        
        return true;
    }
    
    private File mapFileToDirectory(File file, String className) {
        if (StringUtils.contains(className, PropertyConstants.VALUE_DOT_SEPARATOR)) {
            File parent = file.getParentFile();
            int  count  = StringUtils.split(className, PropertyConstants.REGEX_DOT_SEPARATOR).length - 1;
            
            while (count > 1) {
                parent = parent.getParentFile();
                --count;
            }
            
            return parent;
        }
        
        return file;
    }
}
