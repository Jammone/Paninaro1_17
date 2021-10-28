package IOpackage;


import jammone.paninaro1_17.BossArea;
import jammone.paninaro1_17.Paninaro1_17;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static IOpackage.LogCTRL.logFiller;
import static com.google.gson.internal.bind.util.ISO8601Utils.format;

public class OfflineCTRL {

    public static void SetupOFFLINEYAML(File directory) {
        if (!directory.exists()) {
            directory.mkdirs();
            File p2kFile = new File(directory + "/players2kick.yaml"); //file paninaro.yml
            if (!p2kFile.exists()) {
                //il file offlinse_status.yaml non esistava
                try {
                    Paninaro1_17.ExportResource("/players2kick.yaml");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.getMessage();
                    e.printStackTrace();
                }
                return;
            }
        }
        if (!directory.exists()) {
            directory.mkdirs();
            File bannedFile = new File(directory + "/banned_players.yaml"); //file paninaro.yml
            if (!bannedFile.exists()) {
                //il file offlinse_status.yaml non esistava
                try {
                    Paninaro1_17.ExportResource("/banned_players.yaml");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.getMessage();
                    e.printStackTrace();
                }
                return;
            }
        }

        //pdir esisteva: controllo il file offline_status
        File P2kFile = Paninaro1_17.getP2kFile();
        File BpFile = Paninaro1_17.getBpFile();

        if (!P2kFile.exists()) {
            System.out.println("[Paninaro] il file P2kFile.yaml non esisteva, creato");
            try {
                Paninaro1_17.ExportResource("/players2kick.yaml");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.getMessage();
                e.printStackTrace();
            }
            return;
        }
        if (!BpFile.exists()) {
            System.out.println("[Paninaro] il file BpFile.yaml non esisteva, creato");
            try {
                Paninaro1_17.ExportResource("/banned_players.yaml");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.getMessage();
                e.printStackTrace();
            }
            return;
        }
        //il file esisteva, controllo se c'erano già region registrate
        try {
            readOfflineYAML();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public static void readOfflineYAML() throws Exception {
        File P2kFile = Paninaro1_17.getP2kFile();
        File BpFile = Paninaro1_17.getBpFile();

        if (!P2kFile.exists()|| !BpFile.exists()) {
            System.out.println("[Paninaro] Errore lettura: il file non esiste!");
            return;
        }
        InputStream inputStreamP2k = new FileInputStream(P2kFile);
        InputStream inputStreamBp = new FileInputStream(BpFile);
        Yaml y = new Yaml();
        ArrayList<Object> p2kset = y.load(inputStreamP2k);
        System.out.println(p2kset == null);
        ArrayList<Object> bpset = y.load(inputStreamBp);
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        System.out.println("/----Player2Kick STATUS ----/");
        for(Object boss_fields: p2kset){
            LinkedHashMap<String, Object> campo = (LinkedHashMap<String, Object>) boss_fields;
            String boss_name = (String) campo.get("boss_name");
            System.out.println("nome_boss: " + boss_name);
            ArrayList<String> players_to_kick = (ArrayList<String>) campo.get("players");
            if( players_to_kick!= null) {
                if (!Paninaro1_17.getBossareas().containsKey(boss_name.toLowerCase(Locale.ROOT))) {
                    System.out.println("[Paninaro]ERRORE lettura offline status: boss_name non trovato! lista boss");
                    System.out.println(Paninaro1_17.getBossareas().keySet().toString());
                    return;
                }
                BossArea bossArea = Paninaro1_17.getBossareas().get(boss_name.toLowerCase(Locale.ROOT));
                for (String player2kick : players_to_kick) {
                    bossArea.getPlayer2kick().add(player2kick);
                }
                System.out.println(bossArea.getPlayer2kick().toString());
            }
        }
        System.out.println("/----BannedPlayers STATUS ----/");
        for(Object boss_fields: bpset){
            LinkedHashMap<String, Object> campo = (LinkedHashMap<String, Object>) boss_fields;
            String boss_name = (String) campo.get("boss_name");
            System.out.println("nome_boss: " + boss_name);
            ArrayList<ArrayList<String>> playersList = (ArrayList<ArrayList<String>>) campo.get("players");
            if(playersList != null) {
                if (!Paninaro1_17.getBossareas().containsKey(boss_name.toLowerCase(Locale.ROOT))) {
                    System.out.println("[Paninaro]ERRORE lettura offline status: boss_name non trovato!");
                    return;
                }
                BossArea bossArea = Paninaro1_17.getBossareas().get(boss_name.toLowerCase(Locale.ROOT));
                for (ArrayList<String> item : playersList) {
                    System.out.println(item.get(0) + " " + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(item.get(1)));
                    bossArea.getKillerHashmap().put(item.get(0), new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(item.get(1)));
                }
                System.out.println(bossArea.getKillerHashmap());
            }
        }

    }

    public static void writeOffilineYAML() throws Exception{
        File P2kFile = Paninaro1_17.getP2kFile();
        File BpFile = Paninaro1_17.getBpFile();
        if (!P2kFile.exists()|| !BpFile.exists()) {
            System.out.println("[Paninaro] Errore lettura: il file non esiste!");
            return;
        }

        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        FileWriter fileWriter = new FileWriter(P2kFile,false);
        String towrite = "";
        //----------------------------------------
        for(BossArea bossArea: Paninaro1_17.getBossareas().values()) {
            towrite = towrite +"- boss_name: \"" + bossArea.getBoss().getNome()+"\"\n";
            towrite = towrite +"  players: \n";
            for(Player p : bossArea.getPlayersInside()){
              towrite = towrite + "   - \""+ p.getName()+"\"\n";
            }
            for(String p : bossArea.getPlayer2kick()){
                towrite = towrite + "   - \""+ p+"\"\n";
            }

        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(P2kFile));
        writer.write(towrite);

        writer.close();

        //----------------------------------------
        fileWriter = new FileWriter(BpFile,false);
        towrite = "";

        for(BossArea bossArea: Paninaro1_17.getBossareas().values()) {
            towrite = towrite +"- boss_name: \"" + bossArea.getBoss().getNome()+"\"\n";
            HashMap<String, Date> killerHashmap = bossArea.getKillerHashmap();
            towrite = towrite +"  players:\n";
            for(String killer : killerHashmap.keySet()){
                towrite = towrite + "   - [\""+killer+"\", \""+format.format(killerHashmap.get(killer))+"\"]\n";
            }
        }
        writer = new BufferedWriter(new FileWriter(BpFile));
        writer.write(towrite);
        writer.close();


        logFiller("[" + format.format(now) + "] Plugin in spegnimento, gli stati dei boss sono stati" +
                " correttamente caricati su file\n");
    }
}

