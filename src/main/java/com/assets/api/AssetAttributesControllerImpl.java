package com.assets.api;

import com.assets.api.interfaces.AssetAttributesController;
import com.assets.model.tables.Attributes;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AssetAttributesControllerImpl implements AssetAttributesController {
    private final DSLContext context;

    @Autowired
    public AssetAttributesControllerImpl(DSLContext ctx) {
        context = ctx;
    }

    public ResponseEntity<Map<String, String>> getAllAttributes(long id) {
        Result<Record> result = context.select().from(Attributes.ATTRIBUTES)
                .where(Attributes.ATTRIBUTES.ASSETID.eq(BigInteger.valueOf(id)))
                .fetch();
        if (result.size() > 0) {

            Map<String, String> body = result.stream().collect(
                    Collectors.toMap(r -> r.getValue(Attributes.ATTRIBUTES.NAME)
                            , r -> r.getValue(Attributes.ATTRIBUTES.VALUE)));
            return ResponseEntity.ok().body(body);
        }
        return ResponseEntity.notFound().build();
    }

    public ResponseEntity<String> getAttributeByName(long id, String name) {
        Result<Record> result = context.select().from(Attributes.ATTRIBUTES)
                .where(Attributes.ATTRIBUTES.ASSETID.eq(BigInteger.valueOf(id)))
                .and(Attributes.ATTRIBUTES.NAME.eq(name))
                .fetch();
        if (result.size() > 0) {
            return ResponseEntity.ok().body(result.get(0).getValue(Attributes.ATTRIBUTES.VALUE));
        }
        return ResponseEntity.notFound().build();
    }

    public ResponseEntity addAttributes(long id, Map<String, String> attributes) {
        context.transaction(configuration -> attributes.forEach((key, value) -> DSL.using(configuration)
                .insertInto(Attributes.ATTRIBUTES, Attributes.ATTRIBUTES.ASSETID, Attributes.ATTRIBUTES.NAME, Attributes.ATTRIBUTES.VALUE)
                .values(BigInteger.valueOf(id), key, value)
                .execute()));

        return ResponseEntity.ok().build();
    }

    public ResponseEntity updateAttribute(long id
            , String name
            , String value) {
        context.update(Attributes.ATTRIBUTES)
                .set(Attributes.ATTRIBUTES.VALUE, value)
                .where(Attributes.ATTRIBUTES.ASSETID.eq(BigInteger.valueOf(id)))
                .and(Attributes.ATTRIBUTES.NAME.eq(name))
                .execute();

        return ResponseEntity.ok().build();
    }


    public ResponseEntity removeAttribute(long id, String name) {

        int deleted = context.delete(Attributes.ATTRIBUTES)
                .where(Attributes.ATTRIBUTES.ASSETID.eq(BigInteger.valueOf(id)))
                .and(Attributes.ATTRIBUTES.NAME.eq(name))
                .execute();

        return (deleted > 0 ? ResponseEntity.ok() : ResponseEntity.notFound()).build();
    }
}
