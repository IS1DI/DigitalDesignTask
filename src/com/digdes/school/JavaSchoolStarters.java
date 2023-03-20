package com.digdes.school;

import java.util.List;
import java.util.Map;

public class JavaSchoolStarters {
    public static void main(String[] args) {
        try {
            JavaSchoolStarter t = new JavaSchoolStarter();
            //Вставка строки в коллекцию
            List<Map<String,Object>> result1 = t.execute("INSERT VALUES 'lastName' = 'Федоров' , 'id'=3, 'age'=40, 'active'=true");
            System.out.println(result1);
            List<Map<String,Object>> result2 = t.execute("UPDATE VALUES 'active'=false, 'cost'=10.1 where 'id'=3");
            System.out.println(result2);
            List<Map<String,Object>> result3 = t.execute("SELECT");
            System.out.println(result3);

        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

}