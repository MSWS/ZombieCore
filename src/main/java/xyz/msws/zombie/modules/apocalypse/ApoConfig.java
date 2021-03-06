package xyz.msws.zombie.modules.apocalypse;

import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.data.ConfigCollection;
import xyz.msws.zombie.data.ZombieConfig;
import xyz.msws.zombie.modules.ModuleConfig;

import java.util.Collection;
import java.util.HashSet;

public abstract class ApoConfig extends ModuleConfig<ApoModule> {

    protected ConfigCollection<String> maps = new ConfigCollection<>(new HashSet<>(), String.class);
    protected boolean startLoads;

    public ApoConfig(ZCore plugin, ZombieConfig config) {
        super(plugin, config);
    }

    @Override
    public String getName() {
        return "apocalypse";
    }

    public Collection<String> getMaps() {
        return maps;
    }

    public boolean doStartLoads() {
        return startLoads;
    }
}
