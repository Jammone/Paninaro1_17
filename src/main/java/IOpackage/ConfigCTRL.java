package IOpackage;
import PaniBoss.PaniBoss;
import jammone.paninaro1_17.BossArea;
import jammone.paninaro1_17.Paninaro1_17;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import static IOpackage.LogCTRL.logFiller;
import static jammone.paninaro1_17.Paninaro1_17.ExportResource;

public class ConfigCTRL {

    public static void SetupConfigYAML(File directory) {

        /** file di Paninaro */
        //System.out.println(Pdir.getPath());
        if (!directory.exists()) {
            directory.mkdirs();
            File Pfile = new File(directory + "/config.yaml"); //file config.yaml
            if (!Pfile.exists()) {
                try {
                    ExportResource("/config.yaml");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.getMessage();
                    e.printStackTrace();
                    return;
                }
            }
        }
        //pdir esisteva: controllo il file
        File Cfile = Paninaro1_17.getCfile();
        if (!Cfile.exists()) {
            System.out.println("[Paninaro] il file config.yaml non esisteva, creato");
            try {
                ExportResource("/config.yaml");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.getMessage();
                e.printStackTrace();
            }
        }
        //il file esisteva, controllo se c'erano già region registrate
        try {
            readConfigYAML(Cfile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }


    public static void readConfigYAML(File file) throws Exception {
        if(!file.exists()){ System.out.println("[Paninaro] Errore lettura: il file non esiste!"); return; }
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        InputStream inputStream = new FileInputStream(file);
        Yaml y = new Yaml();
        LinkedHashMap<Object,Object> prefs =  y.load(inputStream );
        InputStream is = new FileInputStream(file);
        //LinkedHashMap<Object,Object>  aux =  y.load(is);
        HashMap<String,BossArea> boss2Bossarea = Paninaro1_17.getBossareas();
        ArrayList<String> operatorlist =  Paninaro1_17.getOperatorList();
        boss2Bossarea.clear();
        System.out.println("[Paninaro] Caricamento database in corso");

        ArrayList<HashMap<String,Object>>  regionmap =( ArrayList<HashMap<String,Object>>) prefs.get("regions");

        for(HashMap<String,Object> boss_item  : regionmap) {
            String boss_name = (String) boss_item.get("boss_name");
            int boss_respawntime = (int) boss_item.get("respawn_time");

            HashMap<String,Object> boss_spawn_coords = (HashMap<String,Object>) boss_item.get("boss_spawn_coords");
            Location boss_spawn_location = generateLocation(boss_spawn_coords);

            HashMap<String,Object> boss_warp_coords = (HashMap<String,Object>) boss_item.get("warp_coords");
            Location boss_warp_location = generateLocation(boss_warp_coords);

            HashMap<String,Object> boss_join_coords = (HashMap<String,Object>) boss_item.get("join_coords");
            Location boss_join_location = generateLocation(boss_join_coords);

            HashMap<String,Object> boss_leave_coords = (HashMap<String,Object>) boss_item.get("leave_coords");
            Location boss_leave_location = generateLocation(boss_leave_coords);

            int boss_join_npc_id = (int) boss_item.get("join_npc_id");
            int boss_leave_npc_id = (int) boss_item.get("leave_npc_id");
            if(boss_join_npc_id == -1 || boss_leave_npc_id == -1){
                System.out.println("[Paninaro] Attenzione! NPC di entrata e/o uscita di "+ boss_name+" non " +
                        "inizializzati! Configurarli il prima possibile");
            }
            int max_num_players = (int) boss_item.get("max_num_players");

            PaniBoss newBoss = new PaniBoss(boss_name,boss_spawn_location, boss_respawntime);
            BossArea newBossArea = new BossArea(boss_warp_location, newBoss,max_num_players,
                    boss_join_npc_id,boss_leave_npc_id,boss_join_location,boss_leave_location );
            System.out.println("[Paninaro1_17] Generato nuova bossArea");
            System.out.println(newBossArea.toString());
            Paninaro1_17.getBossareas().put(boss_name.toLowerCase(Locale.ROOT),newBossArea);
            ArrayList<String>  operatorslist =(ArrayList<String>) prefs.get("operators");
            Paninaro1_17.getOperatorList().addAll(operatorslist);
        }
    }


    private static Location generateLocation(HashMap<String, Object> location_hashmap){
        World world= Bukkit.getServer().getWorld((String)location_hashmap.get("world"));
        double[] location_vector = new double[]{
                (double) (Integer) location_hashmap.get("x"),
                (double)(Integer) location_hashmap.get("y"),
                (double)(Integer) location_hashmap.get("z"),
                (double)(Integer) location_hashmap.get("yaw"),
                (double)(Integer) location_hashmap.get("pitch")};
        Location ret =  new Location(world,
                location_vector[0],
                location_vector[1],
                location_vector[2],
                (float)location_vector[3],
                (float)location_vector[4]);
        return  ret;
    }
}
