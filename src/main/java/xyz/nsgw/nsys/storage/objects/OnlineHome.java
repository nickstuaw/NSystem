/*
 * Copyright (c) Nicholas Williams 2021.
 */

package xyz.nsgw.nsys.storage.objects;

import xyz.nsgw.nsys.storage.objects.locations.Home;

public class OnlineHome {
    private final Home home;
    public OnlineHome(final Home h) {
        home = h;
    }
    public Home getHome() {
        return home;
    }
}
