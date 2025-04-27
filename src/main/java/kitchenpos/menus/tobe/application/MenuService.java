package kitchenpos.menus.tobe.application;

import kitchenpos.menus.tobe.domain.Menu;
import kitchenpos.menus.tobe.domain.MenuRepository;
import kitchenpos.products.tobe.domain.event.ProductPriceChangedEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Service
public class MenuService {
    private final MenuRepository menuRepository;

    public MenuService(
            final MenuRepository menuRepository
    ) {
        this.menuRepository = menuRepository;
    }

    // 상품 가격 변경 트랜잭션이 커밋된 후,
    // 메뉴 업데이트 트랜잭션이 새로 열려서 안정적으로 동작함.
    @Transactional(propagation = Propagation.REQUIRES_NEW) // 새로운 트랜잭션 시작
    @TransactionalEventListener() // default: AFTER_COMMIT -> 트랜잭션 커밋 후 실행
    public void handleProductPriceChanged(final ProductPriceChangedEvent event) {
        final List<Menu> menus = menuRepository.findAllByProductId(event.productId());

        for (final Menu menu : menus) {
            menu.refreshDisplayedStatus();
        }
    }
}
