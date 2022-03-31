package com.cothis.batchtransaction.job;

import com.cothis.batchtransaction.dao.SettleMapper;
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
import org.springframework.batch.item.*;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class CreateSettleJobConfig {

    private static final int CHUNK_SIZE = 1000;
    private static final int TEST_DATA_SIZE = 5000;
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final SettleService settleService;
    private final SettleMapper settleMapper;

    @Bean
    public Job createSettleJob() {
        return jobBuilderFactory.get("createSettleJob")
                .incrementer(new RunIdIncrementer())
                .start(createSettleStep())
                .next(createChunkStep())
                .build();
    }

    @Bean
    public Step createSettleStep() {
        return stepBuilderFactory.get("createSettleStep")
                .tasklet(createSettleTask(null))
                .build();
    }

    @Bean
    public Step createChunkStep() {
        return stepBuilderFactory.get("createSettleChunkStep")
                .<Settle, Settle>chunk(CHUNK_SIZE)
                .reader(settleItemReader())
                .processor(settleItemProcessor())
                .writer(settleItemWriter(settleService))
                .build();
    }

    @Bean
    @StepScope
    public Tasklet createSettleTask(@Value("#{jobParameters['createType']}") CreateType createType) {
        return (stepContribution, chunkContext) -> {
            List<Settle> testDatas = createTestData();

            long startTime = System.currentTimeMillis();

            if (createType == CreateType.SIMPLE) {
                settleService.createSettlesSimple(testDatas);
            } else {
                settleService.createSettlesAllWithBulk(testDatas);
            }

            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;
            log.info("elapsedTime: {} ms", elapsedTime);
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public ItemReader<Settle> settleItemReader() {
        return new ListItemReader<>(createTestData());
    }

    @Bean
    public ItemProcessor<Settle, Settle> settleItemProcessor() {
        return item -> {
            item.setAmount(BigDecimal.valueOf(Math.random() * 100));
            return item;
        };
    }

    @Bean
    public ItemWriter<Settle> settleItemWriter(SettleService settleService) {
        return settles -> {
            log.info("writer called");
            settleService.createSettles(settles);
        };
    }

    private List<Settle> createTestData() {
        String odNo = UUID.randomUUID().toString().substring(0, 8);
        return IntStream.range(1, TEST_DATA_SIZE + 1)
                .mapToObj(i -> new Settle(odNo, i))
                .collect(Collectors.toList());
    }

    public enum CreateType {
        SIMPLE, BULK
    }
}
