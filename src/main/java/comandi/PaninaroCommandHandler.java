package comandi;


import IOpackage.ConfigCTRL;
import IOpackage.OfflineCTRL;
import PaniBoss.PaniBoss;
import jammone.paninaro1_17.BossArea;
import jammone.paninaro1_17.Paninaro1_17;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Boss;
import org.bukkit.entity.Player;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static IOpackage.ConfigCTRL.readConfigYAML;
import static IOpackage.LogCTRL.logFiller;

public class PaninaroCommandHandler implements CommandExecutor {
    private final String _HELP = "help";
    private final String _SET = "set";
    private final String _GETQUEUE ="queue";
    private final String _RELOAD ="reload";
    private final String _BOSSLIST ="list";
    private final String _STATUS ="status";
    private final String _LOCK ="lock";
    private final String _CLEARQUEUE ="clearqueue";
    private final String _DEBUG = "debug";
    private final String _RESPAWN = "respawn";
    private final String _KILL = "kill";


    @Override
    public boolean onCommand( CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) return false;
        Player p = (Player) sender;
        if(!Paninaro1_17.getOperatorList().contains(p.getName())) {
            p.sendMessage(ChatColor.RED+""+"Non hai il permesso di usare il comando "+ ChatColor.DARK_RED+""+"/paninaro"+ChatColor.RED+""+
                    ", per le funzioni da utente utilizza il comando "+ ChatColor.DARK_RED+""+"/boss");
            return false;
        }
        if(args.length==0){
            p.sendMessage(ChatColor.RED+""+"Comando errato: scrivi "+ChatColor.AQUA+""+ "/paninaro help" +ChatColor.RED+""+" per ricevere la lista dei comandi");
            return false;
        }
        HashMap<String, BossArea> bossareas = Paninaro1_17.getBossareas();

