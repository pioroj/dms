package pl.com.bottega.dms.model.printing;


import pl.com.bottega.dms.model.Document;

import java.math.BigDecimal;

public class PagesCountPrintCostCalculator implements PrintCostCalculator {

    private static final BigDecimal PAGES_ADDITIONAL_COST = BigDecimal.valueOf(40);
    private static final int PAGES_COUNT_LIMIT = 100;

    private PrintCostCalculator printCostCalculator;

    public PagesCountPrintCostCalculator(PrintCostCalculator printCostCalculator) {
        this.printCostCalculator = printCostCalculator;
    }

    @Override
    public BigDecimal calculateCost(Document document) {
        BigDecimal cost = printCostCalculator.calculateCost(document);

        if (document.getPagesCount() > PAGES_COUNT_LIMIT)
            cost = cost.add(PAGES_ADDITIONAL_COST);

        return cost;
    }
}
