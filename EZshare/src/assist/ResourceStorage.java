package assist;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import dao.Resource;


public class ResourceStorage {
	
	private ConcurrentHashMap<Tuple, Resource> resources 
		= new ConcurrentHashMap<Tuple, Resource>();
	
	public synchronized void storeResource(Resource resource) {
		resources.put(resource.getResourceKey(), resource);
	}
	
	public synchronized void updateResource(Resource resource){
		resources.replace(resource.getResourceKey(), resource);
	}
	

	public synchronized void removeResource(Tuple key) {
		resources.remove(key);
	}
	
	public boolean checkResource(Tuple key) {
		return resources.containsKey(key);
	}
	
	public boolean checkOwner(Resource resource){
		Iterator<Entry<Tuple, Resource>> iter = resources.entrySet().iterator();
		while (iter.hasNext()) {
			ConcurrentHashMap.Entry entry = (ConcurrentHashMap.Entry) iter.next();
			Resource val = (Resource) entry.getValue();
			if(val.getChannel().equals(resource.getChannel()) &&
					val.getUri().equals(resource.getUri())){
				return false;
			}
		}
		return true;
	}
	
	public Resource getFetchResource(String channel, String uri){
		Iterator<Entry<Tuple, Resource>> iter = resources.entrySet().iterator();
		while (iter.hasNext()) {
			ConcurrentHashMap.Entry entry = (ConcurrentHashMap.Entry) iter.next();
			Resource val = (Resource) entry.getValue();
			if(val.getChannel().equals(channel) && val.getUri().equals(uri)){
				return val;
			}
		}
		return null;
	}
	
	public ArrayList<Resource> getMatchingResources (Resource template, String hostnamePort) {
		
		ArrayList<Resource> matchResources = new ArrayList<Resource>();
		Iterator<Entry<Tuple, Resource>> iter = resources.entrySet().iterator();
		while (iter.hasNext()) {
			ConcurrentHashMap.Entry entry = (ConcurrentHashMap.Entry) iter.next();
			Resource val = (Resource) entry.getValue();
			matchResources.add(val);
			/*
			 * The purpose of the query is to match the template against 
			 * existing resources. The template will match a candidate resource if:
			 * 1. (The template channel equals (case sensitive) the resource channel 
			 * 2. AND If the template contains an owner that is not "", 
			 *    then the candidate owner must equal it (case sensitive) 
			 * 3. AND Any tags present in the template also are present 
			 * 	  in the candidate (case insensitive) 
			 * 4. AND If the template contains a URI then the 
			 * 	  candidate URI matches (case sensitive) 
			 * 5. AND (The candidate name contains the template name as a 
			 *    substring (for non "" template name) 
			 * 6. OR The candidate description contains the template description 
			 *    as a substring (for non "" template descriptions)
			 * 7. OR The template description and name are both ""))
			 */
			
			/*
			if(template.getChannel().equals(val.getChannel())){
				if(template.getOwner() != ""){
					if(template.getOwner().equals(val.getOwner())){
						if(val.getTags().toString().contains(template.getTags().toString())){
							if(template.getUri() != ""){
								if(template.getUri().equals(val.getUri())){
									if(val.getName().contains(template.getName()) || 
											val.getDescription().contains(template.getDescription())){
										matchResources.add(val);
									}
								}
							}else{
								if(val.getName().contains(template.getName()) || 
										val.getDescription().contains(template.getDescription())){
									matchResources.add(val);
								}
							}
						}
					}
				}else{
					if(val.getTags().toString().contains(template.getTags().toString())){
						if(template.getUri() != ""){
							if(template.getUri().equals(val.getUri())){
								if(val.getName().contains(template.getName()) || 
										val.getDescription().contains(template.getDescription())){
									matchResources.add(val);
								}
							}
						}else{
							if(val.getName().contains(template.getName()) || 
									val.getDescription().contains(template.getDescription())){
								matchResources.add(val);
							}
						}
					}
				}
			}
				*/
	}

	return matchResources;
	}
}
/*
public void printResource(){
	Iterator<Entry<Tuple, Resource>> iter = resources.entrySet().iterator();
	while (iter.hasNext()) {
		ConcurrentHashMap.Entry entry = (ConcurrentHashMap.Entry) iter.next();
		Resource val = (Resource) entry.getValue();
		System.out.println(val.getName());
	}
}
*/
