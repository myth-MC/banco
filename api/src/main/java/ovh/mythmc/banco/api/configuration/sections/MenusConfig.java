package ovh.mythmc.banco.api.configuration.sections;

import de.exlll.configlib.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class MenusConfig {

    private InfoMenu info = new InfoMenu("ʙᴀɴᴄᴏ", "Displays detailed information about the plugin and server environment.", "<yellow><bold>%s</bold></yellow>", "<gray>%s</gray>");

    private BalanceTopMenu balanceTop = new BalanceTopMenu("ʙᴀɴᴄᴏ", "Displays the players with the highest total balance based on their items.", "<yellow><bold>%s</bold></yellow> <green>%s</green> <gray>-</gray> <green>%s</green>");

    private TransactionHistoryMenu transactionHistory = new TransactionHistoryMenu(
        "ʙᴀɴᴄᴏ", 
        "Displays your latest transactions, including deposits and withdrawals. The log resets automatically on each server restart.", 
        "Displays %s's latest transactions, including deposits and withdrawals. The log resets automatically on each server restart.", 
        "MM/dd/yyyy HH:mm"
    );

    private BalanceConvertMenu balanceConvert = new BalanceConvertMenu(
        "ʙᴀɴᴄᴏ", 
        "This menu allows you to convert your balance into the equivalent amount of any available item. Select an item to see how many units you could obtain based on your balance.",
        "Compact all",
        "Convert to <item>",
        "Select the amount of <green><item></green> to convert into.",
        "Are you sure you want to compact your balance?",
        "<yellow>Please make sure you have enough space in your inventory.</yellow> After compacting your balance, you will receive the following items:"
    );

    public record InfoMenu(String title, String description, String keyFormat, String valueFormat) { }

    public record BalanceTopMenu(String title, String description, String format) { }

    public record TransactionHistoryMenu(String title, String description, String othersDescription, String dateFormat) { }

    public record BalanceConvertMenu(String title, String description, String compactButton, String itemButton, String itemDescription, String compactTitle, String compactWarning) { }

}
