package finals.shotefplus.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aliza on 05/06/2017.
 */

public class LinkedObjects {

    Customer customer;
    List<PriceOffer> priceOfferList;
    List<Work> workList;
    Receipt receipt;
    List<Expense> expenseList;

    public LinkedObjects() {
        this.customer = new Customer();
        this.priceOfferList = new ArrayList<PriceOffer>();
        this.workList = new ArrayList<Work>();
        this.receipt = new Receipt();
        this.expenseList = new ArrayList<Expense>();
    }


    public LinkedObjects(Customer customer, List<PriceOffer> priceOfferList,
                         List<Work> workList, Receipt receipt, List<Expense> expenseList) {
        this.customer = customer;
        this.priceOfferList = priceOfferList;
        this.workList = workList;
        this.receipt = receipt;
        this.expenseList = expenseList;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<PriceOffer> getPriceOfferList() {
        return priceOfferList;
    }

    public void setPriceOfferList(List<PriceOffer> priceOfferList) {
        this.priceOfferList = priceOfferList;
    }

    public List<Work> getWorkList() {
        return workList;
    }

    public void setWorkList(List<Work> workList) {
        this.workList = workList;
    }

    public Receipt getReceipt() {
        return receipt;
    }

    public void setReceipt(Receipt receipt) {
        this.receipt = receipt;
    }

    public List<Expense> getExpenseList() {
        return expenseList;
    }

    public void setExpenseList(List<Expense> expenseList) {
        this.expenseList = expenseList;
    }

    public void addToExpenseList(Expense expense) {
        this.expenseList.add(expense);
    }

    public void addToWorkList(Work work) {
        this.workList.add(work);
    }

    public void addToPriceOfferList(PriceOffer priceOffer) {
        this.priceOfferList.add(priceOffer);
    }

    public PriceOffer getPriceOffer(int position) {
        return priceOfferList.get(position);
    }

    public Expense getExpense(int position) {
        return expenseList.get(position);
    }

    public Work getWork(int position) {
        return workList.get(position);
    }
}
