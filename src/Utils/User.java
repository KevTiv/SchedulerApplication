package Utils;

/**
 *User class is used to store an instance of the user that is logged in for the current session.
 */
public class User {

    private static String username;

    public User(String username){
        this.username = username;
    }

    /**
     * @return username.
     */
    public static String getUsername() {
        return username;
    }
}
