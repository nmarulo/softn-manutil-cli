package red.softn.utils.files.template;

import java.io.File;
import java.io.Serializable;
import java.net.URI;

@Deprecated
public class TestClassTemplate extends File implements Serializable {
    
    public void FirstMethod() {
        System.out.println("FirstMethod");
    }
    
    public TestClassTemplate(String pathname) {
        super(pathname);
    }
    
    public TestClassTemplate(String parent, String child) {
        super(parent, child);
    }
    
    public TestClassTemplate(File parent, String child) {
        super(parent, child);
    }
    
    public TestClassTemplate(URI uri) {
        super(uri);
    }
}
