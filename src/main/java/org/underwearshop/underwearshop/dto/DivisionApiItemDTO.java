package org.underwearshop.underwearshop.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DivisionApiItemDTO {
    private String name;
    private SettlementDTO settlement;

    @Getter
    @Setter
    public static class SettlementDTO {
        private Long id;
        private String name;
        private RegionDTO region;
    }

    @Getter
    @Setter
    public static class RegionDTO {
        private Long id;
        private String name;
        private ParentDTO parent;
    }

    @Getter
    @Setter
    public static class ParentDTO {
        private Long id;
        private String name; // oblast
    }
}