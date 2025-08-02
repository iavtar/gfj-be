package com.iavtar.gfj_be.repository;

import com.iavtar.gfj_be.entity.LedgerTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LedgerRespository extends JpaRepository<LedgerTransaction, Long> {

    Optional<LedgerTransaction> findByTransactionId(String transactionId);

}
