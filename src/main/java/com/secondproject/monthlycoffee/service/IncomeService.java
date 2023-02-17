package com.secondproject.monthlycoffee.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.secondproject.monthlycoffee.dto.income.IncomeSumDto;
import com.secondproject.monthlycoffee.dto.post.ExpenseImageDto;
import com.secondproject.monthlycoffee.dto.expense.ExpenseDetailDto;
import com.secondproject.monthlycoffee.dto.income.IncomeDeleteDto;
import com.secondproject.monthlycoffee.dto.income.IncomeDto;
import com.secondproject.monthlycoffee.dto.income.IncomeEditDto;
import com.secondproject.monthlycoffee.dto.income.IncomeListDetailDto;
import com.secondproject.monthlycoffee.dto.income.IncomeExpenseListDto;
import com.secondproject.monthlycoffee.dto.income.IncomeNewDto;
import com.secondproject.monthlycoffee.dto.income.IncomeStringDateDto;
import com.secondproject.monthlycoffee.entity.ExpenseInfo;
import com.secondproject.monthlycoffee.entity.IncomeInfo;
import com.secondproject.monthlycoffee.entity.MemberInfo;
import com.secondproject.monthlycoffee.repository.ExpenseInfoRepository;
import com.secondproject.monthlycoffee.repository.IncomeInfoRepository;
import com.secondproject.monthlycoffee.repository.MemberInfoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class IncomeService {
    private final IncomeInfoRepository incomeRepo;
    private final MemberInfoRepository memberRepo;
    private final ExpenseInfoRepository expenseRepo;

    // 수입 등록
    public IncomeDto newIncome(IncomeNewDto data, Long memberId) {
        if(memberRepo.findById(memberId).isEmpty()) {
            throw new NoSuchElementException("존재하지 않는 회원입니다.");
        }
        MemberInfo member = memberRepo.findById(memberId).orElseThrow();
        IncomeInfo newIncome = new IncomeInfo(data.amount(), data.note(), data.date(), member);
        if(data.amount() <= 0){
            throw new NoSuchElementException("수입은 0원 이상의 금액이어야 합니다."); 
        }
        incomeRepo.save(newIncome);
        return new IncomeDto(newIncome);
    }


    // 수입 전체 조회
    @Transactional(readOnly = true)
    public Page<IncomeDto> incomeList(Long id, Pageable pageable) {
        MemberInfo member = memberRepo.findById(id).orElseThrow();
        return incomeRepo.findByMember(member, pageable).map(IncomeDto::new);
    }


    // 수입 상세 조회
    @Transactional(readOnly = true)
    public IncomeDto incomeDetail(Long id) {
        IncomeInfo income = incomeRepo.findById(id).orElseThrow();
        return new IncomeDto(income);
    }


    // 수입 수정
    public IncomeDto modifyIncome(IncomeEditDto edit, Long id) {
        IncomeInfo income = incomeRepo.findById(id).orElseThrow();
        MemberInfo member = memberRepo.findById(income.getMember().getId()).orElseThrow();
        if(member.getId()!=id) {
            throw new IllegalArgumentException("본인만 수정이 가능합니다."); 
        }
        if(edit.amount() <= 0){
            throw new NoSuchElementException("수입은 0원 이상의 금액이어야 합니다."); 
        }
        income.modifyBudgetDetail(edit.amount(), edit.note(), edit.date());
        return new IncomeDto(income);
    }


    // 수입 삭제
    public IncomeDeleteDto deleteIncome(Long id) {
        IncomeInfo income = incomeRepo.findById(id).orElseThrow();
        MemberInfo member = memberRepo.findById(income.getMember().getId()).orElseThrow();
        if(member.getId()!=id) {
            throw new IllegalArgumentException("본인만 삭제 가능합니다."); 
        }
        incomeRepo.delete(income);
        return new IncomeDeleteDto(id, "수입이 삭제되었습니다.");
    }


    // 수입 연월별 합계
    public IncomeSumDto sumIncomeByYearMonth(YearMonth date, Long id) {
        MemberInfo member = memberRepo.findById(id).orElseThrow();
        LocalDate firstDate = date.atDay(1); 
        LocalDate endDate = date.atEndOfMonth();  
        IncomeSumDto income = incomeRepo.sumByYearMonth(member, firstDate, endDate);
        return income;
    }


    // 수입+지출 연월별 리스트
    public List<IncomeExpenseListDto> searchIncomeByYearMonth(YearMonth date, Long id) {
        MemberInfo member = memberRepo.findById(id).orElseThrow();
        LocalDate firstDate = date.atDay(1); 
        LocalDate endDate = date.atEndOfMonth(); 
        
        List<IncomeInfo> incomeInfos = incomeRepo.findByYearMonth(member, firstDate, endDate);
        List<ExpenseInfo> expenseInfos = expenseRepo.findByYearMonth(member, firstDate, endDate);
        
        List<IncomeExpenseListDto> incomeExpenseList = new ArrayList<IncomeExpenseListDto>();
        List<IncomeListDetailDto> incomeList = new ArrayList<IncomeListDetailDto>();
        List<ExpenseDetailDto> expenseList = new ArrayList<ExpenseDetailDto>();

        IncomeExpenseListDto incomeExpense = new IncomeExpenseListDto();

        for(IncomeInfo i : incomeInfos) {
            incomeExpense.setYearMonth(date);
            IncomeListDetailDto incomeListSet = new IncomeListDetailDto();

            incomeListSet.setId(i.getId());
            incomeListSet.setAmount(i.getAmount());
            incomeListSet.setNote(i.getNote());
            incomeListSet.setDate(i.getDate());

            incomeList.add(incomeListSet);
            incomeExpense.setIncome(incomeList);
        }
            
        for(ExpenseInfo e : expenseInfos) {
            ExpenseDetailDto expenseListSet = new ExpenseDetailDto();
            expenseListSet.setId(e.getId());
            expenseListSet.setCategory(e.getCategory());
            expenseListSet.setBrand(e.getBrand());
            expenseListSet.setPrice(e.getPrice());
            expenseListSet.setMemo(e.getMemo());
            expenseListSet.setTumbler(e.getTumbler());
            expenseListSet.setTaste(e.getTaste());
            expenseListSet.setMood(e.getMood());
            expenseListSet.setBean(e.getBean());
            expenseListSet.setLikeHate(e.getLikeHate());
            expenseListSet.setPayment(e.getPayment());
            expenseListSet.setDate(e.getDate());
            expenseListSet.setImages(e.getExpenseImages().stream().map(ExpenseImageDto::new).toList());
            expenseList.add(expenseListSet);
        }
        incomeExpense.setExpense(expenseList);
        incomeExpenseList.add(incomeExpense);

        return incomeExpenseList;
    }

}
