package red.softn.utils.files;

import java.io.File;

public class TemplateFile {
    
    private String content;
    
    private File file;
    
    private String encoding;
    
    public TemplateFile(File file, String content) {
        this.file = file;
        this.content = content;
        this.encoding = "UTF-8";
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public File getFile() {
        return file;
    }
    
    public void setFile(File file) {
        this.file = file;
    }
    
    public String getEncoding() {
        return encoding;
    }
    
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
}
