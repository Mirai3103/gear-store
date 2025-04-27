package com.ecom.controller.rest;

import com.ecom.dtos.requests.ImportRequest;
import com.ecom.model.Import;
import com.ecom.model.ImportDetails;
import com.ecom.service.ImportService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/form/import")
@RequiredArgsConstructor
public class    ImportFormController {
    private final ImportService importService;
    private final ObjectMapper objectMapper;

    @PostMapping()
    public String createImport(@RequestParam String rawJson) throws JsonProcessingException {
        ImportRequest importObj = objectMapper.readValue(rawJson, ImportRequest.class);
        Import createdImport = importService.addImport(importObj);
        return "redirect:/Admin/Profile";
    }
}
