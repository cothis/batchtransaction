package com.cothis.batchtransaction.dao;

import com.cothis.batchtransaction.domain.Settle;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SettleMapper {

    @Select("SELECT * FROM se.settle")
    List<Settle> findAll();

    @Insert("INSERT INTO se.settle(od_no, seq, amount) VALUES(#{odNo}, #{seq}, #{amount})")
    void createSettle(Settle settle);

    void createSettles(List<? extends Settle> settles);
}
