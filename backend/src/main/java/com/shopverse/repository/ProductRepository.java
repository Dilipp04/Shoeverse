package com.shopverse.repository;

import com.shopverse.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    Optional<Product> findBySlug(String slug);

    Optional<Product> findBySku(String sku);

    boolean existsBySlug(String slug);

    boolean existsBySku(String sku);

    Page<Product> findByCategoryIdAndActiveTrue(Long categoryId, Pageable pageable);

    Page<Product> findByActiveTrue(Pageable pageable);

    @Query("""
            SELECT p FROM Product p
            WHERE p.active = true
              AND (
                  LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                  OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
                  OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :keyword, '%'))
              )
            """)
    Page<Product> searchActiveProducts(@Param("keyword") String keyword, Pageable pageable);

    Page<Product> findByActiveTrueAndDiscountPriceIsNotNull(Pageable pageable);

    long countByActiveTrue();

    @Query("""
            SELECT p FROM Product p
            WHERE p.active = true
              AND p.category.id = :categoryId
              AND (:minPrice IS NULL OR COALESCE(p.discountPrice, p.price) >= :minPrice)
              AND (:maxPrice IS NULL OR COALESCE(p.discountPrice, p.price) <= :maxPrice)
            """)
    Page<Product> filterByCategoryAndPrice(
            @Param("categoryId") Long categoryId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable
    );
}
