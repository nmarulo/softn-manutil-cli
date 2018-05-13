package red.softn.utils.files;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ModuleProject {
    
    public static final String KEY_MODULES = "project.modules.";
    
    public static final String KEY_CLASSES_TEMPLATE_PATH = "project.classes.template.path.%1$s";
    
    public static final String KEY_CLASSES_TEMPLATE_NAME = "project.classes.template.name.%1$s";
    
    public static final String KEY_CLASSES_TEMPLATE_TYPE = "project.classes.template.type.%1$s";
    
    public static final String KEY_CLASSES_TEMPLATE_REPLACE = "project.classes.template.replace.%1$s";
    
    private String directoryName;
    
    private String packageName;
    
    /**
     * Por ejemplo: de la key "project.modules.10", el valor de "keyProperty" seria solo "10"
     */
    private String keyProperty;
    
    private File classTemplateFile;
    
    private String classNameTemplate;
    
    private Map<String, String> stringReplaceTemplate;
    
    private String classExtension;
    
    public ModuleProject() {
        this.stringReplaceTemplate = new HashMap<>();
    }
    
    public void addStringReplaceTemplate(String value, String replace) {
        this.stringReplaceTemplate.put(value, replace);
    }
    
    public String stringReplaceTemplate(String className) {
        try {
            String fileString = FileUtils.readFileToString(this.classTemplateFile, "UTF-8");
            
            for (Map.Entry<String, String> entry : this.stringReplaceTemplate.entrySet()) {
                fileString = StringUtils.replaceAll(fileString, entry.getKey(), entry.getValue());
            }
            
            String classNameTemplate = FilenameUtils.getBaseName(this.classTemplateFile.getName());
            fileString = StringUtils.replaceAll(fileString, classNameTemplate, className);
            
            return fileString;
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
    
    public String getDirectoryName() {
        return directoryName;
    }
    
    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }
    
    public String getPackageName() {
        return packageName;
    }
    
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    
    public String getKeyProperty() {
        return keyProperty;
    }
    
    public void setKeyProperty(String keyProperty) {
        this.keyProperty = keyProperty;
    }
    
    public File getClassTemplateFile() {
        return classTemplateFile;
    }
    
    public void setClassTemplateFile(File classTemplateFile) {
        this.classTemplateFile = classTemplateFile;
        
        if (!this.classTemplateFile.exists() || !this.classTemplateFile.isFile()) {
            throw new RuntimeException("El fichero no existe.");
        }
    }
    
    public String getClassNameTemplate() {
        return classNameTemplate;
    }
    
    public void setClassNameTemplate(String classNameTemplate) {
        this.classNameTemplate = classNameTemplate;
    }
    
    public String getClassExtension() {
        return classExtension;
    }
    
    public void setClassExtension(String classExtension) {
        this.classExtension = classExtension;
    }
}
