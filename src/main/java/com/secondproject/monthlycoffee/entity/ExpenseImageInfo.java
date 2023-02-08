package com.secondproject.monthlycoffee.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ExpenseImageInfo {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "eii_id", nullable = false) 
  private Long id;
  
  @Column(name = "eii_filename", nullable = false) 
  private String filename;
  
  @Column(name = "eii_uri", nullable = false) 
  private String uri;
  
  @JoinColumn(name = "eii_ei_id", nullable = false) 
  @ManyToOne(fetch = FetchType.LAZY)
  private ExpenseInfo expense;
}
