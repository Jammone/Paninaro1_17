package PaniBoss;

import jammone.paninaro1_17.Paninaro1_17;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Arrays;
import java.util.UUID;

public class PaniBoss {

    public enum STATUS{LIBERO, PAUSA, MORTO, OCCUPATO, MANUTENZIONE, ATTESA}


    private final String nome;
    private final Location respawn_location;
    private final int respawn_time;
    private  STATUS status;
    private NPC npc;
    private UUID bossUID;
    private int taskID = -1;


    public PaniBoss(String nome, Location respawn_location, int respawn_time) {
        this.nome = nome;
        this.respawn_location = respawn_location;
        this.respawn_time = respawn_time;

        if (!updateNPC()) {
            System.out.println("[Paninaro] Errore generazione del boss " + nome + ": boss non trovato in citizen. riprovare" +
                    "più tardi");
            Bukkit.getScheduler().scheduleSyncDelayedTask(Paninaro1_17.getInstance(),new findNPC(this),20L*3);
        }else {
            this.npc.spawn(respawn_location);
            this.status = STATUS.LIBERO;
        }
    }

    public boolean updateNPC(){
        /**
         * aggiorna l'NPC in bossEntity, se fallisce (il boss non è stato trovato in citizen)
         * ritorna false;
         * */
        for (NPC npc : CitizensAPI.getNPCRegistry()) {
           // System.out.println("[Paninaro]scrolling npc: " +npc.getName());
            if (this.nome.equals(npc.getName())) {
                this.npc= npc;
                return true;
            }
        }
        return false;
    }

    public String getNome() {
        return nome;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public NPC getNpc() {
        return npc;
    }

    @Override
    public String toString() {
        return "PaniBoss{" +
                "nome='" + nome + '\'' +
                ",\n\t\t\t\t\t respawn_location= [" + respawn_location.getWorld().getName() +
                ", "+respawn_location.getX()+", "+ respawn_location.getY()+", "+
                respawn_location.getZ()+"],\n\t\t\t\t\t "+
                "respawn_time=" + respawn_time +" secondi"+
                '}';
    }
    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public int getRespawn_time() {
        return respawn_time;
    }

    public Location getRespawn_location() {
        return respawn_location;
    }
}

class findNPC implements Runnable{

private PaniBoss paniBoss;

    public findNPC(PaniBoss paniBoss) {
        this.paniBoss = paniBoss;
    }

    @Override
    public void run() {
        if( paniBoss.updateNPC()) {
            System.out.println("[Paninaro] FindNPC call: boss " + paniBoss.getNome() + "trovato");

            paniBoss.getNpc().spawn(paniBoss.getRespawn_location());
            paniBoss.setStatus(PaniBoss.STATUS.LIBERO);
        }
    }
}
