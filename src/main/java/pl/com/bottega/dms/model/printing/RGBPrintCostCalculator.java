package pl.com.bottega.dms.model.printing;

import pl.com.bottega.dms.model.Document;

import java.math.BigDecimal;

public class RGBPrintCostCalculator implements PrintCostCalculator {

    private static final double RGB_PAGE_COST = 0.1;

    public BigDecimal calculateCost(Document document) {
        return BigDecimal.valueOf(document.getPagesCount() * RGB_PAGE_COST);
    }

}
