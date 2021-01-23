package com.netcracker.odstc.logviewer;

import com.netcracker.odstc.logviewer.serverconnection.managers.JobScheduleManager;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

@SpringBootTest
class RealtimeLogViewerApplicationTests {
	@MockBean
	JobScheduleManager jobScheduleManager;
	@MockBean
	RequestMappingHandlerAdapter requestMappingHandlerAdapter;
	@MockBean
	Validator validator;

	@Test
	void contextLoads() {
	}

}
