package jammone.paninaro1_17;

import IOpackage.CleanInv;
import PaniBoss.PaniBoss;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.*;

import static IOpackage.LogCTRL.logFiller;

public class BossArea {
    //name of bossarea, aka warp e region name

    private final PaniBoss boss;
    private final int maxnumplayers;
    private final Location warp_location;
    private int join_npc_id;
    private int leave_npc_id;
    private final Location join_location;
    private final Location leave_location;

    private final LinkedList<Player> playersInside = new LinkedList();
    private final LinkedList<Player> queue = new LinkedList();
    private final ArrayList<String> player2kick = new ArrayList<String>();
    private HashMap<String, Date> killerHashmap = new HashMap<>();



    public BossArea(Location warp_location,PaniBoss boss, int max_num_players,
                    int join_npc_id, int leave_npc_id, Location join_location,
                    Location leave_location) {
        this.maxnumplayers = max_num_players;
        this.boss = boss;
        this.warp_location = warp_location;
        this.join_location = join_location;
        this.leave_location = leave_location;
        this.join_npc_id = join_npc_id;
        this.leave_npc_id = leave_npc_id;
    }

    public void bossDeathEvent(){
        this.boss.setStatus(PaniBoss.STATUS.MORTO);
        Date now = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(now);
        c.add(Calendar.DATE, 3);
        killerHashmap.put(this.getPlayersInside().getFirst().getName(),c.getTime());
        Paninaro1_17.giveMeaBreath(this,boss.getRespawn_time(),true);
    }

    public void playerEnterRequest(Player player){
        PaniBoss.STATUS status = boss.getStatus();
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        if(this.killerHashmap.containsKey(player.getName())){
            if(now.before(killerHashmap.get(player.getName()))){
                player.sendMessage(ChatColor.RED+""+ "Il boss si rifiuta di combatterti! Puoi risfidarlo dal " +
                        format.format(killerHashmap.get(player.getName())));
                return;
            }
        }

        if(status == PaniBoss.STATUS.LIBERO){
            boss.setStatus(PaniBoss.STATUS.OCCUPATO);
            this.playersInside.add(player);
            player.teleport(join_location);
            player.sendMessage("Buon Combattimento!");
            logFiller("[" + format.format(now) + "] ["+boss.getNome()+
                    "][Libero->Occupato] Il player " +player.getName()+" è entrato nell'arena \n");
            return;
        }
        if(status == PaniBoss.STATUS.MORTO){
            player.sendMessage("Il boss è morto, riprovare fra poco");
            return;
        }
        if(status== PaniBoss.STATUS.OCCUPATO){
            player.sendMessage("Il boss è occupato con qualcun altro, attendi");
            add2Queue(player);
            return;
        }
        if(status == PaniBoss.STATUS.ATTESA){
            if(queue.getFirst().equals(player)){
                player.teleport(join_location);
                boss.setStatus(PaniBoss.STATUS.OCCUPATO);
                player.sendMessage("Il boss ti stava aspettando, buon combattimento!");
                logFiller("[" + format.format(now) + "] ["+boss.getNome()+
                        "][Attesa->Occupato] Il player " +player.getName()+" è entrato nell'arena, era in coda \n");
                queue.removeFirst();
                this.playersInside.add(player);
            }else{
                player.sendMessage(ChatColor.YELLOW +""+ "Il boss sta aspettano qualcun altro, attendi");
                add2Queue(player);
            }
            return;
        }
        if(status== PaniBoss.STATUS.PAUSA){
            player.sendMessage(ChatColor.YELLOW +""+"Il boss sta riprendendo fiato, attendi");
            add2Queue(player);
        }
        if(status == PaniBoss.STATUS.MANUTENZIONE){
            player.sendMessage(ChatColor.RED +""+"Il boss si sta prendendo le ferie, tornerà appena possibile");
        }
    }

    public void playerLeaveRequest(Player player){
        PaniBoss.STATUS status = boss.getStatus();
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        if(status== PaniBoss.STATUS.OCCUPATO){
            player.teleport(leave_location);
            player.sendMessage(ChatColor.BLUE +""+"Codardo! Torna pure quando trovi un pò di coraggio");
            this.playersInside.remove(player);
            boss.setStatus(PaniBoss.STATUS.PAUSA);
            Paninaro1_17.giveMeaBreath(this,60,false);
            logFiller("[" + format.format(now) + "] ["+boss.getNome()+
                    "][Occupato->Pausa] Il player " +player.getName()+" è fuggito dal boss\n");
            return;
        }
        if(status == PaniBoss.STATUS.MORTO){
            player.teleport(leave_location);
            if(this.playersInside.contains(player))this.playersInside.remove(player);
            logFiller("[" + format.format(now) + "] ["+boss.getNome()+
                    "][Morto] Il player " +player.getName()+" è uscito dall'arena\n");
            return;
        }
        if(status == PaniBoss.STATUS.LIBERO ){
            if(this.playersInside.contains(player))this.playersInside.remove(player);
            System.out.println("[Paninaro]"+player.getName()+" ha richiesto di uscire dall'arena," +
                    "ma era libera");
            logFiller("[" + format.format(now) + "] ["+boss.getNome()+
                    "][Libero] Il player " +player.getName()+" è uscito nell'arena libera (errore)\n");

            player.teleport(leave_location);
        }
    }

    public void playerDeathEvent(Player p) {
        if (!this.playersInside.contains(p)) {
            System.out.println("[Paninaro]Errore playerDeathEvent: player non presente");
            return;
        }
        this.playersInside.remove(p);
        notifyPlayerDeath2Queue();
        if (this.boss.getStatus() == PaniBoss.STATUS.OCCUPATO) {
            boss.setStatus(PaniBoss.STATUS.PAUSA);
            Paninaro1_17.giveMeaBreath(this, 60, false);
            return;
        }
    }

