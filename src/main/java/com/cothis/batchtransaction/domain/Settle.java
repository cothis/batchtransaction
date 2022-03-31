package com.cothis.batchtransaction.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
@ToString
@Setter
public class Settle {
    private final String odNo;
    private final Integer seq;
    private BigDecimal amount;
}
