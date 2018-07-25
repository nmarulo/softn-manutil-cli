package red.softn.utils.files;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.BuilderParameters;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.builder.fluent.PropertiesBuilderParameters;

import java.io.File;
import java.util.function.Consumer;
import java.util.function.Function;

public class FileHelper {
    
    public static File stringToFile(String stringFile) {
        File file = new File(stringFile);
        
        if (!file.exists()) {
            throw new RuntimeException("El fichero no existe. Verifique la ruta: " + stringFile);
        }
        
        if (!file.isFile()) {
            throw new RuntimeException("No es un fichero. Verifique la ruta: " + stringFile);
        }
        
        return file;
    }
    
    public static File stringToDirectory(String stringDirectory) {
        File file = new File(stringDirectory);
        
        if (!file.exists()) {
            throw new RuntimeException("El directorio no existe. Verifique la ruta: " + stringDirectory);
        }
        
        if (!file.isDirectory()) {
            throw new RuntimeException("La ruta no es un directorio. Verifique la ruta: " + stringDirectory);
        }
        
        if (!file.canWrite()) {
            throw new RuntimeException("No se puede escribir en el directorio. Verifique la ruta: " + stringDirectory);
        }
        
        return file;
    }
    
    public static void updatePropertyFile(File propertyFile, Consumer<Configuration> consumer) throws Exception {
        updatePropertyFile(propertyFile, consumer, null);
    }
    
    public static void updatePropertyFile(File propertyFile, Consumer<Configuration> consumer, Function<PropertiesBuilderParameters, PropertiesBuilderParameters> function) throws Exception {
        BuilderParameters                                     builderParameters = parametersProperties(propertyFile, function);
        FileBasedConfigurationBuilder<FileBasedConfiguration> builder           = new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class).configure(builderParameters);
        
        consumer.accept(builder.getConfiguration());
        builder.save();
    }
    
    public static Configuration getPropertiesConfiguration(File propertyFile, Function<PropertiesBuilderParameters, PropertiesBuilderParameters> function) throws Exception {
        BuilderParameters                                     builderParameters = parametersProperties(propertyFile, function);
        FileBasedConfigurationBuilder<FileBasedConfiguration> builder           = new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class).configure(builderParameters);
        
        return builder.getConfiguration();
    }
    
    public static Configuration getPropertiesConfiguration(File propertyFile) throws Exception {
        return getPropertiesConfiguration(propertyFile, null);
    }
    
    private static PropertiesBuilderParameters parametersProperties(File propertyFile) {
        return parametersProperties(propertyFile, null);
    }
    
    private static PropertiesBuilderParameters parametersProperties(File propertyFile, Function<PropertiesBuilderParameters, PropertiesBuilderParameters> function) {
        Parameters parameters = new Parameters();
        PropertiesBuilderParameters propertiesBuilderParameters = parameters.properties()
                                                                            .setFile(propertyFile);
        
        if (function == null) {
            return propertiesBuilderParameters;
        }
        
        return function.apply(propertiesBuilderParameters);
    }
}
