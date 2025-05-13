package com.esprit.entities.baya;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class SmsNotificationbaya {
    public static final String ACCOUNT_SID = "AC969e15ec3a4d30975158c07595fed713";
    public static final String AUTH_TOKEN = "ddc375345fb53a1c5458023cbb256f4f";

    public static void sendSms(String userPhoneNumber) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        Message message = Message.creator(
                new PhoneNumber(userPhoneNumber),       // Numéro saisi par l'utilisateur
                new PhoneNumber("+12199122320"),        // Ton numéro Twilio
                "Votre demande de visite agricole est confirmée."
        ).create();

        System.out.println("SMS envoyé : " + message.getSid());
    }
}
