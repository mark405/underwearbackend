package org.underwearshop.underwearshop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.underwearshop.underwearshop.entity.EmailPaymentDetails;
import org.underwearshop.underwearshop.repository.EmailPaymentDetailsRepository;

@Service
@RequiredArgsConstructor
public class EmailPaymentDetailsService {
    public static final long CONTENT_ID = 1L;
    public static final String DEFAULT_CONTENT = "";

    private final EmailPaymentDetailsRepository emailPaymentDetailsRepository;

    @Transactional(readOnly = true)
    public EmailPaymentDetails get() {
        return emailPaymentDetailsRepository.findById(CONTENT_ID)
                .orElseGet(() -> emailPaymentDetailsRepository.save(
                        EmailPaymentDetails.builder()
                                .id(CONTENT_ID)
                                .content(DEFAULT_CONTENT)
                                .build()
                ));
    }

    @Transactional
    public EmailPaymentDetails update(String content) {
        EmailPaymentDetails emailPaymentDetails = get();
        emailPaymentDetails.setContent(content);

        return emailPaymentDetailsRepository.save(emailPaymentDetails);
    }
}
