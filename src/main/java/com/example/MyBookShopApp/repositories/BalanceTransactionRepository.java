package com.example.MyBookShopApp.repositories;

import com.example.MyBookShopApp.data.entity.payments.BalanceTransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BalanceTransactionRepository extends JpaRepository<BalanceTransactionEntity, Integer> {

    Page<BalanceTransactionEntity> findAllByUserIdOrderByTimeDesc(int userId, Pageable pageable);
}
