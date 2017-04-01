import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ResourceStorage {
	private ConcurrentHashMap<Tuple, Resource> resources 
		= new ConcurrentHashMap<Tuple, Resource>();
	
	public synchronized void storeResource(Resource resource) {
		resources.put(resource.getResourceKey(), resource);
	}
	
	public boolean checkResource(Tuple key) {
		return resources.containsKey(key);
	}
	
	public synchronized void removeResource(String uri) {
		resources.remove(uri);
	}
	
	public ArrayList<Resource> getMatchingResources (Resource template) {
		return null;
		//TODO: returns an arraylist of matching resources
	}
}
