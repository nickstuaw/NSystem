/*
 * Copyright (c) Nicholas Williams 2021.
 */

package xyz.nsgw.nsys.storage.objects;

import xyz.nsgw.nsys.storage.sql.DbData;
import xyz.nsgw.nsys.storage.sql.SQLTable;
import xyz.nsgw.nsys.storage.sql.SQLUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SettingsList {

    public static final String SEPARATOR = "/%//£/";

    private final String key;

    private List<String> list;

    public SettingsList(final String name) {
        key = name;
        list = new ArrayList<>();
    }

    public void add(final String element) {
        if(!list.contains(element)) list.add(element);
    }

    public void del(final String element) {
        list.remove(element);
    }

    public String getKey() {return key;}

    public void setList(String raw) {
        list = raw.isEmpty() ? new ArrayList<>() : Arrays.stream(raw.split(SEPARATOR)).collect(Collectors.toList());
    }
    public List<String> getList() {
        return list;
    }
    private String getListString() {
        return list.isEmpty() ? "" : String.join(SEPARATOR, list);
    }

    public SettingsList loadAttributes(final SQLTable table) {
        List<Object> row = SQLUtils.getRow(table, "\""+key.toString()+"\"", Arrays.stream(DbData.LISTS_COLUMNS).map(c->c[0]).collect(Collectors.toList()).toArray(String[]::new));
        setList((String) row.get(0));
        return this;
    }

    public Object[] getDbValues() {
        return new Object[] {
                /*LIST*/getListString()
        };
    }

}