        switch (args[0].toLowerCase(Locale.ROOT)){
            case _HELP:
                p.sendMessage(ChatColor.GREEN+""+ "<---------------------------------------->");
                p.sendMessage(ChatColor.BLUE +""+ "/paninaro "+ChatColor.AQUA +""+ "status <nome_boss>"+ ChatColor.GREEN +""+": ti teletrasporta dal boss \n"+
                        ChatColor.BLUE +""+ "/paninaro "+ChatColor.AQUA +""+ "queue <nome_boss>"+ ChatColor.GREEN +""+": coda del boss" +
                        ChatColor.BLUE +""+ "/paninaro "+ChatColor.AQUA +""+ "reload"+ ChatColor.GREEN +""+": reload" +
                        ChatColor.BLUE +""+ "/paninaro "+ChatColor.AQUA +""+ "kill <nome_boss>"+ ChatColor.GREEN +""+": uccide il boss" +
                        ChatColor.BLUE +""+ "/paninaro "+ChatColor.AQUA +""+ "spawn <nome_boss>"+ ChatColor.GREEN +""+": rianima il boss" );
                p.sendMessage(ChatColor.GREEN+""+"<---------------------------------------->");
                break;
            case _GETQUEUE:
                if(args.length!=2) {
                    p.sendMessage(ChatColor.YELLOW+""+"Comando errato: scrivi"+ ChatColor.AQUA+""+"/paninaro queue <nome_boss>+"+ ChatColor.YELLOW+""+" per leggere la coda al boss");
                    return  false;
                }
                if(bossareas.containsKey(args[1])) {
                    p.sendMessage(bossareas.get(args[1]).getQueue().toString());
                    return true;
                }
                p.sendMessage(ChatColor.YELLOW+""+"Comando errato: boss non trovato!");
                return false;
            case _RELOAD:
                reload(p);
                break;
            case _BOSSLIST:
                p.sendMessage(ChatColor.GREEN +""+"<---- Lista PaniBoss ---->");
                for(String boss: bossareas.keySet()){
                    p.sendMessage("- "+ChatColor.GREEN+""+boss +", "+bossareas.get(boss).getBoss().getStatus().toString());
                }
                p.sendMessage(ChatColor.GREEN +""+"<-------------------->");
                break;
            case _STATUS:
                status(p,args);
                break;
            case _LOCK:
                if(args.length!=2) {
                    p.sendMessage(ChatColor.YELLOW+""+"Comando errato: scrivi"+ ChatColor.AQUA+""+"/paninaro queue <nome_boss>+"+ ChatColor.YELLOW+""+" per leggere la coda al boss");
                    return  false;
                }
                if(bossareas.containsKey(args[1])) {
                    BossArea bossArea = bossareas.get(args[1]);
                    if(bossArea.getBoss().getStatus() != PaniBoss.STATUS.MANUTENZIONE) {
                        bossArea.evacuate();
                        bossArea.getBoss().setStatus(PaniBoss.STATUS.MANUTENZIONE);
                        return true;
                    }else{
                        bossArea.getBoss().setStatus(PaniBoss.STATUS.LIBERO);
                        return true;
                    }
                }
                break;
            case _CLEARQUEUE:
                if(args.length!=2) {
                    p.sendMessage(ChatColor.YELLOW+""+"Comando errato: scrivi"+ ChatColor.AQUA+""+"/paninaro queue <nome_boss>+"+ ChatColor.YELLOW+""+" per leggere la coda al boss");
                    return  false;
                }
                if(bossareas.containsKey(args[1])) {
                    bossareas.get(args[1]).getQueue().clear();
                    p.sendMessage(ChatColor.GREEN+""+"Coda di accesso al boss "+
                            ChatColor.AQUA+""+bossareas.get(args[1]).getBoss().getNome()+
                            ChatColor.GREEN+""+" cancellata");
                    return true;
                }
                p.sendMessage(ChatColor.YELLOW+""+"Comando errato: boss non trovato!");
                return false;
            case _RESPAWN:
                if(args.length!=2) {
                    p.sendMessage(ChatColor.YELLOW+""+"Comando errato: scrivi"+ ChatColor.AQUA+""+"/paninaro queue <nome_boss>+"+ ChatColor.YELLOW+""+" per leggere la coda al boss");
                    return  false;
                }
                if(bossareas.containsKey(args[1])) {
                    PaniBoss boss = bossareas.get(args[1]).getBoss();
                    boss.getNpc().spawn(boss.getRespawn_location());
                    p.sendMessage(ChatColor.GREEN+""+"Boss "+ ChatColor.AQUA+""+boss.getNome()+
                            ChatColor.GREEN+""+" respawnato");
                }
                break;
            case _DEBUG:
                break;
            case _KILL:
                if(args.length!=2) {
                    p.sendMessage(ChatColor.YELLOW+""+"Comando errato: scrivi"+ ChatColor.AQUA+""+"/paninaro queue <nome_boss>+"+ ChatColor.YELLOW+""+" per leggere la coda al boss");
                    return  false;
                }
                if(bossareas.containsKey(args[1])) {
                    PaniBoss boss = bossareas.get(args[1]).getBoss();
                    boss.getNpc().despawn();
                    p.sendMessage(ChatColor.GREEN+""+"Boss "+ ChatColor.AQUA+""+boss.getNome()+
                            ChatColor.GREEN+""+" despawnato");
                    return  true;
                }
                break;
            default:
                p.sendMessage("comando non registrato");
                break;
        }
        return false;
    }


    private void reload(Player p) {
        try {
            ConfigCTRL.readConfigYAML(Paninaro1_17.directory);
            OfflineCTRL.readOfflineYAML();
            p.sendMessage(ChatColor.GREEN+""+"Database ricaricato");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            p.sendMessage(ChatColor.RED+""+"Errore caricamento dal file, controllare i logs ");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean status(Player p, String[] args) {

        if(args.length!=2) {
            p.sendMessage(ChatColor.YELLOW+""+"Comando errato: scrivi "+ ChatColor.AQUA+""+"/paninaro status <nome_boss>"+ ChatColor.YELLOW+""+" per leggere la coda al boss");
            return  false;
        }
        HashMap<String, BossArea> bossareas = Paninaro1_17.getBossareas();
        String boss_name = args[1].toLowerCase(Locale.ROOT);

        if(!bossareas.containsKey(boss_name)){
            p.sendMessage(ChatColor.YELLOW+""+"Boss non esistente, la lista dei boss disponibili è:");
            for(String b: bossareas.keySet()){
                p.sendMessage("- "+ChatColor.RED+""+b);
            }
            return false;
        }
        BossArea bossArea = bossareas.get(boss_name);

        LinkedList<String> personedentro= new LinkedList<>();
        for(Player pl : bossArea.getPlayersInside()){
            personedentro.add(pl.getName());
        }
        LinkedList<String> personecoda= new LinkedList<>();
        for(Player pl : bossArea.getQueue()){
            personecoda.add(pl.getName());
        }

        PaniBoss boss = bossArea.getBoss();
        p.sendMessage(ChatColor.GREEN+""+ "<--------------------------------->");
        p.sendMessage(ChatColor.GREEN+""+"Boss: "+ChatColor.RED+""+boss.getNome()+
                ChatColor.GREEN+"");
        p.sendMessage("Status: " +boss.getStatus().toString()+"\n"+
                "-> persone dentro: "+ ChatColor.AQUA +"" +personedentro.toString());
        p.sendMessage(ChatColor.GREEN+""+"-> coda "+" ( "+
                ChatColor.AQUA+""+bossArea.getQueue().size()+" "+ChatColor.GREEN+""
                +((bossArea.getQueue().size()==1)?" persona): ":" persone) :") +
                ((bossArea.getQueue().size()==0)?"":ChatColor.AQUA +
                        "" +personecoda.toString())+"\n"+
                ChatColor.GREEN+""+"-> status : " +ChatColor.AQUA+""+
                boss.getStatus().toString()+"\n");
        return true;
    }


}
