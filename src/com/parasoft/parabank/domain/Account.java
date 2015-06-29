package com.parasoft.parabank.domain;

import java.math.BigDecimal;



import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.gson.JsonObject;
import com.parasoft.parabank.util.Util;

/**
 * Domain object representing a customer's bank account
 */
@XmlRootElement(name="account" )
@XmlType(propOrder={"id", "customerId", "type", "balance"})
public class Account {
	

	

	private int id;
	private int customerId;
	private AccountType type;
	private BigDecimal balance;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getCustomerId() {
		return customerId;
	}
	
	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}
	
	public AccountType getType() {
		return type;
	}
	
	public int getIntType() {
	    return type.ordinal();
	}
	
	public void setType(AccountType type) {
		this.type = type;
	}
	
	public void setType(int type) {
        this.type = AccountType.values()[type];
    }
	
	public BigDecimal getBalance() {
		return balance;
	}
	
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	
	public BigDecimal getAvailableBalance() {
	    return balance.signum() < 0 ? new BigDecimal(0) : balance; 
	}
	
	public void credit(BigDecimal amount) {
	    balance = balance.add(amount);
	}
	
	public void debit(BigDecimal amount) {
	    balance = balance.subtract(amount);
	}

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result + customerId;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((balance == null) ? 0 : balance.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Account)) {
            return false;
        }
        Account other = (Account)obj;
        return id == other.id &&
            customerId == other.customerId &&
            type == other.type &&
            Util.equals(balance, other.balance);
    }
    
    public static Account readFrom(JsonObject json) {
    	Account ret = new Account();
    	ret.setBalance(json.get("balance").getAsBigDecimal());
    	ret.setCustomerId(json.get("customerId").getAsInt());
    	ret.setId(json.get("id").getAsInt());
    	ret.setType(AccountType.valueOf(json.get("type").getAsString()));
    	return ret;
    }

    @Override
    public String toString() {
        return "Account [id=" + id + ", customerId=" + customerId + ", type="
                + type + ", balance=" + balance + "]";
    }
    
    public static enum AccountType {
        CHECKING(false), SAVINGS(false), LOAN(true);
        
        private boolean internal;
        
        private AccountType(boolean internal) {
            this.internal = internal;
        }
        
        public boolean isInternal() {
            return internal;
        }
    }
}
