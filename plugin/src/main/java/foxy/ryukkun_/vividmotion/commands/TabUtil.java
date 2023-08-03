package foxy.ryukkun_.vividmotion.commands;


import java.util.HashMap;
import java.util.Set;

public class TabUtil {
    private final HashMap<String, TabUtil> map = new HashMap<>();
    public String name;
    public TabUtil set(String name ,TabUtil util){
        map.put(checkst(name), util);
        this.name = name;
        return this;
    }

    public TabUtil get(String name){
        return map.get(checkst(name));
    }

    public Set<String> get_keys(){
        return map.keySet();
    }

    private String checkst(String text){
        return text.toLowerCase().trim();
    }
}
