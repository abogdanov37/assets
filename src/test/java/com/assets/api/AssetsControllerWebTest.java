package com.assets.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest
public class AssetsControllerWebTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    public void getAsset_RightId_Success() throws Exception {
        //Arrange
//        Map<String, String> attributes = new HashMap<>(3);
//        attributes.put("Расположение", "МАРС");
//        attributes.put("Тип", "Завод");
//        attributes.put("Руководитель", "Богданов А.А.");
//        Asset expectedAsset = new Asset(1, "Завод №1", null, attributes);
//
//        MockDataProvider provider = Mock.of(configCtx.fetchFromJSON(new String(Files.readAllBytes(ResourceUtils.getFile("classpath:get-asset-test.json").toPath()),"UTF-8")));
//        DSLContext dsl = DSL.using(new MockConnection(provider));

        //Act
        mockMvc.perform(get("/assets/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Завод №1"));


//        AssetsController controller = new AssetsController(dsl);
//        CompletableFuture<ResponseEntity<Asset>> assetFuture = controller.getAsset(1);
//        ResponseEntity<Asset> assetResult = assetFuture.get();
//        Asset asset = assetResult.getBody();
//
//        //Assert
//        assertThat(asset)
//                .isEqualToComparingFieldByFieldRecursively(expectedAsset);
    }


}