package com.ecom.controller.rest;

import com.ecom.dtos.requests.ImportRequest;
import com.ecom.model.Import;
import com.ecom.model.ImportDetails;
import com.ecom.service.ImportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/import")
@RequiredArgsConstructor
public class ImportRestController {
    private final ImportService importService;

    @GetMapping("")
    public ResponseEntity<List<Import>> getAllImports(@RequestParam(defaultValue = "1") int page,
                                                      @RequestParam(defaultValue = "") String search,
                                                      @RequestParam(defaultValue = "30") int size, HttpServletResponse response) {
        List<Import> imports = importService.getAllImport(page - 1, size, search).stream().map(i -> {
            i.setDetails(i.getDetails().stream().map(this::briefImportDetails).toList());
            return i;
        }).toList();


        response.setHeader("X-Total-Count", importService.countAllImport(search) + "");
        return ResponseEntity.ok(imports);
    }

    private ImportDetails briefImportDetails(ImportDetails details) {
        if(details.getProduct() != null) {
            details.getProduct()
                    .setDescription(null);
            details.getProduct()
                    .setGalleries(null);
        }

        details.setImportRecord(null);
        return details;
    }

    @PostMapping()
    public ResponseEntity<Import> createImport(@RequestBody ImportRequest importObj) {
        Import createdImport = importService.addImport(importObj);
        return ResponseEntity.ok(createdImport);
    }
}
