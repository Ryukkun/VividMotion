package foxy.ryukkun_.vividmotion.commands;


import foxy.ryukkun_.vividmotion.videoutil.FFmpegSource;
import foxy.ryukkun_.vividmotion.videoutil.GetMap;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
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
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        try {
            if (2 <= args.length){
                if ("give".equalsIgnoreCase(args[0].trim())){
                    String input = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                    FFmpegSource ffs = new FFmpegSource(input);
                    if (!ffs.can_load){
                        return false;
                    }
                    new GetMap(ffs, (Player) sender).start();
                    return true;
                }
            }


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
        TabUtil util = tab_list.get(tab_list.name);

        try{
            for (int i = 0; i < len_1; i++){
                util = util.get(args[i]);
            }
        } catch (Exception ignored){}
        return util;
    }
}
