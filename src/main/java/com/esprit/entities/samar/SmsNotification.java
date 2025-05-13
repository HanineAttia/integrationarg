package com.esprit.entities.samar;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class SmsNotification {
    public static final String ACCOUNT_SID = "ACe7cbf556ff1de5f69449e544830f2ca3";
    public static final String AUTH_TOKEN = "12a3771305d412a62eb1c1757fa524b8";

    public static void sendSms(String nomProduit) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        Message message = Message.creator(
                new PhoneNumber("+21621873307"),  // Numéro du destinataire
                new PhoneNumber("++12313008734"), // Ton numéro Twilio
                "Nouveau produit ajouté : " + nomProduit
        ).create();

        System.out.println("SMS envoyé : " + message.getSid());
    }
}
