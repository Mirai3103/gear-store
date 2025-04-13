package com.ecom.dtos.requests;

import com.ecom.model.Import;
import com.ecom.model.ImportDetails;
import com.ecom.model.Product;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ImportRequest {
    private Integer id;
    private String note;

    @Data
    public static class Detail {
        private int productId;
        private Double cost;       // chi phí nhập 1 đơn vị
        private Integer quantity; // số lượng nhập

        public double getTotal_money() {
            return this.cost * this.quantity;
        }
    }

    private List<Detail> details;

    public Import toEntity() {
        if (this.details == null) {
            this.details = new ArrayList<>();
        }
        Import importRecord = new Import();
        importRecord.setImportDate(LocalDateTime.now());
        importRecord.setNote(this.note);
        var cost = this.details.stream()
                .map(Detail::getTotal_money)
                .reduce(0.0, Double::sum);
        importRecord.setTotal_money(cost);
        return importRecord;
    }

    public List<ImportDetails> getDetails(Import importRecord) {
        if (this.details == null) {
            this.details = new ArrayList<>();
        }
        return this.details.stream()
                .map(detail -> {
                    ImportDetails importDetails = new ImportDetails();
                    importDetails.setImportRecord(importRecord);
                    importDetails.setCost(detail.getCost());
                    importDetails.setProductId(detail.getProductId());
                    importDetails.setQuantity(detail.getQuantity());
                    importDetails.setTotal_money(detail.getCost() * detail.getQuantity());
                    return importDetails;
                })
                .toList();
    }

}
