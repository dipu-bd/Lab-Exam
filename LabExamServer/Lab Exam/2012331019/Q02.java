import java.util.Scanner;

class Main //don't change the class name
{
	public static void main(String arg[])
	{		
		Scanner sc = new Scanner(System.in);
		System.out.println("Hello World!"); 	
		while(sc.hasNext())
		{
			int n = sc.nextInt();
			if(n % 2 == 0)
			{
				System.out.println(n + " is an even number.");			
			}
			else
			{
				System.out.println(n + " is an odd number.");
			}
		}
	}
}
