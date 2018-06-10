package red.softn.utils.files;

import java.io.File;

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
}
