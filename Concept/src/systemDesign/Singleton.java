package systemDesign;

public class Singleton {
	
//	Lazy Instantiation: Instance is created only when needed, saving memory.
//
//	Thread Safety: Use synchronized or other concurrency mechanisms to avoid multiple instance creation in multi-threaded environments.
//
//	Early Instantiation: Instance is created at class loading time (simpler but may waste resources if unused).
	
	//Lazy initialization -- Created when needed
	private static Singleton instance = null;
	
	//Eager initialization -- Created when class is loaded
	//private static Singleton instance = new Singleton();
	
	// Private constructor
	private Singleton() {
		System.out.println("Singleton class constructor");
	}
	
	// Thread-safe method to get the instance
	public synchronized static  Singleton getInstance() {
		if (instance == null) {
			instance = new Singleton();
		}
		return instance;
	}

	public static void main(String[] args) {
//		Singleton s1 = new Singleton();
//		Singleton s2 = new Singleton();
		
		Singleton s1 = Singleton.getInstance();
		Singleton s2 = Singleton.getInstance();
		
		System.out.println("s1 hashcode: " + s1.hashCode() + " &&  s2 hashcode: " + s2.hashCode());
		
		if (s1 == s2) {
			System.out.println("Both references point to the same instance");
		} else {
			System.out.println("References point to different instances");
		}
	}

}
