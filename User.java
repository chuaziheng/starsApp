package project2.starsApp;
import java.util.Scanner;

import java.io.Serializable;
/** Parent class for Admin and Student 
* @author  Chua Zi Heng, Goh Nicholas
* @version 1.0
* @since   2020-11-20
*/
public abstract class User implements Serializable {

    protected String accountID;
    protected String accountPasswordHash;
    final static long serialVersionUID = 123; 

    transient static Scanner sc = new Scanner(System.in);
    
    public User(){};

    public User(String accountID, String accountPasswordHash){
        this.accountID = accountID;
        this.accountPasswordHash = accountPasswordHash;
    }

    public String getUsername(){
        return accountID;
    }
    public String getPassword(){
        return accountPasswordHash;
    }
    
    /** for user to change their password,
     *  first ask user to key in their password which will be unmasked
     *  then ask them to confirm if they want to change
     */
    public void resetPassword(){
        boolean flag = false;
        do {
            System.out.println("Key in your new password: ");
            String newPassword = sc.nextLine();
            System.out.println("Confirm password change? (y/n)");
            char choice = sc.nextLine().charAt(0);
            System.out.println(accountPasswordHash);
            System.out.println(PasswordHashController.hash(newPassword));
            switch (choice){
                case'Y':case'y': 
                    if (!accountPasswordHash.equals(PasswordHashController.hash(newPassword))){
                        accountPasswordHash = PasswordHashController.hash(newPassword);
                        flag = true;
                    }
                    else{
                        System.out.println("Do not use previous password.");
                    }
                    break;
                case 'N':case'n':
                    break;
                
                    }
		} while(!flag);
    }    
    
}
