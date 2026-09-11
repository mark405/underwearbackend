package org.underwearshop.underwearshop.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.underwearshop.underwearshop.entity.AttributeOption;
import org.underwearshop.underwearshop.entity.AttributeType;
import org.underwearshop.underwearshop.repository.AttributeOptionRepository;

import java.util.List;

/**
 * Seeds the attribute_option table with the values that used to be hardcoded in the frontend
 * (app/constants/productOptions.ts), preserving their exact label/value pairs and order. Idempotent:
 * only seeds a type if it doesn't already have any rows, so it's safe to run on every startup and
 * won't stomp on options an admin has since added/removed.
 */
@Component
@Order(20)
@RequiredArgsConstructor
public class AttributeOptionSeeder implements ApplicationRunner {
    private final AttributeOptionRepository attributeOptionRepository;

    private record Seed(String value, String label) {
    }

    private static final List<Seed> COLORS = List.of(
            new Seed("black", "Чорний"),
            new Seed("white", "Білий"),
            new Seed("beige", "Бежевий"),
            new Seed("red", "Червоний"),
            new Seed("burgundy", "Бордо"),
            new Seed("pink", "Рожевий"),
            new Seed("blue", "Синій"),
            new Seed("green", "Зелений"),
            new Seed("purple", "Фіолетовий"),
            new Seed("milky", "Молочний")
    );

    private static final List<Seed> MATERIALS = List.of(
            new Seed("cotton", "Бавовна"),
            new Seed("microfiber", "Мікрофібра"),
            new Seed("lace", "Мереживо"),
            new Seed("mesh", "Сітка"),
            new Seed("satin", "Атлас"),
            new Seed("silk", "Шовк")
    );

    private static final List<Seed> FEATURES = List.of(
            new Seed("seamless", "Безшовна"),
            new Seed("lace", "Мереживна"),
            new Seed("basic", "Базова"),
            new Seed("erotic", "Еротична"),
            new Seed("shaping", "Коригуюча"),
            new Seed("large_bust", "Для великого бюста"),
            new Seed("nursing", "Для годування"),
            new Seed("sport", "Спортивна")
    );

    private static final List<Seed> CIRCUMFERENCES = List.of(
            new Seed("65", "65"),
            new Seed("70", "70"),
            new Seed("75", "75"),
            new Seed("80", "80"),
            new Seed("85", "85"),
            new Seed("90", "90"),
            new Seed("95", "95"),
            new Seed("100", "100"),
            new Seed("105", "105"),
            new Seed("115", "115")
    );

    private static final List<Seed> CUPS = List.of(
            new Seed("A", "A"),
            new Seed("B", "B"),
            new Seed("C", "C"),
            new Seed("D", "D"),
            new Seed("E", "E"),
            new Seed("F", "F"),
            new Seed("G", "G"),
            new Seed("H", "H"),
            new Seed("I", "I")
    );

    private static final List<Seed> SIZES = List.of(
            new Seed("S", "S"),
            new Seed("M", "M"),
            new Seed("L", "L"),
            new Seed("XL", "XL"),
            new Seed("2XL", "2XL"),
            new Seed("3XL", "3XL"),
            new Seed("4XL", "4XL"),
            new Seed("5XL", "5XL"),
            new Seed("6XL", "6XL"),
            new Seed("7XL", "7XL"),
            new Seed("8XL", "8XL"),
            new Seed("9XL", "9XL")
    );

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seed(AttributeType.COLOR, COLORS);
        seed(AttributeType.MATERIAL, MATERIALS);
        seed(AttributeType.FEATURES, FEATURES);
        seed(AttributeType.CIRCUMFERENCE, CIRCUMFERENCES);
        seed(AttributeType.CUP, CUPS);
        seed(AttributeType.SIZE, SIZES);
    }

    private void seed(AttributeType type, List<Seed> seeds) {
        if (attributeOptionRepository.existsByType(type)) {
            return;
        }

        int sortOrder = 0;
        for (Seed seed : seeds) {
            attributeOptionRepository.save(
                    AttributeOption.builder()
                            .type(type)
                            .value(seed.value())
                            .label(seed.label())
                            .sortOrder(sortOrder++)
                            .build()
            );
        }
    }
}
