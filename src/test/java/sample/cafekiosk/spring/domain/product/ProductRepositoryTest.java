package sample.cafekiosk.spring.domain.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static sample.cafekiosk.spring.domain.product.ProductSellingStatus.*;
import static sample.cafekiosk.spring.domain.product.ProductType.*;

@ActiveProfiles("test")
//@SpringBootTest
@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @DisplayName("원하는 판매상태를 가진 상품들을 조회한다.")
    @Test
    void findAllBySellingStatus() {
        //Given
        Product product1 = creaeteProduct("001",HANDMADE, SELLING, "아메리카노", 4000);
        Product product2 =Product.builder()
                .productNumber("002")
                .type(HANDMADE)
                .sellingStatus(HOLD)
                .name("카페라떼")
                .price(4000)
                .build();
        Product product3 =Product.builder()
                .productNumber("003")
                .type(HANDMADE)
                .sellingStatus(STOP_SELLING)
                .name("팥빙수")
                .price(7000)
                .build();

        productRepository.saveAll(List.of(product1, product2, product3));

        //When
        List<Product> products = productRepository.findAllBySellingStatusIn(forDisplay());

        //Then
        assertThat(products).hasSize(2)
                .extracting("productNumber", "name", "sellingStatus")
                .containsExactlyInAnyOrder(
                        tuple("001", "아메리카노", SELLING),
                        tuple("002", "카페라떼", HOLD)
                );

    }

    @DisplayName("원하는 상품 타입을 가진 상품들을 조회한다.")
    @Test
    void test() {
        //Given
        Product product1 = creaeteProduct("001",HANDMADE, SELLING, "아메리카노", 4000);
        Product product2 = creaeteProduct("002",HANDMADE, HOLD, "카페라떼", 4500);
        Product product3 = creaeteProduct("003",BAKERY, STOP_SELLING, "팥빙수", 7000);

        productRepository.saveAll(List.of(product1, product2, product3));

        //When
        List<Product> products = productRepository.findAllByTypeIs(forHandmade());

        //Then
        assertThat(products).hasSize(2)
                .extracting("productNumber", "name", "type")
                .containsExactlyInAnyOrder(
                        tuple("001", "아메리카노", HANDMADE),
                        tuple("002", "카페라떼", HANDMADE)
                );

    }

    @DisplayName("상품번호 리스트로 상품번호를 조회한다.")
    @Test
    void findAllByProductNumberIn() {
        //Given
        Product product1 = creaeteProduct("001",HANDMADE, SELLING, "아메리카노", 4000);
        Product product2 = creaeteProduct("002",HANDMADE, HOLD, "카페라떼", 4500);
        Product product3 = creaeteProduct("003",HANDMADE, STOP_SELLING, "팥빙수", 7000);

        productRepository.saveAll(List.of(product1, product2, product3));

        //When
        List<Product> products = productRepository.findAllByProductNumberIn(List.of("001","002"));

        //Then
        assertThat(products).hasSize(2)
                .extracting("productNumber", "name", "type")
                .containsExactlyInAnyOrder(
                        tuple("001", "아메리카노", HANDMADE),
                        tuple("002", "카페라떼", HANDMADE)
                );

    }

    @DisplayName("가장 마지막으로 저장한 상품의 상품번호를 읽어온다.")
    @Test
    void findLatestProductNumber() {
        //Given
        String targetProductNumber = "003";

        Product product1 = creaeteProduct("001",HANDMADE, SELLING, "아메리카노", 4000);
        Product product2 = creaeteProduct("002",HANDMADE, HOLD, "카페라떼", 4500);
        Product product3 = creaeteProduct(targetProductNumber,HANDMADE, STOP_SELLING, "팥빙수", 7000);

        productRepository.saveAll(List.of(product1, product2, product3));

        //When
        String latestProductNumber = productRepository.findLatestProductNumber();

        //Then
        assertThat(latestProductNumber).isEqualTo(targetProductNumber);

    }

    @DisplayName("가장 마지막으로 저장한 상품의 상품번호를 읽어올 때, 상품이 하나도 없는 경우에은 null을 반환한다")
    @Test
    void findLatestProductNumberWhenProductIsEmpty() {
        //When
        String latestProductNumber = productRepository.findLatestProductNumber();

        //Then
        assertThat(latestProductNumber).isNull();

    }

    private static Product creaeteProduct(
            String productNumber,
            ProductType productType,
            ProductSellingStatus productSellingStatus,
            String name,
            int price
    ) {
        return Product.builder()
                .productNumber(productNumber)
                .type(productType)
                .sellingStatus(productSellingStatus)
                .name(name)
                .price(price)
                .build();
    }
}