package xyz.nsgw.nsys.storage.objects;

import xyz.nsgw.nsys.storage.sql.DbData;
import xyz.nsgw.nsys.storage.sql.SQLTable;
import xyz.nsgw.nsys.storage.sql.SQLUtils;

import java.util.*;
import java.util.stream.Collectors;

public class SettingsMap {

    public static final String SEPARATOR = ",";

    private final String key;

    private HashMap<String,String> map;

    public SettingsMap(final String name) {
        key = name;
        map = new HashMap<>();
    }

    public void put(final String s1, final String s2) {
        map.put(s1,s2);
    }

    public String getFromVal(final String v) {
        Optional<String> o = map.keySet().stream().filter(k->map.get(k).equals(v)).findFirst();
        return o.orElse("");
    }

    public boolean hasVal(final String v) {
        return map.containsValue(v);
    }

    public void del(final String key) {
        map.remove(key);
    }

    public String getKey() {return key;}

    public void setMap(String raw) {
        map = new HashMap<>();
        String[] spl;
        for(String s : raw.split(SEPARATOR)) {
            spl = s.split(",");
            map.put(spl[0], spl[1]);
        }
    }
    public HashMap<String,String> getMap() {return map;}
    private String getMapString() {
        if(map.isEmpty()) return "";
        StringBuilder res = new StringBuilder();
        for(String key : map.keySet()) {
            res.append(key).append(",").append(map.get(key)).append(SEPARATOR);
        }
        return res.toString();
    }

    public SettingsMap loadAttributes(final SQLTable table) {
        List<Object> row = SQLUtils.getRow(table, "\""+key.toString()+"\"", Arrays.stream(DbData.MAPS_COLUMNS).map(c->c[0]).collect(Collectors.toList()).toArray(String[]::new));
        setMap((String) row.get(0));
        return this;
    }

    public Object[] getDbValues() {
        return new Object[] {
                /*MAP*/getMapString()
        };
    }

}
