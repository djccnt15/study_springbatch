package com.djccnt15.study_springbatch.settlement_batch.group;

import com.djccnt15.study_springbatch.domain.ServicePolicy;
import com.djccnt15.study_springbatch.domain.repository.CustomerRepository;
import com.djccnt15.study_springbatch.model.SettleGroupEntity;
import com.djccnt15.study_springbatch.settlement_batch.group.model.Customer;
import com.djccnt15.study_springbatch.settlement_batch.utils.EmailProvider;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Builder
public class SettleGroupItemMailWriter implements ItemWriter<List<SettleGroupEntity>> {

    private final CustomerRepository customerRepository;
    private final EmailProvider emailProvider;
    
    public SettleGroupItemMailWriter() {
        this.customerRepository = new CustomerRepository.Fake();
        this.emailProvider = new EmailProvider.Fake();
    }
    
    @Override
    public void write(Chunk<? extends List<SettleGroupEntity>> chunk) throws Exception {
        for (List<SettleGroupEntity> settleGroups : chunk) {
            if (settleGroups.isEmpty()) {
                continue;
            }
            
            final SettleGroupEntity settleGroup = settleGroups.get(0);
            final Long customerId = settleGroup.getCustomerId();
            final Customer customer = customerRepository.findById(customerId);
            
            final Long totalCount = settleGroups.stream().map(SettleGroupEntity::getTotalCount).reduce(0L, Long::sum);
            final Long totalFee = settleGroups.stream().map(SettleGroupEntity::getTotalFee).reduce(0L, Long::sum);
            
            List<String> detailByService = settleGroups.stream()
                .map(it -> "\n\"%s\" - 총 사용 수: %s, 총 비용: %s".formatted(
                    ServicePolicy.findById(it.getServiceId()).getUrl(),
                    it.getTotalCount(),
                    it.getTotalFee()
                ))
                .toList();
            
            final String body = """
                %s 고객님 유료 API 과금 안내
                총 %s건 사용 및 %s원의 비용 발생
                세부 내역은 아래 참고
                
                %s
                """.formatted(
                customer.getName(),
                totalCount,
                totalFee,
                detailByService
            );
            
            emailProvider.send(customer.getEmail(), "유료 API 과금 안내", body);
        }
    }
}
