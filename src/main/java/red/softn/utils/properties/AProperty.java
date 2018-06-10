package red.softn.utils.properties;

import org.apache.commons.lang3.StringUtils;
import red.softn.utils.files.FileHelper;

import java.io.File;
import java.util.Properties;

public abstract class AProperty {
    
    protected static final String KEY_MODULES = "project.modules.";
    
    protected static final String KEY_CLASSES_TEMPLATE_PATH = "project.classes.template.path.";
    
    protected static final String KEY_CLASSES_TEMPLATE_NAME = "project.classes.template.name.";
    
    protected static final String KEY_CLASSES_TEMPLATE_TYPE = "project.classes.template.type.";
    
    protected static final String KEY_CLASSES_TEMPLATE_REPLACE = "project.classes.template.replace.";
    
    protected Properties masterProperties;
    
    public AProperty(Properties masterProperties) {
        this.masterProperties = masterProperties;
    }
    
    protected String[] mapLastKeyValue(String value) {
        String[] keySplit = StringUtils.split(value, PropertyConstants.REGEX_DOT_SEPARATOR);
        String   lastKey  = keySplit[keySplit.length - 1];
        
        if (!StringUtils.isNumeric(lastKey)) {
            throw new RuntimeException("Formato de clave incorrecto. El ultimo valor de la clave debe ser un numero. Comprobar la clave: ".concat(value));
        }
        
        return new String[] {
            lastKey,
            this.getProperty(value)
        };
    }
    
    protected File getPropertyParseFile(String stringFile) {
        return FileHelper.stringToFile(getProperty(stringFile));
    }
    
    protected int getPropertyParseInt(String key, String defaultValue) {
        return Integer.parseInt(getProperty(key, defaultValue));
    }
    
    protected String getProperty(String key) {
        String value = getProperty(key, "");
        
        if (StringUtils.isEmpty(value)) {
            throw new RuntimeException(String.format("La propiedad \"%1$s\" es requerida.", key));
        }
        
        return value;
    }
    
    protected String getProperty(String key, String defaultValue) {
        return this.masterProperties.getProperty(key, defaultValue);
    }
}
