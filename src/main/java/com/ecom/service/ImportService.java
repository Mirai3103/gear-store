package com.ecom.service;

import com.ecom.dtos.requests.ImportRequest;
import com.ecom.model.Import;

import java.util.List;

public interface ImportService {

    Import addImport(ImportRequest importRequest);

    void undoImport(int importId);

    List<Import> getAllImport(int pageNo, int pageSize, String search);

    Long countAllImport(String search);

}
