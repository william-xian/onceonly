package cn.mx.app.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

import io.onceonly.db.BaseEntity;
import io.onceonly.db.annotation.Extend;

@Entity
@Extend(entity=UserChief.class,autoCreate=true)
public class Wallet extends BaseEntity{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    @Column(nullable = true)
	private int balance;
    @Column(nullable = true)
	private int expenditure;
    @Column(nullable = true)
	private int income;
	public int getBalance() {
		return balance;
	}
	public void setBalance(int balance) {
		this.balance = balance;
	}
	public int getExpenditure() {
		return expenditure;
	}
	public void setExpenditure(int expenditure) {
		this.expenditure = expenditure;
	}
	public int getIncome() {
		return income;
	}
	public void setIncome(int income) {
		this.income = income;
	}
}
