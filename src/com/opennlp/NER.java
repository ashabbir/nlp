package com.opennlp;

public class NER {
	
	public static void main(String[] args){
		System.out.println("hello status main");
		sayHello();
		NER n = new NER();
		n.say();
	}
	
	public static void sayHello(){
		System.out.println("hello static not main");
	}
	
	public void say(){
		System.out.println("hello from non static");
	}

}
