package jammone.paninaro1_17;

import IOpackage.CTRLExecuter;
import IOpackage.OfflineCTRL;
import PaniBoss.PaniBoss;
import Tasks.BossWaitTask;
import Tasks.QueueScroller;
import comandi.BossCommandHandler;
import comandi.PaninaroCommandHandler;
import listeners.CombatListener;
import listeners.ErListener;
import org.bukkit.Bukkit;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static IOpackage.ConfigCTRL.SetupConfigYAML;
import static IOpackage.LogCTRL.logFiller;

public final class Paninaro1_17 extends JavaPlugin {

    private static Paninaro1_17 instance;
    public static Paninaro1_17 getInstance() {return instance; }
    public static File Configfile;
    public static File P2kFile;
    public static File BpFile;
    public static File directory;
    /**----------LISTE -----------*/
    private static ArrayList<String> operatorlist = new ArrayList<>();
    private static HashMap<String,BossArea> bossareas = new HashMap<>();
    public static ArrayList<Player> debugginPlayers = new ArrayList<>();
    public static LinkedHashMap<String, ArrayList<String>> player2kick = new LinkedHashMap<>();

    /****GETTERS****/
    public static ArrayList<String> getOperatorList() { return operatorlist;}
    public static ArrayList<Player> getDebugginPlayers() {
        return debugginPlayers;
    }
    public static HashMap<String,BossArea> getBossareas(){return bossareas;}
    public static LinkedHashMap<String, ArrayList<String>> getPlayer2kick() {
        return player2kick;
    }

    public static File getCfile() {
        return Configfile;
    }

    public static File getP2kFile() {
        return P2kFile;
    }

    public static File getBpFile() {
        return BpFile;
    }

    @Override
    public void onEnable() {
        System.out.println("PANINARO - PANINARO - PANINARO - PANINARO - PANINARO - PANINARO - ");
        instance = this;
        File p = new File(Paninaro1_17.class.getProtectionDomain().getCodeSource().getLocation().getPath().replaceAll("%20", " "));
        directory = new File(p.getParentFile() + "/Paninaro"); //folder
        P2kFile = new File(directory + "/players2kick.yaml");
        BpFile = new File(directory + "/banned_players.yaml");
        Configfile = new File(directory + "/config.yaml");
        Bukkit.getScheduler().scheduleSyncDelayedTask(Paninaro1_17.getInstance(),new CTRLExecuter(directory,1),20L*3);
        Bukkit.getScheduler().scheduleSyncDelayedTask(Paninaro1_17.getInstance(),new  CTRLExecuter(directory,2),20L*4);
        System.out.println("Tra 3 secondi avvierò la ricerca dei boss");
        this.getCommand("boss").setExecutor(new BossCommandHandler());
        this.getCommand("paninaro").setExecutor(new PaninaroCommandHandler());
        PluginManager pmg = getServer().getPluginManager();
        /* Sezione Handlers & GuiEvents */
        pmg.registerEvents(new ErListener(), this);
        pmg.registerEvents(new CombatListener(), this);

        // Start RadioSongPlayer playback
    }

    @Override
    public void onDisable() {
        try {
             OfflineCTRL.writeOffilineYAML();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }







    static public String ExportResource(String resourceName) throws Exception {
        InputStream stream = null;
        OutputStream resStreamOut = null;
        String jarFolder;
        try {
            stream = Paninaro1_17.class.getResourceAsStream(resourceName);//note that each / is a directory down in the "jar tree" been the jar the root of the tree
            if(stream == null) {
                System.out.println("[Paninaro] file non trovato!");
                throw new Exception("Cannot get resource \"" + resourceName + "\" from Jar file.");
            }
            int readBytes;
            byte[] buffer = new byte[4096];
            File p = new File(Paninaro1_17.class.getProtectionDomain().getCodeSource().getLocation().getPath().replaceAll("%20", " "));
            //jarFolder = new File(Paninaro.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile().getPath().replace('\\', '/');
            resStreamOut = new FileOutputStream(p.getParentFile() + "/Paninaro" + resourceName);
            while ((readBytes = stream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            stream.close();
            resStreamOut.close();
        }

        return  resourceName;
    }


    public static void giveMeaBreath(BossArea bossArea, int time, boolean isdead){
        BukkitScheduler scheduler = instance.getServer().getScheduler();
        BossWaitTask bw = new BossWaitTask(bossArea,isdead);
        PaniBoss.STATUS status = bossArea.getBoss().getStatus();
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        String bossname = bossArea.getBoss().getNome();
        int id = scheduler.scheduleSyncDelayedTask(Paninaro1_17.getInstance(), bw, 20 * time);
            logFiller("[" + format.format(now) + "] ["+bossname+"] Il boss ha creato l'evento con " +
                    "ID task: +"+ id+"\n");
        bossArea.getBoss().setTaskID(id);
        System.out.println("[Paninaro]Il boss "+ bossname+" ha generato la task "+ id);
    }

    public static void ScrollAQueue(Player player, BossArea bossArea){

        BukkitScheduler scheduler = instance.getServer().getScheduler();
        int i = scheduler.scheduleSyncDelayedTask(Paninaro1_17.getInstance(), new QueueScroller(player, bossArea), 20 * 30);
        bossArea.getBoss().setTaskID(i);
    }
}
