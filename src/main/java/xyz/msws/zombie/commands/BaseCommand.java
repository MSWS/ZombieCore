package xyz.msws.zombie.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import xyz.msws.zombie.api.ZCore;
import xyz.msws.zombie.utils.MSG;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseCommand extends ZombieCommand implements CommandExecutor, TabCompleter {
    protected Map<String, SubCommand> commands = new HashMap<>();

    protected BaseCommand(String name, ZCore plugin) {
        super(name, plugin);
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        SubCommand cmd = getCommand(args[0]);
        if (cmd == null) {
            MSG.tell(sender, "Unknown command.");
            return true;
        }

        return cmd.execute(sender, commandLabel, args);
    }

    public void sendHelp(CommandSender sender) {
        MSG.tell(sender, "Sub-Commands for %s:", this.getName());
        for (Map.Entry<String, SubCommand> cmd : commands.entrySet()) {
            if (cmd.getValue().getPermission() != null && !sender.hasPermission(cmd.getValue().getPermission()))
                continue;
            MSG.tell(sender, cmd.getValue().getDescription().isEmpty() ? "/%s %s" : "/%s %s - %s", this.getName(), cmd.getKey(), cmd.getValue().getDescription());
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return execute(sender, label, args);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String label, String[] args) throws IllegalArgumentException {
        List<String> result = super.tabComplete(sender, label, args);
        if (args.length == 0)
            return result;
        for (Map.Entry<String, SubCommand> cmd : commands.entrySet()) {
            if (cmd.getValue().getPermission() != null && !sender.hasPermission(cmd.getValue().getPermission()))
                continue;
            if (cmd.getKey().equalsIgnoreCase(args[0])) {
                result.addAll(cmd.getValue().tab(sender, label, args));
                continue;
            }
            if (cmd.getKey().startsWith(args[0].toLowerCase())) {
                result.add(cmd.getKey());
            }
            for (String alias : cmd.getValue().getAliases()) {
                if (alias.startsWith(args[0].toLowerCase()))
                    result.add(alias);
            }
        }
        SubCommand selected = getCommand(args[0]);
        if (selected != null)
            result.addAll(selected.tabComplete(sender, label, args));
        return result;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return tabComplete(sender, alias, args);
    }

    private SubCommand getCommand(String key) {
        for (SubCommand cmd : commands.values()) {
            if (cmd.getName().equalsIgnoreCase(key))
                return cmd;
            if (cmd.getAliases().contains(key.toLowerCase()))
                return cmd;
        }
        return null;
    }
}