    public void playerQuitEvent(Player p){
        if (!this.playersInside.contains(p) && !this.queue.contains(p)) {
            System.out.println("[Paninaro]Errore playerQuitEvent: player non presente");
            return;
        }
        if(this.playersInside.contains(p)) {
            System.out.println("player si è disconnesso mentre combatteva dal boss");
            //il player stava combattendo
            this.playersInside.remove(p);
            if (this.boss.getStatus() == PaniBoss.STATUS.OCCUPATO) {
                this.player2kick.add(p.getName());
                boss.setStatus(PaniBoss.STATUS.PAUSA);
                Paninaro1_17.giveMeaBreath(this, 60, false);
            }
        }else if(this.queue.contains(p)){
            queue.remove(p);
            this.notifyPlayerLeave2Queue();
        }
    }


    private void add2Queue(Player player) {
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        if(queue.contains(player)){
            int playerdavanti = this.queue.indexOf(player);
            player.sendMessage(ChatColor.GREEN+""+ "Sei in coda, " +
                    (playerdavanti!=0? "ci sono "+ChatColor.AQUA+""+
                            playerdavanti+ChatColor.GREEN+"" +
                            " giocatori davanti a te": "sei il prossimo ad entrare") );
            return;
        }
        this.queue.addLast(player);

        logFiller("[" + format.format(now) + "] ["+boss.getNome()+
                "][---] Il player " +player.getName()+" è stato aggiunto alla coda ( "+this.queue.indexOf(player)+1+" posizione)\n");

        System.out.println("[Paninaro] " + player.getName() + " è stato aggiunto alla coda di " + this.boss.getNome());
        if(queue.size()==1) player.sendMessage(ChatColor.GREEN+""+ "Sei stato aggiunto alla coda, sei il prossimo ad entrare!");
        else{player.sendMessage(ChatColor.GREEN+""+ "Sei stato aggiunto alla coda di attesa, ci sono "+ChatColor.AQUA+""+  (queue.size()-1)+ChatColor.GREEN+""+ " giocatori davanti a te");}

    }

    public void notify2Queue() {
        for (Player p : queue) {
            if(queue.indexOf(p)==0) continue;
            p.sendMessage(ChatColor.GREEN+""+ "La coda per il boss " + ChatColor.RED+""+ this.boss.getNome()+ ChatColor.GREEN+ "" +" avanza! Ci sono" + ChatColor.AQUA+ "" +(queue.indexOf(p)) +ChatColor.GREEN+""+  " giocatori in coda rimanenti prima di te");
        }
    }
    public void notifyPlayerLeave2Queue() {
        for (Player p : queue) {
            if(queue.indexOf(p)==0) continue;
            p.sendMessage(ChatColor.GREEN+""+ "Un giocatore ha rinunciato a sfidare  " + ChatColor.RED+""+ this.boss.getNome()+ ChatColor.GREEN+ "" +" ci sono " + ChatColor.AQUA+ "" +(queue.indexOf(p)) +ChatColor.GREEN+""+  " giocatori in coda rimanenti prima di te");
        }
    }
    public void notifyPlayerDeath2Queue() {
        for (Player p : queue) {
            if(queue.indexOf(p)==0) continue;
            p.sendMessage(ChatColor.GREEN+""+ "Un giocatore è morto mentre combatteva  " + ChatColor.RED+""+
                    this.boss.getNome()+ ChatColor.GREEN+ "" +", ci sono " + ChatColor.AQUA+ "" +(queue.indexOf(p))
                    +ChatColor.GREEN+""+  " giocatori in coda rimanenti prima di te");
        }
    }

    public String toString() {
        return "BossArea{" +
                "boss=" + boss.toString() +
                ", maxnumplayers=" + maxnumplayers +
                ",  warp_location=[" + warp_location.getWorld().getName() +
                ", "+warp_location.getX()+", "+ warp_location.getY()+", "+
                warp_location.getZ()+"]"+
                ", join_location=[" + join_location.getWorld().getName() +
                ", "+join_location.getX()+", "+ join_location.getY()+", "+
                join_location.getZ()+"]"+
                ", leave_location=[" + leave_location.getWorld().getName() +
                ", "+leave_location.getX()+", "+ leave_location.getY()+", "+
                leave_location.getZ()+"]"+
                ", npc_join: "+join_npc_id+
                ", npc_leave: "+ leave_npc_id+
                "} ";
    }

    public PaniBoss getBoss() {
        return boss;
    }

    public int getMaxnumplayers() {
        return maxnumplayers;
    }

    public Location getWarp_location() {
        return warp_location;
    }

    public LinkedList<Player> getPlayersInside() {
        return playersInside;
    }

    public LinkedList<Player> getQueue() {
        return queue;
    }

    public ArrayList<String> getPlayer2kick() {
        return player2kick;
    }
    

    public int getJoin_npc_id() {
        return join_npc_id;
    }

    public int getLeave_npc_id() {
        return leave_npc_id;
    }

    public Location getJoin_location() {
        return join_location;
    }

    public Location getLeave_location() {
        return leave_location;
    }

    public HashMap<String, Date> getKillerHashmap() {
        return killerHashmap;
    }

    public void evacuate() {
        LinkedList<Player> clone = (LinkedList<Player>) playersInside.clone();
        for(Player p : clone){
            p.teleport(leave_location);
            p.sendMessage("Sei stato teletrasportato fuori dall'arena: il boss era tornato");
        }
        playersInside.clear();
    }
}
