package pl.com.bottega.dms.model.printing;


import pl.com.bottega.dms.model.Document;
import pl.com.bottega.dms.model.DocumentType;

import java.math.BigDecimal;

public class ManualPrintCostCalculator implements PrintCostCalculator {

    private static final double TYPE_MANUAL_ADDITIONAL_COST_FACTOR = 1.3;

    private PrintCostCalculator printCostCalculator;

    public ManualPrintCostCalculator(PrintCostCalculator printCostCalculator) {
        this.printCostCalculator = printCostCalculator;
    }

    @Override
    public BigDecimal calculateCost(Document document) {
        BigDecimal cost = printCostCalculator.calculateCost(document);

        if (document.getDocumentType().equals(DocumentType.MANUAL))
            cost = cost.multiply(BigDecimal.valueOf(TYPE_MANUAL_ADDITIONAL_COST_FACTOR));

        return cost;
    }
}
