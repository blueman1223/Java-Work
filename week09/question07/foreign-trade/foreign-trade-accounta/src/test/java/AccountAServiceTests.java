import io.blueman.foreign.trade.accounta.AccountAApplication;
import io.blueman.foreign.trade.accounta.dto.AccountADTO;
import io.blueman.foreign.trade.accounta.service.AccountAServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

@SpringBootTest(classes = AccountAApplication.class)
class AccountAServiceTests {
	@Autowired
	AccountAServiceImpl service;



	@Test
	void testExchangeRMB() {
		AccountADTO accountADTO = new AccountADTO();
		accountADTO.setUserId("user_a");
		accountADTO.setRmbAmount(BigDecimal.valueOf(-700));
		accountADTO.setDollarAmount(BigDecimal.valueOf(100));
		service.exchangeRMB(accountADTO, "user_b");
	}


}
