import org.json.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.lang.instrument.Instrumentation;




class SameKey extends Exception {

}

class InvalidKey extends Exception {

}

class TimeLimitExpired extends Exception {

}

class KeyLengthExceeded extends Exception{
	
}

class ValueSizeExceeded extends Exception{
	
}

class StorageSizeExceeded extends Exception{

}


public class Function {

    private final String filePath; // Making Immutable filePath
    private int size;
    Function(String path) throws JSONException {
    	filePath = path;
    	size = 0;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(" ", " ");
         try (FileWriter file = new FileWriter(filePath,false))
        		{
        	      file.write(jsonObject.toString());
        	      file.close();
        		}
         catch (IOException E)
         {
        	System.out.println("IOException!!");
         }
        
        
    }
    /*
       Default constructor of class Operation
    */
    Function() throws JSONException {
    	filePath = "Default_File_Path";
    	size = 0;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(" ", " ");
         try (FileWriter file = new FileWriter(filePath,false))
        		{
        	      file.write(jsonObject.toString());
        	      file.close();
        		}
         catch (IOException E)
         {
        	System.out.println("Caught IOException"); 
         }
        
    }
    // Create operation when Time to Live property is not given
    public void create(String Key, JSONObject Value,long valueCount,long totalCount) throws Exception
    {
        try{
            if (Key.length()>32)
                throw new KeyLengthExceeded();
            if(valueCount > 16000)
                throw new ValueSizeExceeded();
            if(size+totalCount > 1024*1024*1024)
                throw new StorageSizeExceeded();
        }
        catch (KeyLengthExceeded e) {
            System.out.println(" Key length exceeds maximum limit, please enter valid Key");
        }
        catch (ValueSizeExceeded e){
            System.out.println(" Value size exceeds maximum limit, please enter valid Value");
        }
        catch (StorageSizeExceeded e){
            System.out.println(" Date Store exceeds maximum limit, please delete a few items");
        }

        try (FileReader reader = new FileReader(filePath)) {
            JSONTokener tokener = new JSONTokener(reader);
            JSONObject jsonObject = new JSONObject(tokener);
            if (jsonObject.has(Key))
                throw new SameKey();
            JSONArray array = new JSONArray();
            array.put(Value);
            jsonObject.put(Key, array);
            size += totalCount;
            try (FileWriter file = new FileWriter(filePath,false))
            {
                file.write(jsonObject.toString());
                file.close();
            } catch (IOException e) {
                System.out.println("Caught IO Exception");
            }

        } catch (SameKey e) {
            System.out.println("Key already exists. Duplicate keys not allowed");
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found");
        } catch (IOException e) {
            System.out.println("Caught IO Exception");
        }
    }
    // Create method with Time To Live property
    public void create(String Key, JSONObject Value, int TTL,long valueCount,long totalCount) throws Exception // Create method when TimeToLive is provided
    {
        try{
            if (Key.length()>32)
                throw new KeyLengthExceeded();
            if(valueCount > 16000)
                throw new ValueSizeExceeded();
            if(size+totalCount > 1024*1024*1024)
                throw new StorageSizeExceeded();
        }
        catch (KeyLengthExceeded e) {
            System.out.println(" Key length exceeds maximum limit, please enter valid Key");
        }
        catch (ValueSizeExceeded e){
            System.out.println(" Value size exceeds maximum limit, please enter valid Value");
        }
        catch (StorageSizeExceeded e){
            System.out.println(" Date Store exceeds maximum limit, please delete a few items");
        }
         try (FileReader reader = new FileReader(filePath)) {
            
        	 JSONTokener tokener = new JSONTokener(reader);
             JSONObject jsonObject = new JSONObject(tokener);
            if (jsonObject.has(Key))
                throw new SameKey();
            JSONArray array = new JSONArray(); // Array storing {value, TimeToLive}
            array.put(Value);
            array.put(TTL);

            LocalTime timeObject = LocalTime.now();
            int exactTime = timeObject.toSecondOfDay();
            array.put(exactTime);
            jsonObject.put(Key, array);
            size += totalCount;
            try (FileWriter file = new FileWriter(filePath,false))
            {
                file.write(jsonObject.toString());
                file.close();

            } catch (IOException e) {
                System.out.println("Caught IO Exception");
            }

        } catch (SameKey e) {
            System.out.println("KEY already exists, Duplicate keys not allowed");
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found");
        } catch (IOException e) {
            System.out.println("Caught IO Exception");
        } 
    }

    // Read method to return a JSON Object corresponding to the Key given
    public JSONObject read(String Key) throws Exception
    {
        
        try (FileReader reader = new FileReader(filePath)) {

            JSONTokener tokener = new JSONTokener(reader);
            JSONObject jsonObject = new JSONObject(tokener);
            if (jsonObject.has(Key))
            {
                new JSONArray();
                JSONArray array;
                array = jsonObject.getJSONArray(Key);
                LocalTime timeObject = LocalTime.now();
                int currentTime = timeObject.toSecondOfDay();
                if(array.length() <= 2){
                    return array.getJSONObject(0);
                }
                else if (array.length() > 2 && (currentTime  < (array.getInt(2))+array.getInt(1))) //Checking if object is not expired
                    return array.getJSONObject(0);
                else
                    throw new TimeLimitExpired();

            } else
                throw new InvalidKey();

        } catch (TimeLimitExpired e) {
            System.out.println("Time to Live Expired");
        } catch (InvalidKey e) {
            System.out.println("Invalid Key, Enter a valid key to continue");
        } catch (FileNotFoundException e) {
            System.out.println("File Does Not Found");
        } catch (IOException e) {
            System.out.println("Caught IO Exception");
        }
		return null;
    }

    // Delete method for a <Key, Value> Pair
    public void delete(String Key) throws Exception
    {
       
        try (FileReader reader = new FileReader(filePath))
        {
        	JSONTokener tokener = new JSONTokener(reader);
            JSONObject jsonObject = new JSONObject(tokener);
            if (jsonObject.has(Key))
            {
                JSONArray array = new JSONArray();
                array = jsonObject.getJSONArray(Key);
                LocalTime timeObject = LocalTime.now();
                int currentTime = timeObject.toSecondOfDay();
                if (array.length() < 2 || (currentTime  < (array.getInt(2))+array.getInt(1))) { //Checking if object is not expired
                    size -= Key.length() + array.getJSONObject(0).toString().length();
                    jsonObject.remove(Key);
                }
                else
                    throw new TimeLimitExpired();

                try (FileWriter file = new FileWriter(filePath,false))
                {

                    file.write(jsonObject.toString());
                    file.close();
                }
            }
                else
                    throw new InvalidKey();

            } 
            catch (InvalidKey e) {
                System.out.println("Invalid Key, Enter a valid key to continue");
            } catch (IOException e) {
                System.out.println("Caught IO Exception");
            }

         catch (TimeLimitExpired e) {
            System.out.println("Key Exceeded Time To Live");
        }
    }
    
   
}