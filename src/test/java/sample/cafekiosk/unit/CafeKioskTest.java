package sample.cafekiosk.unit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sample.cafekiosk.unit.Beverage.Americano;
import sample.cafekiosk.unit.Beverage.Latte;
import sample.cafekiosk.unit.order.Order;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CafeKioskTest {

    @Test
    void add_manual_test() {
        CafeKiosk cafeKiosk = new CafeKiosk();
        cafeKiosk.add(new Americano());

        System.out.println(">>> 담긴 음료 수 : " + cafeKiosk.getBeverages().size());
        System.out.println(">>> 담긴 음료 : " + cafeKiosk.getBeverages().get(0).getName());
    }

    //@DisplayName("음료 1개 추가 테스트")
    @DisplayName("음료 1개를 추가하면 주문 목록에 담긴다.")
    @Test
    void add() {
        // Given 단계
        CafeKiosk cafeKiosk = new CafeKiosk();

        // When 단계
        cafeKiosk.add(new Americano());

        // Then 단계
        assertThat(cafeKiosk.getBeverages()).hasSize(1);
        assertThat(cafeKiosk.getBeverages().get(0).getName()).isEqualTo("아메리카노");
    }

    @DisplayName("음료 여러개를 추가하면 주문 목록에 담긴다.")
    @Test
    void addSeveralBeverages() {
        // Given 단계
        CafeKiosk cafeKiosk = new CafeKiosk();
        Americano americano = new Americano();

        // When 단계
        cafeKiosk.add(americano, 2);

        // Given 단계
        // 해피케이스에 대한 테스트
        assertThat(cafeKiosk.getBeverages()).hasSize(2);
        assertThat(cafeKiosk.getBeverages().get(0).getName()).isEqualTo("아메리카노");
        assertThat(cafeKiosk.getBeverages().get(1).getName()).isEqualTo("아메리카노");
    }

    @DisplayName("음료를 0개 추가하면 음료 갯수 오류 메세지가 나온다.")
    @Test
    void addZeroBeverages() {
        // Given 단계
        CafeKiosk cafeKiosk = new CafeKiosk();
        Americano americano = new Americano();

        // When 단계 + Then 단계
        // 예외케이스에 대한 테스트(경계값에서 테스트)
        assertThatThrownBy(() -> cafeKiosk.add(americano, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("음료는 1잔 이상 주문하실 수 있습니다.");
    }



    @DisplayName("음료 주문을 제거하면 주문 목록에서 제거된다.")
    @Test
    void remove() {
        // Given 단계
        CafeKiosk cafeKiosk = new CafeKiosk();
        Americano americano = new Americano();

        cafeKiosk.add(americano);
        assertThat(cafeKiosk.getBeverages()).hasSize(1);

        // When 단계
        cafeKiosk.remove(americano);

        // Then 단계
        assertThat(cafeKiosk.getBeverages()).isEmpty();
    }

    @DisplayName("음료 주문을 통채로 제거하면 주문 목록에서 모든 음료가 제거된다.")
    @Test
    void clear() {
        // Given 단계
        CafeKiosk cafeKiosk = new CafeKiosk();
        Americano americano = new Americano();
        Latte latte = new Latte();

        cafeKiosk.add(americano);
        cafeKiosk.add(latte);
        assertThat(cafeKiosk.getBeverages()).hasSize(2);

        // When 단계
        cafeKiosk.clear();

        // Then 단계
        assertThat(cafeKiosk.getBeverages()).isEmpty();
    }

    @Test
    void calculateTotalPrice() {
        // Given 단계 - 테스트에 필요한 객체를 준비하고 상황을 만듦.
        CafeKiosk cafeKiosk = new CafeKiosk();
        Americano americano = new Americano();
        Latte latte = new Latte();

        cafeKiosk.add(americano);
        cafeKiosk.add(latte);

        // When 단계 - 보통 한줄인 경우가 많음. 메서드를 호출하는 단계
        int totalPrice = cafeKiosk.calculateTotalPrice();

        // Then 단계 - 검증하는 단계
        assertThat(totalPrice).isEqualTo(8500);
    }

    @DisplayName("영업 시간에는 주문을 생성할 수 있다.")
    @Test
    void createOrderWithCurrentTime() {
        // Given 단계
        CafeKiosk cafeKiosk = new CafeKiosk();
        Americano americano = new Americano();

        cafeKiosk.add(americano);

        // When 단계
        Order order = cafeKiosk.createOrder(LocalDateTime.of(2025,5,1,11, 0));

        // Then 단계
        // 가게 운영시간 안에서 실행할때만 성공하는 테스트에서 운영시간기준 테스트가 가능해짐
        assertThat(order.getBeverages()).hasSize(1);
        assertThat(order.getBeverages().get(0).getName()).isEqualTo("아메리카노");
    }

    @DisplayName("영업 종료 시간 이후에는 주문을 생성할 수 없다.")
    @Test
    void createOrderOutsideOpenTime() {
        // Given 단계
        CafeKiosk cafeKiosk = new CafeKiosk();
        Americano americano = new Americano();

        cafeKiosk.add(americano);

        // When + Then 단계
        // 가게 운영시간 안에서 실행할때만 성공하는 테스트에서 운영시간기준 테스트가 가능해짐
        assertThatThrownBy(() -> cafeKiosk.createOrder(LocalDateTime.of(2025,5,1,9, 0)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("주문 시간이 아닙니다. 관리자에게 문의하세요.");
    }
}