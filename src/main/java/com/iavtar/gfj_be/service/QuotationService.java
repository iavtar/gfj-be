package com.iavtar.gfj_be.service;

import com.iavtar.gfj_be.entity.Quotation;
import com.iavtar.gfj_be.model.response.PagedUserResponse;
import com.iavtar.gfj_be.model.response.ServiceResponse;
import com.iavtar.gfj_be.repository.QuotationRepository;
import com.iavtar.gfj_be.utility.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
@Slf4j
public class QuotationService {
    @Autowired
    private QuotationRepository quotationRepository;
    @Autowired
    private CommonUtil          commonUtil;

    public Quotation createQuotation(Quotation quotation) {
        return quotationRepository.save(Quotation.builder().data(quotation.getData()).price(quotation.getPrice()).agentId(quotation.getAgentId())
                .clientId(quotation.getClientId()).quotationStatus(quotation.getQuotationStatus()).updatedAt(LocalDateTime.now()).build());
    }

    public Quotation updateQuotation(Quotation quotation) {
        Quotation existingQuotation = quotationRepository.findById(quotation.getId()).orElse(null);
        if (existingQuotation == null) {
            log.info("Quotation with id {} not found", quotation.getId());
            throw new IllegalArgumentException("Quotation with id " + quotation.getId() + " not found");
        }
        if (quotation.getClientId() != null) {
            existingQuotation.setClientId(quotation.getClientId());
        }
        if (quotation.getAgentId() != null) {
            existingQuotation.setAgentId(quotation.getAgentId());
        }
        if (quotation.getData() != null) {
            existingQuotation.setData(quotation.getData());
        }
        if (quotation.getPrice() != null) {
            existingQuotation.setPrice(quotation.getPrice());
        }
        if(quotation.getQuotationStatus() != null) {
            existingQuotation.setQuotationStatus(quotation.getQuotationStatus());
        }
        existingQuotation.setUpdatedAt(LocalDateTime.now());
        return quotationRepository.save(quotation);
    }

    public void deleteQuotation(Long quotationId) {
        log.info("Deleting quotation with id {}", quotationId);
        quotationRepository.findById(quotationId).ifPresent(existingQuotation -> quotationRepository.deleteById(quotationId));
    }

    public Quotation findQuotationById(Long id) {
        log.info("Finding quotation with id {}", id);
        return quotationRepository.findById(id).orElse(null);
    }

    public PagedUserResponse<Quotation> findAllQuotationsByClient(Long clientId, int offset, int size, String sortBy) {
        log.info("Getting all quotations for client: {}", clientId);
        return commonUtil.findAllQuotationsByClient(clientId, offset, size, sortBy);
    }

    public PagedUserResponse<Quotation> findAllQuotationsByAgent(Long agentId, int offset, int size, String sortBy) {
        log.info("Getting all quotations for agent: {}", agentId);
        return commonUtil.findAllQuotationsByAgent(agentId, offset, size, sortBy);
    }

    public PagedUserResponse<Quotation> findAllQuotationsByClientAndAgent(Long clientId, Long agentId, int offset, int size, String sortBy) {
        log.info("Getting all quotations for client: {} and agent: {}", clientId, agentId);
        return commonUtil.findAllQuotationsByClientAndAgent(clientId, agentId, offset, size, sortBy);
    }

    public ResponseEntity<?> uploadQuotationImage(MultipartFile file, Long quotationId) {
        try {
            String url = commonUtil.uploadFile(file, quotationId);
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            log.error("Error uploading quotations image: {}", e.getMessage(), e);
            return new ResponseEntity<>(
                    ServiceResponse.builder()
                            .message("Error uploading quotations image")
                            .build(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
