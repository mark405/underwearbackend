package org.underwearshop.underwearshop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.underwearshop.underwearshop.dto.EmailPaymentDetailsDTO;
import org.underwearshop.underwearshop.dto.EmailPaymentDetailsUpdateDTO;
import org.underwearshop.underwearshop.service.EmailPaymentDetailsService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/email-payment-details")
public class EmailPaymentDetailsController {
    private final EmailPaymentDetailsService emailPaymentDetailsService;

    @GetMapping("/admin")
    public EmailPaymentDetailsDTO get() {
        return new EmailPaymentDetailsDTO(emailPaymentDetailsService.get());
    }

    @PutMapping("/admin")
    public EmailPaymentDetailsDTO update(@RequestBody @Valid EmailPaymentDetailsUpdateDTO dto) {
        return new EmailPaymentDetailsDTO(emailPaymentDetailsService.update(dto.getContent()));
    }
}
