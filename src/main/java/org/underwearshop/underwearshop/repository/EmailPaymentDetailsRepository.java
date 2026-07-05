package org.underwearshop.underwearshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.underwearshop.underwearshop.entity.EmailPaymentDetails;

@Repository
public interface EmailPaymentDetailsRepository extends JpaRepository<EmailPaymentDetails, Long> {
}
