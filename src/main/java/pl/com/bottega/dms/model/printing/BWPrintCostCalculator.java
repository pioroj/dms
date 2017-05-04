package pl.com.bottega.dms.model.printing;

import pl.com.bottega.dms.model.Document;

import java.math.BigDecimal;

public class BWPrintCostCalculator implements PrintCostCalculator {

    private static final double BLACK_WHITE_PAGE_COST = 0.03;

    public BigDecimal calculateCost(Document document) {
        return BigDecimal.valueOf(document.getPagesCount() * BLACK_WHITE_PAGE_COST);
    }

}
