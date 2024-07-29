package com.microservices.projectservice.controller;

import com.microservices.projectservice.dto.StageCreateRequest;
import com.microservices.projectservice.dto.StageResponse;
import com.microservices.projectservice.dto.StageUpdateRequest;
import com.microservices.projectservice.service.StageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/${api.version}/stage")
@RequiredArgsConstructor
public class StageController {

    private final StageService stageService;

    @GetMapping(path = "/{stageId}")
    @ResponseStatus(HttpStatus.OK)
    public StageResponse getStage(@PathVariable String stageId) {
        return stageService.getStage(stageId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String createStage(@RequestBody StageCreateRequest stageCreateRequest) {
        return stageService.createStage(stageCreateRequest);
    }

    @PatchMapping(path = "/{stageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateStage(@PathVariable String stageId, @RequestBody StageUpdateRequest stageUpdateRequest) {
        stageService.updateStage(stageId, stageUpdateRequest);
    }

    @DeleteMapping(path = "/{stageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStage(@PathVariable String stageId) {
        stageService.deleteStage(stageId);
    }

}
