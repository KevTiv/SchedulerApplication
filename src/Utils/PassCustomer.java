package Utils;

/**
 *PassCustomer class is used as a container to pass Customer objects between windows easily.
 */
public class PassCustomer {

    private static Customer customer;

    public PassCustomer(Customer customer){
        this.customer = customer;
    }


    /**
     * @return The Customer to be passed.
     */
    public static Customer getCustomer(){
        return customer;
    }
}
