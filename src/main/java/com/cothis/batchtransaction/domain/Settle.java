package com.cothis.batchtransaction.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
@ToString
public class Settle {
    private final String odNo;
    private final Integer seq;
    private final BigDecimal amount;
}
