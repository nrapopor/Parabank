package com.parasoft.parabank.domain;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.gson.JsonObject;
import com.parasoft.parabank.util.Util;

/**
 * Domain object representing a bank account transaction
 */
@XmlRootElement(name="transaction" )
@XmlType(propOrder={"id", "accountId", "type", "date", "amount", "description"})
public class Transaction {
    public static enum TransactionType {
        Credit, Debit;
    }
    
    private int id;
    private int accountId;
    private TransactionType type;
    private Date date;
    private BigDecimal amount;
    private String description;
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getAccountId() {
        return accountId;
    }
    
    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }
    
    public TransactionType getType() {
        return type;
    }
    
    public int getIntType() {
        return type.ordinal();
    }
    
    public void setType(TransactionType type) {
        this.type = type;
    }
    
    public void setType(int type) {
        this.type = TransactionType.values()[type];
    }
    
    public Date getDate() {
        return date;
    }
    
    public void setDate(Date date) {
        this.date = date;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    public static Transaction readFrom(JsonObject json) throws ParseException {
    	 
    	Transaction ret = new Transaction();
    	ret.setAccountId(json.get("accountId").getAsInt());
    	ret.setAmount(json.get("amount").getAsBigDecimal());
    	ret.setDescription(json.get("description").getAsString());
    	ret.setId(json.get("id").getAsInt());    
    	String dt = json.get("date").getAsString();
    	DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ");    	
    	DateTime dateTime = dtf.parseDateTime(dt);
    	Date date = dateTime.toDate();
    	ret.setDate(date);    
    	ret.setType(TransactionType.valueOf(json.get("type").getAsString()));    
    	return ret;
    }
    
    
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result + accountId;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((date == null) ? 0 : date.toString().hashCode());
        result = prime * result + ((amount == null) ? 0 : amount.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Transaction)) {
            return false;
        }
        Transaction other = (Transaction) obj;
        return id == other.id &&
            accountId == other.accountId &&
            type == other.type &&
            Util.equals(date == null ? null : date.toString(), other.date == null ? null : other.date.toString()) &&
            Util.equals(amount, other.amount) &&
            Util.equals(description, other.description);
    }

    @Override
    public String toString() {
        return "Transaction [id=" + id + ", accountId=" + accountId + ", type="
                + type + ", date=" + date + ", amount=" + amount
                + ", description=" + description + "]";
    }
}
