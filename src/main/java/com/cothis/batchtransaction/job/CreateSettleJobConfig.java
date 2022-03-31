package com.cothis.batchtransaction.job;

import com.cothis.batchtransaction.domain.Settle;
import com.cothis.batchtransaction.service.SettleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class CreateSettleJobConfig {

    private static final int TEST_DATA_SIZE = 5000;
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final SettleService settleService;

    @Bean
    public Job createSettleJob() {
        return jobBuilderFactory.get("createSettleJob")
                .incrementer(new RunIdIncrementer())
                .start(createSettleStep())
                .build();
    }

    @Bean
    public Step createSettleStep() {
        return stepBuilderFactory.get("createSettleStep")
                .tasklet(createSettleTask(null))
                .build();
    }

    @Bean
    @StepScope
    public Tasklet createSettleTask(@Value("#{jobParameters['createType']}") CreateType createType) {
        return (stepContribution, chunkContext) -> {
            List<Settle> testDatas = createTestData();

            long startTime = System.currentTimeMillis();

            if (createType == CreateType.SIMPLE) {
                settleService.createSettles(testDatas);
            } else {
                settleService.createSettlesBulk(testDatas);
            }

            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;
            log.info("elapsedTime: {} ms", elapsedTime);
            return RepeatStatus.FINISHED;
        };
    }

    private List<Settle> createTestData() {
        String odNo = UUID.randomUUID().toString().substring(0, 8);
        return IntStream.range(1, TEST_DATA_SIZE + 1)
                .mapToObj(i -> new Settle(odNo, i, BigDecimal.valueOf(Math.random() * 100)))
                .collect(Collectors.toList());
    }

    public enum CreateType {
        SIMPLE, BULK
    }
}
