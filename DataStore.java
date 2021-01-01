import org.json.JSONObject;

public class DataStore {

	static JSONObject jsonObject;
	public static void main(String[] args) throws Exception {
		Function function = new Function("/Users/rohanxyzg/projects/Freshworks_Backend_Assignment/Code/data.json");


		FastReader inputStream = new FastReader();
		String primaryKey ="";
		String objectKey="";
		String objectValue="";
		long valueCount = 0, totalCount = 0;
		boolean state = true;
		while(state)
		{
			
			System.out.println("Select the Function\n"+
			                         "1 - for Create \n"+
				                	 "2 - for Create with Time to Live\n"+
			                         "3 - for Read\n"+
				                	 "4 - for Delete\n"+
				                	 "5 - for exit");
			
			int number = inputStream.nextInt();
			
			
			switch(number) {
			case 1:
				valueCount = 0;
				totalCount = 0;
				jsonObject = new JSONObject();
				System.out.println("Create an Object");
				System.out.println("Enter Primary Key");
				primaryKey = inputStream.next();
				totalCount += primaryKey.length();
				System.out.println("Enter Key Value pair for object");
				System.out.println("Enter Object Key");
				objectKey = inputStream.next();
				valueCount += objectKey.length();
				System.out.println("Enter Object value");
				objectValue = inputStream.next();
				valueCount += objectValue.length();
				totalCount += valueCount;
				jsonObject.put(objectKey, objectValue);
				function.create(primaryKey, jsonObject, valueCount, totalCount);
				System.out.println("Your object has been added to our data-store");

				continue;
			case 2:
				int TTL = 0;
				valueCount = 0;
				totalCount = 0;
				jsonObject = new JSONObject();
				System.out.println("Create an Object with a specified Time to Live property");
				System.out.println("Enter Primary Key");
				primaryKey = inputStream.next();
				totalCount += primaryKey.length();
				System.out.println("Enter Time to Live(TTL) in seconds");
				TTL = inputStream.nextInt();
				System.out.println("Enter Key Value pair for object");
				System.out.println("Enter Object Key");
				objectKey = inputStream.next();
				valueCount += objectKey.length();
				System.out.println("Enter Object Value");
				objectValue = inputStream.next();
				valueCount += objectValue.length();
				totalCount += valueCount;
				jsonObject.put(objectKey, objectValue);
				function.create(primaryKey, jsonObject,TTL,valueCount,totalCount);
				System.out.println("Your object has been added to our data-store");
				continue;
			case 3:
				System.out.println("Read an Object");
				System.out.println("Enter the Primary key for object");
				primaryKey = inputStream.next();
				jsonObject = function.read(primaryKey);
				System.out.println(jsonObject);
				continue;
			case 4:
				System.out.println("Delete an Object");
				System.out.println("Enter the Primary key for object");
				primaryKey = inputStream.next();
			    function.delete(primaryKey);
				System.out.println("Successfully Deleted!!");
				continue;
			case 5:
				System.out.println("EXIT");
				state = false;
				break;
			}
		}
	     
		
		
		System.out.println("END!");
		
		
		

	}

}
