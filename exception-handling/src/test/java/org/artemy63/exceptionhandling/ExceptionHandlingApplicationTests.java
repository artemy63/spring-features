package org.artemy63.exceptionhandling;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.artemy63.exceptionhandling.dto.ErrorResponseDTO;
import org.artemy63.exceptionhandling.dto.SuccessResponseDTO;
import org.artemy63.exceptionhandling.exception.HandledException;
import org.artemy63.exceptionhandling.exception.UnHandledException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ExceptionHandlingApplicationTests {

	private static final String EXCEPTION_HANDLER_V1_CONTROLLER_PATH = "/exception-handling/v1";
	private static final String EXCEPTION_HANDLER_V2_CONTROLLER_PATH = "/exception-handling/v2";
	private static final String GIVE_ME_SUCCESS_PATH = "/give-me-success";
	private static final String GIVE_ME_HANDLED_EXCEPTION_PATH = "/give-me-handled-exception";
	private static final String GIVE_ME_UNHANDLED_EXCEPTION_PATH = "/give-me-unhandled-exception";
	private static final String GIVE_ME_HANDLED_GLOBALLY_EXCEPTION_PATH = "/give-me-handled-globally-exception";

	private MockMvc mockMvc;
	private ObjectMapper objectMapper;

	@Autowired
	private WebApplicationContext context;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context)
				.alwaysDo(MockMvcResultHandlers.print())
				.build();
		objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	@Test
	public void testGiveMeSuccess() throws Exception {
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(EXCEPTION_HANDLER_V1_CONTROLLER_PATH + GIVE_ME_SUCCESS_PATH))
				.andExpect(status().isOk())
				.andReturn();

		SuccessResponseDTO successResponseDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), SuccessResponseDTO.class);
		Assert.assertEquals("give-me-success", successResponseDTO.getName());
		Assert.assertNull(successResponseDTO.getProperties());
		Assert.assertEquals("success name", successResponseDTO.getData().getName());
		Assert.assertNotNull(successResponseDTO.getData().getValues());
		Assert.assertEquals("one success value", successResponseDTO.getData().getValues().get(0));
	}

	@Test
	public void testGiveHandledExceptionV1() throws Exception {
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
				.get(EXCEPTION_HANDLER_V1_CONTROLLER_PATH + GIVE_ME_HANDLED_EXCEPTION_PATH)
				.param("isThrowException", "true"))
				.andExpect(status().isOk())
				.andReturn();

		ErrorResponseDTO errorResponseDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorResponseDTO.class);
		Assert.assertEquals("ExceptionHandlingV1Controller#HandledException occurs :: ", errorResponseDTO.getMessage());
		Assert.assertEquals("org.artemy63.exceptionhandling.controller.ExceptionHandlingV1Controller", errorResponseDTO.getSourceClass());
	}

	@Test
	public void testGiveHandledGloballyExceptionV1() throws Exception {
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
				.get(EXCEPTION_HANDLER_V1_CONTROLLER_PATH + GIVE_ME_HANDLED_GLOBALLY_EXCEPTION_PATH)
				.param("isThrowException", "true"))
				.andExpect(status().isOk())
				.andReturn();

		ErrorResponseDTO errorResponseDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorResponseDTO.class);
		Assert.assertEquals("ExceptionHandlingV1Controller#HandledGloballyException occurs :: ", errorResponseDTO.getMessage());
		Assert.assertEquals("org.artemy63.exceptionhandling.controller.ExceptionHandlingV1Controller", errorResponseDTO.getSourceClass());
	}

	@Test
	public void testGiveHandledExceptionV2() throws Exception {
		try {
			mockMvc.perform(MockMvcRequestBuilders
                    .get(EXCEPTION_HANDLER_V2_CONTROLLER_PATH + GIVE_ME_HANDLED_EXCEPTION_PATH)
                    .param("isThrowException", "true"))
                    .andExpect(status().is4xxClientError())
                    .andReturn();
		} catch (Exception e) {
			Throwable resolvedException = e.getCause();
			Assert.assertEquals(HandledException.class, resolvedException.getClass());
			Assert.assertEquals("ExceptionHandlingV1Controller should handle it!", ((HandledException) resolvedException).getProperty());
			Assert.assertEquals("ExceptionHandlingV2Controller#HandledException occurs :: ", resolvedException.getMessage());
		}
	}

	@Test
	public void testGiveHandledGloballyExceptionV2() throws Exception {
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
				.get(EXCEPTION_HANDLER_V2_CONTROLLER_PATH + GIVE_ME_HANDLED_GLOBALLY_EXCEPTION_PATH)
				.param("isThrowException", "true"))
				.andExpect(status().isOk())
				.andReturn();

		ErrorResponseDTO errorResponseDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorResponseDTO.class);
		Assert.assertEquals("ExceptionHandlingV2Controller#HandledGloballyException occurs :: ", errorResponseDTO.getMessage());
		Assert.assertEquals("org.artemy63.exceptionhandling.controller.ExceptionHandlingV2Controller", errorResponseDTO.getSourceClass());
	}

	@Test
	public void testGiveUnHandledException() throws Exception {
		try {
			mockMvc.perform(MockMvcRequestBuilders
                    .get(EXCEPTION_HANDLER_V1_CONTROLLER_PATH + GIVE_ME_UNHANDLED_EXCEPTION_PATH, true)
                    .param("isThrowException", "true"))
                    .andExpect(status().is4xxClientError())
                    .andReturn();
			Assert.fail();
		} catch (Exception e) {
			Throwable resolvedException = e.getCause();
			Assert.assertEquals(UnHandledException.class, resolvedException.getClass());
			Assert.assertEquals("ExceptionHandlingV1Controller should not handle it!", ((UnHandledException) resolvedException).getProperty());
			Assert.assertEquals("ExceptionHandlingV1Controller#UnHandledException occurs ::", resolvedException.getMessage());
		}
	}

}
