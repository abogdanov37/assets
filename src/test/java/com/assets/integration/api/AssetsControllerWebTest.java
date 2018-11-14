package com.assets.integration.api;

import com.assets.api.interfaces.AssetsController;
import com.assets.integration.configuration.DatabaseStructureSetup;
import com.assets.integration.configuration.PersistanceTestConfiguration;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(AssetsController.class)
@ContextConfiguration(classes = PersistanceTestConfiguration.class)
public class AssetsControllerWebTest {

    @Rule
    @Autowired
    public DatabaseStructureSetup setUpDatabase;
    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    @Sql(scripts = "classpath:db/test-asset-data.sql", config = @SqlConfig(encoding = "UTF-8"))
    @DisplayName("Check that assets service return value with id 1")
    public void getAsset_RightId_Success() throws Exception {

        MvcResult result = mockMvc
                .perform(get("/assets/1")
                        .accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.name").value("Завод №1"));
    }

}