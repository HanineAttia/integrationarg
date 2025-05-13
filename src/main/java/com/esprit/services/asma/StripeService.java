package com.esprit.services.asma;

import com.esprit.entities.asma.PanierProduit;
import com.esprit.entities.samar.Produit;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import java.util.ArrayList;
import java.util.List;

public class StripeService {
    public StripeService() {
        Stripe.apiKey = "sk_test_51PwqSE2N3wIt098Pd4YFcuKRoZvLHgsdbBCS7sJDWdvvQ5dKOi7Z2eI43Q9KJeae18SqVGuufu43ttVsnuSmKaEc00RFsD56dj";
    }

    public Session createCheckoutSession(List<PanierProduit> panierProduits) throws StripeException {
        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();

        for (PanierProduit item : panierProduits) {
            Produit p = item.getProduit();
            SessionCreateParams.LineItem.PriceData priceData =
                    SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency("eur")
                            .setUnitAmount((long) (p.getPrixUnitaire() * 100)) // en centimes
                            .setProductData(
                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                            .setName(p.getNom())
                                            .build())
                            .build();

            lineItems.add(
                    SessionCreateParams.LineItem.builder()

                            .setQuantity((long) item.getQuantite())
                            .setPriceData(priceData)
                            .build()
            );
        }

        SessionCreateParams params = SessionCreateParams.builder()
                .addAllLineItem(lineItems)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:4242/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl("http://localhost:4242/cancel")
                .build();

        return Session.create(params);
    }
}
