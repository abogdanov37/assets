package com.assets.api.interfaces;

import com.assets.transfer.Asset;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;


@RestController
public interface AssetsController {

    @RequestMapping(value = "/assets/{id}"
            , method = RequestMethod.GET
            , produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @Async
    @PreAuthorize("hasRole('admin')")
    CompletableFuture<ResponseEntity<Asset>> getAsset(@PathVariable("id") long id);

    @RequestMapping(value = "/assets/{id}/subtree"
            , method = RequestMethod.GET
            , produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    ResponseEntity<List<Asset>> getSubTree(@PathVariable("id") long id);

    @RequestMapping(value = "/assets"
            , method = RequestMethod.POST
            , produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    ResponseEntity addAsset(@RequestBody Asset asset);

    @RequestMapping(value = "/assets"
            , method = RequestMethod.PUT
            , produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    ResponseEntity updateAsset(@RequestBody Asset asset);

    @RequestMapping(value = "/assets/{id}"
            , method = RequestMethod.DELETE
            , produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    ResponseEntity removeAsset(@PathVariable("id") long id);
}
