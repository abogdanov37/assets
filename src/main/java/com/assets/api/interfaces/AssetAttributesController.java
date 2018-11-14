package com.assets.api.interfaces;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public interface AssetAttributesController {

    @RequestMapping(value = "/assets/{id}/attributes"
            , method = RequestMethod.GET
            , produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    ResponseEntity<Map<String, String>> getAllAttributes(@PathVariable("id") long id);

    @RequestMapping(value = "/assets/{id}/attributes/{name}"
            , method = RequestMethod.GET
            , produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    ResponseEntity<String> getAttributeByName(@PathVariable("id") long id, @PathVariable("name") String name);

    @RequestMapping(value = "/assets/{id}/attributes"
            , method = RequestMethod.POST
            , produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    ResponseEntity addAttributes(@PathVariable("id") long id, @RequestBody Map<String, String> attributes);

    @RequestMapping(value = "/assets/{id}/attributes/{name}"
            , method = RequestMethod.PUT
            , produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    ResponseEntity updateAttribute(@PathVariable("id") long id
            , @PathVariable("name") String name
            , @RequestBody String value);

    @RequestMapping(value = "/assets/{id}/attributes/{name}"
            , method = RequestMethod.DELETE
            , produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    ResponseEntity removeAttribute(@PathVariable("id") long id, @PathVariable("name") String name);
}
