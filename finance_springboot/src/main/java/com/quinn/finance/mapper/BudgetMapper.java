package com.quinn.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quinn.finance.entity.Budget;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDate;

@Mapper
public interface BudgetMapper extends BaseMapper<Budget> {
    @Select("SELECT b.* FROM fin_budget b WHERE b.user_id = #{userId} " +
            "AND (#{month} IS NULL OR b.month = #{month}) " +
            "AND (#{category} IS NULL OR b.category = #{category})")
    IPage<Budget> queryBudgets(Page<Budget> page,
                              @Param("userId") Long userId,
                              @Param("month") String month,
                              @Param("category") String category);

    @Select("SELECT COALESCE(SUM(t.amount), 0) FROM fin_transaction t " +
            "WHERE t.user_id = #{userId} AND t.type = 0 " +
            "AND t.category = #{category} " +
            "AND t.date LIKE CONCAT(#{month}, '%')")
    BigDecimal getSpentAmount(@Param("userId") Long userId,
                             @Param("category") String category,
                             @Param("month") String month);
}