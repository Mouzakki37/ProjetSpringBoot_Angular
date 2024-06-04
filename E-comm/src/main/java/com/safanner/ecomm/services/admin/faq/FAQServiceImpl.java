package com.safanner.ecomm.services.admin.faq;

import com.safanner.ecomm.dto.FAQDto;
import com.safanner.ecomm.entities.FAQ;
import com.safanner.ecomm.entities.Product;
import com.safanner.ecomm.repositories.FAQRepository;
import com.safanner.ecomm.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FAQServiceImpl implements FAQService{
    private final FAQRepository faqRepository;

    private final ProductRepository productRepository;

    public FAQDto postFAQ(Long productId, FAQDto faqDto) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if(optionalProduct.isPresent()) {
            FAQ faq = new FAQ();
            faq.setQuestion(faqDto.getQuestion());
            faq.setAnswer(faqDto.getAnswer());
            faq.setProduct(optionalProduct.get());

            return faqRepository.save(faq).getFAQDto();
        }

        return null;
    }

}
