package com.algo.dev.photographer;

import com.algo.dev.photographer.repository.PhotographerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(properties = {
		"spring.cache.type=none",
		"spring.redis.host=disabled",
		"spring.profiles.active=test",
		"spring.cloud.vault.enabled=false"})
class CraftDemoPhotographerServiceApplicationTests {

	@MockBean
	private PhotographerRepository photographerRepository;

	@Test
	void contextLoads() {
	}

}
