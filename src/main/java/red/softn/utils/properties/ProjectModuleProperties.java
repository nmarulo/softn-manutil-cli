package red.softn.utils.properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Map;

public class ProjectModuleProperties {
    
    public static final String REGEX_CLASS_NAME = "#\\{className\\}";
    
    public static final String REGEX_CLASS_WITH_PACKAGE = "#\\{classWithPackage\\}";
    
    public static final String REGEX_ONLY_PACKAGE = "#\\{onlyPackage\\}";
    
    private static final String REGEX_CLASS_NAME_FINAL = "#\\{classNameFinal\\}";
    
    /**
     * Por ejemplo: de la key "project.modules.10", el valor de "keyModule" seria solo "10"
     */
    private String keyModule;
    
    private String directoryName;
    
    private String packageNames;
    
    private File projectClassesTemplatePath;
    
    private String projectClassesTemplateName;
    
    private String projectClassesTemplateType;
    
    private Map<Integer, String> projectClassesTemplateReplace;
    
    public String getContentFileTemplate(String className, String classWithPackage, String onlyPackage, String classNameFinal) {
        try {
            String fileString = FileUtils.readFileToString(this.projectClassesTemplatePath, "UTF-8");
            String[] valueList = this.projectClassesTemplateReplace.entrySet()
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
    
    public String replaceRegex(String value, String className, String classPackage, String onlyPackage, String classNameFinal) {
        value = replaceClassNameFinal(value, classNameFinal);
        value = replaceClassWithPackage(value, classPackage);
        value = replaceValueClassName(value, className);
        value = replaceOnlyPackage(value, onlyPackage);
        
        return value;
    }
    
    public String getKeyModule() {
        return keyModule;
    }
    
    public void setKeyModule(String keyModule) {
        this.keyModule = keyModule;
    }
    
    public String getDirectoryName() {
        return directoryName;
    }
    
    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }
    
    public String getPackageNames() {
        return packageNames;
    }
    
    public void setPackageNames(String packageNames) {
        this.packageNames = packageNames;
    }
    
    public File getProjectClassesTemplatePath() {
        return projectClassesTemplatePath;
    }
    
    public void setProjectClassesTemplatePath(File projectClassesTemplatePath) {
        this.projectClassesTemplatePath = projectClassesTemplatePath;
    }
    
    public String getProjectClassesTemplateName() {
        return projectClassesTemplateName;
    }
    
    public void setProjectClassesTemplateName(String projectClassesTemplateName) {
        this.projectClassesTemplateName = projectClassesTemplateName;
    }
    
    public String getProjectClassesTemplateType() {
        return projectClassesTemplateType;
    }
    
    public void setProjectClassesTemplateType(String projectClassesTemplateType) {
        this.projectClassesTemplateType = projectClassesTemplateType;
    }
    
    public Map<Integer, String> getProjectClassesTemplateReplace() {
        return projectClassesTemplateReplace;
    }
    
    public void setProjectClassesTemplateReplace(Map<Integer, String> projectClassesTemplateReplace) {
        this.projectClassesTemplateReplace = projectClassesTemplateReplace;
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
