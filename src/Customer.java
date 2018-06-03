import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Customer {
	private int id;
	private int myBarber;
	private Lock lock;
	private Condition condition;
	
	public Customer(int id) {
		this.id=id;
		lock=new ReentrantLock();
		condition=lock.newCondition();
	}
	public void setBarber(int b) {
		myBarber=b;
	}
	public int getId() {
		return id;
	}
	public Lock getLock() {
		return lock;
	}
	public Condition getCondition() {
		return condition;
	}
	public int getBar() {
		return myBarber;
	}
}
