package net.okocraft.specialitems;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;

public class SpecialItems extends JavaPlugin {

    @Override
    public void onEnable() {
        PluginCommand command = Objects.requireNonNull(getCommand("specialitems"));
        command.setExecutor(this);
        command.setTabCompleter(this);
        saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("引数なしでこのコマンドを実行できるのはプレイヤーのみです。");
                return false;
            }
            ItemStack handItem = ((Player) sender).getInventory().getItemInMainHand();
            getConfig().set("items." + ChatColor.stripColor(handItem.getItemMeta().getDisplayName()).replaceAll(" ", ""), handItem);
            sender.sendMessage("アイテムを登録しました。");
            saveConfig();
            reloadConfig();
            return false;
        }

        if (!getConfig().isConfigurationSection("items") || !getConfig().isItemStack("items." + args[0])) {
            sender.sendMessage("その名前のアイテムはありません。");
            return false;
        }

        ItemStack item = getConfig().getItemStack("items." + args[0]);

        if (args.length == 1) {
            sender.sendMessage("操作を指定してください。(give|take)");
            return false;
        }

        if (args[1].equalsIgnoreCase("give")) {
            if (args.length == 2) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage("プレイヤー以外がこのコマンドを実行する場合は、プレイヤー名を指定してください。");
                    return false;
                }

                Player player = (Player) sender;
                player.getInventory().addItem(item);
            } else if (args.length == 3) {
                Player player = Bukkit.getPlayer(args[2]);
                int amount = 1;
                if (player == null) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage("プレイヤー以外がこのコマンドを実行する場合は、プレイヤー名を指定してください。");
                        return false;
                    }
                    player = (Player) sender;

                    try {
                        amount = Integer.parseInt(args[2]);
                        if (amount < 1) {
                            throw new NumberFormatException("正の数のみです。");
                        }
                    } catch (NumberFormatException e) {
                        sender.sendMessage("3つ目の引数にはプレイヤー名またはアイテム数を自然数で指定してください。");
                        return false;
                    }
                }
                item.setAmount(amount);
                player.getInventory().addItem(item);
            } else if (args.length == 4) {
                Player player = Bukkit.getPlayer(args[2]);
                int amount = 1;
                if (player == null) {
                    sender.sendMessage("そのプレイヤーはオンラインではありません。");
                    return false;
                }
                try {
                    amount = Integer.parseInt(args[3]);
                    if (amount < 0) {
                        throw new NumberFormatException("正の数のみです。");
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage("指定された数字は不正です。");
                    return false;
                }
                item.setAmount(amount);
                player.getInventory().addItem(item);
            }
        } else if (args[1].equalsIgnoreCase("take")) {
            if (args.length == 2) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage("プレイヤー以外がこのコマンドを実行する場合は、プレイヤー名を指定してください。");
                    return false;
                }

                Player player = (Player) sender;
                player.getInventory().removeItem(item);
            } else if (args.length == 3) {
                Player player = Bukkit.getPlayer(args[2]);
                int amount = 1;
                if (player == null) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage("プレイヤー以外がこのコマンドを実行する場合は、プレイヤー名を指定してください。");
                        return false;
                    }
                    player = (Player) sender;

                    try {
                        amount = Integer.parseInt(args[2]);
                        if (amount < 1) {
                            throw new NumberFormatException("正の数のみです。");
                        }
                    } catch (NumberFormatException e) {
                        sender.sendMessage("3つ目の引数にはプレイヤー名またはアイテム数を自然数で指定してください。");
                        return false;
                    }
                }
                item.setAmount(amount);
                player.getInventory().removeItem(item);
            } else if (args.length == 4) {
                Player player = Bukkit.getPlayer(args[2]);
                int amount = 1;
                if (player == null) {
                    sender.sendMessage("そのプレイヤーはオンラインではありません。");
                    return false;
                }
                try {
                    amount = Integer.parseInt(args[3]);
                    if (amount < 0) {
                        throw new NumberFormatException("正の数のみです。");
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage("指定された数字は不正です。");
                    return false;
                }
                item.setAmount(amount);
                player.getInventory().removeItem(item);
            }
        } else if (args[1].equalsIgnoreCase("unregister")) {
            ItemStack handItem = ((Player) sender).getInventory().getItemInMainHand();
            getConfig().set("items." + ChatColor.stripColor(handItem.getItemMeta().getDisplayName()).replaceAll(" ", ""), null);
            sender.sendMessage("アイテムを登録解除しました。");
            return true;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!getConfig().isConfigurationSection("items")) {
            return List.of();
        }
        List<String> items = new ArrayList<>(getConfig().getConfigurationSection("items").getKeys(false));
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], items, new ArrayList<>());
        }
        if (args.length == 2) {
            return StringUtil.copyPartialMatches(args[1], List.of("give", "take", "unregister"), new ArrayList<>());
        }

        if (!args[1].equalsIgnoreCase("take") || !args[1].equalsIgnoreCase("give")) {
            return List.of();
        }

        if (args.length == 3) {
            List<String> playersOrAmount = Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
            for (int i = 1; i <= 5; i++) {
                playersOrAmount.add(String.valueOf(i));
            }

            return StringUtil.copyPartialMatches(args[2], playersOrAmount, new ArrayList<>());
        }

        if (args.length == 4) {
            try {
                Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                return List.of();
            }
            List<String> amount = Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
            for (int i = 1; i <= 5; i++) {
                amount.add(String.valueOf(i));
            }

            return StringUtil.copyPartialMatches(args[3], amount, new ArrayList<>());
        }
        return List.of();
    }
}
