package com.aurasoftwareinc.java.challenge1;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static android.content.ContentValues.TAG;

public class JsonMarshal
{
    private static final Set<Class> WRAPPER_TYPES = new HashSet(Arrays.asList(
            Boolean.class, Character.class, Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, Void.class));

    public static JSONObject marshalJSON(Object object)
    {
        JSONObject json = new JSONObject();

        //Remove the package name from the name of the key for easier readability
        String objectKeyTitle = object.getClass().getName().substring(object.getClass().getName().lastIndexOf('.') + 1);

        try {
            json.put(objectKeyTitle, InspectObject(object));
        }
        catch (JSONException exc) {

        }


        //Loop over the members of the class (make any private members accessible)
        //If the member variable is primitive, store it in the json object
        //If the member variable is not primitive but implements the JsonMarshal interface, inspect it (recursively)
        //If the member variable is not primitive and does not implement the interface, store it in the json file as string
        //Remember to check for serialVersionUID and not to include it in the Json object

        return json;
    }

    //This function parses the members of the passed object recursively
    //If the member implements the interface JsonMarshal, the function parses its member objects
    //If the member does not implement the interface, the function adds it to the json object depending on its type
    private static JSONObject InspectObject(Object object) {

        JSONObject json = new JSONObject();

        Field[] parentObjectMembers = object.getClass().getDeclaredFields();

        //Loop over the members of the passed object
        for (int i=0;i<parentObjectMembers.length;i++) {
            parentObjectMembers[i].setAccessible(true);
            try {
                Object memberAsObject = parentObjectMembers[i].get(object);
                //Handle non primitive types
                if (!parentObjectMembers[i].getType().isPrimitive()) {
                    //Recursion in case the member object implements JsonMarshalInterface
                    if (memberAsObject instanceof JsonMarshalInterface) {
                        String objectKeyTitle = memberAsObject.getClass().getName().substring(memberAsObject.getClass().getName().lastIndexOf('.') + 1);
                        try {
                            json.put(objectKeyTitle, InspectObject(memberAsObject));
                        }
                        catch (JSONException exc) {

                        }
                    }
                    //Check for JSONObjects, JSONArrays and wrapped primitive types add them to the resulting json object
                    //Ignore synthetic members (the compiler generates a member called $change which is generated at runtime)
                    else if (!parentObjectMembers[i].isSynthetic()){
                        if ((memberAsObject instanceof JSONObject) || (memberAsObject instanceof JSONArray) || (WRAPPER_TYPES.contains(parentObjectMembers[i].getType()) && parentObjectMembers[i].get(object)!=null)) {
                            try {
                                json.put(parentObjectMembers[i].getName(), parentObjectMembers[i].get(object));
                            }
                            catch (JSONException exc) {

                            }
                        }
                        //Check for byte arrays
                        else if  (memberAsObject instanceof byte[]) {
                            try {
                                json.put(parentObjectMembers[i].getName(), new String((byte[]) parentObjectMembers[i].get(object)));
                            }
                            catch (JSONException exc) {
                                Log.d("Something went wrong.. ", exc.getMessage());
                            }
                        }
                        else {
                            //If the object is not primitive nor wrapped primitive and does not implement the JSonMarshal interface, then show it as a string of its name
                            try {
                                json.put(parentObjectMembers[i].getName(), parentObjectMembers[i].getName());
                            } catch (JSONException exception) {
                            }
                        }
                    }
                }
                //Store primitive objects in the json object directly
                //Do not include the object whose name is serialVersionUID since it's generated at runtime
                else if (parentObjectMembers[i].getName() != "serialVersionUID") {
                    try {
                        json.put(parentObjectMembers[i].getName(), parentObjectMembers[i].get(object).toString());
                    }
                    catch (JSONException exception) {
                    }
                }
            }
            catch (IllegalAccessException exception) {
            }
        }

        return json;
    }

    public static boolean unmarshalJSON(Object object, JSONObject json)
    {
        //
        // Todo: replace contents of this method with Your code.
        //

        return true;
    }
}