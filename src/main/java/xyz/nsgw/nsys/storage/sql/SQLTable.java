/*
 * Copyright (c) Nicholas Williams 2021.
 */

package xyz.nsgw.nsys.storage.sql;

import co.aikar.idb.Database;
import xyz.nsgw.nsys.NSys;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SQLTable {

    private final String name, colsJoined;
    private final String[] pk;
    private final String[][] cols;

    public SQLTable(final String table, final String[] pkey, final String[][] columns, final String[][] addColumns) {
        name = table;
        pk = pkey;
        cols = columns;
        colsJoined = joinCols();
        init();
        if(addColumns == null) return;
        List<String> joined = new ArrayList<>();
        Arrays.stream(addColumns).forEach(c -> joined.add("ADD COLUMN IF NOT EXISTS " + String.join(" ",c)));
        String query = "ALTER TABLE " + name + " " + String.join(", ", joined) + ";";
        SQLUtils.updateAndIgnore(query);
    }

    public String joinCols() {
        List<String> res = new ArrayList<>();
        for(String[] col : cols) {
            res.add(col[0] + " " + col[1]);
        }
        return String.join(", ", res);
    }

    public void init() {
        SQLUtils.update("CREATE TABLE IF NOT EXISTS "+ name
                + " (" + String.join(" ",pk) +", "+ colsJoined + ", PRIMARY KEY ("+pk[0]+"));");
    }

    public void doNewCols(int index, String def) {
        SQLUtils.updateAndIgnore("ALTER TABLE "+name+" ADD ("+ cols[index][0]+" "+cols[index][1] +" DEFAULT "+def +");");
    }

    public String getName() {
        return name;
    }

    public String[][] getCols() {
        return cols;
    }

    public List<String> getColLabels() {
        List<String> labels = new ArrayList<>();
        for(String[] col : cols) {
            labels.add(col[0]);
        }
        return labels;
    }

    public String[] getPk() {
        return pk;
    }

    public String getPkLabel() {
        return pk[0];
    }

    public Object getRow(Database db, final String key) {
        List<Object> objects = new ArrayList<>();

        try(Connection con = db.getConnection();
            PreparedStatement pst =
                    con.prepareStatement("SELECT * FROM "+name+" WHERE "+getPkLabel()+"="+key+";");
            ResultSet rs = pst.executeQuery();) {
            if(cols.length == 0) {
                int i = 1;
                while(rs.next()) {
                    objects.add(rs.getObject(i));
                    i++;
                }
            }
            else {
                while (rs.next()) {
                    for(String[] col : cols) {
                        objects.add(rs.getObject(col[0]));
                    }
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return objects;
    }

}
