package red.softn.utils.cli;

import org.apache.commons.lang3.StringUtils;
import red.softn.utils.properties.ProjectManagerProperties;

import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
    
    private static boolean MODE_DEBUG = false;
    
    /*
     * Comandos:
     *  p - [Requerido] Establece la ruta del fichero "properties".
     *  c - [Requerido] Establece el nombre que se le agregara a los ficheros.
     *  m - [Opcional] Establece el nombre del modulo. Solo creara las clases de este modulo.
     *  help - [Opcional] Imprime la lista de comando disponibles.
     *                      En este caso ya no serán obligatorias las opciones requeridas.
     *  debug - [Opcional] En caso de error, imprime la traza de la excepción.
     */
    public static void main(String[] args) {
        checkDebug(args);
        
        //--edit-properties -p ... -json ....
        //--create-classes -p ... -c ... -m ...
        //--help
        
        try {
            ProjectManagerCli projectManagerCli = new ProjectManagerCli(args);
            
            if (projectManagerCli.isNotOptionHelp()) {
                if (projectManagerCli.isHasOptCreateClasses()) {
                    initProject(projectManagerCli);
                } else if (projectManagerCli.isHasOptEditProperties()) {
                    initEditProperties(projectManagerCli);
                } else {
                    throw new Exception("Error desconocido.");
                }
            }
        } catch (Exception ex) {
            println(ex.getMessage(), ex);
        }
    }
    
    private static void initEditProperties(ProjectManagerCli projectManagerCli) {
        String propertiesPath = projectManagerCli.getValueProperties();
        String json           = projectManagerCli.getValueJson();
        
        println("Estableciendo fichero...");
        println("Procesando Json...");
        println("Editando fichero properties...");
        println("Finalizado correctamente.");
    }
    
    private static void initProject(ProjectManagerCli projectManagerCli) {
        String propertiesPath = projectManagerCli.getValueProperties();
        String moduleName     = projectManagerCli.getValueModule();
        String className      = projectManagerCli.getValueClass();
        
        println("Estableciendo fichero...");
        ProjectManagerProperties projectManagerProperties = new ProjectManagerProperties(propertiesPath);
        println("Estableciendo propiedades...");
        
        if (StringUtils.isEmpty(moduleName)) {
            println("Creando clases en todos los directorios...");
            projectManagerProperties.createClasses(className);
        } else {
            println(String.format("Creando clases para el directorio \"%1$s\"...", moduleName));
            projectManagerProperties.createClassesModule(className, moduleName);
        }
        
        println("Finalizado correctamente.");
    }
    
    private static void checkDebug(String[] arg) {
        String value = Arrays.stream(arg)
                             .collect(Collectors.joining(" "));
        MODE_DEBUG = StringUtils.containsIgnoreCase(value, "-debug");
    }
    
    private static void println(String value) {
        println(value, false, null);
    }
    
    private static void println(String value, Exception ex) {
        println(value, false, ex);
    }
    
    private static void println(String value, boolean wait, Exception exception) {
        System.out.println(value);
        
        if (MODE_DEBUG && exception != null) {
            exception.printStackTrace();
        }
        
        if (wait) {
            try (Scanner scanner = new Scanner(System.in)) {
                System.out.print("Pulse \"ENTER\" para continuar...");
                scanner.nextLine();
            }
        }
    }
    
}
