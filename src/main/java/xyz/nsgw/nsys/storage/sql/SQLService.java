/*
© Copyright Nick Williams 2021.
Credit should be given to the original author where this code is used.
 */

package xyz.nsgw.nsys.storage.sql;

import co.aikar.idb.*;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalNotification;
import org.bukkit.Bukkit;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.nsgw.nsys.storage.objects.Profile;
import xyz.nsgw.nsys.storage.objects.SettingsList;
import xyz.nsgw.nsys.storage.objects.locations.Warp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class SQLService {

    private final SQLTable listsTable;
    private final SQLTable profileTable;
    private final SQLTable warpTable;

    @NonNull
    private final LoadingCache<@NotNull String, @NotNull SettingsList> listsCache;

    private final LoadingCache<@NotNull UUID, @NotNull Profile> profileCache;
    private final LoadingCache<@NotNull String, @NotNull Warp> warpCache;

    public SQLService(String host, String database, String username, String password) {

        DatabaseOptions options = DatabaseOptions.builder()
                .mysql(username,
                        password,
                        database,
                        host).build();

        SQLUtils.setDb(PooledDatabaseOptions.builder().options(options).createHikariDatabase());

        profileTable = new SQLTable("nsys_profiles", DbData.PROFILE_PK , DbData.PROFILE_COLUMNS);

        profileCache = CacheBuilder.newBuilder()
                .removalListener(this::saveProfile)
                .build(CacheLoader.from(this::loadProfile));

        listsTable = new SQLTable("nsys_settings", DbData.LISTS_PK , DbData.LISTS_COLUMNS);

        listsCache = CacheBuilder.newBuilder()
                .removalListener(this::saveList)
                .build(CacheLoader.from(this::loadList));

        warpTable = new SQLTable("nsys_warps", DbData.WARP_PK , DbData.WARP_COLUMNS);

        warpCache = CacheBuilder.newBuilder()
                .removalListener(this::saveWarp)
                .build(CacheLoader.from(this::loadWarp));

    }

    public void onDisable() {
        profileCache.invalidateAll();
        profileCache.cleanUp();
    }

    /*  ------------------------------------
        ------------- LISTS -------------
        ------------------------------------    */

    @NonNull
    private SettingsList loadList(@NotNull final String name) {
        SettingsList list = new SettingsList(name);
        if(! SQLUtils.holdsKey(listsTable, "\""+name+"\"")) return list;
        return list.loadAttributes(listsTable);
    }
    private void saveList(@NotNull final RemovalNotification<@NotNull String, @NotNull SettingsList> notification) {
        SettingsList list = notification.getValue();
        setRow( listsTable, list.getKey(), list.getDbValues());
    }
    public void validateList(@NotNull final SettingsList list) {
        this.listsCache.put(list.getKey(), list);
    }
    public void invalidateList( final SettingsList list) {
        this.listsCache.invalidate(list.getKey());
    }
    public SettingsList wrapList(@NotNull final String name) {
        try {
            return listsCache.get(name);
        }
        catch(final ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Nullable
    public SettingsList wrapListIfLoaded(@NotNull final String name) {
        return listsCache.getIfPresent(name);
    }

    /*  ------------------------------------
        ------------- PROFILES -------------
        ------------------------------------    */

    @NonNull
    private Profile loadProfile(@NotNull final UUID uuid) {
        Profile profile = new Profile(uuid);
        if(! SQLUtils.holdsKey(profileTable, "\""+uuid+"\"")) return profile;
        return profile.loadAttributes(profileTable);
    }
    private void saveProfile(@NotNull final RemovalNotification<@NotNull UUID, @NotNull Profile> notification) {
        Profile profile = notification.getValue();
        setRow( profileTable, profile.getKey().toString(), profile.getDbValues());
    }
    public void validateProfile(@NotNull final Profile profile) {
        this.profileCache.put(profile.getKey(), profile);
    }
    public void invalidateProfile(@NotNull final Profile profile) {
        this.profileCache.invalidate(profile.getKey());
    }
    public Profile wrapProfile(@NotNull final UUID uuid) {
        try {
            return profileCache.get(uuid);
        }
        catch(final ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Nullable
    public Profile wrapProfileIfLoaded(@NotNull final UUID uuid) {
        return profileCache.getIfPresent(uuid);
    }

    /*  -------------------------------------
        --------------- WARPS ---------------
        -------------------------------------   */

    @NonNull
    private Warp loadWarp(@NotNull final String name) {
        Warp warp = new Warp(name);
        if(! SQLUtils.holdsKey(warpTable, "\""+name+"\"")) return warp;
        return warp.loadAttributes(warpTable);
    }
    private void saveWarp(@NotNull final RemovalNotification<@NotNull String, @NotNull Warp> notification) {
        Warp warp = notification.getValue();
        // OWNER_UUID,
        setRow( warpTable, warp.getKey(), warp.getDbValues());
    }
    public void validateWarp(@NotNull final Warp warp) {
        this.warpCache.put(warp.getKey(), warp);
    }
    public void invalidateWarp(@NotNull final Warp warp) {
        this.warpCache.invalidate(warp.getKey());
    }
    public Warp wrapWarp(@NotNull final String name) {
        try {
            return warpCache.get(name);
        }
        catch(final ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Nullable
    public Warp wrapWarpIfLoaded(@NotNull final String name) {
        return warpCache.getIfPresent(name);
    }

    public void invalidateAndDeleteWarp(@NotNull final Warp warp) {
        invalidateWarp(warp);
        SQLUtils.delRow(warpTable, "\""+warp.getKey()+"\"");
    }




    /**
     * @param values - the values only. (just values not colLabel=value etc)
     */
    public void setRow(final SQLTable table, final String key, final Object... values) {
        List<String> columnLabels = table.getColLabels(),
                equivalents = new ArrayList<>();
        for(String lbl : columnLabels) {
            equivalents.add(lbl + " = ?");
        }
        List<Object> objs = new ArrayList<>();
        objs.add(key);
        objs.addAll(Arrays.asList(values));
        objs.addAll(Arrays.asList(values));
        /*Bukkit.getServer().getLogger().info("INSERT INTO "+table.getName()+" ("+table.getPkLabel()+","+String.join(",",columnLabels) + ") VALUES (?" + ",?".repeat(columnLabels.size())+")" +
                " ON DUPLICATE KEY UPDATE " + String.join(", ",equivalents));
        for(Object o : objs) {
            Bukkit.getServer().getLogger().info(o.toString());
        }//debug*/
        SQLUtils.getDb().createTransaction(stm -> {
            stm.executeUpdateQuery("INSERT INTO "+table.getName()+" ("+table.getPkLabel()+","+String.join(",",columnLabels) + ") VALUES (?" + ",?".repeat(columnLabels.size())+")" +
                    " ON DUPLICATE KEY UPDATE " + String.join(", ",equivalents) + ";", objs.toArray(Object[]::new));
            return true;
        });
    }
}
