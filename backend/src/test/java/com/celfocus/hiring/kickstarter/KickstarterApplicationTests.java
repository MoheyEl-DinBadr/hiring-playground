package com.celfocus.hiring.kickstarter;

import com.celfocus.hiring.kickstarter.security.TestOAuth2Config;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestOAuth2Config.class)
@SpringBootTest
class KickstarterApplicationTests {

	@Test
	void contextLoads() {
	}

}
