package red.softn.utils.properties.objects;

import java.util.Map;

public class ModulePropertyDO {
    
    private int projectModuleId;
    
    private String projectModuleName;
    
    private String projectModulePackage;
    
    private String projectClassesTemplatePath;
    
    private String projectClassesTemplateName;
    
    private String projectClassesTemplateType;
    
    private Map<Integer, String> projectClassesTemplateReplace;
    
    public int getProjectModuleId() {
        return projectModuleId;
    }
    
    public void setProjectModuleId(int projectModuleId) {
        this.projectModuleId = projectModuleId;
    }
    
    public String getProjectModuleName() {
        return projectModuleName;
    }
    
    public void setProjectModuleName(String projectModuleName) {
        this.projectModuleName = projectModuleName;
    }
    
    public String getProjectModulePackage() {
        return projectModulePackage;
    }
    
    public void setProjectModulePackage(String projectModulePackage) {
        this.projectModulePackage = projectModulePackage;
    }
    
    public String getProjectClassesTemplatePath() {
        return projectClassesTemplatePath;
    }
    
    public void setProjectClassesTemplatePath(String projectClassesTemplatePath) {
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
}
