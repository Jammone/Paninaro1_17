package listeners;


import jammone.paninaro1_17.BossArea;
import jammone.paninaro1_17.Paninaro1_17;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static IOpackage.LogCTRL.logFiller;

public class CombatListener implements Listener {


    @EventHandler
    public void PlayerDied(PlayerDeathEvent e) {
       Player player = e.getEntity().getPlayer();
       for(BossArea b : Paninaro1_17.getBossareas().values()){
            if(b.getPlayersInside().contains(player)){
                Date now = new Date();
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                logFiller("[" + format.format(now) + "] ["+b.getBoss().getNome()+
                        "][Morto] Il boss "+ b.getBoss().getNome()+" e ha ucciso " +
                        " "+ b.getPlayersInside().getFirst().getName());
                b.playerDeathEvent(player);
                e.getDrops().clear();
            }
        }
    }

    @EventHandler
    public void EntityDeathEvent(NPCDeathEvent e) {
        HashMap<String, BossArea> bossareas = Paninaro1_17.getBossareas();
        String dead_npc_name = e.getNPC().getName().toLowerCase(Locale.ROOT);
        if(bossareas.containsKey(dead_npc_name)){
            BossArea bossArea = bossareas.get(dead_npc_name);
            Date now = new Date();
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            logFiller("[" + format.format(now) + "] ["+bossArea.getBoss().getNome()+
                    "][Morto] Il boss "+ bossArea.getBoss().getNome()+" è morto per mano " +
                    "di "+ bossArea.getPlayersInside().getFirst().getName());
            bossArea.bossDeathEvent();
        }
    }

}
