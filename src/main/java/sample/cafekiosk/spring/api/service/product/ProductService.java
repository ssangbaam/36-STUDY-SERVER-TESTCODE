package sample.cafekiosk.spring.api.service.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sample.cafekiosk.spring.api.controller.product.dto.request.ProductCreateRequest;
import sample.cafekiosk.spring.api.service.product.response.ProductResponse;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductRepository;
import sample.cafekiosk.spring.domain.product.ProductSellingStatus;
import sample.cafekiosk.spring.domain.product.ProductType;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {
    private final ProductRepository productRepository;

    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request) {
        // productNumber
        // 001 002 003 004
        // DB에서 마지막 저장된 Product의 상품 번호를 읽어와서 + 1
        // 009 -> 010
        String nextProductNumber = createNextProductNumber();

        Product product = request.toEntity(nextProductNumber);
        Product saveProduct = productRepository.save(product);

        return ProductResponse.of(saveProduct);
    }

    private String createNextProductNumber() {
        String lastestProductNumber = productRepository.findLatestProductNumber();

        if(lastestProductNumber == null) {
            return "001";
        }

        int lastestProductNumberInt = Integer.parseInt(lastestProductNumber);
        int nextProductNumberInt = lastestProductNumberInt + 1;

        // 9 -> 009, 12 -> 012
        return String.format("%03d", nextProductNumberInt);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> sellingProducts() {
        List<Product> products = productRepository.findAllBySellingStatusIn(ProductSellingStatus.forDisplay());

        return products.stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> HandmadeProducts() {
        List<Product> products = productRepository.findAllByTypeIs(ProductType.forHandmade());

        return products.stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }
}
