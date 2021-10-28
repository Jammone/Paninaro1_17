package IOpackage;

import jammone.paninaro1_17.Paninaro1_17;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class CleanInv {

    public static void saveDeletedInventory(Player player){
        File p = new File(Paninaro1_17.class.getProtectionDomain().getCodeSource().getLocation().getPath().replaceAll("%20", " "));
        File Pdir = new File(p.getParentFile() + "/Paninaro"); //folder
        File Lfile = new File(Pdir + "/deleted_inventories.txt"); //file di deleted inv
        if (Lfile.exists()) {
            try {
                FileWriter myWriter = new FileWriter(Lfile, true);
                Date now = new Date();
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                myWriter.write("- player: "+player.getName()+", data: +"+format.format(now)+"\n");
                for(ItemStack it :   player.getInventory().getContents()){
                    if(it== null) continue;
                    String material = it.getType().toString();
                    int quantity = it.getAmount();
                    Map<Enchantment, Integer> enchantments = it.getEnchantments();
                    myWriter.write("\t item: "+material+", quantità: "+quantity+", incantamenti: \n");
                    for(Enchantment ent : enchantments.keySet()){
                        myWriter.write("\t\t\t"+ent.toString()+", lvl"+ enchantments.get(ent)+ "\n");
                    }
                }
                myWriter.write("---------------------------------\n");
                myWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            try {
                Lfile.createNewFile();
                saveDeletedInventory(player);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}


