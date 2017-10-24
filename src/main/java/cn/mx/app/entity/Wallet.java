package cn.mx.app.entity;

import io.onceonly.db.annotation.Col;
import io.onceonly.db.annotation.Tbl;
import io.onceonly.db.tbl.BaseEntity;


@Tbl(extend=UserChief.class,autoCreate=true)
public class Wallet extends BaseEntity{
    @Col(nullable = true)
	private int balance;
    @Col(nullable = true)
	private int expenditure;
    @Col(nullable = true)
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
