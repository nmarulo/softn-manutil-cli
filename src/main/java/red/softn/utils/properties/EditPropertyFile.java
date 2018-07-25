package red.softn.utils.properties;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.configuration2.Configuration;
import red.softn.utils.files.FileHelper;
import red.softn.utils.properties.objects.GenericPropertyDO;
import red.softn.utils.properties.objects.ModulePropertyDO;

import java.io.File;
import java.util.Map;

public class EditPropertyFile {
    
    private File propertyFile;
    
    private String json;
    
    private GenericPropertyDO genericPropertyDO;
    
    public EditPropertyFile(String fileName, String json) {
        this.propertyFile = FileHelper.stringToFile(fileName);
        this.json = json;
    }
    
    public void processJson() {
        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            this.genericPropertyDO = gsonBuilder.create()
                                                .fromJson(this.json, GenericPropertyDO.class);
        } catch (JsonSyntaxException jsonEx) {
            throw new JsonSyntaxException(String.format("El formato JSON es invalido. Verificar: %1$s", this.json), jsonEx);
        }
    }
    
    public void editPropertyFile() {
        try {
            FileHelper.updatePropertyFile(this.propertyFile, this::setProperties);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    private void setProperties(Configuration propertiesConfiguration) {
        propertiesConfiguration.clear();
        propertiesConfiguration.addProperty(PropertyConstants.KEY_DIRECTORY, this.genericPropertyDO.getProjectDirectory());
        propertiesConfiguration.addProperty(PropertyConstants.KEY_DIRECTORY_PACKAGES, this.genericPropertyDO.getProjectDirectoryPackages());
        propertiesConfiguration.addProperty(PropertyConstants.KEY_MAIN_PACKAGES, this.genericPropertyDO.getProjectMainPackages());
        propertiesConfiguration.addProperty(PropertyConstants.KEY_CLASS_TYPE, this.genericPropertyDO.getProjectClassType());
        propertiesConfiguration.addProperty(PropertyConstants.KEY_PROJECT_PACKAGE_SEPARATOR, this.genericPropertyDO.getProjectPackageSeparator());
        propertiesConfiguration.addProperty(PropertyConstants.KEY_MODULE_FORMAT_SEPARATOR, this.genericPropertyDO.getProjectModuleFormatSeparator());
        propertiesConfiguration.addProperty(PropertyConstants.KEY_MODULE_FORMAT_POSITION_DIRECTORY, this.genericPropertyDO.getProjectModuleFormatPositionDirectory());
        propertiesConfiguration.addProperty(PropertyConstants.KEY_MODULE_FORMAT_POSITION_PACKAGE, this.genericPropertyDO.getProjectModuleFormatPositionPackage());
        
        this.genericPropertyDO.getProjectModules()
                              .forEach(value -> this.setPropertiesModules(value, propertiesConfiguration));
    }
    
    private void setPropertiesModules(ModulePropertyDO modulePropertyDO, Configuration propertiesConfiguration) {
        Map<Integer, String> replace = modulePropertyDO.getProjectClassesTemplateReplace();
        
        //project.modules.0=module-a:base.module
        String moduleName      = modulePropertyDO.getProjectModuleName();
        String modulePackage   = modulePropertyDO.getProjectModulePackage();
        String moduleSeparator = propertiesConfiguration.getString(PropertyConstants.KEY_MODULE_FORMAT_SEPARATOR);
        String projectModule   = String.format("%1$s%2$s%3$s", moduleName, moduleSeparator, modulePackage);
        String keyProperty     = String.format("%1$s%2$s", AProperty.KEY_MODULES, modulePropertyDO.getProjectModuleId());
        propertiesConfiguration.addProperty(keyProperty, projectModule);
        
        //project.classes.template.path.0
        keyProperty = String.format("%1$s%2$s", AProperty.KEY_CLASSES_TEMPLATE_PATH, modulePropertyDO.getProjectModuleId());
        propertiesConfiguration.addProperty(keyProperty, modulePropertyDO.getProjectClassesTemplatePath());
        
        //project.classes.template.name.0
        keyProperty = String.format("%1$s%2$s", AProperty.KEY_CLASSES_TEMPLATE_NAME, modulePropertyDO.getProjectModuleId());
        propertiesConfiguration.addProperty(keyProperty, modulePropertyDO.getProjectClassesTemplateName());
        
        if (modulePropertyDO.getProjectClassesTemplateType() != null) {
            //project.classes.template.type.0
            keyProperty = String.format("%1$s%2$s", AProperty.KEY_CLASSES_TEMPLATE_TYPE, modulePropertyDO.getProjectModuleId());
            propertiesConfiguration.addProperty(keyProperty, modulePropertyDO.getProjectClassesTemplateType());
        }
        
        if (!replace.isEmpty()) {
            replace.forEach((key, value) -> {
                //project.classes.template.replace.0.0
                String keyPropertyReplace = String.format("%1$s%2$s.%3$s", AProperty.KEY_CLASSES_TEMPLATE_REPLACE, modulePropertyDO.getProjectModuleId(), key);
                propertiesConfiguration.addProperty(keyPropertyReplace, value);
            });
        }
    }
}
