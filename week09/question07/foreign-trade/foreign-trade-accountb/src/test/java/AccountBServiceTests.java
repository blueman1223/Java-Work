import io.blueman.foreign.trade.accountb.AccountBApplication;
import io.blueman.foreign.trade.accountb.dto.AccountBDTO;
import io.blueman.foreign.trade.accountb.service.AccountBServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

@SpringBootTest(classes = AccountBApplication.class)
class AccountBServiceTests {
	@Autowired
	AccountBServiceImpl service;



	@Test
	void testExchangeUSD() {
		AccountBDTO accountADTO = new AccountBDTO();
		accountADTO.setUserId("user_b");
		accountADTO.setRmbAmount(BigDecimal.valueOf(700));
		accountADTO.setDollarAmount(BigDecimal.valueOf(-100));
		service.exchangeDollar(accountADTO, "user_a");
	}


}
