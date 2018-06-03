import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;

import javax.net.ssl.SSLException;

public class Driver {
	private static Shop shop;
	private static int serviceTime;
	private static int nBarbers;
	private static int nChairs;
	private static int nCustomers;
	
	
	
	public static void main(String[] args) throws InterruptedException {
		Driver driver=new Driver();
		Scanner scanner=new Scanner(System.in);
		String in=scanner.nextLine();//输入
		String[] arr=in.split("\\s+");
		if(arr.length!=4)
			return;
		
		nBarbers=Integer.parseInt(arr[0]);
		nChairs=Integer.parseInt(arr[1]);
		nCustomers=Integer.parseInt(arr[2]);
		serviceTime=Integer.parseInt(arr[3]);
		
		
//		nBarbers=1;
//		nChairs=1;
//		nCustomers=10;
//		serviceTime=1000;
		
		
		shop=new Shop(nBarbers, nChairs);
		
		
		for(int i=0;i<nBarbers;i++) {
			BarThread barThread=driver.new BarThread(i);
			barThread.start();
		}
		
		Vector<Thread> threads = new Vector<>();  
		for(int i=0;i<nCustomers;i++) {
			CusThread cusThread=driver.new CusThread(i);
			Random random=new Random();
			Thread.sleep(random.nextInt(1000));
			threads.add(cusThread);
			cusThread.start();
		}
		for (Thread thread : threads) {  
		      try {  
		        // 等待所有线程执行完毕  
		        thread.join();
		      } catch (InterruptedException e) {  
		        e.printStackTrace();  
		      }  
		    }  
		System.out.println("没有理发离开的用户数量为："+shop.getDropsoff());
	}
	
	private class BarThread extends Thread{
		private int id;
		public BarThread(int id) {
			this.id=id;
		}
		public void run() {
			while(true) {
				try {
					shop.helloCustomer(id);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					sleep(serviceTime);
				} catch (InterruptedException e) {
					System.out.print("Bar"+id+"异常");
				}
				try {
					shop.byeCustomer(id);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}
	}
	
	private class CusThread extends Thread{
		private int id;
		private int barber=-1;
		public CusThread(int id) {
			this.id=id;
		}
		@Override
		public void run() {
			try {
				if((barber=shop.visitShop(id))!=-1)
					shop.leaveShop(id, barber);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
