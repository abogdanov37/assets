package com.assets.api;

import com.assets.model.tables.Attributes;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
public class AssetAttributesController {
    private final DSLContext context;

    @Autowired
    public AssetAttributesController(DSLContext ctx) {
        context = ctx;
    }

    @RequestMapping(value = "/assets/{id}/attributes"
            , method = RequestMethod.GET
            , produces="application/json;charset=UTF-8")
    public ResponseEntity<Map<String, String>> getAllAttributes(@PathVariable("id") long id) {
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

    @RequestMapping(value = "/assets/{id}/attributes/{name}"
            , method = RequestMethod.GET
            , produces="application/json;charset=UTF-8")
    public ResponseEntity<String> getAttributeByName(@PathVariable("id") long id, @PathVariable("name") String name) {
        Result<Record> result = context.select().from(Attributes.ATTRIBUTES)
                .where(Attributes.ATTRIBUTES.ASSETID.eq(BigInteger.valueOf(id)))
                .and(Attributes.ATTRIBUTES.NAME.eq(name))
                .fetch();
        if (result.size() > 0) {
            return ResponseEntity.ok().body(result.get(0).getValue(Attributes.ATTRIBUTES.VALUE));
        }
        return ResponseEntity.notFound().build();
    }

    @RequestMapping(value = "/assets/{id}/attributes"
            , method = RequestMethod.POST
            , produces="application/json;charset=UTF-8")
    public ResponseEntity addAttributes(@PathVariable("id") long id, @RequestBody Map<String, String> attributes) {
        context.transaction(configuration -> {
            attributes.entrySet().forEach(e -> {
                DSL.using(configuration)
                        .insertInto(Attributes.ATTRIBUTES, Attributes.ATTRIBUTES.ASSETID, Attributes.ATTRIBUTES.NAME, Attributes.ATTRIBUTES.VALUE)
                        .values(BigInteger.valueOf(id), e.getKey(), e.getValue())
                        .execute();
            });
        });

        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/assets/{id}/attributes/{name}"
            , method = RequestMethod.PUT
            , produces="application/json;charset=UTF-8")
    public ResponseEntity updateAttribute(@PathVariable("id") long id
            , @PathVariable("name") String name
            , @RequestBody String value) {
        context.update(Attributes.ATTRIBUTES)
                .set(Attributes.ATTRIBUTES.VALUE, value)
                .where(Attributes.ATTRIBUTES.ASSETID.eq(BigInteger.valueOf(id)))
                .and(Attributes.ATTRIBUTES.NAME.eq(name))
                .execute();

        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/assets/{id}/attributes/{name}"
            , method = RequestMethod.DELETE
            , produces="application/json;charset=UTF-8")
    public ResponseEntity removeAttribute(@PathVariable("id") long id, @PathVariable("name") String name) {

        int deleted = context.delete(Attributes.ATTRIBUTES)
                .where(Attributes.ATTRIBUTES.ASSETID.eq(BigInteger.valueOf(id)))
                .and(Attributes.ATTRIBUTES.NAME.eq(name))
                .execute();

        return (deleted > 0 ? ResponseEntity.ok() : ResponseEntity.notFound()).build();
    }
}
