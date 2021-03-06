package com.assets.api;

import com.assets.api.interfaces.AssetsController;
import com.assets.model.tables.Assets;
import com.assets.model.tables.Attributes;
import com.assets.transfer.Asset;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@Component
public class AssetsControllerImpl implements AssetsController {
    private final DSLContext context;

    @Autowired
    public AssetsControllerImpl(DSLContext ctx) {
        context = ctx;
    }

    public CompletableFuture<ResponseEntity<Asset>> getAsset(long id) {
        CompletionStage<Result<Record>> result = context.select().from(Assets.ASSETS)
                .leftJoin(Attributes.ATTRIBUTES)
                .on(Attributes.ATTRIBUTES.ASSETID.eq(Assets.ASSETS.ID))
                .where(Assets.ASSETS.ID.eq(BigInteger.valueOf(id)))
                .fetchAsync();
        return result.toCompletableFuture().thenApply(records -> {
            if (records.size() > 0) {
                Asset asset = new Asset(records.get(0).getValue(Assets.ASSETS.ID).longValue(),
                        records.get(0).getValue(Assets.ASSETS.NAME),
                        records.get(0).getValue(Assets.ASSETS.PARENTID) != null
                                ? records.get(0).getValue(Assets.ASSETS.PARENTID).longValue()
                                : null,
                        records.stream()
                                .filter(r -> r.getValue(Attributes.ATTRIBUTES.NAME) != null)
                                .collect(Collectors.toMap(r -> r.getValue(Attributes.ATTRIBUTES.NAME)
                                        , r -> r.getValue(Attributes.ATTRIBUTES.VALUE)))
                );
                return ResponseEntity.ok().body(asset);
            }
            return ResponseEntity.notFound().build();
        });
    }

    public ResponseEntity<List<Asset>> getSubTree(long id) {
        Result<Record> result = context
                .fetch("WITH RECURSIVE tree(id, name, parentid) AS ( \n" +
                        " SELECT a.id, a.name, a.parentid \n" +
                        " FROM \"asset\".\"ASSETS\" a\n" +
                        " WHERE a.id = ? \n" +
                        " UNION ALL \n" +
                        " SELECT aa.id, aa.name, aa.parentid \n" +
                        " FROM tree t \n" +
                        " INNER JOIN \"asset\".\"ASSETS\" aa ON t.id = aa.parentid \n" +
                        ") SELECT r.id, r.name, r.parentid, atr.name, atr.value \n" +
                        "FROM tree r\n" +
                        "LEFT OUTER JOIN \"asset\".\"ATTRIBUTES\" atr on atr.assetid = r.id \n" +
                        "ORDER BY r.parentid;", id);

        if (result.size() > 0) {
            Record previous = null;
            Asset asset = null;
            List<Asset> body = new ArrayList<>();
            for (Record r : result) {
                if (previous == null
                        || previous.getValue("ID", BigDecimal.class).longValue() != r.getValue("ID", BigDecimal.class).longValue()) {
                    if (asset != null) {
                        body.add(asset);
                    }
                    asset = new Asset(r.getValue("ID", BigDecimal.class).longValue(),
                            r.getValue(Assets.ASSETS.NAME),
                            r.getValue("PARENTID", BigDecimal.class) != null
                                    ? r.getValue(Assets.ASSETS.PARENTID).longValue()
                                    : null,
                            new HashMap<>());
                    asset.getAttributes().put(r.getValue(Attributes.ATTRIBUTES.NAME), r.getValue(Attributes.ATTRIBUTES.VALUE));
                    previous = r;
                } else {
                    asset.getAttributes().put(r.getValue(Attributes.ATTRIBUTES.NAME), r.getValue(Attributes.ATTRIBUTES.VALUE));
                }
            }
            body.add(asset);
            return ResponseEntity.ok().body(body);
        }
        return ResponseEntity.notFound().build();
    }

    public ResponseEntity addAsset(Asset asset) {
        context.transaction(configuration -> {
            DSL.using(configuration).insertInto(Assets.ASSETS, Assets.ASSETS.ID, Assets.ASSETS.NAME, Assets.ASSETS.PARENTID)
                    .values(BigInteger.valueOf(asset.getId()), asset.getName(), asset.getParentId() != null ? BigInteger.valueOf(asset.getParentId()) : null)
                    .execute();

            asset.getAttributes().forEach((key, value) -> DSL.using(configuration)
                    .insertInto(Attributes.ATTRIBUTES, Attributes.ATTRIBUTES.ASSETID, Attributes.ATTRIBUTES.NAME, Attributes.ATTRIBUTES.VALUE)
                    .values(BigInteger.valueOf(asset.getId()), key, value)
                    .execute());

        });

        return ResponseEntity.ok().build();
    }

    public ResponseEntity updateAsset(Asset asset) {
        context.transaction(configuration -> {
            DSL.using(configuration).delete(Assets.ASSETS)
                    .where(Assets.ASSETS.ID.eq(BigInteger.valueOf(asset.getId())))
                    .execute();

            DSL.using(configuration).insertInto(Assets.ASSETS, Assets.ASSETS.ID, Assets.ASSETS.NAME, Assets.ASSETS.PARENTID)
                    .values(BigInteger.valueOf(asset.getId()), asset.getName(), asset.getParentId() != null ? BigInteger.valueOf(asset.getParentId()) : null)
                    .execute();

            asset.getAttributes().forEach((key, value) -> DSL.using(configuration)
                    .insertInto(Attributes.ATTRIBUTES, Attributes.ATTRIBUTES.ASSETID, Attributes.ATTRIBUTES.NAME, Attributes.ATTRIBUTES.VALUE)
                    .values(BigInteger.valueOf(asset.getId()), key, value)
                    .execute());
        });

        return ResponseEntity.ok().build();
    }

    public ResponseEntity removeAsset(long id) {
        int deleted = context.delete(Assets.ASSETS)
                .where(Assets.ASSETS.ID.eq(BigInteger.valueOf(id)))
                .execute();

        return (deleted > 0 ? ResponseEntity.ok() : ResponseEntity.notFound()).build();
    }
}
