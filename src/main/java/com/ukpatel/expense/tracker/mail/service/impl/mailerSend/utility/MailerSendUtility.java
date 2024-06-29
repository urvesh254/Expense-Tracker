package com.ukpatel.expense.tracker.mail.service.impl.mailerSend.utility;

import com.ukpatel.expense.tracker.mail.service.impl.mailerSend.property.MailerSendProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.ukpatel.expense.tracker.mail.service.impl.mailerSend.constant.MailerSendConstant.*;

@Component
@RequiredArgsConstructor
public class MailerSendUtility {

    private final MailerSendProperty mailerSendProperty;

    public Map<String, Object> getAuthenticationCodeMailArgs(
            String toName,
            String toEmailId,
            String authCode
    ) {
        return getAuthenticationCodeMailArgs(
                mailerSendProperty.getFromName(),
                mailerSendProperty.getFromEmailId(),
                toName,
                toEmailId,
                authCode
        );
    }

    public Map<String, Object> getAuthenticationCodeMailArgs(
            String fromName,
            String fromEmailId,
            String toName,
            String toEmailId,
            String authCode
    ) {
        Map<String, Object> args = new HashMap<>();

        args.put(KEY_API_KEY, mailerSendProperty.getApiToken());

        // from
        args.put(KEY_FROM_NAME, fromName);
        args.put(KEY_FROM_EMAIL_ID, fromEmailId);

        // to
        args.put(KEY_TO_NAME, toName);
        args.put(KEY_TO_EMAIL_ID, toEmailId);

        // subject
        args.put(KEY_EMAIL_SUBJECT, mailerSendProperty.getForgotPasswordEmailSubject());

        // template
        char[] authCodeCharArray = authCode.toCharArray();
        Map<String, String> parameters = IntStream.range(0, authCodeCharArray.length)
                .boxed()
                .collect(Collectors.toMap(
                        i -> "code_digit_" + (i + 1),
                        i -> String.valueOf(authCodeCharArray[i])
                ));
        parameters.put("recipient_name", toName);
        args.put(KEY_IS_TEMPLATE, true);
        args.put(KEY_EMAIL_TEMPLATE_ID, mailerSendProperty.getAuthenticationCodeMailTemplateId());
        args.put(KEY_EMAIL_TEMPLATE_PARAMETERS, parameters);

        return args;
    }

}
