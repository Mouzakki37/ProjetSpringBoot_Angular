package com.safanner.ecomm.services.admin.faq;

import com.safanner.ecomm.dto.FAQDto;

public interface FAQService {
    FAQDto postFAQ(Long productId, FAQDto faqDto);
}
