package comandi;


import jammone.paninaro1_17.BossArea;
import jammone.paninaro1_17.Paninaro1_17;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;

public class BossCommandHandler implements CommandExecutor {
    private final String _STATUS = "status";
    private final String _HELP = "help";
    private final String _BOSSLIST = "list";
    private final String _CREDITS = "credits";


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player p = (Player) sender;
        if (args.length == 0) {
            p.sendMessage(ChatColor.YELLOW + "" + "Comando errato, scrivi " +
                    ChatColor.AQUA + "" + "/boss help" + ChatColor.YELLOW + "" +
                    " per vedere la lista dei comandi");
            return false;
        }
        switchCommand(p,args);
        return false;
    }

    private void switchCommand(Player p,String[] args) {
        HashMap<String, BossArea> bossareas = Paninaro1_17.getBossareas();
        switch (args[0].toLowerCase(Locale.ROOT)){
            case _HELP:
                p.sendMessage(ChatColor.GREEN+""+ "<---------------------------------------->");
                p.sendMessage(ChatColor.BLUE +""+ "/boss "+ChatColor.AQUA +""+ "<nome_boss>"+ ChatColor.GREEN +""+": ti teletrasporta dal boss \n"+
                        ChatColor.BLUE +""+ "/boss"+ChatColor.AQUA +""+" list"+ ChatColor.GREEN +""+": ti elenca tutti i boss attualmente esistenti \n"+
                        ChatColor.BLUE +""+ "/boss"+ChatColor.AQUA +""+" credits"+ ChatColor.GREEN +""+": informazioni sul plugin");
                p.sendMessage(ChatColor.GREEN+""+"<---------------------------------------->");
                break;
            case _BOSSLIST:
                p.sendMessage(ChatColor.GREEN +""+"<---- Lista PaniBoss ---->");
                for(String boss: bossareas.keySet()){
                    p.sendMessage("- "+ChatColor.GREEN+""+boss);
                }
                p.sendMessage(ChatColor.GREEN +""+"<-------------------->");
                break;
            case _CREDITS:
                p.sendMessage(ChatColor.GREEN+""+ "<----------------CREDITS----------------->");
                p.sendMessage(ChatColor.GREEN +""+ "Il plugin"+ChatColor.RED+""+" Paninaro"+ChatColor.GREEN+""+", raggiungibile dal comando "+ChatColor.RED+""+"/boss"+ChatColor.GREEN+""
                        +" (per gli utenti) e "+ChatColor.AQUA+"" +"/paninaro"+ChatColor.GREEN+""+" (per gli operatori), è stato creato da "+ChatColor.RED+""+"Nonick96"+
                        ChatColor.GREEN+""+" aka "+ChatColor.RED+""+" Jammone "+ChatColor.GREEN+""+" ("+ChatColor.AQUA+""+"https://github.com/Jammone"+
                        ChatColor.GREEN+""+"). Vietata la distribuzione salvo approvazione del suddetto sviluppatore");
                p.sendMessage(ChatColor.GREEN+""+"<---------------------------------------->");
                break;
            default:
                if(bossareas.keySet().contains(args[0].toLowerCase(Locale.ROOT))){
                    BossArea bossArea = bossareas.get(args[0].toLowerCase(Locale.ROOT));
                    p.teleport(bossArea.getWarp_location());
                }else{
                    p.sendMessage(ChatColor.YELLOW+""+"Boss non esistente, la lista dei boss disponibili è:");
                    for(String boss: bossareas.keySet()){
                        p.sendMessage("- "+ChatColor.RED+""+boss);
                    }
                    p.sendMessage(ChatColor.YELLOW+""+"scrivi "+ChatColor.BLUE +""+ "/boss "+ChatColor.AQUA +""+" help"+ ChatColor.YELLOW +""+
                            " per vedere la lista dei comandi");
                }
                break;
        }
    }


  /*  public void statusCMD(Player p, BossArea bossArea){
        PaniBoss.STATUS status = bossArea.getStatus();
        if(status == PaniBoss.STATUS.MORTO){
            p.sendMessage(ChatColor.RED+""+"--> Il boss " + ChatColor.AQUA +"" +bossArea.getBossName()+ ChatColor.RED+""+" è morto, ma risorgerà!");
            return;
        }

        if(status == PaniBoss.STATUS.PAUSA){
            p.sendMessage(ChatColor.YELLOW+""+"--> Il boss " + ChatColor.AQUA +"" +bossArea.getBossName()+ ChatColor.YELLOW+""+" sta riprendendo fiato, tornerà presto");
            return;
        }

        if(status == PaniBoss.STATUS.LIBERO){
            p.sendMessage(ChatColor.GREEN+""+"--> Il boss " + ChatColor.AQUA +"" +bossArea.getBossName()+ ChatColor.GREEN+""+" è libero e attende un degno sfidante");
            return;
        }

        if(status == PaniBoss.STATUS.ATTESA){
            if(bossArea.isInQueue(p)){
                if(bossArea.getQueue().indexOf(p) ==0){
                    p.sendMessage(ChatColor.GREEN+""+"--> Il boss " + ChatColor.AQUA +"" +bossArea.getBossName()+ ChatColor.GREEN+""+" sta attendendo che TU lo raggiunga per sfidarlo, Sbrigati non farlo attendere!!");
                    return;
                }else{
                    p.sendMessage(ChatColor.YELLOW+""+"--> Il boss "+ ChatColor.AQUA +"" +bossArea.getBossName()+ ChatColor.YELLOW+""+" sta attendendo un altro giocatore");
                    return;
                }
            }
            p.sendMessage(ChatColor.YELLOW+""+"--> Il boss "+ ChatColor.AQUA +"" +bossArea.getBossName()+ ChatColor.YELLOW+""+" sta attendendo che il prossimo giocatore in coda lo raggiunga per sfidarlo!");
            return;
        }

        if(status== PaniBoss.STATUS.MANUTENZIONE){
            p.sendMessage(ChatColor.RED+""+"--> Il boss "+ ChatColor.AQUA +"" +bossArea.getBossName()+ ChatColor.GREEN+""+" è in manutenzione e non è disponibile a combattere");

        }

        if(status== PaniBoss.STATUS.OCCUPATO){
            p.sendMessage(ChatColor.YELLOW+""+"--> Il boss "+ ChatColor.AQUA +"" +bossArea.getBossName()+ ChatColor.YELLOW+""+" sta combattendo con uno o più giocatori!");
            return;
        }
    }*/

}

