package luke.qam2.model;

import javafx.collections.ObservableList;

public class Customer {

    public Customer(int inId, String inName, String inAddress, String inPostalCode, String inPhone, int inDivisionId){
        customerId = inId;
        customerName = inName;
        address = inAddress;
        postalCode = inPostalCode;
        phone = inPhone;
        divisionId = inDivisionId;
    }

    public int customerId;
    public String customerName;
    public String address;
    public String postalCode;
    public String phone;
    public int divisionId;
}
