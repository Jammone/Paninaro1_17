package Tasks;


import PaniBoss.PaniBoss;
import jammone.paninaro1_17.BossArea;
import jammone.paninaro1_17.Paninaro1_17;
import org.bukkit.entity.Player;

import java.util.LinkedList;

public class BossWaitTask implements Runnable{
    private BossArea bossArea;
    private  boolean isdead;
    public BossWaitTask(BossArea bossArea, boolean isdead) {
        this.bossArea = bossArea;
        this.isdead = isdead;
    }

    @Override
    public void run() {
        //System.out.println("[Paninaro] BossWaitTask task triggerata");
        bossArea.getBoss().setTaskID(-1);
        LinkedList<Player> queue = bossArea.getQueue();
        PaniBoss boss = bossArea.getBoss();
        if(boss.getStatus() == PaniBoss.STATUS.MORTO){
            bossArea.evacuate();
            boss.getNpc().spawn(boss.getRespawn_location());
        }

        if(queue.isEmpty()){
            boss.setStatus(PaniBoss.STATUS.LIBERO);
        }
        else{
            bossArea.getBoss().setStatus(PaniBoss.STATUS.ATTESA);
            Player player = queue.getFirst();
            player.sendMessage("Sei il prossimo ad entrare, corri dal boss o perderai" +
                    "la tua occasione di sfidarlo!");
            Paninaro1_17.ScrollAQueue(player,bossArea);
        }

    }
}
