package com.netcracker.odstc.logviewer;

import com.netcracker.odstc.logviewer.serverconnection.managers.ServerManager;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class RealtimeLogViewerApplicationTests {
	@MockBean
	ServerManager serverManager;

	@Test
	void contextLoads() {
	}

}
