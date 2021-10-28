package IOpackage;

import jammone.paninaro1_17.Paninaro1_17;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LogCTRL {


    public static void logFiller(String string) {
        File p = new File(Paninaro1_17.class.getProtectionDomain().getCodeSource().getLocation().getPath().replaceAll("%20", " "));
        File Pdir = new File(p.getParentFile() + "/Paninaro"); //folder
        File Lfile = new File(Pdir + "/log.txt"); //file di log
        if (Lfile.exists()) {
            try {
                FileWriter myWriter = new FileWriter(Lfile, true);
                myWriter.write(string);
                myWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            try {
                Lfile.createNewFile();
                FileWriter myWriter = new FileWriter(Lfile, true);
                myWriter.write(string);
                myWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
