package red.softn.utils.files;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

public class ModuleProject {
    
    public static final String KEY_MODULES = "project.modules.";
    
    public static final String KEY_CLASSES_TEMPLATE_PATH = "project.classes.template.path.%1$s";
    
    public static final String KEY_CLASSES_TEMPLATE_NAME = "project.classes.template.name.%1$s";
    
    public static final String KEY_CLASSES_TEMPLATE_TYPE = "project.classes.template.type.%1$s";
    
    public static final String KEY_CLASSES_TEMPLATE_REPLACE = "project.classes.template.replace.%1$s";
    
    public static final String REGEX_CLASS_NAME = "#\\{className\\}";
    
    public static final String REGEX_CLASS_WITH_PACKAGE = "#\\{classWithPackage\\}";
    
    public static final String REGEX_ONLY_PACKAGE = "#\\{onlyPackage\\}";
    
    private static final String REGEX_CLASS_NAME_FINAL = "#\\{classNameFinal\\}";
    
    private String directoryName;
    
    private String packageName;
    
    /**
     * Por ejemplo: de la key "project.modules.10", el valor de "keyProperty" seria solo "10"
     */
    private String keyProperty;
    
    private File classTemplateFile;
    
    private String classNameTemplate;
    
    private Map<Integer, String> stringReplaceTemplate;
    
    private String classExtension;
    
    public ModuleProject() {
        this.stringReplaceTemplate = new TreeMap<>();
    }
    
    public void addStringReplaceTemplate(int key, String value) {
        if (this.stringReplaceTemplate.containsKey(key)) {
            throw new RuntimeException("La clave ya existe.");
        }
        
        this.stringReplaceTemplate.put(key, value);
    }
    
    public String stringReplaceTemplate(String className, String classWithPackage, String onlyPackage, String classNameFinal) {
        try {
            String fileString = FileUtils.readFileToString(this.classTemplateFile, "UTF-8");
            String[] valueList = this.stringReplaceTemplate.entrySet()
                                                           .stream()
                                                           .map(Map.Entry::getValue)
                                                           .map(value -> replaceRegex(value, className, classWithPackage, onlyPackage, classNameFinal))
                                                           .toArray(String[]::new);
            
            fileString = replaceRegex(fileString, className, classWithPackage, onlyPackage, classNameFinal);
            
            if (valueList.length == 0) {
                return fileString;
            }
            
            return String.format(fileString, valueList);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
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
    
    public void setStringReplaceTemplate(Map<Integer, String> stringReplaceTemplate) {
        this.stringReplaceTemplate = stringReplaceTemplate;
    }
    
    public String replaceRegex(String value, String className, String classPackage, String onlyPackage, String classNameFinal) {
        value = replaceClassNameFinal(value, classNameFinal);
        value = replaceClassWithPackage(value, classPackage);
        value = replaceValueClassName(value, className);
        value = replaceOnlyPackage(value, onlyPackage);
        
        return value;
    }
    
    private String replaceClassWithPackage(String value, String classPackage) {
        return StringUtils.replaceAll(value, REGEX_CLASS_WITH_PACKAGE, classPackage);
    }
    
    private String replaceOnlyPackage(String value, String onlyPackage) {
        return StringUtils.replaceAll(value, REGEX_ONLY_PACKAGE, onlyPackage);
    }
    
    private String replaceClassNameFinal(String value, String classNameFinal) {
        return StringUtils.replaceAll(value, REGEX_CLASS_NAME_FINAL, classNameFinal);
    }
    
    private String replaceValueClassName(String value, String className) {
        return StringUtils.replaceAll(value, REGEX_CLASS_NAME, className);
    }
}
