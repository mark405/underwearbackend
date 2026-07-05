package org.underwearshop.underwearshop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.underwearshop.underwearshop.entity.InfoPageContent;
import org.underwearshop.underwearshop.repository.InfoPageContentRepository;

@Service
@RequiredArgsConstructor
public class InfoPageContentService {
    public static final long CONTENT_ID = 1L;

    public static final String DEFAULT_CONTENT = """
            Обмін та повернення

            Відповідно до чинного законодавства України, спідня білизна належить до категорії товарів, які не підлягають обміну та поверненню після отримання.

            Якщо під час огляду посилки у відділенні перевізника ви виявили виробничий брак, невідповідність замовленому товару, кольору, розміру або неповну комплектацію, будь ласка, одразу повідомте про це працівника служби доставки та оформіть відмову від отримання.

            Оплата

            Ми пропонуємо зручні способи оплати замовлень:

            Оплата на рахунок ФОП
            Після оформлення замовлення ви отримаєте реквізити для оплати. Відправка товару здійснюється після зарахування коштів на рахунок.

            Післяплата (накладений платіж) Новою поштою
            Ви можете оплатити замовлення під час отримання у відділенні Нової пошти або поштоматі (за наявності такої послуги). Звертаємо увагу, що у цьому випадку перевізник додатково стягує комісію за переказ коштів згідно зі своїми тарифами.

            Будь ласка, перевіряйте товар під час отримання посилки
            Звертаємо вашу увагу, що перевірка товару повинна здійснюватися безпосередньо у відділенні пошти під час отримання посилки.

            У разі доставки до поштомату або через Укрпошту можливість скласти акт огляду відсутня. Проте ми завжди прагнемо знайти рішення та допомогти нашим клієнтам у спірних ситуаціях.

            Претензії щодо якості, комплектності чи відповідності товару після отримання та огляду поза відділенням перевізника не розглядаються.

            Дякуємо за розуміння та довіру до нашого магазину. 💛

            Післяплата (накладений платіж)
            Для замовлень, оформлених з післяплатою через Нову пошту, передбачена передоплата у розмірі 150 грн.

            Передоплата є підтвердженням замовлення та зараховується у його загальну вартість. Під час отримання посилки ви сплачуєте лише залишок суми.

            У разі відмови від отримання замовлення передоплата не повертається, оскільки використовується для компенсації витрат на доставку та повернення посилки.

            Звертаємо увагу, що при оплаті післяплатою Нова пошта додатково стягує комісію за переказ коштів у розмірі 20 грн + 2% від суми післяплати. Дана комісія встановлюється перевізником та оплачується покупцем під час отримання замовлення.

            Дякуємо за розуміння та відповідальне ставлення до оформлення замовлень.""";

    private final InfoPageContentRepository infoPageContentRepository;

    @Transactional(readOnly = true)
    public InfoPageContent get() {
        return infoPageContentRepository.findById(CONTENT_ID)
                .orElseGet(() -> infoPageContentRepository.save(
                        InfoPageContent.builder()
                                .id(CONTENT_ID)
                                .content(DEFAULT_CONTENT)
                                .build()
                ));
    }

    @Transactional
    public InfoPageContent update(String content) {
        InfoPageContent infoPageContent = get();
        infoPageContent.setContent(content);

        return infoPageContentRepository.save(infoPageContent);
    }
}
