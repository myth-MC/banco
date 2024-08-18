package ovh.mythmc.banco.api.items;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.event.impl.BancoItemRegisterEvent;
import ovh.mythmc.banco.api.event.impl.BancoItemUnregisterEvent;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BancoItemManager {

    public static final BancoItemManager instance = new BancoItemManager();
    private static final List<BancoItem> itemsList = new ArrayList<>();

    public void registerAll(final @NotNull List<BancoItem> items) {
        itemsList.clear();

        items.forEach(this::register);
    }

    public void register(final @NotNull BancoItem... items) {
        Arrays.asList(items).forEach(bancoItem -> {
            itemsList.add(bancoItem);

            // Call BancoItemRegisterEvent
            Banco.get().getEventManager().publish(new BancoItemRegisterEvent(bancoItem));
        });
    }

    public void unregister(final @NotNull BancoItem... items) {
        Arrays.asList(items).forEach(bancoItem -> {
            itemsList.remove(bancoItem);

            // Call BancoItemUnregisterEvent
            Banco.get().getEventManager().publish(new BancoItemUnregisterEvent(bancoItem));
        });
    }

    public void clear() { itemsList.clear(); }

    public List<BancoItem> get() { return List.copyOf(itemsList); }

    public BancoItem get(final @NotNull String materialName,
                         final @NotNull String displayName,
                         final Integer customModelData) {
        for (BancoItem item : get())
            if (Objects.equals(materialName, item.name())
                    && Objects.equals(displayName, item.displayName())
                    && Objects.equals(customModelData, item.customModelData()))

                return item;

        return null;
    }

    public BigDecimal value(final @NotNull BancoItem item, int amount) {
        return item.value().multiply(BigDecimal.valueOf(amount));
    }

}
