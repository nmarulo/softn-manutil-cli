package red.softn.utils.cli;

import org.apache.commons.lang3.StringUtils;
import red.softn.utils.files.ProjectManager;

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
     *  h - [Opcional] Imprime la lista de comando disponibles.
     *  help - [Opcional] En caso de error, imprime la traza de la excepción.
     */
    public static void main(String[] args) {
        checkDebug(args);
        
        try {
            ProjectManagerCli projectManagerCli = new ProjectManagerCli(args);
            
            if (projectManagerCli.isNotOptionHelp()) {
                initProject(projectManagerCli);
            }
        } catch (Exception ex) {
            println(ex.getMessage(), true, ex);
        }
    }
    
    private static void initProject(ProjectManagerCli projectManagerCli) {
        String propertiesPath = projectManagerCli.getValueProperties();
        String moduleName     = projectManagerCli.getValueModule();
        String className      = projectManagerCli.getValueClass();
        
        println("Estableciendo fichero \"properties\"...");
        ProjectManager projectManager = new ProjectManager(propertiesPath);
        println("Creando clases...");
        
        if (StringUtils.isEmpty(moduleName)) {
            projectManager.createClasses(className);
        } else {
            projectManager.createClassModule(moduleName, className);
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
    
    private static void println(String value, boolean wait, Exception ex) {
        System.out.println(value);
        
        if (wait) {
            if (MODE_DEBUG) {
                ex.printStackTrace();
            }
            
            try (Scanner scanner = new Scanner(System.in)) {
                System.out.print("Pulse \"ENTER\" para continuar...");
                scanner.nextLine();
            }
        }
    }
    
}
