import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Barber {
	private int id;
	private Customer myCus;
	private Lock lock;
	private Condition condition;
	private boolean busy;
	
	public Barber(int id) {
		this.id=id;
		lock=new ReentrantLock();
		condition=lock.newCondition();
		busy=false;
	}
	public int getId() {
		return id;
	}
	public void setBusy(Boolean b) {
		busy=b;
	}
	public Boolean getBusy() {
		return busy;
	}
	public Lock getLock() {
		return lock;
	}
	public Condition getCondition() {
		return condition;
	}
	
	public void setCustomer(Customer customer) {
		this.myCus=customer;
	}
	public Customer getCustomer() {
		return myCus;
	}
}
