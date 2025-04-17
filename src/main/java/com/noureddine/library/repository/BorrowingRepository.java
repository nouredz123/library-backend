package com.noureddine.library.repository;

import com.noureddine.library.entity.Borrowing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowingRepository extends JpaRepository<Borrowing, Long> {
    public Optional<Borrowing> findByInventoryNumber(String inventoryNumber);
    List<Borrowing> findByMemberId(Long memberId);
    boolean existsByInventoryNumber(String inventoryNumber);
    void deleteByInventoryNumber(String inventoryNumber);
}
