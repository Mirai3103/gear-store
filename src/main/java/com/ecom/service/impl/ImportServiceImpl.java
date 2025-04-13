package com.ecom.service.impl;

import com.ecom.dtos.requests.ImportRequest;
import com.ecom.model.Import;
import com.ecom.repository.ImportDetailsRepository;
import com.ecom.repository.ImportRepository;
import com.ecom.repository.ProductRepository;
import com.ecom.repository.UserRepository;
import com.ecom.service.ImportService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImportServiceImpl implements ImportService {
    private final ImportRepository importRepository;
    private final ImportDetailsRepository importDetailsRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public Import addImport(ImportRequest importRequest) {
        var importEntity = importRequest.toEntity();
        importRepository.save(importEntity);
        var importDetails = importRequest.getDetails(importEntity);
        importDetailsRepository.saveAll(importDetails);
//        plush product quantity
        importRequest.getDetails().forEach(detail -> {
            productRepository.increaseStockById(detail.getProductId(), detail.getQuantity());
        });

        return importEntity;
    }

    @Override
    public void undoImport(int importId) {

    }

    @Override
    public List<Import> getAllImport(int pageNo, int pageSize, String search) {
        var pageable = org.springframework.data.domain.PageRequest.of(pageNo, pageSize);
        var importPage = importRepository.findAll(pageable);
        return importPage.getContent();
    }

    @Override
    public Long countAllImport(String search) {
        return importRepository.count();
    }
}
