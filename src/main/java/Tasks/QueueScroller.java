package Tasks;

import PaniBoss.PaniBoss;
import jammone.paninaro1_17.BossArea;
import jammone.paninaro1_17.Paninaro1_17;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import static IOpackage.LogCTRL.logFiller;

public class QueueScroller implements Runnable{
    private final BossArea bossArea;
    private Player p;
    public QueueScroller(Player p,BossArea bossArea) {
        this.bossArea = bossArea;
        this.p = p;
    }

    @Override
    public void run() {
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        PaniBoss boss = bossArea.getBoss();
        PaniBoss.STATUS status = bossArea.getBoss().getStatus();
        LinkedList<Player> queue = bossArea.getQueue();
        if(status== status.OCCUPATO){
            if(bossArea.getPlayersInside().contains(p)){
                //è entrato tutto apposto, il boss è pieno
                //p.sendMessage("[TASK] sei entrato entro il tempo limite, gg");
                return;
            }
        }

        if(status == PaniBoss.STATUS.ATTESA) {
            if(queue.isEmpty()) {
                boss.setStatus(PaniBoss.STATUS.LIBERO);
                logFiller("[" + format.format(now) + "] [" + boss.getNome() + "][Attesa->Libero]" +
                        " Il Boss ha terminato la pausa ed è ora libero \n");
                return;
            }

            if (bossArea.getQueue().getFirst().equals(p)) {
                //il player non è ancora entrato, è ancora il primo in coda
                p.sendMessage(ChatColor.RED + "" + "Tempo scaduto! ");
                bossArea.getQueue().removeFirst();
                if(bossArea.getQueue().isEmpty()){
                    p.sendMessage(ChatColor.GREEN+""+"Sei fortunato: Il boss non ha nessuno in attesa, presentati "+
                            "all'arena per entrare prima che lo facciano altri!");
                    boss.setStatus(PaniBoss.STATUS.LIBERO);
                    logFiller("[" + format.format(now) + "] [" + boss.getNome() + "][Libero]" +
                            " Il player "+p.getName()+" non è entrato in tempo, ma il Boss ha terminato la coda ed è ora libero \n");
                    return;
                }


                bossArea.notify2Queue();
                Player nextPlayer = bossArea.getQueue().getFirst();
                System.out.println(nextPlayer.getName() + " è il prossimo in coda che" +
                        " deve entrare (precedente non ha accettato)");
                nextPlayer.sendMessage(ChatColor.GREEN + "" + "Sei il prossimo a poter combattere "
                        + ChatColor.RED + ""
                        + boss.getNome() + ChatColor.GREEN + "" +
                        "! Entra nell'arena entro 30 secondi o perderai la tua occasione!");
                logFiller("[" + format.format(now) + "] [" + boss.getNome() + "][Attesa]" +
                        p.getName()+" non è entrato in tempo, la coda avanza e si attende "
                        +nextPlayer.getName() +"\n");
                Paninaro1_17.ScrollAQueue(nextPlayer, bossArea);
            }
        }
    }
}


