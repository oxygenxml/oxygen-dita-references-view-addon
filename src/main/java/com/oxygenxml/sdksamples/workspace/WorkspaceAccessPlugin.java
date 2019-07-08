package com.oxygenxml.sdksamples.workspace;

import ro.sync.exml.plugin.Plugin;
import ro.sync.exml.plugin.PluginDescriptor;

/**
 * Workspace access plugin. 
 */
public class WorkspaceAccessPlugin extends Plugin {
  /**
   * The static plugin instance.
   */
  private static WorkspaceAccessPlugin instance = null;

  /**
   * Constructs the plugin.
   * 
   * @param descriptor The plugin descriptor
   */
  public WorkspaceAccessPlugin(PluginDescriptor descriptor) {
    super(descriptor);

    if (instance != null) {
      throw new IllegalStateException("Already instantiated!");
    }
    instance = this;
  }
  
  /**
   * Get the plugin instance.
   * 
   * @return the shared plugin instance.
   */
  public static WorkspaceAccessPlugin getInstance() {
    return instance;
  }
  
//	public static void main(String[] args)
//			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InstantiationException, InvocationTargetException {
//
//		Method[] methods = EditorArea.class.getMethods();
//		for (Method method : methods) {
//			System.out.println("method: " + method.getName());
//		}
//		Class myClass = CustomWorkspaceAccessPluginExtension.class;
//		// public fields declared in the class
//		Field[] fields = myClass.getFields();
//		for (Field field : fields) {
//			System.out.println("field: " + field + " " +  field.getType());
//		}
//		Field field = myClass.getField("root");
//		System.out.println("name " + field.getName());
//
//		Field field = myClass.getField("aici");
//		CustomWorkspaceAccessPluginExtension objectInstance = new CustomWorkspaceAccessPluginExtension();
//
//		Method[] methods = myClass.getMethods();
//		for (Method method : methods) {
//			System.err.println("methodName: " + method.getName() + " |modifier: " + method.getModifiers()
//					+ " |param count: " + method.getParameterCount());
//		}
//		
//		
//		Method method = myClass.getDeclaredMethod("myFunction");	
//		method.setAccessible(true);
//		Object myObject = myClass.newInstance();
//		Object tObject = method.invoke(myObject);
//		
//		System.err.println(method.getName());
//		System.err.println(method.getDefaultValue());
//		
//
//	}
}