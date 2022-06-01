//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common.capabilities;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraftforge.common.util.LazyOptional;

public class Capability<T> {
    private final String name;
    List<Consumer<Capability<T>>> listeners = new ArrayList();

    public String getName() {
        return this.name;
    }

    @Nonnull
    public <R> LazyOptional<R> orEmpty(Capability<R> toCheck, LazyOptional<T> inst) {
        return this == toCheck ? inst.cast() : LazyOptional.empty();
    }

    public boolean isRegistered() {
        return this.listeners == null;
    }

    public synchronized Capability<T> addListener(Consumer<Capability<T>> listener) {
        if (this.isRegistered()) {
            listener.accept(this);
        } else {
            this.listeners.add(listener);
        }

        return this;
    }

    public Capability(String name) {
        this.name = name;
    }

    void onRegister() {
        List<Consumer<Capability<T>>> listeners = this.listeners;
        this.listeners = null;
        listeners.forEach((l) -> {
            l.accept(this);
        });
    }
}
