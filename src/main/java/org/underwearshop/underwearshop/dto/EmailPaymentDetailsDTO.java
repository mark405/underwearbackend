package org.underwearshop.underwearshop.dto;

import lombok.Getter;
import org.underwearshop.underwearshop.entity.EmailPaymentDetails;

@Getter
public class EmailPaymentDetailsDTO {
    private final String content;

    public EmailPaymentDetailsDTO(EmailPaymentDetails emailPaymentDetails) {
        this.content = emailPaymentDetails.getContent();
    }
}
