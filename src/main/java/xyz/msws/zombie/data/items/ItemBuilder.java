package xyz.msws.zombie.data.items;

import com.google.common.collect.Sets;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import xyz.msws.zombie.modules.Module;
import xyz.msws.zombie.utils.MSG;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ItemBuilder extends Module {

    private final List<ItemAttribute> attr = new ArrayList<>();

    public ItemBuilder(Plugin plugin) {
        super(plugin);
    }

    public void addAttribute(ItemAttribute attr) {
        this.attr.add(attr);
    }

    public List<ItemAttribute> getAttributes() {
        return attr;
    }

    public ItemStack build(String args) {
        return build(args, Bukkit.getConsoleSender());
    }

    public ItemStack build(String args, CommandSender sender) {
        ItemStack base = null;
        Material mat = null;

        String matName = args.split(" ")[0];
        int amo = 1;

        List<String> attributes = new ArrayList<>();

        boolean amoSpecified = args.split(" ").length >= 2 && StringUtils.isNumeric(args.split(" ")[1]);

        if (amoSpecified)
            amo = Integer.parseInt(args.split(" ")[1]);
        
        StringBuilder last = new StringBuilder();
        for (String arg : (String[]) ArrayUtils.subarray(args.split(" "), amoSpecified ? 2 : 1,
                args.split(" ").length)) {
            if ((last.length() > 0) && arg.contains(":")) {
                attributes.add(last.toString().trim());
                last = new StringBuilder(arg + " ");
                continue;
            }
            last.append(arg).append(" ");
        }
        if (last.toString().contains(":"))
            attributes.add(last.toString().trim());

        // Reverse order
        for (Material m : Material.values()) { // Check incomplete middles
            if (MSG.normalize(m.toString()).contains(MSG.normalize(matName))) {
                mat = m;
                break;
            }
        }
        for (Material m : Material.values()) { // Check incomplete beginnings
            if (MSG.normalize(m.toString()).startsWith(MSG.normalize(matName))) {
                mat = m;
                break;
            }
        }
        for (Material m : Material.values()) { // Check exact name first
            if (m.toString().replace("_", "").equalsIgnoreCase(matName.replace("_", ""))) {
                mat = m;
                break;
            }
        }

        if (mat == null)
            return null;

        base = new ItemStack(mat, amo);

        for (ItemAttribute at : attr)
            for (String s : attributes)
                base = at.modify(s, base);
        return base;
    }

    public String toString(ItemStack item) {
        StringBuilder result = new StringBuilder();
        for (ItemAttribute at : attr) {
            String mod = at.getModification(item);
            if (mod == null || mod.isEmpty())
                continue;
            result.append(mod).append(" ");
        }
        return (MSG.normalize(item.getType().toString()) + " " + item.getAmount() + " " + result).trim();
    }

    public String humanReadable(ItemStack item) {
        StringBuilder result = new StringBuilder();
        if (item == null || item.getType() == Material.AIR)
            return null;
        result.append(MSG.NUMBER)
                .append(item.getAmount() == 1 ? (isVowel(item.getType().toString().charAt(0)) ? "an " : "a ")
                        : item.getAmount() + " ")
                .append(MSG.FORMAT_INFO);
        result.append(MSG.camelCase(item.getType().toString()))
                .append((item.getAmount() == 1 || item.getType().toString().toLowerCase().endsWith("s")) ? " " : "s ");
        for (int i = 0; i < attr.size(); i++) {
            String mod = attr.get(i).humanReadable(item);
            if (mod == null || mod.isEmpty())
                continue;

            result.append(MSG.theme()).append(mod).append(" ");
        }
        return result.toString().trim();
    }

    private static final HashSet<Character> vowels = Sets.newHashSet('a', 'e', 'i', 'o', 'u', 'A', 'E', 'I', 'O', 'U');

    private boolean isVowel(char c) {
        return vowels.contains(c);
    }


    @Override
    public void enable() {
        attr.add(new NameAttribute());
        attr.add(new UnbreakableAttribute());
        attr.add(new EnchantmentAttribute());
        attr.add(new DamageAttribute());
        attr.add(new OwnerAttribute());
        attr.add(new LoreAttribute());
        attr.add(new ItemFlagAttribute());
        attr.add(new PotionAttribute());
        attr.add(new PatternAttribute());
        attr.add(new StoredEnchantmentAttribute());
        attr.add(new EntityAttribute());
        attr.add(new FireworkAttribute());
        attr.add(new CommandAttribute());
        attr.add(new VanillaAttribute());
    }

    @Override
    public void disable() {
        attr.clear();
    }

}
