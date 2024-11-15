package ovh.mythmc.banco.api.data;

import de.exlll.configlib.Configuration;
import lombok.Getter;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;

import ovh.mythmc.banco.api.accounts.LegacyAccount;

import java.util.ArrayList;
import java.util.List;

@ApiStatus.Internal
@Configuration
@Getter
@Deprecated
@ScheduledForRemoval
public class BancoData { // used for migration

    protected List<LegacyAccount> accounts = new ArrayList<>();

}