package red.softn.utils.properties;

import com.google.gson.GsonBuilder;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.lang3.StringUtils;
import red.softn.utils.files.FileHelper;
import red.softn.utils.properties.objects.GenericPropertyDO;
import red.softn.utils.properties.objects.ModulePropertyDO;

import java.io.File;
import java.util.List;
import java.util.TreeMap;

public class PropertyFile {
    
    private File propertyFile;
    
    private GenericPropertyDO genericPropertyDO;
    
    public PropertyFile(String propertyFile) {
        this.propertyFile = FileHelper.stringToFile(propertyFile);
    }
    
    public void propertiesDO() throws Exception {
        this.genericPropertyDO = new GenericPropertyDO();
        Configuration configuration = FileHelper.getPropertiesConfiguration(this.propertyFile);
        
        this.genericPropertyDO.setProjectDirectory(configuration.getString(PropertyConstants.KEY_DIRECTORY));
        this.genericPropertyDO.setProjectDirectoryPackages(configuration.getString(PropertyConstants.KEY_DIRECTORY_PACKAGES));
        this.genericPropertyDO.setProjectMainPackages(configuration.getString(PropertyConstants.KEY_MAIN_PACKAGES));
        this.genericPropertyDO.setProjectClassType(configuration.getString(PropertyConstants.KEY_CLASS_TYPE));
        this.genericPropertyDO.setProjectPackageSeparator(configuration.getString(PropertyConstants.KEY_PROJECT_PACKAGE_SEPARATOR));
        this.genericPropertyDO.setProjectModuleFormatSeparator(configuration.getString(PropertyConstants.KEY_MODULE_FORMAT_SEPARATOR));
        this.genericPropertyDO.setProjectModuleFormatPositionDirectory(configuration.getString(PropertyConstants.KEY_MODULE_FORMAT_POSITION_DIRECTORY));
        this.genericPropertyDO.setProjectModuleFormatPositionPackage(configuration.getString(PropertyConstants.KEY_MODULE_FORMAT_POSITION_PACKAGE));
        
        String                 keyModules = StringUtils.removeEnd(AProperty.KEY_MODULES, ".");
        List<ModulePropertyDO> list       = this.genericPropertyDO.getProjectModules();
        configuration.getKeys(keyModules)
                     .forEachRemaining(value -> modulePropertiesDO(value, configuration, list));
        this.genericPropertyDO.setProjectModules(list);
    }
    
    private void modulePropertiesDO(String key, Configuration configuration, List<ModulePropertyDO> list) {
        ModulePropertyDO modulePropertyDO                 = new ModulePropertyDO();
        String           moduleId                         = StringUtils.removeStart(key, AProperty.KEY_MODULES);
        String           projectModule                    = configuration.getString(key);
        String           moduleFormatSeparator            = configuration.getString(PropertyConstants.KEY_MODULE_FORMAT_SEPARATOR);
        String[]         splitProjectModule               = StringUtils.split(projectModule, moduleFormatSeparator);
        int              positionDirectory                = configuration.getInt(PropertyConstants.KEY_MODULE_FORMAT_POSITION_DIRECTORY);
        int              positionPackage                  = configuration.getInt(PropertyConstants.KEY_MODULE_FORMAT_POSITION_PACKAGE);
        String           keyProjectClassesTemplatePath    = AProperty.KEY_CLASSES_TEMPLATE_PATH + moduleId;
        String           keyProjectClassesTemplateName    = AProperty.KEY_CLASSES_TEMPLATE_NAME + moduleId;
        String           keyProjectClassesTemplateType    = AProperty.KEY_CLASSES_TEMPLATE_TYPE + moduleId;
        String           keyProjectClassesTemplateReplace = AProperty.KEY_CLASSES_TEMPLATE_REPLACE + moduleId;
        
        modulePropertyDO.setProjectModuleId(Integer.parseInt(moduleId));
        modulePropertyDO.setProjectModuleName(splitProjectModule[positionDirectory]);
        modulePropertyDO.setProjectModulePackage(splitProjectModule[positionPackage]);
        modulePropertyDO.setProjectClassesTemplatePath(configuration.getString(keyProjectClassesTemplatePath));
        modulePropertyDO.setProjectClassesTemplateName(configuration.getString(keyProjectClassesTemplateName));
        modulePropertyDO.setProjectClassesTemplateType(configuration.getString(keyProjectClassesTemplateType));
        
        TreeMap<Integer, String> stringTreeMap = new TreeMap<>();
        configuration.getKeys(keyProjectClassesTemplateReplace)
                     .forEachRemaining(value -> templateReplace(value, keyProjectClassesTemplateReplace, configuration, stringTreeMap));
        
        modulePropertyDO.setProjectClassesTemplateReplace(stringTreeMap);
        list.add(modulePropertyDO);
    }
    
    private void templateReplace(String key, String keyProjectClassesTemplateReplace, Configuration configuration, TreeMap<Integer, String> map) {
        String pos = StringUtils.removeStart(key, keyProjectClassesTemplateReplace + ".");
        map.put(Integer.parseInt(pos), configuration.getString(key));
    }
    
    public String toJson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        
        return gsonBuilder.create()
                          .toJson(this.genericPropertyDO);
    }
}
