package com.quinn.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quinn.finance.entity.Transaction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDate;

@Mapper
public interface TransactionMapper extends BaseMapper<Transaction> {

    @Select("SELECT COALESCE(SUM(amount), 0) FROM fin_transaction " +
            "WHERE user_id = #{userId} AND type = #{type} " +
            "AND date BETWEEN #{startDate} AND #{endDate}")
    BigDecimal sumAmountByType(@Param("userId") Long userId,
                              @Param("type") Integer type,
                              @Param("startDate") LocalDate startDate,
                              @Param("endDate") LocalDate endDate);

    IPage<Transaction> queryTransactions(Page<Transaction> page,
                                        @Param("userId") Long userId,
                                        @Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate,
                                        @Param("type") Integer type,
                                        @Param("category") String category);
}