package com.shopverse.service.impl;

import com.shopverse.dto.request.ProductRequests;
import com.shopverse.dto.response.PageResponse;
import com.shopverse.dto.response.ProductResponse;
import com.shopverse.entity.Category;
import com.shopverse.entity.Product;
import com.shopverse.exception.BadRequestException;
import com.shopverse.exception.ResourceNotFoundException;
import com.shopverse.mapper.ProductMapper;
import com.shopverse.repository.CategoryRepository;
import com.shopverse.repository.ProductRepository;
import com.shopverse.service.ProductService;
import com.shopverse.specification.ProductSpecification;
import com.shopverse.util.SlugUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> getProducts(Pageable pageable) {
        return mapPage(productRepository.findByActiveTrue(pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getById(Long id) {
        return ProductMapper.toResponse(findActiveProduct(id));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getBySlug(String slug) {
        Product product = productRepository.findBySlug(slug)
                .filter(Product::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return ProductMapper.toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> search(String keyword, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        Specification<Product> spec = Specification.where(ProductSpecification.isActive())
                .and(ProductSpecification.keywordSearch(keyword))
                .and(ProductSpecification.hasCategory(categoryId))
                .and(ProductSpecification.priceBetween(minPrice, maxPrice));
        return mapPage(productRepository.findAll(spec, pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> getByCategory(Long categoryId, Pageable pageable) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category not found");
        }
        return mapPage(productRepository.findByCategoryIdAndActiveTrue(categoryId, pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> getDiscounted(Pageable pageable) {
        return mapPage(productRepository.findByActiveTrueAndDiscountPriceIsNotNull(pageable));
    }

    @Override
    @Transactional
    public ProductResponse create(ProductRequests.ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        String slug = generateUniqueSlug(request.getName());
        validateSkuUnique(request.getSku(), null);

        Product product = Product.builder()
                .name(request.getName())
                .slug(slug)
                .description(request.getDescription())
                .price(request.getPrice())
                .discountPrice(request.getDiscountPrice())
                .stockQuantity(request.getStockQuantity())
                .sku(request.getSku())
                .brand(request.getBrand())
                .category(category)
                .imageUrl(request.getImageUrl())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();

        return ProductMapper.toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductResponse update(Long id, ProductRequests.ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        validateSkuUnique(request.getSku(), id);

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setDiscountPrice(request.getDiscountPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setSku(request.getSku());
        product.setBrand(request.getBrand());
        product.setCategory(category);
        product.setImageUrl(request.getImageUrl());
        if (request.getActive() != null) {
            product.setActive(request.getActive());
        }

        return ProductMapper.toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        product.setActive(false);
        productRepository.save(product);
    }

    private Product findActiveProduct(Long id) {
        return productRepository.findById(id)
                .filter(Product::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    private String generateUniqueSlug(String name) {
        String baseSlug = SlugUtil.toSlug(name);
        String slug = baseSlug;
        int counter = 1;
        while (productRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + counter++;
        }
        return slug;
    }

    private void validateSkuUnique(String sku, Long excludeId) {
        productRepository.findBySku(sku).ifPresent(existing -> {
            if (excludeId == null || !existing.getId().equals(excludeId)) {
                throw new BadRequestException("SKU already exists");
            }
        });
    }

    private PageResponse<ProductResponse> mapPage(Page<Product> page) {
        return PageResponse.from(page.map(ProductMapper::toResponse));
    }
}
