import java.util.Scanner;

public class temp {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		System.out.println("enter");
		String t = sc.nextLine();
		System.out.println(t.matches("[0-9]+"));
		System.out.println(Integer.parseInt(t)==12);
	}
}
