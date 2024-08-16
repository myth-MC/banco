package ovh.mythmc.banco.api.data;

import de.exlll.configlib.Configuration;
import lombok.Getter;
import ovh.mythmc.banco.api.accounts.Account;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Getter
public class BancoData {

    protected List<Account> accounts = new ArrayList<>();

}
