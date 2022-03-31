package com.cothis.batchtransaction.service;

import com.cothis.batchtransaction.dao.SettleMapper;
import com.cothis.batchtransaction.domain.Settle;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class SettleService {

    private static final int CHUNK_SIZE = 1000;
    private final SettleMapper settleMapper;

    public void createSettles(List<Settle> settles) {
        settles.forEach(settleMapper::createSettle);
        throw new IllegalStateException("error");
    }

    public void createSettlesBulk(List<Settle> settles) {
        AtomicInteger ai = new AtomicInteger();
        Collection<List<Settle>> chunkedDatas = settles.stream()
                .collect(Collectors.groupingBy(settle -> ai.getAndIncrement() / CHUNK_SIZE))
                .values();
        chunkedDatas.forEach(settleMapper::createSettles);
    }
}
