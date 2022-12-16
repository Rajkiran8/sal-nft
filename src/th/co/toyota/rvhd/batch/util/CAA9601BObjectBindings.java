package th.co.toyota.rvhd.batch.util;

import java.util.Hashtable;

public class CAA9601BObjectBindings {
  private static Hashtable objectClassBindings = new Hashtable();
  
  public static void bindObject(Object object2bind) {
    bindObject(object2bind, null);
  }
  
  public static void bindObject(Object object2bind, Object token) {
    if (token != null) {
      objectClassBindings.put(token, object2bind);
    } else {
      objectClassBindings.put(object2bind.getClass(), object2bind);
    } 
  }
  
  public static Object getObject(Object key) {
    return objectClassBindings.get(key);
  }
  
  public static Object unbindObject(Object key) {
    return objectClassBindings.remove(key);
  }
}
