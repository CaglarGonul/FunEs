package com.cgon.hqls;

import game_db.entity.Userlogin;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;

public class SelectHQLS {
    public static Userlogin getUserByUname(String userName,Session session){
        Query q = session.createQuery("from Userlogin where email=:uEmail");
        q.setString("uEmail", userName);
        List results = q.list();
        if(results.isEmpty())
            return null;
        Userlogin u = (Userlogin)results.get(0);
        return u;
    }
}
