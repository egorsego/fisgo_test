package steps.payment;

import hub_emulator.response.enums.TypeResponseExPurchase;

public interface PaymentSteps {

    void payOnePositionCash(int sum);

    void payOnePositionCard(int sum);

    void payGoodsFromDb(String code);

    void addPositions(String sum, int count);

    void completePurchase();

    void completePurchase(PaymentsType paymentsType);

    void sendExternalPurchase(TypeResponseExPurchase type);

}
