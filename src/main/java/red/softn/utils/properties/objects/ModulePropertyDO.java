package red.softn.utils.properties.objects;

import java.util.TreeMap;

public class ModulePropertyDO {
    
    private int projectModuleId;
    
    private String projectModuleName;
    
    private String projectModulePackage;
    
    private String projectClassesTemplatePath;
    
    private String projectClassesTemplateName;
    
    private String projectClassesTemplateType;
    
    private TreeMap<Integer, String> projectClassesTemplateReplace;
    
    public ModulePropertyDO() {
        this.projectClassesTemplateReplace = new TreeMap<>();
    }
    
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
    
    public TreeMap<Integer, String> getProjectClassesTemplateReplace() {
        return projectClassesTemplateReplace;
    }
    
    public void setProjectClassesTemplateReplace(TreeMap<Integer, String> projectClassesTemplateReplace) {
        this.projectClassesTemplateReplace = projectClassesTemplateReplace;
    }
}
