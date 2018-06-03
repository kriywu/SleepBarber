import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Shop {
	private static int nDropsoff;//未接受服务退出的人数
	private int nBarbers;
	private int nChairs;
	private ArrayList<Barber> barList;
	private ArrayList<Customer> cusList;//等待队列
	
	private Lock lock=new ReentrantLock();
	
	public int visitShop(int  id) throws InterruptedException {
		lock.lock();
		int barId;
		Barber barber;
		Customer customer=new Customer(id);
		if(cusList.size()>nChairs) {
			System.out.println("顾客\t"+id+"\t离开了理发店因为没有空位置了");
			nDropsoff++;
			lock.unlock();
			return -1;
		}
		//理发师都在工作
		if(getSleepBarber()==-1) {
			cusList.add(customer);//进入等待队列
			System.out.println("客户\t"+id+"\t就座,"+"\t就坐的位置是 "+cusList.size());
			
			lock.unlock();
			customer.getLock().lock();
			customer.getCondition().await();//阻塞
			customer.getLock().unlock();
			
			//理发师找用户
			lock.lock();
			barId=customer.getBar();
			barber=barList.get(barId);
			System.out.println("顾客 \t"+id+"\t走到理发师\t\t"+barId);
		}else {
			//用户找理发师
			barId=getSleepBarber();//找到理发师
			customer.setBarber(barId);
			barber=barList.get(barId);
			barber.setCustomer(customer);//告诉理发师自己
			barber.setBusy(true);//设置理发师为忙碌
			System.out.println("顾客 \t"+id+"\t叫醒理发师\t\t"+barId);
		}
		
		lock.unlock();
		barber.getLock().lock();
		barber.getCondition().signalAll();
		barber.getLock().unlock();
		
		
		return barId;
	}
	public void leaveShop(int cusId,int barId) throws InterruptedException {
		lock.lock();
		Barber barber=barList.get(barId);
		Customer customer=barber.getCustomer();
		
		System.out.println("顾客\t"+cusId+"\t等待理发师\t\t"+barId+"\t完成理发");
		
		lock.unlock();
		//等待理发师理发结束
		customer.getLock().lock();
		customer.getCondition().await();
		customer.getLock().unlock();
		
		lock.lock();
		System.out.println("客户\t"+cusId+"\t回答“好的”然后离开");
		
		barber.getLock().lock();
		barber.getCondition().signalAll();//离开
		barber.getLock().unlock();
		lock.unlock();
	}
	public void helloCustomer(int id) throws InterruptedException {
		lock.lock();
		Barber barber=barList.get(id);
		Customer customer;
		barber.getLock().lock();
		//店里面没有顾客
		if(cusList.size()==0) {
			System.out.println("理发师\t"+id+"\t去睡觉了因为没有客户");
			barber.setBusy(false);
			
			lock.unlock();
			barber.getLock().lock();
			barber.getCondition().await();
			barber.getLock().unlock();
			
			lock.lock();
			customer=barber.getCustomer();
		}else {
			customer=cusList.get(0);
			cusList.remove(0);
			customer.setBarber(id);//告诉用户自己的位置
			barber.setCustomer(customer);//锁定自己的用户
			
			lock.unlock();//释放锁
			customer.getLock().lock();;
			customer.getCondition().signalAll();//激活椅子上的客户
			customer.getLock().unlock();
			
			
			barber.getLock().lock();
			barber.getCondition().await();
			barber.getLock().unlock();
			//等待用户走过来
			lock.lock();
		}
		
		System.out.println("理发师\t"+id+"\t正在服务客户 \t"+customer.getId());
		
		lock.unlock();
	}
	public void byeCustomer(int id) throws InterruptedException {
		lock.lock();
		Barber barber=barList.get(id);
		Customer customer=barber.getCustomer();
		
		System.out.println("理发师\t"+id+"\t告诉用户 \t\t"+customer.getId()+"\t发理好了");
		
		lock.unlock();
		customer.getLock().lock();
		customer.getCondition().signalAll();//通知客户理发完了
		customer.getLock().unlock();
		
		
		barber.getLock().lock();
		barber.getCondition().await();
		barber.getLock().unlock();
		lock.lock();
		System.out.println("理发师\t"+id+"\t理发完成，呼叫下一个用户");
		lock.unlock();
	}
	
	
	public void addDropsoff() {
		nDropsoff++;
	}
	public int getDropsoff() {
		return nDropsoff; 
	}
	public int getSleepBarber() {
		
		lock.lock();
		for(Barber b:barList) {
			if(b.getBusy()==false) {
				lock.unlock();
				return b.getId();
			}
		}
		lock.unlock();
		return -1;
	}
	
	public Shop(int b,int c) {
		nBarbers=b;
		nChairs=c;
		barList=new ArrayList<>();
		for(int i=0;i<nBarbers;i++) {
			barList.add(new Barber(i));
		}
		cusList=new ArrayList<>();
	}
}
