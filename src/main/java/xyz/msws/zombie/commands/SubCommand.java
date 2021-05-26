package xyz.msws.zombie.commands;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import xyz.msws.zombie.api.ZCore;

import java.util.List;

public abstract class SubCommand extends ZombieCommand {

    protected SubCommand(String name, ZCore plugin) {
        super(name, plugin);
    }

    protected String[] shift(String[] array) {
        return shift(array, 1);
    }

    protected String[] shift(String[] array, int depth) {
        String[] nArgs = new String[array.length - depth];
        for (int i = 0; i < array.length; i++) {
            if (i >= nArgs.length)
                break;
            nArgs[i] = array[i + depth];
        }
        return nArgs;
    }

    protected abstract boolean exec(CommandSender sender, String label, String[] args);

    protected List<String> tab(CommandSender sender, String alias, String[] args) {
        return super.tabComplete(sender, alias, args);
    }

    protected List<String> tab(CommandSender sender, String alias, String[] args, Location location) {
        return super.tabComplete(sender, alias, args, location);
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        return exec(sender, label, shift(args));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        return tab(sender, alias, shift(args));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args, Location location) throws IllegalArgumentException {
        return super.tabComplete(sender, alias, shift(args), location);
    }
}
