package com.assets.api;

import com.assets.transfer.Asset;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.tools.jdbc.Mock;
import org.jooq.tools.jdbc.MockConnection;
import org.jooq.tools.jdbc.MockDataProvider;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

class AssetsControllerTest {

    private DSLContext configCtx = DSL.using(new DefaultConfiguration());

    @Test
    void getAsset_RightId_Success() throws ExecutionException, InterruptedException, IOException {
        //Arrange
        Map<String, String> attributes = new HashMap<>(3);
        attributes.put("Расположение", "МАРС");
        attributes.put("Тип", "Завод");
        attributes.put("Руководитель", "Богданов А.А.");
        Asset expectedAsset = new Asset(1, "Завод №1", null, attributes);

        MockDataProvider provider = Mock.of(configCtx.fetchFromJSON(new String(Files.readAllBytes(ResourceUtils.getFile("classpath:get-asset-test.json").toPath()),"UTF-8")));
        DSLContext dsl = DSL.using(new MockConnection(provider));

        //Act
        AssetsController controller = new AssetsController(dsl);
        CompletableFuture<ResponseEntity<Asset>> assetFuture = controller.getAsset(1);
        ResponseEntity<Asset> assetResult = assetFuture.get();
        Asset asset = assetResult.getBody();

        //Assert
        assertThat(asset)
                .isEqualToComparingFieldByFieldRecursively(expectedAsset);
    }

    @Test
    void getSubTree() {
    }

    @Test
    void addAsset() {
    }

    @Test
    void updateAsset() {
    }

    @Test
    void removeAsset() {
    }
}