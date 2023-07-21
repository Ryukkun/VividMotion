package foxy.ryukkun_.vividmotion.commands;


import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class Main implements CommandExecutor, TabCompleter {
    TabUtil tab_list = new TabUtil().set(
            "vividmotion",
             new TabUtil().set(
                     "set",
                     new TabUtil()
             ).set(
                     "give",
                     new TabUtil()
             )
    );
    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] strings) {
        try {


        } catch (Exception ignored){}

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        try {
            int len_1 = args.length-1;
            TabUtil util = getTabUtil(args);

            if (util == null){
                return null;
            }
            List<String> res = new ArrayList<>();
            String subt = args[len_1];

            for (String sub : util.get_keys()){
                if (subt.length() == 0 || sub.startsWith(subt)){
                    res.add(sub);
                }
            }
            return res;

        } catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }


    private TabUtil getTabUtil(String[] args){
        int len_1 = args.length-1;
        TabUtil res = null, util = tab_list.get(tab_list.name);

        try{
            for (int i = 0; i < len_1; i++){
                res = util.get(args[i]);
            }
        } catch (Exception ignored){}
        return res;
    }
}
