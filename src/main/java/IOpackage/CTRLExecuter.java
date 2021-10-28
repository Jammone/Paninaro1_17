package IOpackage;

import jammone.paninaro1_17.Paninaro1_17;

import java.io.File;

import static IOpackage.ConfigCTRL.SetupConfigYAML;

public class CTRLExecuter implements Runnable {
    private int i;
    File directory;

    public CTRLExecuter(File directory, int i) {
        this.i = i;
        this.directory = directory;
    }

    @Override
    public void run() {
        if(i==1){
            ConfigCTRL.SetupConfigYAML(directory);
        }else{
            OfflineCTRL.SetupOFFLINEYAML(directory);
        }
    }
}
