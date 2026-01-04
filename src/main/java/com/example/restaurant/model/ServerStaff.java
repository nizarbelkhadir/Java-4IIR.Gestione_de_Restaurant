package com.example.restaurant.model;

import java.util.concurrent.atomic.AtomicBoolean;

public class ServerStaff extends User {
    private final AtomicBoolean busy = new AtomicBoolean(false);

    public ServerStaff() {}
    public ServerStaff(String id, String name) { super(id, name); }

    public boolean isBusy() { return busy.get(); }
    public void setBusy(boolean b) { busy.set(b); }
}
