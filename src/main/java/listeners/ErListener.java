package listeners;

import IOpackage.CleanInv;
import jammone.paninaro1_17.BossArea;
import jammone.paninaro1_17.Paninaro1_17;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Boss;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static IOpackage.LogCTRL.logFiller;

public class ErListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void playerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        for(BossArea b : Paninaro1_17.getBossareas().values()){
            if(b.getPlayer2kick().contains(player.getName()) ){
                CleanInv.saveDeletedInventory(player);
                player.getInventory().clear();
                player.teleport(b.getLeave_location());
                player.sendMessage(ChatColor.YELLOW +""+"Ti sei disconnesso nell'arena: gli assistenti del boss ti hanno derubato" +
                        " e portato via. Stai attento la prossima volta!");
                b.getPlayer2kick().remove(player.getName());
            }
        }
    }


    @EventHandler
    public void PlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        for(BossArea b : Paninaro1_17.getBossareas().values()){
            if(b.getPlayersInside().contains(p)){
                Date now = new Date();
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                logFiller("[" + format.format(now) + "] ["+b.getBoss().getNome()+
                        "]["+b.getBoss().getStatus().toString()+"] Il player +"+p.getName()+
                        "si è disconnesso mentre era nell'arena") ;
                b.playerQuitEvent(p);
                return;
            }
            if(b.getQueue().contains(p)){
                b.playerQuitEvent(p);
            }
        }
    }


    @EventHandler
    public void playerInteract(NPCRightClickEvent e){
        Player player = e.getClicker();
        int npc_id = e.getNPC().getId();
        for(BossArea bossArea : Paninaro1_17.getBossareas().values()){
            if(bossArea.getJoin_npc_id() == npc_id){
                System.out.println("join request sent");
                bossArea.playerEnterRequest(player);
                return;
            }else if(bossArea.getLeave_npc_id() == npc_id){
                System.out.println("leave request sent");
                bossArea.playerLeaveRequest(player);
                return;
            }
        }
    }
}